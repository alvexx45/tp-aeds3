package algorithms;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Implementação do algoritmo de criptografia RSA
 * 
 * Fluxo:
 * 1. Escolher dois números primos extensos p e q (maiores de 10^100)
 * 2. Calcular n = p * q e z = (p-1) * (q-1)
 * 3. Escolher um número d relativamente primo a z
 * 4. Escolher e tal que (e*d) mod z = 1
 * 5. Para cifrar: c = m^e mod n
 * 6. Para decifrar: m = c^d mod n
 * 7. Chave pública: (e, n)
 * 8. Chave privada: (d, n)
 */
public class RSA {
    
    private BigInteger n;  // n = p * q
    private BigInteger e;  // expoente público
    private BigInteger d;  // expoente privado
    private int bitLength; // tamanho da chave em bits
    
    /**
     * Classe para representar a chave pública
     */
    public static class ChavePublica {
        private BigInteger e;
        private BigInteger n;
        
        public ChavePublica(BigInteger e, BigInteger n) {
            this.e = e;
            this.n = n;
        }
        
        public BigInteger getE() {
            return e;
        }
        
        public BigInteger getN() {
            return n;
        }
        
        @Override
        public String toString() {
            return "ChavePublica{e=" + e + ", n=" + n + "}";
        }
    }
    
    /**
     * Classe para representar a chave privada
     */
    public static class ChavePrivada {
        private BigInteger d;
        private BigInteger n;
        
        public ChavePrivada(BigInteger d, BigInteger n) {
            this.d = d;
            this.n = n;
        }
        
        public BigInteger getD() {
            return d;
        }
        
        public BigInteger getN() {
            return n;
        }
        
        @Override
        public String toString() {
            return "ChavePrivada{d=" + d + ", n=" + n + "}";
        }
    }
    
    /**
     * Construtor que gera um novo par de chaves RSA
     * @param bitLength tamanho da chave em bits (recomendado: 512, 1024, 2048)
     *                  Para p e q maiores que 10^100, use bitLength >= 512
     */
    public RSA(int bitLength) {
        this.bitLength = bitLength;
        gerarChaves();
    }
    
    /**
     * Construtor para usar chaves existentes
     * @param e expoente público
     * @param d expoente privado
     * @param n módulo
     */
    public RSA(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
        this.bitLength = n.bitLength();
    }
    
    /**
     * Gera um par de chaves RSA
     */
    private void gerarChaves() {
        Random random = new SecureRandom();
        
        // Passo 1: Escolher dois números primos extensos p e q
        // Para garantir que sejam maiores que 10^100, usamos bitLength/2 bits para cada
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        
        // Garantir que p != q
        while (p.equals(q)) {
            q = BigInteger.probablePrime(bitLength / 2, random);
        }
        
        // Passo 2: Calcular n = p * q e z = (p-1) * (q-1)
        this.n = p.multiply(q);
        BigInteger z = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        
        // Passo 3: Escolher d relativamente primo a z (gcd(d, z) = 1)
        // Começamos com um valor aleatório e verificamos se é relativamente primo
        this.d = gerarDRelativamentePrimo(z, random);
        
        // Passo 4: Escolher e tal que (e * d) mod z = 1
        // e é o inverso modular de d em relação a z
        this.e = d.modInverse(z);
    }
    
    /**
     * Gera um número d que seja relativamente primo a z
     * @param z valor de (p-1) * (q-1)
     * @param random gerador de números aleatórios
     * @return d relativamente primo a z
     */
    private BigInteger gerarDRelativamentePrimo(BigInteger z, Random random) {
        BigInteger d;
        do {
            // Gera um número entre 2 e z-1
            d = new BigInteger(z.bitLength() - 1, random);
            
            // Garante que d > 1
            if (d.compareTo(BigInteger.ONE) <= 0) {
                d = BigInteger.valueOf(2);
            }
            
            // Garante que d < z
            if (d.compareTo(z) >= 0) {
                d = d.mod(z);
            }
            
        } while (!d.gcd(z).equals(BigInteger.ONE)); // Continua até gcd(d, z) = 1
        
        return d;
    }
    
    /**
     * Cifra uma mensagem usando a chave pública
     * Passo 5: c = m^e mod n
     * @param mensagem mensagem a ser cifrada (como BigInteger)
     * @return mensagem cifrada
     */
    public BigInteger cifrar(BigInteger mensagem) {
        return mensagem.modPow(e, n);
    }
    
    /**
     * Cifra uma string convertendo para bytes e depois para BigInteger
     * @param mensagem string a ser cifrada
     * @return mensagem cifrada
     */
    public BigInteger cifrar(String mensagem) {
        byte[] bytes = mensagem.getBytes();
        BigInteger m = new BigInteger(1, bytes); // 1 para número positivo
        return cifrar(m);
    }
    
    /**
     * Decifra uma mensagem usando a chave privada
     * Passo 6: m = c^d mod n
     * @param mensagemCifrada mensagem cifrada
     * @return mensagem original
     */
    public BigInteger decifrar(BigInteger mensagemCifrada) {
        return mensagemCifrada.modPow(d, n);
    }
    
