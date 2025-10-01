package model;

import java.time.LocalDate;

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
}