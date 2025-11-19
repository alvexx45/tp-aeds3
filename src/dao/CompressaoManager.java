package dao;

import algorithms.LZW;
import algorithms.Huffman;
import java.io.*;
import java.nio.file.*;

public class CompressaoManager {
    
    private static final String PASTA_DADOS = "src/dados";
    private static final String PASTA_COMPRIMIDA = "src/dados_comprimidos";
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
        
        // Calcula o tamanho original total
        long tamanhoOriginal = calcularTamanhoDiretorio(pastaDados);
        
        // Cria a pasta de dados comprimidos
        File pastaComprimida = new File(PASTA_COMPRIMIDA);
        pastaComprimida.mkdirs();
        
        // Comprime todos os arquivos da pasta dados
        comprimirDiretorio(pastaDados, pastaComprimida, algoritmo);
        
        // Calcula o tamanho comprimido
        long tamanhoComprimido = calcularTamanhoDiretorio(pastaComprimida);
        
        // Salva os metadados da compressão com as estatísticas
        salvarMetadados(algoritmo, tamanhoOriginal, tamanhoComprimido);
        
        // Remove a pasta original após compressão bem-sucedida
        deletarDiretorio(pastaDados);
    }
    
    /**
     * Descomprime os dados e restaura a pasta dados
     * @throws Exception
     */
    public void descomprimirDados() throws Exception {
        File pastaComprimida = new File(PASTA_COMPRIMIDA);
        
        if (!pastaComprimida.exists()) {
            throw new Exception("Pasta de dados comprimidos não encontrada!");
        }
        
        // Lê o algoritmo usado
        String algoritmo = obterAlgoritmoUtilizado();
        
        // Cria a pasta de dados
        File pastaDados = new File(PASTA_DADOS);
        pastaDados.mkdirs();
        
        // Descomprime todos os arquivos
        descomprimirDiretorio(pastaComprimida, pastaDados, algoritmo);
        
        // Remove a pasta comprimida e metadados após descompressão bem-sucedida
        deletarDiretorio(pastaComprimida);
        new File(ARQUIVO_METADADOS).delete();
    }
    
    /**
     * Verifica se os dados estão comprimidos
     * @return true se estão comprimidos, false caso contrário
     */
    public boolean verificarDadosComprimidos() {
        return new File(PASTA_COMPRIMIDA).exists() && new File(ARQUIVO_METADADOS).exists();
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
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (linha.startsWith("ALGORITMO:")) {
                        return linha.substring("ALGORITMO:".length()).trim();
                    }
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
    private void comprimirDiretorio(File dirOrigem, File dirDestino, String algoritmo) throws Exception {
        File[] arquivos = dirOrigem.listFiles();
        
        if (arquivos == null) return;
        
        for (File arquivo : arquivos) {
            if (arquivo.isDirectory()) {
                // Cria o subdiretório correspondente na pasta de destino
                File subDirDestino = new File(dirDestino, arquivo.getName());
                subDirDestino.mkdirs();
                
                // Recursivamente comprime o subdiretório
                comprimirDiretorio(arquivo, subDirDestino, algoritmo);
            } else {
                // Lê o arquivo original
                byte[] dadosOriginais = Files.readAllBytes(arquivo.toPath());
                
                // Comprime os dados usando o algoritmo selecionado
                byte[] dadosComprimidos;
                if (algoritmo.equals("HUFFMAN")) {
                    dadosComprimidos = Huffman.comprimirBytes(dadosOriginais);
                } else {
                    dadosComprimidos = LZW.codifica(dadosOriginais);
                }
                
                // Salva o arquivo comprimido com extensão .compressed
                File arquivoComprimido = new File(dirDestino, arquivo.getName() + ".compressed");
                try (FileOutputStream fos = new FileOutputStream(arquivoComprimido)) {
                    fos.write(dadosComprimidos);
                }
            }
        }
    }
    
    /**
     * Descomprime recursivamente um diretório
     */
    private void descomprimirDiretorio(File dirOrigem, File dirDestino, String algoritmo) throws Exception {
        File[] arquivos = dirOrigem.listFiles();
        
        if (arquivos == null) return;
        
        for (File arquivo : arquivos) {
            if (arquivo.isDirectory()) {
                // Cria o subdiretório correspondente na pasta de destino
                File subDirDestino = new File(dirDestino, arquivo.getName());
                subDirDestino.mkdirs();
                
                // Recursivamente descomprime o subdiretório
                descomprimirDiretorio(arquivo, subDirDestino, algoritmo);
            } else if (arquivo.getName().endsWith(".compressed")) {
                // Lê o arquivo comprimido
                byte[] dadosComprimidos = Files.readAllBytes(arquivo.toPath());
                
                // Descomprime os dados usando o algoritmo correto
                byte[] dadosDescomprimidos;
                if (algoritmo.equals("HUFFMAN")) {
                    dadosDescomprimidos = Huffman.descomprimirBytes(dadosComprimidos);
                } else {
                    dadosDescomprimidos = LZW.decodifica(dadosComprimidos);
                }
                
                // Salva o arquivo descomprimido removendo a extensão .compressed
                String nomeOriginal = arquivo.getName().substring(0, arquivo.getName().length() - ".compressed".length());
                File arquivoDescomprimido = new File(dirDestino, nomeOriginal);
                try (FileOutputStream fos = new FileOutputStream(arquivoDescomprimido)) {
                    fos.write(dadosDescomprimidos);
                }
            }
        }
    }
    
    /**
     * Calcula o tamanho total de um diretório recursivamente
     */
    private long calcularTamanhoDiretorio(File diretorio) {
        long tamanho = 0;
        if (diretorio.isDirectory()) {
            File[] arquivos = diretorio.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    if (arquivo.isDirectory()) {
                        tamanho += calcularTamanhoDiretorio(arquivo);
                    } else {
                        tamanho += arquivo.length();
                    }
                }
            }
        } else {
            tamanho = diretorio.length();
        }
        return tamanho;
    }
    
    /**
     * Salva os metadados da compressão com estatísticas
     */
    private void salvarMetadados(String algoritmo, long tamanhoOriginal, long tamanhoComprimido) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_METADADOS))) {
            // Informações básicas
            pw.println("=====================================");
            pw.println("  RELATÓRIO DE COMPRESSÃO DE DADOS");
            pw.println("=====================================");
            pw.println();
            
            // Algoritmo utilizado
            pw.println("ALGORITMO UTILIZADO: " + algoritmo);
            pw.println("DATA DA COMPRESSÃO: " + new java.util.Date(System.currentTimeMillis()));
            pw.println();
            
            // Estatísticas de tamanho
            pw.println("-------------------------------------");
            pw.println("  ESTATÍSTICAS DE COMPRESSÃO");
            pw.println("-------------------------------------");
            pw.println();
            
            pw.printf("a. Tamanho do arquivo original:     %,d bytes (%.2f KB / %.2f MB)%n", 
                tamanhoOriginal, 
                tamanhoOriginal / 1024.0, 
                tamanhoOriginal / (1024.0 * 1024.0));
            
            pw.printf("b. Tamanho do arquivo comprimido:   %,d bytes (%.2f KB / %.2f MB)%n", 
                tamanhoComprimido, 
                tamanhoComprimido / 1024.0, 
                tamanhoComprimido / (1024.0 * 1024.0));
            
            pw.println();
            
            // Cálculo da taxa de compressão
            double taxaCompressao = (double) tamanhoComprimido / tamanhoOriginal;
            double percentualReducao = (1 - taxaCompressao) * 100;
            long espacoEconomizado = tamanhoOriginal - tamanhoComprimido;
            
            pw.println("c. Cálculo da taxa de compressão:");
            pw.printf("   Taxa = Tamanho Comprimido / Tamanho Original%n");
            pw.printf("   Taxa = %,d / %,d%n", tamanhoComprimido, tamanhoOriginal);
            pw.printf("   Taxa = %.4f (%.2f%%)%n", taxaCompressao, taxaCompressao * 100);
            pw.println();
            
            pw.printf("   Percentual de redução: %.2f%%%n", percentualReducao);
            pw.printf("   Espaço economizado: %,d bytes (%.2f KB / %.2f MB)%n", 
                espacoEconomizado,
                espacoEconomizado / 1024.0,
                espacoEconomizado / (1024.0 * 1024.0));
            pw.println();
            
            // Interpretação do resultado
            pw.println("d. Interpretação do resultado:");
            if (taxaCompressao < 0.6) {
                pw.println("   ★★★ EXCELENTE: Compressão muito eficiente!");
                pw.println("   O algoritmo " + algoritmo + " reduziu o tamanho para menos da metade.");
            } else if (taxaCompressao < 0.8) {
                pw.println("   ★★☆ BOM: Compressão eficiente.");
                pw.println("   O algoritmo " + algoritmo + " conseguiu uma redução significativa.");
            } else if (taxaCompressao < 1) {
                pw.println("   ★☆☆ BAIXA: Compressão moderada.");
                pw.println("   O algoritmo " + algoritmo + " teve eficiência limitada.");
            } else if (taxaCompressao == 1) {
                pw.println("   ☆☆☆ SEM COMPRESSÃO: Compressão não ocorreu.");
                pw.println("   O algoritmo " + algoritmo + " não conseguiu reduzir o tamanho.");
            } else {
                pw.println("   ⚠ EXPANSÃO: Ocorreu expansão! Não utilize esse algoritmo.");
                pw.println("   O algoritmo " + algoritmo + " realizou expansão do tamanho.");
            }
            
            pw.println();
            pw.println("-------------------------------------");
            
            // Informações técnicas para leitura programática
            pw.println();
            pw.println("# Dados técnicos (não remover):");
            pw.println("ALGORITMO:" + algoritmo);
            pw.println("TAMANHO_ORIGINAL:" + tamanhoOriginal);
            pw.println("TAMANHO_COMPRIMIDO:" + tamanhoComprimido);
            pw.println("TAXA_COMPRESSAO:" + taxaCompressao);
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
