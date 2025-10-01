package model;

import java.io.*;
import dao.Registro;

public class Servico implements Registro {
    private int id;
    private String nome;
    private int valor;

    public Servico() {
        this(-1, "", -1);
    }

    public Servico(int id, String nome, int valor) {
        setId(id);
        setNome(nome);
        setValor(valor);
    }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setValor(int valor) { this.valor = valor; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getValor() { return valor; }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(getId());
        dos.writeUTF(getNome());
        dos.writeInt(getValor());
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        
        setId(dis.readInt());
        setNome(dis.readUTF());
        setValor(dis.readInt());
    }

}