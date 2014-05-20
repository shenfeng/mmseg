package me.shenfeng.mmseg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by feng on 5/19/14.
 */

class TNode {
    char c;
    float weight = Float.NaN; // special value for intermediate node

    TNode[] children;

    TNode(char c) {
        this.c = c;
    }

    private static TNode[] add(TNode[] nodes, TNode node) {
        int hash = node.c;

        for (int i = hash; i < hash + nodes.length + 1; i++) {
            int idx = i % nodes.length;
            if (nodes[idx] == null) {
                nodes[idx] = node;
                return nodes;
            } else if (nodes[idx].c == node.c) {
                return nodes;
            }
        }

        TNode[] tmp = new TNode[nodes.length * 2];
        for (TNode child : nodes) {
            if (child != null) {
                add(tmp, child); // will not recursive call
            }
        }
        add(tmp, node);
        return tmp;
    }

    public TNode put(char c) {
        if (children == null) {
            children = new TNode[4];
        }

        TNode node = new TNode(c);
        children = add(children, node);
        return node;
    }

    public TNode get(char c) {
        if (children == null) return null;

        int hash = c;

        for (int i = hash; i < hash + children.length + 1; i++) {
            int idx = i % children.length;
            TNode node = children[idx];
            if (node == null) {
                return null;
            } else if (node.c == c) {
                return node;
            }
        }

        return null;
    }
}

class Trie2 {
    final private TNode root = new TNode('\0');

    public void addWord(String word, float weight) {
        if (word.length() > 0) {
            TNode node = root;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                node = node.put(c);
            }
            node.weight = weight;
        }
    }

    public float get(String word) {
        TNode node = root;
        for (int i = 0; i < word.length(); i++) {
            node = node.get(word.charAt(i));
            if (node == null) {
                return Float.NaN;
            }
        }
        return node.weight;
    }

    public int maxMatch(String str, int offset) {
        TNode node = root;
        int lastMatchIdx = offset;

        for (int i = offset; i < str.length(); i++) {
            char c = str.charAt(i);
            node = node.get(c);
            if (node == null) {
                break;
            } else if (node.weight != Float.NaN) {
                lastMatchIdx = i;
            }
        }
        return lastMatchIdx - offset + 1;
    }

    public int maxMatch(char buffer[], int offset, int length) {
        TNode node = root;
        int lastMatchIdx = offset;

        for (int i = offset; i < offset + length; i++) {
            char c = buffer[i];
            node = node.get(c);
            if (node == null) {
                break;
            } else if (node.weight != Float.NaN) {
                lastMatchIdx = i;
            }
        }
        return lastMatchIdx - offset + 1;
    }


    public List<Term> forwardMaxCut(String input) {
        List<Term> results = new ArrayList<Term>();

        int start = 0;

        while (start < input.length()) {
            int size = maxMatch(input, start);
            int end = start + size;
            results.add(new Term(start, size, input.substring(start, end)));

            start = end;
        }
        return results;
    }

    public static void main(String[] args) {
        Trie2 trie = new Trie2();

//        trie.addWords(Arrays.asList("北京大学", "北京大学", "北京", "大学生", "电影节"));
//
//        String input = "北京   大学生生生电影\n节";

        for (String s : Arrays.asList("姓名", "性别", "男", "籍贯", "身份证", ":", "：")) {
            trie.addWord(s, 1.0f);
        }


        String input = "姓名：张三性别：男籍贯：山东";

        System.out.println(trie.forwardMaxCut(input));

    }
}


public class Trie2Dictionary implements Dictionary {

    final Trie2 trie = new Trie2();

    public Trie2Dictionary(InputStream is) {

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                trie.addWord(line, 1.0f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int maxMath(char[] buffer, int offset, int length) {

        return trie.maxMatch(buffer, offset, length);
    }
}