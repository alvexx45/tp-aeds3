package dao;
import model.Pet;

public class PetDAO {
    private Arquivo<Pet> arqPets;
    private IndiceHashExtensivel indiceHash;

    public PetDAO() throws Exception {
        arqPets = new Arquivo<>("pets", Pet.class.getConstructor());
        indiceHash = new IndiceHashExtensivel("pets");
    }

    public boolean incluirPet(Pet pet) throws Exception {
        // Criar o pet no arquivo principal
        int idGerado = arqPets.create(pet);
        
        if (idGerado > 0) {
            // Inserir relacionamento na Hash Extensível
            String cpfDono = pet.getDono() != null ? pet.getDono().getCpf() : null;
            if (cpfDono != null && !cpfDono.isEmpty()) {
                indiceHash.inserir(cpfDono, idGerado);
            }
            return true;
        }
        
        return false;
    }

    public boolean alterarPet(Pet pet) throws Exception {
        return arqPets.update(pet);
    }

    public boolean excluirPet(int id) throws Exception {
        // Buscar o pet primeiro para obter o CPF do dono
        Pet pet = arqPets.read(id);
        if (pet == null) {
            return false;
        }
        
        // Remover do arquivo principal
        boolean removido = arqPets.delete(id);
        
        if (removido) {
            // Remover relacionamento da Hash Extensível
            String cpfDono = pet.getDono() != null ? pet.getDono().getCpf() : null;
            if (cpfDono != null && !cpfDono.isEmpty()) {
                indiceHash.remover(cpfDono, id);
            }
        }
        
        return removido;
    }

    public Pet buscarPet(int id) throws Exception {
        return arqPets.read(id);
    }

    public java.util.List<Pet> buscarPetsPorCpfDono(String cpfDono) throws Exception {
        java.util.List<Pet> pets = new java.util.ArrayList<>();
        
        // Buscar IDs dos pets usando a Hash Extensível (O(1) em média)
        java.util.List<Integer> idsPets = indiceHash.buscarIdsPetsPorCpf(cpfDono);
        
        // Buscar cada pet pelo ID usando o índice sequencial do Arquivo
        for (Integer idPet : idsPets) {
            Pet pet = arqPets.read(idPet);
            if (pet != null) {
                pets.add(pet);
            }
        }
        
        return pets;
    }

    public Pet buscarPrimeiroPetPorCpfDono(String cpfDono) throws Exception {
        return arqPets.findBy(p -> p.getDono() != null && p.getDono().getCpf().equals(cpfDono));
    }

    public void close() throws Exception {
        // Fechar o arquivo se necessário
    }
}
