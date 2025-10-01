package model;

import java.io.*;

public class Cliente {
    private String cpf;
    private String nome;
    private String email;
    private String[] telefones;

    public Cliente() {
        this("", "", "", new String[0]);
    }

    public Cliente(String cpf, String nome, String email, String[] telefones) {
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

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.cpf);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeInt(this.telefones.length);
        for (String telefone : this.telefones) {
            dos.writeUTF(telefone);
        }
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.cpf = dis.readUTF();
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        int telefonesLength = dis.readInt();
        this.telefones = new String[telefonesLength];
        for (int i = 0; i < telefonesLength; i++) {
            this.telefones[i] = dis.readUTF();
        }
    }
}
