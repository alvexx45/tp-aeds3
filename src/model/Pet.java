package model;

import java.io.*;

public class Pet {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private float peso;
    private Cliente dono;

    public Pet() {
        this(-1, "", "", "", 0F, null);
    }

    public Pet(int id, String nome, String especie, String raca, float peso, Cliente dono) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.peso = peso;
        this.dono = dono;
    }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaca(String raca) { this.raca = raca; }
    public void setPeso(float peso) { this.peso = peso; }
    public void setDono(Cliente dono) { this.dono = dono; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public float getPeso() { return peso; }
    public Cliente getDono() { return dono; }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.especie);
        dos.writeUTF(this.raca);
        dos.writeFloat(this.peso);
        dos.writeUTF(this.dono.getCpf());
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.especie = dis.readUTF();
        this.raca = dis.readUTF();
        this.peso = dis.readFloat();
        this.dono.setCpf(dis.readUTF());
    }
}