package algorithms;

import java.util.ArrayList;

public class Kmp {
    public static ArrayList<Integer> computeLPSArray(String pattern) {
        int n = pattern.length();
        ArrayList<Integer> lps = new ArrayList<>();
        for (int k = 0; k < n; k++) lps.add(0);

        // length of the previous longest prefix suffix
        int len = 0;
        int i = 1;

        while (i < n) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps.set(i, len);
                i++;
            } else {
                if (len != 0) {
                    // fall back in the pattern
                    len = lps.get(len - 1);
                } else {
                    lps.set(i, 0);
                    i++;
                }
            }
        }

        return lps;
    }
    
    /**
     * Verifica se o padrão existe no texto (retorna boolean)
     */
    public static boolean contains(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        
        ArrayList<Integer> lps = computeLPSArray(pattern);
        
        int i = 0; // índice para texto
        int j = 0; // índice para padrão
        
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            
            if (j == m) {
                return true; // Padrão encontrado
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps.get(j - 1);
                } else {
                    i++;
                }
            }
        }
        
        return false; // Padrão não encontrado
    }
}
