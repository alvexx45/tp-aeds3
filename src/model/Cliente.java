package model;

class Cliente {
    String cpf;
    String nome;
    String email;
    String[] telefones;

    Cliente() {
        this("", "", "", new String[0]);
    }

    Cliente(String cpf, String nome, String email, String[] telefones) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefones = telefones;
    }

    
}
