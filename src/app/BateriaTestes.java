package app;

import dao.ClienteDAO;
import dao.PetDAO;
import dao.ServicoDAO;
import model.Cliente;
import model.Pet;
import model.Servico;
import java.util.List;

/**
 * Classe para executar bateria de testes com dados de exemplo
 */
public class BateriaTestes {
    
    private ClienteDAO clienteDAO;
    private PetDAO petDAO;
    private ServicoDAO servicoDAO;
    
    public BateriaTestes() throws Exception {
        this.clienteDAO = new ClienteDAO();
        this.petDAO = new PetDAO();
        this.servicoDAO = new ServicoDAO();
    }
    
    public void executar() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("=== BATERIA DE TESTES - INSERINDO DADOS DE EXEMPLO ===");
        System.out.println("=".repeat(60));
        
        try {
            // Inserir clientes
            System.out.println("\n[1/3] Inserindo Clientes...");
            inserirClientes();
            
            // Inserir serviços
            System.out.println("\n[2/3] Inserindo Serviços...");
            inserirServicos();
            
            // Inserir pets
            System.out.println("\n[3/3] Inserindo Pets...");
            inserirPets();
            
            // Exibir resumo
            System.out.println("\n" + "=".repeat(60));
            exibirResumo();
            System.out.println("=".repeat(60));
            
            System.out.println("\n✓ Bateria de testes concluída com sucesso!");
            
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante a bateria de testes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void inserirClientes() throws Exception {
        String[][] dadosClientes = {
            {"12345678901", "João Silva", "joao.silva@email.com", "31999991111", "31988881111"},
            {"98765432100", "Maria Santos", "maria.santos@email.com", "31999992222"},
            {"11122233344", "Pedro Oliveira", "pedro.oliveira@email.com", "31999993333", "31988883333"},
            {"55566677788", "Ana Costa", "ana.costa@email.com", "31999994444"},
            {"99988877766", "Carlos Souza", "carlos.souza@email.com", "31999995555", "31988885555"},
            {"44455566677", "Juliana Lima", "juliana.lima@email.com", "31999996666"},
            {"22233344455", "Roberto Alves", "roberto.alves@email.com", "31999997777"},
            {"88899900011", "Fernanda Rocha", "fernanda.rocha@email.com", "31999998888", "31988888888"},
            {"66677788899", "Lucas Martins", "lucas.martins@email.com", "31999999999"},
            {"33344455566", "Camila Ferreira", "camila.ferreira@email.com", "31999990000"}
        };
        
        int count = 0;
        for (String[] dados : dadosClientes) {
            String cpf = dados[0];
            String nome = dados[1];
            String email = dados[2];
            String[] telefones = new String[dados.length - 3];
            System.arraycopy(dados, 3, telefones, 0, telefones.length);
            
            Cliente cliente = new Cliente(-1, cpf, nome, email, telefones);
            if (clienteDAO.incluirCliente(cliente)) {
                count++;
                System.out.println("   ✓ Cliente " + count + ": " + nome + " (CPF: " + cpf + ")");
            }
        }
        
        System.out.println("   Total: " + count + " clientes inseridos");
    }
    
    private void inserirServicos() throws Exception {
        String[][] dadosServicos = {
            {"Banho", "50"},
            {"Tosa", "60"},
            {"Banho e Tosa", "100"},
            {"Consulta Veterinária", "120"},
            {"Vacinação", "80"},
            {"Exame de Sangue", "150"},
            {"Raio-X", "200"},
            {"Castração", "300"},
            {"Limpeza de Dentes", "180"},
            {"Aplicação de Antipulgas", "45"},
            {"Corte de Unhas", "30"},
            {"Hidratação", "70"},
            {"Hospedagem (diária)", "90"},
            {"Adestramento (hora)", "100"},
            {"Microchipagem", "150"}
        };
        
        int count = 0;
        for (String[] dados : dadosServicos) {
            String nome = dados[0];
            int valor = Integer.parseInt(dados[1]);
            
            Servico servico = new Servico(-1, nome, valor);
            if (servicoDAO.incluirServico(servico)) {
                count++;
                System.out.println("   ✓ Serviço " + count + ": " + nome + " (R$ " + valor + ",00)");
            }
        }
        
        System.out.println("   Total: " + count + " serviços inseridos");
    }
    
    private void inserirPets() throws Exception {
        // Buscar clientes para associar aos pets
        Cliente joao = clienteDAO.buscarClientePorCPF("12345678901");
        Cliente maria = clienteDAO.buscarClientePorCPF("98765432100");
        Cliente pedro = clienteDAO.buscarClientePorCPF("11122233344");
        Cliente ana = clienteDAO.buscarClientePorCPF("55566677788");
        Cliente carlos = clienteDAO.buscarClientePorCPF("99988877766");
        Cliente juliana = clienteDAO.buscarClientePorCPF("44455566677");
        Cliente roberto = clienteDAO.buscarClientePorCPF("22233344455");
        Cliente fernanda = clienteDAO.buscarClientePorCPF("88899900011");
        Cliente lucas = clienteDAO.buscarClientePorCPF("66677788899");
        Cliente camila = clienteDAO.buscarClientePorCPF("33344455566");
        
        Object[][] dadosPets = {
            {"Rex", "Cachorro", "Labrador", 28.5f, joao},
            {"Mimi", "Gato", "Persa", 4.2f, joao},
            {"Thor", "Cachorro", "Golden Retriever", 32.0f, maria},
            {"Bolinha", "Cachorro", "Poodle", 6.5f, pedro},
            {"Nina", "Gato", "Siamês", 3.8f, pedro},
            {"Mel", "Cachorro", "Beagle", 12.3f, ana},
            {"Duque", "Cachorro", "Pastor Alemão", 35.0f, carlos},
            {"Pipoca", "Gato", "Vira-lata", 4.0f, carlos},
            {"Pretinha", "Gato", "Preto", 3.5f, juliana},
            {"Bob", "Cachorro", "Bulldog", 22.0f, roberto},
            {"Luna", "Cachorro", "Husky", 24.5f, fernanda},
            {"Felix", "Gato", "Maine Coon", 6.8f, fernanda},
            {"Max", "Cachorro", "Shih Tzu", 7.2f, lucas},
            {"Bella", "Cachorro", "Yorkshire", 3.2f, camila},
            {"Mingau", "Gato", "Vira-lata", 4.5f, camila}
        };
        
        int count = 0;
        for (Object[] dados : dadosPets) {
            String nome = (String) dados[0];
            String especie = (String) dados[1];
            String raca = (String) dados[2];
            float peso = (Float) dados[3];
            Cliente dono = (Cliente) dados[4];
            
            if (dono != null) {
                Pet pet = new Pet(-1, nome, especie, raca, peso, dono);
                if (petDAO.incluirPet(pet)) {
                    count++;
                    System.out.println("   ✓ Pet " + count + ": " + nome + " (" + especie + " - " + raca + ") - Dono: " + dono.getNome());
                }
            }
        }
        
        System.out.println("   Total: " + count + " pets inseridos");
    }
    
    private void exibirResumo() throws Exception {
        System.out.println("\n=== RESUMO DOS DADOS INSERIDOS ===\n");
        
        // Contar clientes
        System.out.println("📋 CLIENTES:");
        for (int i = 1; i <= 15; i++) {
            Cliente c = clienteDAO.buscarCliente(i);
            if (c != null) {
                System.out.println("   ID " + c.getId() + ": " + c.getNome() + " (CPF: " + c.getCpf() + ")");
            }
        }
        
        // Contar serviços
        System.out.println("\n💼 SERVIÇOS:");
        for (int i = 1; i <= 20; i++) {
            Servico s = servicoDAO.buscarServico(i);
            if (s != null) {
                System.out.println("   ID " + s.getId() + ": " + s.getNome() + " (R$ " + s.getValor() + ",00)");
            }
        }
        
        // Contar pets e demonstrar relacionamento 1:N
        System.out.println("\n🐾 PETS (demonstrando relacionamento 1:N via Hash Extensível):");
        
        // Buscar pets por CPF de alguns donos
        String[] cpfsParaTestar = {"12345678901", "98765432100", "11122233344", "99988877766", "88899900011", "33344455566"};
        
        for (String cpf : cpfsParaTestar) {
            Cliente dono = clienteDAO.buscarClientePorCPF(cpf);
            if (dono != null) {
                List<Pet> pets = petDAO.buscarPetsPorCpfDono(cpf);
                if (!pets.isEmpty()) {
                    System.out.println("   👤 " + dono.getNome() + " (CPF: " + cpf + ") tem " + pets.size() + " pet(s):");
                    for (Pet pet : pets) {
                        System.out.println("      🐕 " + pet.getNome() + " - " + pet.getEspecie() + " (" + pet.getRaca() + ")");
                    }
                }
            }
        }
    }
}
