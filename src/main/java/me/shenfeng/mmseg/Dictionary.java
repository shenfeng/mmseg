package me.shenfeng.mmseg;

public interface Dictionary {

    /**
     * @param buffer
     * @param offset
     * @param length
     * @return max match size
     */
    public int maxMath(char[] buffer, int offset, int length);

}
