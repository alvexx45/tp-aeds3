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
        setId(id);
        setNome(nome);
        setEspecie(especie);
        setRaca(raca);
        setPeso(peso);
        setDono(dono);
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

        dos.writeInt(getId());
        dos.writeUTF(getNome());
        dos.writeUTF(getEspecie());
        dos.writeUTF(getRaca());
        dos.writeFloat(getPeso());
        dos.writeInt(getDono().getId());
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        setId(dis.readInt());
        setNome(dis.readUTF());
        setEspecie(dis.readUTF());
        setRaca(dis.readUTF());
        setPeso(dis.readFloat());

        int id = dis.readInt();
        Cliente dono = new Cliente();
        dono.setId(id);
        setDono(dono);
    }
}