package dao;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

public class IndiceSequencial {
    private RandomAccessFile arquivo;
    private ArrayList<RegistroIndice> indices;
    
    // Estrutura de um registro no índice: [ID(4 bytes)][Endereço(8 bytes)] = 12 bytes
    private static final int TAM_REGISTRO_INDICE = 12;
    
    private class RegistroIndice implements Comparable<RegistroIndice> {
        int id;
        long endereco;
        
        public RegistroIndice(int id, long endereco) {
            this.id = id;
            this.endereco = endereco;
        }
        
        @Override
        public int compareTo(RegistroIndice outro) {
            return Integer.compare(this.id, outro.id);
        }
    }
    
    public IndiceSequencial(String nomeEntidade) throws Exception {
        String caminhoIndice = "./dados/" + nomeEntidade + "/" + nomeEntidade + ".idx";
        this.arquivo = new RandomAccessFile(caminhoIndice, "rw");
        this.indices = new ArrayList<>();
        
        carregarIndices();
    }
    
    private void carregarIndices() throws Exception {
        indices.clear();
        arquivo.seek(0);
        
        while (arquivo.getFilePointer() < arquivo.length()) {
            int id = arquivo.readInt();
            long endereco = arquivo.readLong();
            indices.add(new RegistroIndice(id, endereco));
        }
        
        // Garantir que os índices estejam ordenados
        Collections.sort(indices);
    }
    
    public void inserir(int id, long endereco) throws Exception {
        // Inserir na posição correta para manter ordem
        RegistroIndice novoRegistro = new RegistroIndice(id, endereco);
        
        int posicao = buscaBinariaInsercao(id);
        indices.add(posicao, novoRegistro);
        
        salvarIndices();
    }
    
    public long buscar(int id) throws Exception {
        // Busca binária na lista ordenada
        int posicao = buscaBinaria(id);
        if (posicao >= 0) {
            return indices.get(posicao).endereco;
        }
        return -1; // Não encontrado
    }
    
    public boolean remover(int id) throws Exception {
        int posicao = buscaBinaria(id);
        if (posicao >= 0) {
            indices.remove(posicao);
            salvarIndices();
            return true;
        }
        return false; // Não encontrado
    }
    
    public boolean atualizar(int id, long novoEndereco) throws Exception {
        int posicao = buscaBinaria(id);
        if (posicao >= 0) {
            indices.get(posicao).endereco = novoEndereco;
            salvarIndices();
            return true;
        }
        return false; // Não encontrado
    }
    
    private int buscaBinaria(int id) {
        int inicio = 0;
        int fim = indices.size() - 1;
        
        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            int idMeio = indices.get(meio).id;
            
            if (idMeio == id) {
                return meio;
            } else if (idMeio < id) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        
        return -1; // Não encontrado
    }
    
    private int buscaBinariaInsercao(int id) {
        int inicio = 0;
        int fim = indices.size();
        
        while (inicio < fim) {
            int meio = (inicio + fim) / 2;
            if (indices.get(meio).id < id) {
                inicio = meio + 1;
            } else {
                fim = meio;
            }
        }
        
        return inicio;
    }
    
    private void salvarIndices() throws Exception {
        arquivo.setLength(0); // Limpar arquivo
        arquivo.seek(0);
        
        for (RegistroIndice registro : indices) {
            arquivo.writeInt(registro.id);
            arquivo.writeLong(registro.endereco);
        }
    }
    
    public int getTamanho() {
        return indices.size();
    }
    
    public void close() throws Exception {
        if (arquivo != null) {
            arquivo.close();
        }
    }
}