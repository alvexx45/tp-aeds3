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
        setData(data);
        setCliente(cliente);
        setServico(servico);
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
        
        dos.writeLong(getData().toEpochDay());
        dos.writeInt(getCliente().getId());
        dos.writeInt(getServico().getId());
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        
        setData(LocalDate.ofEpochDay(dis.readLong()));

        int idCliente = dis.readInt();
        Cliente cliente = new Cliente();
        cliente.setId(idCliente);
        setCliente(cliente);
    
        int idServ = dis.readInt();
        Servico servico = new Servico();
        servico.setId(idServ);
        setServico(servico);
    }
}