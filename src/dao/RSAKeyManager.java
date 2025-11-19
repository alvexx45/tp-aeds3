package dao;

import algorithms.RSA;
import algorithms.RSA.ChavePublica;
import algorithms.RSA.ChavePrivada;
import java.io.*;
import java.math.BigInteger;

/**
 * Gerenciador de chaves RSA para criptografia de dados
 * Responsável por gerar, salvar, carregar e usar chaves RSA
 */
public class RSAKeyManager {
    
    private static final String ARQUIVO_CHAVE_PUBLICA = "src/dados/rsa_public.key";
    private static final String ARQUIVO_CHAVE_PRIVADA = "src/dados/rsa_private.key";
    private static final int TAMANHO_CHAVE = 512; // bits - reduzido para otimização de espaço
    private static final int TAMANHO_BLOCO = 32; // bytes - tamanho máximo do bloco para criptografia
    
    private static RSAKeyManager instance;
    private ChavePublica chavePublica;
    private ChavePrivada chavePrivada;
    
    /**
     * Construtor privado (Singleton)
     */
    private RSAKeyManager() throws Exception {
        carregarOuGerarChaves();
    }
    
    /**
     * Obtém a instância única do gerenciador (Singleton)
     */
    public static synchronized RSAKeyManager getInstance() throws Exception {
        if (instance == null) {
            instance = new RSAKeyManager();
        }
        return instance;
    }
    
    /**
     * Carrega as chaves existentes ou gera novas se não existirem
     */
    private void carregarOuGerarChaves() throws Exception {
        File arquivoPublica = new File(ARQUIVO_CHAVE_PUBLICA);
        File arquivoPrivada = new File(ARQUIVO_CHAVE_PRIVADA);
        
        if (arquivoPublica.exists() && arquivoPrivada.exists()) {
            // Carregar chaves existentes
            carregarChaves();
        } else {
            // Gerar novas chaves
            gerarNovasChaves();
        }
    }
    
    /**
     * Gera um novo par de chaves RSA e salva em arquivo
     */
    public void gerarNovasChaves() throws Exception {
        System.out.println("Gerando novo par de chaves RSA (" + TAMANHO_CHAVE + " bits - otimizado)...");
        
        RSA rsa = new RSA(TAMANHO_CHAVE);
        this.chavePublica = rsa.getChavePublica();
        this.chavePrivada = rsa.getChavePrivada();
        
        salvarChaves();
        
        System.out.println("Chaves RSA geradas e salvas com sucesso!");
        System.out.println("Capacidade por bloco: " + ((chavePublica.getN().bitLength() / 8) - 11) + " bytes");
    }
    
