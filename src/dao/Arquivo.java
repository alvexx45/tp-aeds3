package dao;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class Arquivo<T extends Registro> {
    private static final int TAM_CABECALHO = 12;
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    private IndiceSequencial indice;

    public Arquivo(String nomeArquivo, Constructor<T> construtor) throws Exception {
        File diretorio = new File("src/dados");
        if (!diretorio.exists()) diretorio.mkdirs();

        diretorio = new File("src/dados/" + nomeArquivo);
        if (!diretorio.exists()) diretorio.mkdir();

        this.nomeArquivo = "src/dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        this.indice = new IndiceSequencial(nomeArquivo);

        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);    // Último ID usado
            arquivo.writeLong(-1);  // Lista de registros excluídos
        }
    }

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        long endereco = getDeleted(dados.length);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');  // Lápide
            arquivo.writeShort(dados.length);
            arquivo.write(dados);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte(' ');  // Remove a lápide
            arquivo.skipBytes(2);
            arquivo.write(dados);
        }
        
        // Adicionar entrada no índice sequencial
        indice.inserir(obj.getId(), endereco);
        
        return obj.getId();
    }

    public T read(int id) throws Exception {
        // Buscar endereço no índice primeiro (busca binária O(log n))
        long endereco = indice.buscar(id);
        if (endereco == -1) {
            return null; // Não encontrado no índice
        }
        
        // Ir diretamente ao endereço no arquivo (acesso direto O(1))
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        if (lapide != ' ') {
            return null; // Registro foi excluído
        }
        
        short tamanho = arquivo.readShort();
        byte[] dados = new byte[tamanho];
        arquivo.read(dados);
        
        T obj = construtor.newInstance();
        obj.fromByteArray(dados);
        return obj;
    }

    public boolean delete(int id) throws Exception {
        // Buscar endereço no índice
        long endereco = indice.buscar(id);
        if (endereco == -1) {
            return false; // Não encontrado
        }
        
        // Verificar se o registro ainda existe
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        if (lapide != ' ') {
            return false; // Já foi excluído
        }
        
        short tamanho = arquivo.readShort();
        
        // Marcar como excluído no arquivo de dados
        arquivo.seek(endereco);
        arquivo.writeByte('*');
        addDeleted(tamanho, endereco);
        
        // Remover do índice
        indice.remover(id);
        
        return true;
    }

    public boolean update(T novoObj) throws Exception {
        int id = novoObj.getId();
        
        // Buscar endereço atual no índice
        long enderecoAtual = indice.buscar(id);
        if (enderecoAtual == -1) {
            return false; // Registro não encontrado
        }
        
        // Verificar se o registro ainda existe
        arquivo.seek(enderecoAtual);
        byte lapide = arquivo.readByte();
        if (lapide != ' ') {
            return false; // Registro foi excluído
        }
        
        short tamanhoAtual = arquivo.readShort();
        byte[] novosDados = novoObj.toByteArray();
        short novoTam = (short) novosDados.length;

        if (novoTam <= tamanhoAtual) {
            // Cabe no espaço atual - sobrescrever
            arquivo.seek(enderecoAtual + 3);
            arquivo.write(novosDados);
        } else {
            // Não cabe - marcar como excluído e criar novo registro
            arquivo.seek(enderecoAtual);
            arquivo.writeByte('*');
            addDeleted(tamanhoAtual, enderecoAtual);

            // Encontrar novo espaço
            long novoEndereco = getDeleted(novosDados.length);
            if (novoEndereco == -1) {
                arquivo.seek(arquivo.length());
                novoEndereco = arquivo.getFilePointer();
                arquivo.writeByte(' ');
                arquivo.writeShort(novoTam);
                arquivo.write(novosDados);
            } else {
                arquivo.seek(novoEndereco);
                arquivo.writeByte(' ');
                arquivo.skipBytes(2);
                arquivo.write(novosDados);
            }
            
            // Atualizar índice com novo endereço
            indice.atualizar(id, novoEndereco);
        }
        
        return true;
    }

    private void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception {
        long posicao = 4; // Posição após o int do último ID
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;

        if (endereco == -1) {
            arquivo.seek(4); // Posição do ponteiro para lista de excluídos
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(endereco + 1);
                int tamanho = arquivo.readShort();
                proximo = arquivo.readLong();

                if (tamanho > tamanhoEspaco) {
                    if (posicao == 4) // Se estamos no início da lista de excluídos
                        arquivo.seek(posicao);
                    else
                        arquivo.seek(posicao + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(endereco);
                    break;
                }

                if (proximo == -1) {
                    arquivo.seek(endereco + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(-1);
                    break;
                }

                posicao = endereco;
                endereco = proximo;
            } while (endereco != -1);
        }
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        long posicao = 4; // Posição após o int do último ID
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;
        int tamanho;

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();

            if (tamanho > tamanhoNecessario) {
                if (posicao == 4) // Se estamos no início da lista de excluídos
                    arquivo.seek(posicao);
                else
                    arquivo.seek(posicao + 3);
                arquivo.writeLong(proximo);
                return endereco;
            }
            posicao = endereco;
            endereco = proximo;
        }
        return -1;
    }

    // Método para busca sequencial quando necessário (para outros campos que não a PK)
    public T findBy(java.util.function.Predicate<T> condition) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (condition.test(obj)) {
                    return obj;
                }
            }
        }
        return null;
    }

    // Método para buscar TODOS os registros que atendem a um critério
    public java.util.List<T> findAll(java.util.function.Predicate<T> condition) throws Exception {
        java.util.List<T> resultado = new java.util.ArrayList<>();
        arquivo.seek(TAM_CABECALHO);
        
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (condition.test(obj)) {
                    resultado.add(obj);  // Adiciona TODOS os que atendem ao critério
                }
            }
        }
        return resultado;
    }

    // Método para obter estatísticas do índice
    public String getEstatisticasIndice() {
        return "Registros no índice: " + indice.getTamanho();
    }

    public void close() throws Exception {
        if (indice != null) {
            indice.close();
        }
        arquivo.close();
    }
}