    /**
     * Decifra uma mensagem e converte de volta para String
     * @param mensagemCifrada mensagem cifrada
     * @return string original
     */
    public String decifrarParaString(BigInteger mensagemCifrada) {
        BigInteger m = decifrar(mensagemCifrada);
        byte[] bytes = m.toByteArray();
        return new String(bytes);
    }
    
    /**
     * Cifra usando chave pública específica
     * @param mensagem mensagem a ser cifrada
     * @param chavePublica chave pública a ser usada
     * @return mensagem cifrada
     */
    public static BigInteger cifrarComChavePublica(BigInteger mensagem, ChavePublica chavePublica) {
        return mensagem.modPow(chavePublica.getE(), chavePublica.getN());
    }
    
    /**
     * Cifra string usando chave pública específica
     * @param mensagem string a ser cifrada
     * @param chavePublica chave pública a ser usada
     * @return mensagem cifrada
     */
    public static BigInteger cifrarComChavePublica(String mensagem, ChavePublica chavePublica) {
        byte[] bytes = mensagem.getBytes();
        BigInteger m = new BigInteger(1, bytes);
        return cifrarComChavePublica(m, chavePublica);
    }
    
    /**
     * Decifra usando chave privada específica
     * @param mensagemCifrada mensagem cifrada
     * @param chavePrivada chave privada a ser usada
     * @return mensagem original
     */
    public static BigInteger decifrarComChavePrivada(BigInteger mensagemCifrada, ChavePrivada chavePrivada) {
        return mensagemCifrada.modPow(chavePrivada.getD(), chavePrivada.getN());
    }
    
    /**
     * Decifra e converte para string usando chave privada específica
     * @param mensagemCifrada mensagem cifrada
     * @param chavePrivada chave privada a ser usada
     * @return string original
     */
    public static String decifrarParaStringComChavePrivada(BigInteger mensagemCifrada, ChavePrivada chavePrivada) {
        BigInteger m = decifrarComChavePrivada(mensagemCifrada, chavePrivada);
        byte[] bytes = m.toByteArray();
        return new String(bytes);
    }
    
    // Getters para as chaves
    
    public ChavePublica getChavePublica() {
        return new ChavePublica(e, n);
    }
    
    public ChavePrivada getChavePrivada() {
        return new ChavePrivada(d, n);
    }
    
    public BigInteger getE() {
        return e;
    }
    
    public BigInteger getD() {
        return d;
    }
    
    public BigInteger getN() {
        return n;
    }
    
    public int getBitLength() {
        return bitLength;
    }
    
    /**
     * Método main para testar o algoritmo
     */
    public static void main(String[] args) {
        System.out.println("=== Teste do Algoritmo RSA ===\n");
        
        // Cria uma instância RSA com chaves de 512 bits (garantindo p e q > 10^100)
        System.out.println("Gerando chaves RSA de 512 bits...");
        RSA rsa = new RSA(512);
        
        System.out.println("\n--- Chaves Geradas ---");
        System.out.println("Chave Pública (e, n):");
        System.out.println("e = " + rsa.getE());
        System.out.println("n = " + rsa.getN());
        System.out.println("\nChave Privada (d, n):");
        System.out.println("d = " + rsa.getD());
        System.out.println("n = " + rsa.getN());
        
        // Teste 1: Cifrar e decifrar um número
        System.out.println("\n--- Teste 1: Cifrando um número ---");
        BigInteger mensagemOriginal = new BigInteger("123456789");
        System.out.println("Mensagem original: " + mensagemOriginal);
        
        BigInteger mensagemCifrada = rsa.cifrar(mensagemOriginal);
        System.out.println("Mensagem cifrada: " + mensagemCifrada);
        
        BigInteger mensagemDecifrada = rsa.decifrar(mensagemCifrada);
        System.out.println("Mensagem decifrada: " + mensagemDecifrada);
        System.out.println("Sucesso: " + mensagemOriginal.equals(mensagemDecifrada));
        
        // Teste 2: Cifrar e decifrar uma string (email)
        System.out.println("\n--- Teste 2: Cifrando um email ---");
        String email = "usuario@petshop.com";
        System.out.println("Email original: " + email);
        
        BigInteger emailCifrado = rsa.cifrar(email);
        System.out.println("Email cifrado: " + emailCifrado);
        
        String emailDecifrado = rsa.decifrarParaString(emailCifrado);
        System.out.println("Email decifrado: " + emailDecifrado);
        System.out.println("Sucesso: " + email.equals(emailDecifrado));
        
        // Teste 3: Usando chaves públicas e privadas separadamente
        System.out.println("\n--- Teste 3: Usando chaves separadas ---");
        ChavePublica chavePublica = rsa.getChavePublica();
        ChavePrivada chavePrivada = rsa.getChavePrivada();
        
        String mensagem = "dados.secretos@email.com";
        System.out.println("Mensagem: " + mensagem);
        
        BigInteger cifrado = RSA.cifrarComChavePublica(mensagem, chavePublica);
        System.out.println("Cifrado com chave pública: " + cifrado);
        
        String decifrado = RSA.decifrarParaStringComChavePrivada(cifrado, chavePrivada);
        System.out.println("Decifrado com chave privada: " + decifrado);
        System.out.println("Sucesso: " + mensagem.equals(decifrado));
        
        System.out.println("\n=== Todos os testes concluídos ===");
    }
}
