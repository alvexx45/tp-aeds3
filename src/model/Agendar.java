package model;

import java.io.*;
import java.time.LocalDate;
import dao.Registro;

public class Agendar implements Registro {
    private int id;
    private LocalDate data;
    private int idPet;      // Apenas o ID do pet
    private int idServico;  // Apenas o ID do serviço

    public Agendar() {
        this(-1, null, -1, -1);
    }

    public Agendar(int id, LocalDate data, int idPet, int idServico) {
        setId(id);
        setData(data);
        setIdPet(idPet);
        setIdServico(idServico);
    }

    public void setId(int id) { this.id = id; }
    public void setData(LocalDate data) { this.data = data; }
    public void setIdPet(int idPet) { this.idPet = idPet; }
    public void setIdServico(int idServico) { this.idServico = idServico; }
    
    public int getId() { return id; }
    public LocalDate getData() { return data; }
    public int getIdPet() { return idPet; }
    public int getIdServico() { return idServico; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(getId());
        dos.writeUTF(getData() != null ? getData().toString() : "");
        dos.writeInt(getIdPet());
        dos.writeInt(getIdServico());
        
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        setId(dis.readInt());
        String dataStr = dis.readUTF();
        setData(!dataStr.isEmpty() ? LocalDate.parse(dataStr) : null);
        setIdPet(dis.readInt());
        setIdServico(dis.readInt());
    }
}