package drawbotV3_2;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.Test;

import drawbotV3_2.B;


public class IntToBytesTest {

	@Test
	public void testByteBuffer() {
		short sh = Short.MIN_VALUE; 
		byte[] bs; 
		float eps = 0.0f;
		int iter = 0;
//		for(; sh < Short.MAX_VALUE * .75 && iter++ < 10000; sh = (short) (sh + 11))
//		{
//			bs = ShortToBytes.Convert(sh);
//			assertEquals(sh, shortValue(bs), eps);
//		}
//		B.bugln("did " + iter + " tests");
		
		int size = 128;
		char[] ch = new char[size];
		for(int i=0; i < size; ++i) {
			ch[i] = toChar(i);
		}
		String str = new String(ch);
		B.bugln(str);
		for(int i=0; i < size; ++i) {
			int j = str.charAt(i);
			B.bug(j);
			assertEquals(j,i,eps);
		}
	}
	private short shortValue(byte[] bs) {
		return (short) ((bs[0] << 8) | (bs[1] & 0xFF)); 
	}
	private char toChar(int i) {
		return (char) i;
	}
}
