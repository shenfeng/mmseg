package me.shenfeng.mmseg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by feng on 5/17/14.
 */

class Trie {
    public static final char SENTINEL = '\0';
    public Map<Character, Trie> map = new HashMap<Character, Trie>();

    public java.util.HashSet<Character> ignores;

    public Trie() {
    }

    public Trie(java.util.HashSet<Character> ignores) {
        this.ignores = ignores;
    }

    public void addWords(List<String> words) {

        for (String word : words) {
            Trie trie = this;

            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (!trie.map.containsKey(c)) {
                    trie.map.put(c, new Trie());
                }
                trie = trie.map.get(c);
            }
            trie.map.put(SENTINEL, null);  // word flag
        }
    }

    public int forwardMaxMatch(int start, String word) {
        int last_match_idx = start;
        Trie trie = this;

        for (int i = start; i < word.length(); i++) {
            char c = word.charAt(i);

            if (ignores != null && ignores.contains(c)) { // ignore
                continue;
            }

            Trie tmp = trie.map.get(c);
            if (tmp == null) {
                break;
            } else {
                trie = tmp;
                if (trie.map.containsKey(SENTINEL)) {
                    last_match_idx = i;
                }
            }
        }

        return last_match_idx;
    }

    public int forwardMaxMatch(char[] sen, int offset, int length) {
        int last_match_idx = offset;
        Trie trie = this;


        for (int i = offset; i < offset + length; i++) {
            char c = sen[i];
            Trie tmp = trie.map.get(c);
            if (tmp == null) {
                break;
            } else {
                trie = tmp;
                if (trie.map.containsKey(SENTINEL)) {
//                    System.out.println("-------------");
                    last_match_idx = i;
                }
            }
        }

        return last_match_idx - offset + 1;
//        return last_match_idx == offset ? offset + length - 1 : last_match_idx;
    }

}

public class TrieDictionary implements Dictionary {

    private final Trie trie;

    public TrieDictionary(InputStream is) {

        trie = new Trie();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> words = new ArrayList<String>();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(words.size());
        trie.addWords(words);
    }


    @Override
    public int maxMath(char[] buffer, int offset, int length) {

        return trie.forwardMaxMatch(buffer, offset, length);

    }
}

