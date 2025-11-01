package dao;

import java.io.*;

/**
 * Registro para o relacionamento N:N entre Pet e Serviço através do Agendamento
 * Armazenado na Árvore B+ com chave composta (idPet, idServico)
 */
public class RegistroAgendamento implements RegistroArvoreBMais<RegistroAgendamento> {
    private int idPet;      // Primeira chave
    private int idServico;  // Segunda chave
    private int idAgendamento; // ID do agendamento no arquivo principal

    public RegistroAgendamento() {
        this(-1, -1, -1);
    }

    public RegistroAgendamento(int idPet, int idServico, int idAgendamento) {
        this.idPet = idPet;
        this.idServico = idServico;
        this.idAgendamento = idAgendamento;
    }

    public int getIdPet() { return idPet; }
    public int getIdServico() { return idServico; }
    public int getIdAgendamento() { return idAgendamento; }

    public void setIdPet(int idPet) { this.idPet = idPet; }
    public void setIdServico(int idServico) { this.idServico = idServico; }
    public void setIdAgendamento(int idAgendamento) { this.idAgendamento = idAgendamento; }

    @Override
    public short size() {
        // 3 inteiros (4 bytes cada) = 12 bytes
        return 12;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(idPet);
        dos.writeInt(idServico);
        dos.writeInt(idAgendamento);
        
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        idPet = dis.readInt();
        idServico = dis.readInt();
        idAgendamento = dis.readInt();
    }

    @Override
    public int compareTo(RegistroAgendamento obj) {
        // Comparação por chave composta (idPet, idServico)
        // Primeiro compara por idPet
        if (this.idPet != obj.idPet) {
            return Integer.compare(this.idPet, obj.idPet);
        }
        // Se idPet for igual, compara por idServico
        return Integer.compare(this.idServico, obj.idServico);
    }

    @Override
    public RegistroAgendamento clone() {
        return new RegistroAgendamento(this.idPet, this.idServico, this.idAgendamento);
    }

    @Override
    public String toString() {
        return String.format("Pet: %d, Servico: %d, Agendamento: %d", idPet, idServico, idAgendamento);
    }
}
