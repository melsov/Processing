package drawbotV3_2;

import java.nio.ByteBuffer;

import drawbotV3_2.Asserter;
import drawbotV3_2.B;

public class ShortToBytes {

	public static byte[] Convert(int i) {
		Asserter.assertTrue(Math.abs(i) < Short.MAX_VALUE + (i<0 ? 1 : 0), "Trying to convert an int to a short that's beyond short max or min: " + i );
		return Convert((short)i);
	}
	public static byte[] Convert(short sh) {
		ByteBuffer bb = ByteBuffer.allocate(Short.SIZE / Byte.SIZE);
		bb.putShort(sh);
		return bb.array();
	}
	public static String printBytes(byte[] bs) {
		String res = "";
		for (byte b : bs) {
			res += b + " , ";
		}
		return res;
	}
	public static void debugBytes(byte[] bs) { B.bugln( printBytes(bs)); }
}
