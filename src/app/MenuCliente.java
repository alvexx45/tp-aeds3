package app;

import dao.ClienteDAO;
import model.Cliente;
import java.util.Scanner;

public class MenuCliente {
    private ClienteDAO clienteDAO;
    private Scanner scanner;

    public MenuCliente() throws Exception {
        this.clienteDAO = new ClienteDAO();
        this.scanner = new Scanner(System.in);
    }

    public void exibirMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            System.out.println("\n=== MENU CLIENTE ===");
            System.out.println("1 - Incluir Cliente");
            System.out.println("2 - Buscar Cliente por ID");
            System.out.println("3 - Alterar Cliente por ID");
            System.out.println("4 - Excluir Cliente por ID");
            System.out.println("5 - Buscar Cliente por CPF");
            System.out.println("6 - Alterar Cliente por CPF");
            System.out.println("7 - Excluir Cliente por CPF");
            System.out.println("8 - Buscar Cliente por Email");
            System.out.println("9 - Buscar Clientes por Nome");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                
                switch (opcao) {
                    case 1:
                        incluirCliente();
                        break;
                    case 2:
                        buscarClientePorId();
                        break;
                    case 3:
                        alterarClientePorId();
                        break;
                    case 4:
                        excluirClientePorId();
                        break;
                    case 5:
                        buscarClientePorCPF();
                        break;
                    case 6:
                        alterarClientePorCPF();
                        break;
                    case 7:
                        excluirClientePorCPF();
                        break;
                    case 8:
                        buscarClientePorEmail();
                        break;
                    case 9:
                        buscarClientesPorNome();
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

    private void incluirCliente() throws Exception {
        System.out.println("\n=== INCLUIR CLIENTE ===");
        
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Quantos telefones deseja cadastrar? ");
        int numTelefones = Integer.parseInt(scanner.nextLine());
        
        String[] telefones = new String[numTelefones];
        for (int i = 0; i < numTelefones; i++) {
            System.out.print("Telefone " + (i + 1) + ": ");
            telefones[i] = scanner.nextLine();
        }
        
        Cliente cliente = new Cliente(-1, cpf, nome, email, telefones);
        
        if (clienteDAO.incluirCliente(cliente)) {
            System.out.println("Cliente incluído com sucesso! ID: " + cliente.getId());
        } else {
            System.out.println("Erro ao incluir cliente!");
        }
    }

    private void buscarClientePorId() throws Exception {
        System.out.println("\n=== BUSCAR CLIENTE POR ID ===");
        
        System.out.print("Digite o ID do cliente: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Cliente cliente = clienteDAO.buscarCliente(id);
        
        if (cliente != null) {
            exibirCliente(cliente);
        } else {
            System.out.println("Cliente não encontrado!");
        }
    }

    private void alterarClientePorId() throws Exception {
        System.out.println("\n=== ALTERAR CLIENTE POR ID ===");
        
        System.out.print("Digite o ID do cliente a ser alterado: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Cliente clienteExistente = clienteDAO.buscarCliente(id);
        
        if (clienteExistente == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        
        System.out.println("Cliente atual:");
        exibirCliente(clienteExistente);
        
        System.out.println("\nDigite os novos dados (Enter para manter o valor atual):");
        
        System.out.print("CPF [" + clienteExistente.getCpf() + "]: ");
        String cpf = scanner.nextLine();
        if (cpf.trim().isEmpty()) {
            cpf = clienteExistente.getCpf();
        }
        
        System.out.print("Nome [" + clienteExistente.getNome() + "]: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) {
            nome = clienteExistente.getNome();
        }
        
        System.out.print("Email [" + clienteExistente.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (email.trim().isEmpty()) {
            email = clienteExistente.getEmail();
        }
        
        System.out.print("Deseja alterar os telefones? (s/n): ");
        String alterarTelefones = scanner.nextLine();
        
        String[] telefones = clienteExistente.getTelefones();
        if (alterarTelefones.equalsIgnoreCase("s")) {
            System.out.print("Quantos telefones? ");
            int numTelefones = Integer.parseInt(scanner.nextLine());
            
            telefones = new String[numTelefones];
            for (int i = 0; i < numTelefones; i++) {
                System.out.print("Telefone " + (i + 1) + ": ");
                telefones[i] = scanner.nextLine();
            }
        }
        
        Cliente clienteAlterado = new Cliente(id, cpf, nome, email, telefones);
        
        if (clienteDAO.alterarCliente(clienteAlterado)) {
            System.out.println("Cliente alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar cliente!");
        }
    }

    private void excluirClientePorId() throws Exception {
        System.out.println("\n=== EXCLUIR CLIENTE POR ID ===");
        
        System.out.print("Digite o ID do cliente a ser excluído: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Cliente cliente = clienteDAO.buscarCliente(id);
        
        if (cliente == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        
        System.out.println("Cliente a ser excluído:");
        exibirCliente(cliente);
        
        System.out.print("Confirma a exclusão? (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.equalsIgnoreCase("s")) {
            if (clienteDAO.excluirCliente(id)) {
                System.out.println("Cliente excluído com sucesso!");
            } else {
                System.out.println("Erro ao excluir cliente!");
            }
        } else {
            System.out.println("Exclusão cancelada!");
        }
    }

    private void buscarClientePorCPF() throws Exception {
        System.out.println("\n=== BUSCAR CLIENTE POR CPF ===");
        
        System.out.print("Digite o CPF do cliente: ");
        String cpf = scanner.nextLine();
        
        Cliente cliente = clienteDAO.buscarClientePorCPF(cpf);
        
        if (cliente != null) {
            exibirCliente(cliente);
        } else {
            System.out.println("Cliente com CPF " + cpf + " não encontrado!");
        }
    }

    private void alterarClientePorCPF() throws Exception {
        System.out.println("\n=== ALTERAR CLIENTE POR CPF ===");
        
        System.out.print("Digite o CPF do cliente a ser alterado: ");
        String cpf = scanner.nextLine();
        
        Cliente clienteExistente = clienteDAO.buscarClientePorCPF(cpf);
        
        if (clienteExistente == null) {
            System.out.println("Cliente com CPF " + cpf + " não encontrado!");
            return;
        }
        
        System.out.println("Cliente atual:");
        exibirCliente(clienteExistente);
        
        System.out.println("\nDigite os novos dados (Enter para manter o valor atual):");
        System.out.println("NOTA: O CPF não pode ser alterado.");
        
        System.out.print("Nome [" + clienteExistente.getNome() + "]: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) {
            nome = clienteExistente.getNome();
        }
        
        System.out.print("Email [" + clienteExistente.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (email.trim().isEmpty()) {
            email = clienteExistente.getEmail();
        }
        
        System.out.print("Deseja alterar os telefones? (s/n): ");
        String alterarTelefones = scanner.nextLine();
        
        String[] telefones = clienteExistente.getTelefones();
        if (alterarTelefones.equalsIgnoreCase("s")) {
            System.out.print("Quantos telefones? ");
            int numTelefones = Integer.parseInt(scanner.nextLine());
            
            telefones = new String[numTelefones];
            for (int i = 0; i < numTelefones; i++) {
                System.out.print("Telefone " + (i + 1) + ": ");
                telefones[i] = scanner.nextLine();
            }
        }
        
        Cliente clienteAlterado = new Cliente(-1, cpf, nome, email, telefones);
        
        if (clienteDAO.alterarClientePorCPF(cpf, clienteAlterado)) {
            System.out.println("Cliente alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar cliente!");
        }
    }

    private void excluirClientePorCPF() throws Exception {
        System.out.println("\n=== EXCLUIR CLIENTE POR CPF ===");
        
        System.out.print("Digite o CPF do cliente a ser excluído: ");
        String cpf = scanner.nextLine();
        
        Cliente cliente = clienteDAO.buscarClientePorCPF(cpf);
        
        if (cliente == null) {
            System.out.println("Cliente com CPF " + cpf + " não encontrado!");
            return;
        }
        
        System.out.println("Cliente a ser excluído:");
        exibirCliente(cliente);
        
        System.out.print("Confirma a exclusão? (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.equalsIgnoreCase("s")) {
            if (clienteDAO.excluirClientePorCPF(cpf)) {
                System.out.println("Cliente excluído com sucesso!");
            } else {
                System.out.println("Erro ao excluir cliente!");
            }
        } else {
            System.out.println("Exclusão cancelada!");
        }
    }

    private void buscarClientePorEmail() throws Exception {
        System.out.println("\n=== BUSCAR CLIENTE POR EMAIL ===");
        
        System.out.print("Digite o email do cliente: ");
        String email = scanner.nextLine();
        
        Cliente cliente = clienteDAO.buscarClientePorEmail(email);
        
        if (cliente != null) {
            exibirCliente(cliente);
        } else {
            System.out.println("Cliente com email " + email + " não encontrado!");
        }
    }

    private void buscarClientesPorNome() throws Exception {
        System.out.println("\n=== BUSCAR CLIENTES POR NOME ===");
        
        System.out.print("Digite parte do nome do cliente: ");
        String nome = scanner.nextLine();
        
        java.util.List<Cliente> clientes = clienteDAO.buscarClientesPorNome(nome);
        
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com nome contendo '" + nome + "'!");
        } else {
            System.out.println("\n" + clientes.size() + " cliente(s) encontrado(s):");
            for (Cliente cliente : clientes) {
                exibirCliente(cliente);
            }
        }
    }

    private void exibirCliente(Cliente cliente) {
        System.out.println("\n--- DADOS DO CLIENTE ---");
        System.out.println("ID: " + cliente.getId());
        System.out.println("CPF: " + cliente.getCpf());
        System.out.println("Nome: " + cliente.getNome());
        System.out.println("Email: " + cliente.getEmail());
        System.out.print("Telefones: ");
        
        String[] telefones = cliente.getTelefones();
        if (telefones.length == 0) {
            System.out.println("Nenhum telefone cadastrado");
        } else {
            for (int i = 0; i < telefones.length; i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(telefones[i]);
            }
            System.out.println();
        }
        System.out.println("------------------------");
    }
}