package model;

import java.io.*;

public class Servico {
    private int id;
    private String nome;
    private int valor;

    public Servico() {
        this(-1, "", -1);
    }

    public Servico(int id, String nome, int valor) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
    }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setValor(int valor) { this.valor = valor; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getValor() { return valor; }
}