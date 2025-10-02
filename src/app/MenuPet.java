package app;

import dao.PetDAO;
import dao.ClienteDAO;
import model.Pet;
import model.Cliente;
import java.util.Scanner;

public class MenuPet {
    private PetDAO petDAO;
    private ClienteDAO clienteDAO;
    private Scanner scanner;

    public MenuPet() throws Exception {
        this.petDAO = new PetDAO();
        this.clienteDAO = new ClienteDAO();
        this.scanner = new Scanner(System.in);
    }

    public void exibirMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            System.out.println("\n=== MENU PET ===");
            System.out.println("1 - Incluir Pet");
            System.out.println("2 - Buscar Pet");
            System.out.println("3 - Alterar Pet");
            System.out.println("4 - Excluir Pet");
            System.out.println("5 - Listar Pets de um Cliente");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                
                switch (opcao) {
                    case 1:
                        incluirPet();
                        break;
                    case 2:
                        buscarPet();
                        break;
                    case 3:
                        alterarPet();
                        break;
                    case 4:
                        excluirPet();
                        break;
                    case 5:
                        listarPetsDeCliente();
                        break;
                    case 0:
                        System.out.println("Voltando ao menu principal...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void incluirPet() throws Exception {
        System.out.println("\n=== INCLUIR PET ===");
        
        System.out.print("Nome do Pet: ");
        String nome = scanner.nextLine();
        
        System.out.print("Espécie: ");
        String especie = scanner.nextLine();
        
        System.out.print("Raça: ");
        String raca = scanner.nextLine();
        
        System.out.print("Peso: ");
        float peso = Float.parseFloat(scanner.nextLine());
        
        System.out.print("CPF do Dono: ");
        String cpfDono = scanner.nextLine();
        
        // Verificar se o cliente existe
        Cliente dono = clienteDAO.buscarClientePorCPF(cpfDono);
        if (dono == null) {
            System.out.println("Cliente com CPF " + cpfDono + " não encontrado!");
            System.out.print("Deseja cadastrar este cliente primeiro? (s/n): ");
            String resposta = scanner.nextLine();
            if (resposta.equalsIgnoreCase("s")) {
                dono = cadastrarNovoCliente(cpfDono);
                if (dono == null) {
                    System.out.println("Não foi possível cadastrar o cliente. Pet não será incluído.");
                    return;
                }
            } else {
                System.out.println("Pet não pode ser incluído sem um dono válido.");
                return;
            }
        }
        
        Pet pet = new Pet(-1, nome, especie, raca, peso, dono);
        
        if (petDAO.incluirPet(pet)) {
            System.out.println("Pet incluído com sucesso! ID: " + pet.getId());
        } else {
            System.out.println("Erro ao incluir pet!");
        }
    }

    private Cliente cadastrarNovoCliente(String cpf) throws Exception {
        System.out.println("\n=== CADASTRO RÁPIDO DE CLIENTE ===");
        
        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Quantos telefones? ");
        int numTelefones = Integer.parseInt(scanner.nextLine());
        
        String[] telefones = new String[numTelefones];
        for (int i = 0; i < numTelefones; i++) {
            System.out.print("Telefone " + (i + 1) + ": ");
            telefones[i] = scanner.nextLine();
        }
        
        Cliente cliente = new Cliente(-1, cpf, nome, email, telefones);
        
        if (clienteDAO.incluirCliente(cliente)) {
            System.out.println("Cliente cadastrado com sucesso! ID: " + cliente.getId());
            return cliente;
        } else {
            System.out.println("Erro ao cadastrar cliente!");
            return null;
        }
    }

    private void buscarPet() throws Exception {
        System.out.println("\n=== BUSCAR PET ===");
        
        System.out.print("Digite o ID do pet: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Pet pet = petDAO.buscarPet(id);
        
        if (pet != null) {
            // Carregar dados completos do dono
            Cliente dono = clienteDAO.buscarClientePorCPF(pet.getDono().getCpf());
            pet.setDono(dono);
            exibirPet(pet);
        } else {
            System.out.println("Pet não encontrado!");
        }
    }

    private void alterarPet() throws Exception {
        System.out.println("\n=== ALTERAR PET ===");
        
        System.out.print("Digite o ID do pet a ser alterado: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Pet petExistente = petDAO.buscarPet(id);
        
        if (petExistente == null) {
            System.out.println("Pet não encontrado!");
            return;
        }
        
        // Carregar dados completos do dono
        Cliente dono = clienteDAO.buscarClientePorCPF(petExistente.getDono().getCpf());
        petExistente.setDono(dono);
        
        System.out.println("Pet atual:");
        exibirPet(petExistente);
        
        System.out.println("\nDigite os novos dados (Enter para manter o valor atual):");
        
        System.out.print("Nome [" + petExistente.getNome() + "]: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) {
            nome = petExistente.getNome();
        }
        
        System.out.print("Espécie [" + petExistente.getEspecie() + "]: ");
        String especie = scanner.nextLine();
        if (especie.trim().isEmpty()) {
            especie = petExistente.getEspecie();
        }
        
        System.out.print("Raça [" + petExistente.getRaca() + "]: ");
        String raca = scanner.nextLine();
        if (raca.trim().isEmpty()) {
            raca = petExistente.getRaca();
        }
        
        System.out.print("Peso [" + petExistente.getPeso() + "]: ");
        String pesoStr = scanner.nextLine();
        float peso = petExistente.getPeso();
        if (!pesoStr.trim().isEmpty()) {
            peso = Float.parseFloat(pesoStr);
        }
        
        System.out.print("CPF do Dono [" + petExistente.getDono().getCpf() + "]: ");
        String cpfDono = scanner.nextLine();
        if (cpfDono.trim().isEmpty()) {
            cpfDono = petExistente.getDono().getCpf();
        }
        
        // Verificar se o novo dono existe (se foi alterado)
        Cliente novoDono = dono;
        if (!cpfDono.equals(petExistente.getDono().getCpf())) {
            novoDono = clienteDAO.buscarClientePorCPF(cpfDono);
            if (novoDono == null) {
                System.out.println("Cliente com CPF " + cpfDono + " não encontrado!");
                return;
            }
        }
        
        Pet petAlterado = new Pet(id, nome, especie, raca, peso, novoDono);
        
        if (petDAO.alterarPet(petAlterado)) {
            System.out.println("Pet alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar pet!");
        }
    }

    private void excluirPet() throws Exception {
        System.out.println("\n=== EXCLUIR PET ===");
        
        System.out.print("Digite o ID do pet a ser excluído: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Pet pet = petDAO.buscarPet(id);
        
        if (pet == null) {
            System.out.println("Pet não encontrado!");
            return;
        }
        
        // Carregar dados completos do dono
        Cliente dono = clienteDAO.buscarClientePorCPF(pet.getDono().getCpf());
        pet.setDono(dono);
        
        System.out.println("Pet a ser excluído:");
        exibirPet(pet);
        
        System.out.print("Confirma a exclusão? (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.equalsIgnoreCase("s")) {
            if (petDAO.excluirPet(id)) {
                System.out.println("Pet excluído com sucesso!");
            } else {
                System.out.println("Erro ao excluir pet!");
            }
        } else {
            System.out.println("Exclusão cancelada!");
        }
    }

    private void listarPetsDeCliente() throws Exception {
        System.out.println("\n=== LISTAR PETS DE CLIENTE ===");
        
        System.out.print("Digite o CPF do cliente: ");
        String cpfDono = scanner.nextLine();
        
        // Verificar se o cliente existe
        Cliente cliente = clienteDAO.buscarClientePorCPF(cpfDono);
        if (cliente == null) {
            System.out.println("Cliente com CPF " + cpfDono + " não encontrado!");
            return;
        }
        
        System.out.println("\nCliente: " + cliente.getNome());
        System.out.println("CPF: " + cliente.getCpf());
        
        java.util.List<Pet> pets = petDAO.buscarPetsPorCpfDono(cpfDono);
        
        if (pets.isEmpty()) {
            System.out.println("Este cliente não possui pets cadastrados.");
        } else {
            System.out.println("\nPets encontrados:");
            for (Pet pet : pets) {
                pet.setDono(cliente); // Definir dados completos do dono
                exibirPet(pet);
            }
        }
    }

    private void exibirPet(Pet pet) {
        System.out.println("\n--- DADOS DO PET ---");
        System.out.println("ID: " + pet.getId());
        System.out.println("Nome: " + pet.getNome());
        System.out.println("Espécie: " + pet.getEspecie());
        System.out.println("Raça: " + pet.getRaca());
        System.out.println("Peso: " + pet.getPeso() + " kg");
        if (pet.getDono() != null) {
            System.out.println("Dono: " + pet.getDono().getNome() + " (CPF: " + pet.getDono().getCpf() + ")");
        }
        System.out.println("--------------------");
    }
}