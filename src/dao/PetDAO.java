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
        // Validar se já existe pet com mesmo nome para o mesmo dono
        String cpfDono = pet.getDono() != null ? pet.getDono().getCpf() : null;
        if (cpfDono != null && !cpfDono.isEmpty()) {
            java.util.List<Pet> petsDoMesmoDono = buscarPetsPorCpfDono(cpfDono);
            for (Pet petExistente : petsDoMesmoDono) {
                if (petExistente.getNome().equalsIgnoreCase(pet.getNome())) {
                    throw new IllegalArgumentException("Já existe um pet com o nome '" + pet.getNome() + 
                                                     "' para o dono com CPF: " + cpfDono);
                }
            }
        }
        
        // Criar o pet no arquivo principal
        int idGerado = arqPets.create(pet);
        
        if (idGerado > 0) {
            // Inserir relacionamento na Hash Extensível
            if (cpfDono != null && !cpfDono.isEmpty()) {
                indiceHash.inserir(cpfDono, idGerado);
            }
            return true;
        }
        
        return false;
    }

    public boolean alterarPet(Pet pet) throws Exception {
        // Buscar pet existente
        Pet petExistente = arqPets.read(pet.getId());
        if (petExistente == null) {
            throw new IllegalArgumentException("Pet não encontrado com ID: " + pet.getId());
        }
        
        // Validar se nome mudou e se já existe outro pet com o mesmo nome para o mesmo dono
        String cpfDono = pet.getDono() != null ? pet.getDono().getCpf() : null;
        if (cpfDono != null && !cpfDono.isEmpty()) {
            // Se o nome mudou, verificar se já existe outro pet com esse nome
            if (!petExistente.getNome().equalsIgnoreCase(pet.getNome())) {
                java.util.List<Pet> petsDoMesmoDono = buscarPetsPorCpfDono(cpfDono);
                for (Pet outropet : petsDoMesmoDono) {
                    if (outropet.getId() != pet.getId() && 
                        outropet.getNome().equalsIgnoreCase(pet.getNome())) {
                        throw new IllegalArgumentException("Já existe outro pet com o nome '" + pet.getNome() + 
                                                         "' para o dono com CPF: " + cpfDono);
                    }
                }
            }
        }
        
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

    public java.util.List<Pet> listarTodosPets() throws Exception {
        // Retorna todos os pets usando findAll com condição sempre verdadeira
        return arqPets.findAll(pet -> true);
    }

    public void close() throws Exception {
        // Fechar o arquivo se necessário
    }
}
