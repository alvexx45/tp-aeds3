package app;

import dao.ServicoDAO;
import model.Servico;
import java.util.Scanner;

public class MenuServico {
    private ServicoDAO servicoDAO;
    private Scanner scanner;

    public MenuServico() throws Exception {
        this.servicoDAO = new ServicoDAO();
        this.scanner = new Scanner(System.in);
    }

    public void exibirMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            System.out.println("\n=== MENU SERVIÇO ===");
            System.out.println("1 - Incluir Serviço");
            System.out.println("2 - Buscar Serviço");
            System.out.println("3 - Alterar Serviço");
            System.out.println("4 - Excluir Serviço");
            System.out.println("5 - Buscar Serviço por Nome");
            System.out.println("6 - Buscar Serviços por Faixa de Preço");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                
                switch (opcao) {
                    case 1:
                        incluirServico();
                        break;
                    case 2:
                        buscarServico();
                        break;
                    case 3:
                        alterarServico();
                        break;
                    case 4:
                        excluirServico();
                        break;
                    case 5:
                        buscarServicoPorNome();
                        break;
                    case 6:
                        buscarServicosPorFaixaPreco();
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

    private void incluirServico() throws Exception {
        System.out.println("\n=== INCLUIR SERVIÇO ===");
        
        System.out.print("Nome do Serviço: ");
        String nome = scanner.nextLine();
        
        // Verificar se já existe um serviço com este nome
        Servico servicoExistente = servicoDAO.buscarServicoPorNome(nome);
        if (servicoExistente != null) {
            System.out.println("Já existe um serviço com o nome '" + nome + "' (ID: " + servicoExistente.getId() + ")");
            System.out.print("Deseja continuar mesmo assim? (s/n): ");
            String resposta = scanner.nextLine();
            if (!resposta.equalsIgnoreCase("s")) {
                System.out.println("Inclusão cancelada.");
                return;
            }
        }
        
        System.out.print("Valor do Serviço (em centavos): ");
        int valor = Integer.parseInt(scanner.nextLine());
        
        if (valor < 0) {
            System.out.println("Valor não pode ser negativo!");
            return;
        }
        
        Servico servico = new Servico(-1, nome, valor);
        
        if (servicoDAO.incluirServico(servico)) {
            System.out.println("Serviço incluído com sucesso! ID: " + servico.getId());
            System.out.println("Valor: R$ " + String.format("%.2f", valor / 100.0));
        } else {
            System.out.println("Erro ao incluir serviço!");
        }
    }

    private void buscarServico() throws Exception {
        System.out.println("\n=== BUSCAR SERVIÇO ===");
        
        System.out.print("Digite o ID do serviço: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Servico servico = servicoDAO.buscarServico(id);
        
        if (servico != null) {
            exibirServico(servico);
        } else {
            System.out.println("Serviço não encontrado!");
        }
    }

    private void alterarServico() throws Exception {
        System.out.println("\n=== ALTERAR SERVIÇO ===");
        
        System.out.print("Digite o ID do serviço a ser alterado: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Servico servicoExistente = servicoDAO.buscarServico(id);
        
        if (servicoExistente == null) {
            System.out.println("Serviço não encontrado!");
            return;
        }
        
        System.out.println("Serviço atual:");
        exibirServico(servicoExistente);
        
        System.out.println("\nDigite os novos dados (Enter para manter o valor atual):");
        
        System.out.print("Nome [" + servicoExistente.getNome() + "]: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) {
            nome = servicoExistente.getNome();
        }
        
        System.out.print("Valor em centavos [" + servicoExistente.getValor() + " = R$ " + 
                         String.format("%.2f", servicoExistente.getValor() / 100.0) + "]: ");
        String valorStr = scanner.nextLine();
        int valor = servicoExistente.getValor();
        if (!valorStr.trim().isEmpty()) {
            valor = Integer.parseInt(valorStr);
            if (valor < 0) {
                System.out.println("Valor não pode ser negativo!");
                return;
            }
        }
        
        // Verificar se o novo nome já existe (se foi alterado)
        if (!nome.equals(servicoExistente.getNome())) {
            Servico servicoComMesmoNome = servicoDAO.buscarServicoPorNome(nome);
            if (servicoComMesmoNome != null && servicoComMesmoNome.getId() != id) {
                System.out.println("Já existe outro serviço com o nome '" + nome + "' (ID: " + servicoComMesmoNome.getId() + ")");
                System.out.print("Deseja continuar mesmo assim? (s/n): ");
                String resposta = scanner.nextLine();
                if (!resposta.equalsIgnoreCase("s")) {
                    System.out.println("Alteração cancelada.");
                    return;
                }
            }
        }
        
        Servico servicoAlterado = new Servico(id, nome, valor);
        
        if (servicoDAO.alterarServico(servicoAlterado)) {
            System.out.println("Serviço alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar serviço!");
        }
    }

    private void excluirServico() throws Exception {
        System.out.println("\n=== EXCLUIR SERVIÇO ===");
        
        System.out.print("Digite o ID do serviço a ser excluído: ");
        int id = Integer.parseInt(scanner.nextLine());
        
        Servico servico = servicoDAO.buscarServico(id);
        
        if (servico == null) {
            System.out.println("Serviço não encontrado!");
            return;
        }
        
        System.out.println("Serviço a ser excluído:");
        exibirServico(servico);
        
        System.out.print("Confirma a exclusão? (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.equalsIgnoreCase("s")) {
            if (servicoDAO.excluirServico(id)) {
                System.out.println("Serviço excluído com sucesso!");
            } else {
                System.out.println("Erro ao excluir serviço!");
            }
        } else {
            System.out.println("Exclusão cancelada!");
        }
    }

    private void buscarServicoPorNome() throws Exception {
        System.out.println("\n=== BUSCAR SERVIÇO POR NOME ===");
        
        System.out.print("Digite o nome do serviço: ");
        String nome = scanner.nextLine();
        
        Servico servico = servicoDAO.buscarServicoPorNome(nome);
        
        if (servico != null) {
            exibirServico(servico);
        } else {
            System.out.println("Serviço com nome '" + nome + "' não encontrado!");
        }
    }

    private void buscarServicosPorFaixaPreco() throws Exception {
        System.out.println("\n=== BUSCAR SERVIÇOS POR FAIXA DE PREÇO ===");
        
        System.out.print("Valor mínimo (em centavos): ");
        int valorMin = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Valor máximo (em centavos): ");
        int valorMax = Integer.parseInt(scanner.nextLine());
        
        if (valorMin < 0 || valorMax < 0) {
            System.out.println("Valores não podem ser negativos!");
            return;
        }
        
        if (valorMin > valorMax) {
            System.out.println("Valor mínimo não pode ser maior que o valor máximo!");
            return;
        }
        
        System.out.println("\nBuscando serviços entre R$ " + String.format("%.2f", valorMin / 100.0) + 
                          " e R$ " + String.format("%.2f", valorMax / 100.0) + ":");
        
        java.util.List<Servico> servicos = servicoDAO.buscarServicosPorFaixaPreco(valorMin, valorMax);
        
        if (servicos.isEmpty()) {
            System.out.println("Nenhum serviço encontrado nesta faixa de preço.");
        } else {
            System.out.println("\nServiços encontrados:");
            for (Servico servico : servicos) {
                exibirServico(servico);
            }
        }
    }

    private void exibirServico(Servico servico) {
        System.out.println("\n--- DADOS DO SERVIÇO ---");
        System.out.println("ID: " + servico.getId());
        System.out.println("Nome: " + servico.getNome());
        System.out.println("Valor: " + servico.getValor() + " centavos (R$ " + 
                          String.format("%.2f", servico.getValor() / 100.0) + ")");
        System.out.println("------------------------");
    }
}