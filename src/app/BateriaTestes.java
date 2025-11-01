package app;

import dao.AgendarDAO;
import dao.ClienteDAO;
import dao.PetDAO;
import dao.ServicoDAO;
import model.Agendar;
import model.Cliente;
import model.Pet;
import model.Servico;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe para executar bateria de testes com dados de exemplo
 */
public class BateriaTestes {
    
    private ClienteDAO clienteDAO;
    private PetDAO petDAO;
    private ServicoDAO servicoDAO;
    private AgendarDAO agendarDAO;
    
    public BateriaTestes() throws Exception {
        this.clienteDAO = new ClienteDAO();
        this.petDAO = new PetDAO();
        this.servicoDAO = new ServicoDAO();
        this.agendarDAO = new AgendarDAO();
    }
    
    public void executar() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("=== BATERIA DE TESTES - INSERINDO DADOS DE EXEMPLO ===");
        System.out.println("=".repeat(60));
        
        try {
            // Inserir clientes
            System.out.println("\n[1/4] Inserindo Clientes...");
            inserirClientes();
            
            // Inserir serviços
            System.out.println("\n[2/4] Inserindo Serviços...");
            inserirServicos();
            
            // Inserir pets
            System.out.println("\n[3/4] Inserindo Pets...");
            inserirPets();
            
            // Inserir agendamentos
            System.out.println("\n[4/4] Inserindo Agendamentos...");
            inserirAgendamentos();
            
            // Testar validações de duplicatas
            System.out.println("\n[5/5] Testando Validações de Duplicatas...");
            testarValidacoesDuplicatas();
            
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
    
    private void inserirAgendamentos() throws Exception {
        int count = 0;
        
        // Rex (ID 1) - 3 agendamentos
        try {
            Agendar ag1 = new Agendar(0, LocalDate.now(), 1, 1); // Rex - Banho
            if (agendarDAO.incluirAgendamento(ag1)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Rex → Banho");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar ag2 = new Agendar(0, LocalDate.now().plusDays(7), 1, 2); // Rex - Tosa
            if (agendarDAO.incluirAgendamento(ag2)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Rex → Tosa");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar ag3 = new Agendar(0, LocalDate.now().plusDays(14), 1, 14); // Rex - Adestramento
            if (agendarDAO.incluirAgendamento(ag3)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Rex → Adestramento");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Mimi (ID 2) - 2 agendamentos
        try {
            Agendar ag4 = new Agendar(0, LocalDate.now().plusDays(1), 2, 1); // Mimi - Banho
            if (agendarDAO.incluirAgendamento(ag4)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Mimi → Banho");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar ag5 = new Agendar(0, LocalDate.now().plusDays(3), 2, 11); // Mimi - Corte de Unhas
            if (agendarDAO.incluirAgendamento(ag5)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Mimi → Corte de Unhas");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Thor (ID 3) - 2 agendamentos
        try {
            Agendar ag6 = new Agendar(0, LocalDate.now().plusDays(2), 3, 3); // Thor - Banho e Tosa
            if (agendarDAO.incluirAgendamento(ag6)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Thor → Banho e Tosa");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar ag7 = new Agendar(0, LocalDate.now().plusDays(10), 3, 5); // Thor - Vacinação
            if (agendarDAO.incluirAgendamento(ag7)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Thor → Vacinação");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Bolinha (ID 4)
        try {
            Agendar ag8 = new Agendar(0, LocalDate.now().plusDays(4), 4, 2); // Bolinha - Tosa
            if (agendarDAO.incluirAgendamento(ag8)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Bolinha → Tosa");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Nina (ID 5)
        try {
            Agendar ag9 = new Agendar(0, LocalDate.now().plusDays(5), 5, 1); // Nina - Banho
            if (agendarDAO.incluirAgendamento(ag9)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Nina → Banho");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Mel (ID 6)
        try {
            Agendar ag10 = new Agendar(0, LocalDate.now().plusDays(6), 6, 4); // Mel - Consulta Veterinária
            if (agendarDAO.incluirAgendamento(ag10)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Mel → Consulta Veterinária");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Duque (ID 7) - 2 agendamentos
        try {
            Agendar ag11 = new Agendar(0, LocalDate.now().plusDays(8), 7, 5); // Duque - Vacinação
            if (agendarDAO.incluirAgendamento(ag11)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Duque → Vacinação");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar ag12 = new Agendar(0, LocalDate.now().plusDays(30), 7, 8); // Duque - Castração
            if (agendarDAO.incluirAgendamento(ag12)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Duque → Castração");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Pipoca (ID 8)
        try {
            Agendar ag13 = new Agendar(0, LocalDate.now().plusDays(9), 8, 10); // Pipoca - Antipulgas
            if (agendarDAO.incluirAgendamento(ag13)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Pipoca → Aplicação de Antipulgas");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Luna (ID 11)
        try {
            Agendar ag14 = new Agendar(0, LocalDate.now().plusDays(15), 11, 13); // Luna - Hospedagem
            if (agendarDAO.incluirAgendamento(ag14)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Luna → Hospedagem (diária)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Bella (ID 14)
        try {
            Agendar ag15 = new Agendar(0, LocalDate.now().plusDays(12), 14, 2); // Bella - Tosa
            if (agendarDAO.incluirAgendamento(ag15)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Bella → Tosa");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        System.out.println("\n   📅 AGENDAMENTOS PASSADOS (histórico):");
        
        // Rex (ID 1) - agendamentos históricos
        try {
            Agendar agPast1 = new Agendar(0, LocalDate.now().minusDays(5), 1, 1); // Rex - Banho (5 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast1)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Rex → Banho (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar agPast2 = new Agendar(0, LocalDate.now().minusDays(30), 1, 4); // Rex - Consulta (30 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast2)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Rex → Consulta (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Mimi (ID 2) - histórico
        try {
            Agendar agPast3 = new Agendar(0, LocalDate.now().minusDays(15), 2, 2); // Mimi - Tosa (15 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast3)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Mimi → Tosa (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar agPast4 = new Agendar(0, LocalDate.now().minusDays(60), 2, 5); // Mimi - Vacinação (60 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast4)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Mimi → Vacinação (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Thor (ID 3) - histórico
        try {
            Agendar agPast5 = new Agendar(0, LocalDate.now().minusDays(10), 3, 1); // Thor - Banho (10 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast5)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Thor → Banho (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Bolinha (ID 4) - histórico
        try {
            Agendar agPast6 = new Agendar(0, LocalDate.now().minusDays(20), 4, 3); // Bolinha - Banho e Tosa (20 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast6)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Bolinha → Banho e Tosa (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        try {
            Agendar agPast7 = new Agendar(0, LocalDate.now().minusDays(90), 4, 10); // Bolinha - Antipulgas (90 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast7)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Bolinha → Antipulgas (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Nina (ID 5) - histórico
        try {
            Agendar agPast8 = new Agendar(0, LocalDate.now().minusDays(7), 5, 11); // Nina - Corte de Unhas (7 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast8)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Nina → Corte de Unhas (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Mel (ID 6) - histórico
        try {
            Agendar agPast9 = new Agendar(0, LocalDate.now().minusDays(45), 6, 1); // Mel - Banho (45 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast9)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Mel → Banho (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Duque (ID 7) - histórico
        try {
            Agendar agPast10 = new Agendar(0, LocalDate.now().minusDays(25), 7, 2); // Duque - Tosa (25 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast10)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Duque → Tosa (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Pipoca (ID 8) - histórico
        try {
            Agendar agPast11 = new Agendar(0, LocalDate.now().minusDays(35), 8, 4); // Pipoca - Consulta (35 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast11)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Pipoca → Consulta (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        // Luna (ID 11) - histórico
        try {
            Agendar agPast12 = new Agendar(0, LocalDate.now().minusDays(50), 11, 3); // Luna - Banho e Tosa (50 dias atrás)
            if (agendarDAO.incluirAgendamento(agPast12)) {
                count++;
                System.out.println("   ✓ Agendamento " + count + ": Luna → Banho e Tosa (passado)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ " + e.getMessage());
        }
        
        System.out.println("\n   Total: " + count + " agendamentos inseridos (futuros e passados)");
        System.out.println("   💡 Relacionamento N:N indexado na Árvore B+");
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
        
        // Contar e demonstrar agendamentos
        System.out.println("\n📅 AGENDAMENTOS:");
        List<Agendar> todosAgendamentos = agendarDAO.listarTodosAgendamentos();
        System.out.println("   Total: " + todosAgendamentos.size() + " agendamentos");
        
        if (!todosAgendamentos.isEmpty()) {
            System.out.println("\n   Exemplos:");
            int mostrados = 0;
            for (Agendar ag : todosAgendamentos) {
                if (mostrados >= 8) break;
                
                Pet pet = petDAO.buscarPet(ag.getIdPet());
                Servico servico = servicoDAO.buscarServico(ag.getIdServico());
                
                if (pet != null && servico != null) {
                    System.out.println(String.format("   📌 %s → %s (Data: %s)", 
                        pet.getNome(), servico.getNome(), ag.getData()));
                    mostrados++;
                }
            }
            
            // Demonstrar busca por pet
            System.out.println("\n   🔍 Agendamentos do Rex:");
            List<Agendar> agendamentosRex = agendarDAO.buscarAgendamentosPorPet(1);
            for (Agendar ag : agendamentosRex) {
                Servico servico = servicoDAO.buscarServico(ag.getIdServico());
                if (servico != null) {
                    System.out.println("      → " + servico.getNome() + " (" + ag.getData() + ")");
                }
            }
        }
    }
    
    /**
     * Testa as validações de duplicatas implementadas
     */
    private void testarValidacoesDuplicatas() {
        System.out.println("   🛡️ Testando validações de duplicatas...");
        
        // Teste 1: Cliente com CPF duplicado
        System.out.println("\n   [Teste 1] Cliente com CPF duplicado:");
        try {
            Cliente clienteDuplicado = new Cliente(-1, "12345678901", "Nome Duplicado", "teste@email.com", new String[]{"31999999999"});
            clienteDAO.incluirCliente(clienteDuplicado);
            System.out.println("   ❌ ERRO: Deveria ter rejeitado CPF duplicado!");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✅ CORRETO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   ⚠️ Erro inesperado: " + e.getMessage());
        }
        
        // Teste 2: Cliente com email duplicado
        System.out.println("\n   [Teste 2] Cliente com email duplicado:");
        try {
            Cliente clienteEmailDuplicado = new Cliente(-1, "00000000000", "Nome Teste", "joao.silva@email.com", new String[]{"31999999999"});
            clienteDAO.incluirCliente(clienteEmailDuplicado);
            System.out.println("   ❌ ERRO: Deveria ter rejeitado email duplicado!");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✅ CORRETO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   ⚠️ Erro inesperado: " + e.getMessage());
        }
        
        // Teste 3: Serviço com nome duplicado
        System.out.println("\n   [Teste 3] Serviço com nome duplicado:");
        try {
            Servico servicoDuplicado = new Servico(-1, "Banho", 3000); // Nome já existe
            servicoDAO.incluirServico(servicoDuplicado);
            System.out.println("   ❌ ERRO: Deveria ter rejeitado nome de serviço duplicado!");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✅ CORRETO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   ⚠️ Erro inesperado: " + e.getMessage());
        }
        
        // Teste 4: Pet com nome duplicado para o mesmo dono
        System.out.println("\n   [Teste 4] Pet com nome duplicado para o mesmo dono:");
        try {
            // Buscar um cliente existente
            Cliente cliente = clienteDAO.buscarClientePorCPF("12345678901");
            if (cliente != null) {
                Pet petDuplicado = new Pet(-1, "Rex", "Cão", "Labrador", 10.5f, cliente); // Nome já pode existir
                petDAO.incluirPet(petDuplicado);
                System.out.println("   ❌ ERRO: Deveria ter rejeitado nome de pet duplicado para o mesmo dono!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("   ✅ CORRETO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   ⚠️ Erro inesperado: " + e.getMessage());
        }
        
        // Teste 5: Alteração mantendo dados próprios (deve funcionar)
        System.out.println("\n   [Teste 5] Alteração mantendo dados próprios:");
        try {
            Cliente cliente = clienteDAO.buscarClientePorCPF("12345678901");
            if (cliente != null) {
                cliente.setNome("João Silva");
                boolean alterado = clienteDAO.alterarCliente(cliente);
                if (alterado) {
                    System.out.println("   ✅ CORRETO: Permitiu alterar mantendo CPF próprio");
                } else {
                    System.out.println("   ❌ ERRO: Não deveria ter rejeitado alteração válida!");
                }
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ Erro inesperado na alteração: " + e.getMessage());
        }
        
        // Teste 6: Agendamento duplicado
        System.out.println("\n   [Teste 6] Agendamento duplicado:");
        try {
            Agendar agDuplicado = new Agendar(0, LocalDate.now(), 1, 1); // Rex + Banho já existe
            agendarDAO.incluirAgendamento(agDuplicado);
            System.out.println("   ❌ ERRO: Deveria ter rejeitado agendamento duplicado!");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✅ CORRETO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   ⚠️ Erro inesperado: " + e.getMessage());
        }
        
        System.out.println("\n   🛡️ Testes de validação de duplicatas concluídos!");
    }
}
