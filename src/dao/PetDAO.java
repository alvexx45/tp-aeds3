package dao;
import model.Pet;

public class PetDAO {
    private Arquivo<Pet> arqPets;

    public PetDAO() throws Exception {
        arqPets = new Arquivo<>("pets", Pet.class.getConstructor());
    }

    public boolean incluirPet(Pet pet) throws Exception {
        return arqPets.create(pet) > 0;
    }

    public boolean alterarPet(Pet pet) throws Exception {
        return arqPets.update(pet);
    }

    public boolean excluirPet(int id) throws Exception {
        return arqPets.delete(id);
    }

    public Pet buscarPet(int id) throws Exception {
        return arqPets.read(id);
    }
}
