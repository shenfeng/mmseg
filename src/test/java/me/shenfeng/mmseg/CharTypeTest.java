package me.shenfeng.mmseg;

import org.junit.Test;

public class CharTypeTest {

	@Test
	public void testCharType() {
		String str = "avcABC012, 我们。是";
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			System.out.println(ch + ": " + Character.getType(ch));
		}
	}
}
