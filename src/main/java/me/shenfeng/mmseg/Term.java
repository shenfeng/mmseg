package me.shenfeng.mmseg;

/**
 * Created by feng on 5/16/14.
 */
public class Term {
    public int idx;
    public int len;
    public String t;

    public Term(int idx, int len, String t) {
        this.idx = idx;
        this.len = len;
        this.t = t;
    }

    @Override
    public String toString() {
        return "Term{" +
                "idx=" + idx +
                ", len=" + len +
                ", t='" + t + '\'' +
                '}';
    }
}
