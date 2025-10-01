package model;

public class Pet {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private float peso;

    public Pet() {
        this(-1, "", "", "", 0F);
    }

    public Pet(int id, String nome, String especie, String raca, float peso) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.peso = peso;
    }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaca(String raca) { this.raca = raca; }
    public void setPeso(float peso) { this.peso = peso; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public float getPeso() { return peso; }
}