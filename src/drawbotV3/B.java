package drawbotV3;

import drawbotV3.Pointt;

public class B {
	public static void bug(String str) {
		System.out.print(str);
	}
	public static void bugln(String str) {
		System.out.println(str);
	}
	public static void bugln() {
		bugln("");
	}
	public static void bug(int i) {
		System.out.println("" + i);
	}
	public static void bug(double d) {
		System.out.println("" + d);
	}
	public static void bug(Pointt p) {
		System.out.println("x: " +p.x+ " y: "+p.y);
	}
}
