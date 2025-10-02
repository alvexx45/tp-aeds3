package app;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;
        
        System.out.println("=== SISTEMA DE GERENCIAMENTO ===");
        System.out.println("Bem-vindo ao sistema!");
        
        while (opcao != 0) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Gerenciar Clientes");
            System.out.println("2 - Gerenciar Pets");
            System.out.println("3 - Gerenciar Serviços");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                
                switch (opcao) {
                    case 1:
                        MenuCliente menuCliente = new MenuCliente();
                        menuCliente.exibirMenu();
                        break;
                    case 2:
                        MenuPet menuPet = new MenuPet();
                        menuPet.exibirMenu();
                        break;
                    case 3:
                        MenuServico menuServico = new MenuServico();
                        menuServico.exibirMenu();
                        break;
                    case 0:
                        System.out.println("Encerrando o sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
            } catch (Exception e) {
                System.out.println("Erro no sistema: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
        System.out.println("Sistema encerrado com sucesso!");
    }
}
