package model;

import java.io.*;
import dao.Registro;

public class Cliente implements Registro {
    private int id;
    private String cpf;
    private String nome;
    private String email;
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
    public void setEmail(String email) { this.email = email; }
    public void setTelefones(String[] telefones) { this.telefones = telefones; }

    public int getId() { return id; }
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String[] getTelefones() { return telefones; }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(getId());
        dos.writeUTF(getCpf());
        dos.writeUTF(getNome());
        dos.writeUTF(getEmail());

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
        setEmail(dis.readUTF());

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
