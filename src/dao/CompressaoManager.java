package dao;

import algorithms.LZW;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CompressaoManager {
    
    private static final String PASTA_DADOS = "src/dados";
    private static final String ARQUIVO_COMPRIMIDO = "src/dados_comprimidos.zip";
    private static final String ARQUIVO_METADADOS = "src/metadados_compressao.txt";
    
    /**
     * Comprime todos os arquivos da pasta dados usando o algoritmo especificado
     * @param algoritmo "HUFFMAN" ou "LZW"
     * @throws Exception
     */
    public void comprimirDados(String algoritmo) throws Exception {
        File pastaDados = new File(PASTA_DADOS);
        
        if (!pastaDados.exists() || !pastaDados.isDirectory()) {
            throw new Exception("Pasta 'dados' não encontrada!");
        }
        
        // Verifica se já existe compressão
        if (verificarDadosComprimidos()) {
            throw new Exception("Dados já estão comprimidos! Descomprima primeiro.");
        }
        
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_COMPRIMIDO);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            // Comprime todos os arquivos da pasta dados
            comprimirDiretorio(pastaDados, pastaDados.getName(), zos, algoritmo);
            
            // Salva os metadados da compressão
            salvarMetadados(algoritmo);
            
            // Remove a pasta original após compressão bem-sucedida
            deletarDiretorio(pastaDados);
        }
    }
    
    /**
     * Descomprime os dados e restaura a pasta dados
     * @throws Exception
     */
    public void descomprimirDados() throws Exception {
        File arquivoComprimido = new File(ARQUIVO_COMPRIMIDO);
        
        if (!arquivoComprimido.exists()) {
            throw new Exception("Arquivo comprimido não encontrado!");
        }
        
        try (FileInputStream fis = new FileInputStream(ARQUIVO_COMPRIMIDO);
             ZipInputStream zis = new ZipInputStream(fis)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // Adiciona o prefixo "src/" ao caminho do arquivo para restaurar na pasta src
                File arquivo = new File("src/" + entry.getName());
                
                if (entry.isDirectory()) {
                    arquivo.mkdirs();
                } else {
                    // Cria os diretórios pai se necessário
                    arquivo.getParentFile().mkdirs();
                    
                    // Lê os dados comprimidos
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    byte[] dadosComprimidos = baos.toByteArray();
                    
                    // Descomprime os dados usando LZW
                    byte[] dadosDescomprimidos = LZW.decodifica(dadosComprimidos);
                    
                    // Escreve o arquivo descomprimido
                    try (FileOutputStream fos = new FileOutputStream(arquivo)) {
                        fos.write(dadosDescomprimidos);
                    }
                }
                zis.closeEntry();
            }
        }
        
        // Remove o arquivo comprimido e metadados após descompressão bem-sucedida
        arquivoComprimido.delete();
        new File(ARQUIVO_METADADOS).delete();
    }
    
    /**
     * Verifica se os dados estão comprimidos
     * @return true se estão comprimidos, false caso contrário
     */
    public boolean verificarDadosComprimidos() {
        return new File(ARQUIVO_COMPRIMIDO).exists() && new File(ARQUIVO_METADADOS).exists();
    }
    
    /**
     * Obtém o algoritmo utilizado na compressão atual
     * @return "HUFFMAN", "LZW" ou "DESCONHECIDO"
     */
    public String obterAlgoritmoUtilizado() {
        try {
            File metadados = new File(ARQUIVO_METADADOS);
            if (!metadados.exists()) {
                return "DESCONHECIDO";
            }
            
            try (BufferedReader br = new BufferedReader(new FileReader(metadados))) {
                String linha = br.readLine();
                if (linha != null && linha.startsWith("ALGORITMO:")) {
                    return linha.substring("ALGORITMO:".length()).trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "DESCONHECIDO";
    }
    
    /**
     * Comprime recursivamente um diretório
     */
    private void comprimirDiretorio(File diretorio, String caminhoBase, ZipOutputStream zos, String algoritmo) throws Exception {
        File[] arquivos = diretorio.listFiles();
        
        if (arquivos == null) return;
        
        for (File arquivo : arquivos) {
            String caminhoNoZip = caminhoBase + "/" + arquivo.getName();
            
            if (arquivo.isDirectory()) {
                // Adiciona entrada de diretório no ZIP
                zos.putNextEntry(new ZipEntry(caminhoNoZip + "/"));
                zos.closeEntry();
                
                // Recursivamente comprime o subdiretório
                comprimirDiretorio(arquivo, caminhoNoZip, zos, algoritmo);
            } else {
                // Lê o arquivo
                byte[] dadosOriginais = Files.readAllBytes(arquivo.toPath());
                
                // Comprime os dados usando LZW
                byte[] dadosComprimidos = LZW.codifica(dadosOriginais);
                
                // Adiciona ao ZIP
                zos.putNextEntry(new ZipEntry(caminhoNoZip));
                zos.write(dadosComprimidos);
                zos.closeEntry();
            }
        }
    }
    
    /**
     * Salva os metadados da compressão
     */
    private void salvarMetadados(String algoritmo) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_METADADOS))) {
            pw.println("ALGORITMO:" + algoritmo);
            pw.println("DATA:" + System.currentTimeMillis());
        }
    }
    
    /**
     * Deleta recursivamente um diretório
     */
    private void deletarDiretorio(File diretorio) throws IOException {
        if (diretorio.isDirectory()) {
            File[] arquivos = diretorio.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    deletarDiretorio(arquivo);
                }
            }
        }
        diretorio.delete();
    }
}
