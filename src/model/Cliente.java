package model;

import java.io.*;
import dao.Registro;
import dao.RSAKeyManager;

public class Cliente implements Registro {
    private int id;
    private String cpf;
    private String nome;
    private String email; // Email será armazenado descriptografado em memória
    private String emailCriptografado; // Email criptografado para persistência
    private String[] telefones;

    public Cliente() {
        this(-1, "", "", "", new String[0]);
    }

    public Cliente(int id, String cpf, String nome, String email, String[] telefones) {
        setId(id);
        setCpf(cpf);
        setNome(nome);
        setEmail(email);
        setTelefones(telefones);
    }

    public void setId(int id) { this.id = id; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setNome(String nome) { this.nome = nome; }
    
    public void setEmail(String email) { 
        this.email = email;
        // Criptografar o email para persistência
        try {
            if (email != null && !email.trim().isEmpty()) {
                this.emailCriptografado = RSAKeyManager.getInstance().criptografar(email);
            } else {
                this.emailCriptografado = email;
            }
        } catch (Exception e) {
            System.err.println("Erro ao criptografar email: " + e.getMessage());
            this.emailCriptografado = email; // Fallback: salvar sem criptografia
        }
    }
    
    public void setTelefones(String[] telefones) { this.telefones = telefones; }

    public int getId() { return id; }
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getEmailCriptografado() { return emailCriptografado; }
    public String[] getTelefones() { return telefones; }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(getId());
        dos.writeUTF(getCpf());
        dos.writeUTF(getNome());
        
        // Salvar o email CRIPTOGRAFADO no arquivo
        String emailParaSalvar = (emailCriptografado != null && !emailCriptografado.trim().isEmpty()) 
                                  ? emailCriptografado 
                                  : "";
        dos.writeUTF(emailParaSalvar);

        dos.writeByte(getTelefones().length);  // Otimização: 1 byte ao invés de 4
        for (String telefone : this.telefones) {
            dos.writeUTF(telefone);
        }

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        
        setId(dis.readInt());
        setCpf(dis.readUTF());
        setNome(dis.readUTF());
        
        // Ler o email CRIPTOGRAFADO do arquivo
        this.emailCriptografado = dis.readUTF();
        
        // Descriptografar o email para uso em memória
        try {
            if (emailCriptografado != null && !emailCriptografado.trim().isEmpty()) {
                this.email = RSAKeyManager.getInstance().descriptografar(emailCriptografado);
            } else {
                this.email = "";
            }
        } catch (Exception e) {
            System.err.println("Erro ao descriptografar email: " + e.getMessage());
            this.email = emailCriptografado; // Fallback: usar o texto criptografado
        }

        int telefonesLength = dis.readByte();  // Otimização: lê 1 byte ao invés de 4
        String[] telefones = new String[telefonesLength];
        for (int i = 0; i < telefonesLength; i++) {
            telefones[i] = dis.readUTF();
        }
        setTelefones(telefones);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s", nome, cpf);
    }
}
