package model;

import java.time.LocalDate;
import java.io.*;

public class Agendar {
    private LocalDate data;
    private Cliente cliente;
    private Servico servico;

    public Agendar() {
        this(null, null, null);
    }

    public Agendar(LocalDate data, Cliente cliente, Servico servico) {
        this.data = data;
        this.cliente = cliente;
        this.servico = servico;
    }

    public void setData(LocalDate data) { this.data = data; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setServico(Servico servico) { this.servico = servico; }

    public LocalDate getData() { return data; }
    public Cliente getCliente() { return cliente; }
    public Servico getServico() { return servico; }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(getId());
        dos.writeUTF(getNome());
        dos.writeUTF(getEspecie());
        dos.writeUTF(getRaca());
        dos.writeFloat(getPeso());
        dos.writeUTF(getDono().getCpf());
        
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
        String cpf = dis.readUTF();
        Cliente dono = new Cliente();
        dono.setCpf(cpf);
        setDono(dono);
    }

}