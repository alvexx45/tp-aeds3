package dao;

import java.io.*;

/**
 * Classe que representa o relacionamento 1:N entre Dono (Cliente) e Pet.
 * Armazena o CPF do dono e o ID do pet.
 * Esta classe será inserida na Hash Extensível para indexar a relação.
 */
public class RelacionamentoPetDono implements RegistroHashExtensivel<RelacionamentoPetDono> {
    private String cpfDono;    // Chave de busca (CPF do cliente/dono)
    private int idPet;         // ID do pet associado ao dono
    
    public static final short TAMANHO_FIXO = 15; // 11 bytes para CPF + 4 bytes para int ID
    
    public RelacionamentoPetDono() {
        this("", -1);
    }
    
    public RelacionamentoPetDono(String cpfDono, int idPet) {
        this.cpfDono = cpfDono;
        this.idPet = idPet;
    }
    
    public String getCpfDono() {
        return cpfDono;
    }
    
    public void setCpfDono(String cpfDono) {
        this.cpfDono = cpfDono;
    }
    
    public int getIdPet() {
        return idPet;
    }
    
    public void setIdPet(int idPet) {
        this.idPet = idPet;
    }
    
    /**
     * Gera um hash code ÚNICO baseado no CPF do dono + ID do Pet.
     * Isso garante que cada relacionamento Pet-Dono tenha uma chave única,
     * permitindo que um dono tenha múltiplos pets (relacionamento 1:N).
     */
    @Override
    public int hashCode() {
        // Combina CPF e ID do Pet para gerar uma chave única
        String cpfLimpo = cpfDono.replaceAll("[^0-9]", "");
        String chaveUnica = cpfLimpo + "#" + idPet;
        return chaveUnica.hashCode();
    }
    
    /**
     * Retorna o tamanho fixo do registro em bytes.
     */
    @Override
    public short size() {
        return TAMANHO_FIXO;
    }
    
    /**
     * Converte o relacionamento para um array de bytes.
     * Formato: [CPF (11 bytes)] [ID Pet (4 bytes)]
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // CPF com tamanho fixo de 11 bytes (preenchido com espaços se necessário)
        String cpfFormatado = String.format("%-11s", cpfDono.replaceAll("[^0-9]", ""));
        dos.writeBytes(cpfFormatado.substring(0, 11));
        
        // ID do Pet (4 bytes)
        dos.writeInt(idPet);
        
        return baos.toByteArray();
    }
    
    /**
     * Reconstrói o relacionamento a partir de um array de bytes.
     */
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        // Ler CPF (11 bytes)
        byte[] cpfBytes = new byte[11];
        dis.readFully(cpfBytes);
        this.cpfDono = new String(cpfBytes).trim();
        
        // Ler ID do Pet (4 bytes)
        this.idPet = dis.readInt();
    }
    
    @Override
    public String toString() {
        return "RelacionamentoPetDono{" +
                "cpfDono='" + cpfDono + '\'' +
                ", idPet=" + idPet +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RelacionamentoPetDono that = (RelacionamentoPetDono) obj;
        return idPet == that.idPet && cpfDono.equals(that.cpfDono);
    }
}
