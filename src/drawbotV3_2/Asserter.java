package drawbotV3_2;

import drawbotV3_2.B;

public class Asserter {
	public static void assertTrue(boolean condition) {
		assertTrue(condition, "");
	}
	public static void assertTrue(boolean condition, String s) {
		if (!condition) {
			B.bug(s);
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	public static void assertFalseAndDie(String s) {
		assertTrue(false, s);
	}
}
