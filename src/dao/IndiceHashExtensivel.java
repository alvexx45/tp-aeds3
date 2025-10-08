package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe gerenciadora do índice Hash Extensível para relacionamentos Pet-Dono.
 * Encapsula a HashExtensivel e fornece métodos específicos para gerenciar
 * o relacionamento 1:N entre Cliente (Dono) e Pet.
 */
public class IndiceHashExtensivel {
    private HashExtensivel<RelacionamentoPetDono> hashExtensivel;
    private static final int REGISTROS_POR_CESTO = 5; // Quantidade de registros por cesto
    
    /**
     * Construtor que inicializa a Hash Extensível.
     * @param nomeEntidade Nome da entidade para criar os arquivos (ex: "pets")
     * @throws Exception se houver erro ao criar a hash extensível
     */
    public IndiceHashExtensivel(String nomeEntidade) throws Exception {
        // Criar diretório se não existir
        File diretorio = new File("src/dados");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
        
        diretorio = new File("src/dados/" + nomeEntidade);
        if (!diretorio.exists()) {
            diretorio.mkdir();
        }
        
        // Inicializar a Hash Extensível
        String caminhoDir = "src/dados/" + nomeEntidade + "/" + nomeEntidade + "_hash.dir";
        String caminhoCestos = "src/dados/" + nomeEntidade + "/" + nomeEntidade + "_hash.db";
        
        hashExtensivel = new HashExtensivel<>(
            RelacionamentoPetDono.class.getConstructor(),
            REGISTROS_POR_CESTO,
            caminhoDir,
            caminhoCestos
        );
    }
    
    /**
     * Insere um relacionamento Pet-Dono na hash extensível.
     * @param cpfDono CPF do dono (cliente)
     * @param idPet ID do pet
     * @return true se inserido com sucesso, false caso contrário
     * @throws Exception se houver erro na inserção
     */
    public boolean inserir(String cpfDono, int idPet) throws Exception {
        RelacionamentoPetDono rel = new RelacionamentoPetDono(cpfDono, idPet);
        return hashExtensivel.create(rel);
    }
    
    /**
     * Busca todos os IDs de pets associados a um CPF de dono.
     * Como agora usamos chave única (CPF + ID), precisamos varrer
     * todos os registros e filtrar por CPF.
     * @param cpfDono CPF do dono para buscar os pets
     * @return Lista com os IDs dos pets do dono
     * @throws Exception se houver erro na busca
     */
    public List<Integer> buscarIdsPetsPorCpf(String cpfDono) throws Exception {
        List<Integer> idsPets = new ArrayList<>();
        String cpfLimpo = cpfDono.replaceAll("[^0-9]", "");
        
        // Listar todos os relacionamentos e filtrar por CPF
        List<RelacionamentoPetDono> todosRelacionamentos = hashExtensivel.listAll();
        
        for (RelacionamentoPetDono rel : todosRelacionamentos) {
            String cpfRelLimpo = rel.getCpfDono().replaceAll("[^0-9]", "");
            if (cpfRelLimpo.equals(cpfLimpo)) {
                idsPets.add(rel.getIdPet());
            }
        }
        
        return idsPets;
    }
    
    /**
     * Busca todos os relacionamentos de um CPF (para quando houver múltiplos pets).
     * Este método percorre todos os relacionamentos e filtra por CPF.
     * @param cpfDono CPF do dono
     * @return Lista de todos os relacionamentos encontrados
     * @throws Exception se houver erro na busca
     */
    public List<RelacionamentoPetDono> buscarRelacionamentosPorCpf(String cpfDono) throws Exception {
        List<RelacionamentoPetDono> relacionamentos = new ArrayList<>();
        String cpfLimpo = cpfDono.replaceAll("[^0-9]", "");
        
        // Listar todos os relacionamentos e filtrar por CPF
        List<RelacionamentoPetDono> todosRelacionamentos = hashExtensivel.listAll();
        
        for (RelacionamentoPetDono rel : todosRelacionamentos) {
            String cpfRelLimpo = rel.getCpfDono().replaceAll("[^0-9]", "");
            if (cpfRelLimpo.equals(cpfLimpo)) {
                relacionamentos.add(rel);
            }
        }
        
        return relacionamentos;
    }
    
    /**
     * Remove um relacionamento específico Pet-Dono da hash.
     * @param cpfDono CPF do dono
     * @param idPet ID do pet a ser removido
     * @return true se removido com sucesso, false caso contrário
     * @throws Exception se houver erro na remoção
     */
    public boolean remover(String cpfDono, int idPet) throws Exception {
        RelacionamentoPetDono rel = new RelacionamentoPetDono(cpfDono, idPet);
        int hashCode = rel.hashCode();
        return hashExtensivel.delete(hashCode);
    }
    
    /**
     * Remove todos os relacionamentos de um CPF (quando o cliente é excluído).
     * @param cpfDono CPF do dono cujos relacionamentos serão removidos
     * @return quantidade de relacionamentos removidos
     * @throws Exception se houver erro na remoção
     */
    public int removerTodosPorCpf(String cpfDono) throws Exception {
        int removidos = 0;
        List<RelacionamentoPetDono> relacionamentos = buscarRelacionamentosPorCpf(cpfDono);
        
        for (RelacionamentoPetDono rel : relacionamentos) {
            if (remover(rel.getCpfDono(), rel.getIdPet())) {
                removidos++;
            }
        }
        
        return removidos;
    }
    
    /**
     * Verifica se existe algum relacionamento para um CPF.
     * @param cpfDono CPF do dono
     * @return true se existir pelo menos um pet associado
     * @throws Exception se houver erro na verificação
     */
    public boolean existeRelacionamento(String cpfDono) throws Exception {
        List<Integer> ids = buscarIdsPetsPorCpf(cpfDono);
        return !ids.isEmpty();
    }
    
    /**
     * Imprime informações da hash extensível (para debug).
     */
    public void print() {
        hashExtensivel.print();
    }
}
