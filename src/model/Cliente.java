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

    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefones(String[] telefones) { this.telefones = telefones; }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String[] getTelefones() { return telefones; }
}