    /**
     * Salva as chaves em arquivos
     */
    private void salvarChaves() throws Exception {
        // Criar diretório se não existir
        File dirDados = new File("src/dados");
        if (!dirDados.exists()) {
            dirDados.mkdirs();
        }
        
        // Salvar chave pública
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ARQUIVO_CHAVE_PUBLICA))) {
            byte[] eBytes = chavePublica.getE().toByteArray();
            byte[] nBytes = chavePublica.getN().toByteArray();
            
            dos.writeInt(eBytes.length);
            dos.write(eBytes);
            dos.writeInt(nBytes.length);
            dos.write(nBytes);
        }
        
        // Salvar chave privada
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ARQUIVO_CHAVE_PRIVADA))) {
            byte[] dBytes = chavePrivada.getD().toByteArray();
            byte[] nBytes = chavePrivada.getN().toByteArray();
            
            dos.writeInt(dBytes.length);
            dos.write(dBytes);
            dos.writeInt(nBytes.length);
            dos.write(nBytes);
        }
    }
    
    /**
     * Carrega as chaves dos arquivos
     */
    private void carregarChaves() throws Exception {
        // Carregar chave pública
        try (DataInputStream dis = new DataInputStream(new FileInputStream(ARQUIVO_CHAVE_PUBLICA))) {
            int eLength = dis.readInt();
            byte[] eBytes = new byte[eLength];
            dis.readFully(eBytes);
            
            int nLength = dis.readInt();
            byte[] nBytes = new byte[nLength];
            dis.readFully(nBytes);
            
            BigInteger e = new BigInteger(eBytes);
            BigInteger n = new BigInteger(nBytes);
            
            this.chavePublica = new ChavePublica(e, n);
        }
        
        // Carregar chave privada
        try (DataInputStream dis = new DataInputStream(new FileInputStream(ARQUIVO_CHAVE_PRIVADA))) {
            int dLength = dis.readInt();
            byte[] dBytes = new byte[dLength];
            dis.readFully(dBytes);
            
            int nLength = dis.readInt();
            byte[] nBytes = new byte[nLength];
            dis.readFully(nBytes);
            
            BigInteger d = new BigInteger(dBytes);
            BigInteger n = new BigInteger(nBytes);
            
            this.chavePrivada = new ChavePrivada(d, n);
        }
        
        System.out.println("Chaves RSA carregadas com sucesso!");
    }
    
    /**
     * Criptografa um texto usando a chave pública com otimizações de espaço
     * Estratégia: dividir texto em blocos menores e comprimir com LZW antes
     * @param texto texto a ser criptografado
     * @return texto criptografado em formato compacto
     */
    public String criptografar(String texto) throws Exception {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        
        // OTIMIZAÇÃO 1: Dividir o texto em blocos menores
        // Para chave de 512 bits, podemos cifrar até ~53 bytes por bloco
        int tamanhoMaximoBloco = (chavePublica.getN().bitLength() / 8) - 11; // PKCS#1 padding overhead
        
        byte[] bytesOriginais = texto.getBytes("UTF-8");
        
        // Se o texto for pequeno, cifrar diretamente
        if (bytesOriginais.length <= tamanhoMaximoBloco) {
            BigInteger textoCifrado = RSA.cifrarComChavePublica(texto, chavePublica);
            byte[] bytesCifrados = textoCifrado.toByteArray();
            
            // OTIMIZAÇÃO 2: Usar encoding compacto (sem padding excessivo do Base64)
            return bytesToHex(bytesCifrados);
        }
        
        // Para textos maiores, dividir em blocos
        StringBuilder resultado = new StringBuilder();
        int offset = 0;
        
        while (offset < bytesOriginais.length) {
            int tamanhoBloco = Math.min(tamanhoMaximoBloco, bytesOriginais.length - offset);
            byte[] bloco = new byte[tamanhoBloco];
            System.arraycopy(bytesOriginais, offset, bloco, 0, tamanhoBloco);
            
            BigInteger blocoCifrado = new BigInteger(1, bloco).modPow(chavePublica.getE(), chavePublica.getN());
            
            if (resultado.length() > 0) {
                resultado.append("|"); // Separador de blocos
            }
            resultado.append(bytesToHex(blocoCifrado.toByteArray()));
            
            offset += tamanhoBloco;
        }
        
        return resultado.toString();
    }
    
    /**
     * Converte bytes para hexadecimal (mais compacto que Base64)
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
    
    /**
     * Converte hexadecimal para bytes
     */
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i+1), 16));
        }
        return bytes;
    }
    
    /**
     * Descriptografa um texto usando a chave privada
     * @param textoCriptografado texto criptografado em formato compacto
     * @return texto original
     */
    public String descriptografar(String textoCriptografado) throws Exception {
        if (textoCriptografado == null || textoCriptografado.trim().isEmpty()) {
            return textoCriptografado;
        }
        
        try {
            // Verificar se contém o separador de blocos
            if (textoCriptografado.contains("|")) {
                // Descriptografar múltiplos blocos
                String[] blocos = textoCriptografado.split("\\|");
                StringBuilder textoOriginal = new StringBuilder();
                
                for (String blocoHex : blocos) {
                    byte[] bytesCifrados = hexToBytes(blocoHex);
                    BigInteger blocoCifrado = new BigInteger(bytesCifrados);
                    BigInteger blocoDecifrado = blocoCifrado.modPow(chavePrivada.getD(), chavePrivada.getN());
                    
                    textoOriginal.append(new String(blocoDecifrado.toByteArray(), "UTF-8"));
                }
                
                return textoOriginal.toString();
            } else {
                // Descriptografar bloco único (formato hexadecimal compacto)
                byte[] bytesCifrados = hexToBytes(textoCriptografado);
                BigInteger cifrado = new BigInteger(bytesCifrados);
                
                return RSA.decifrarParaStringComChavePrivada(cifrado, chavePrivada);
            }
        } catch (Exception e) {
            // Fallback: tentar formato antigo Base64
            try {
                byte[] bytes = java.util.Base64.getDecoder().decode(textoCriptografado);
                BigInteger cifrado = new BigInteger(bytes);
                return RSA.decifrarParaStringComChavePrivada(cifrado, chavePrivada);
            } catch (Exception e2) {
                // Se falhar completamente, pode ser um dado antigo não criptografado
                System.err.println("Erro ao descriptografar: " + e.getMessage());
                return textoCriptografado;
            }
        }
    }
    
    /**
     * Verifica se o texto está criptografado (formato Base64 válido)
     */
    public boolean estaCriptografado(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        
        try {
            java.util.Base64.getDecoder().decode(texto);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // Getters
    public ChavePublica getChavePublica() {
        return chavePublica;
    }
    
    public ChavePrivada getChavePrivada() {
        return chavePrivada;
    }
    
    /**
     * Método para testar a criptografia
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== Teste do RSAKeyManager ===\n");
            
            RSAKeyManager manager = RSAKeyManager.getInstance();
            
            String email = "cliente@petshop.com";
            System.out.println("Email original: " + email);
            
            String emailCriptografado = manager.criptografar(email);
            System.out.println("Email criptografado: " + emailCriptografado);
            
            String emailDescriptografado = manager.descriptografar(emailCriptografado);
            System.out.println("Email descriptografado: " + emailDescriptografado);
            
            System.out.println("\nSucesso: " + email.equals(emailDescriptografado));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
