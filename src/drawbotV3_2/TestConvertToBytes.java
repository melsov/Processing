package drawbotV3_2;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestConvertToBytes {

	@Test
	public void testConvertBytes() {
		float eps = 0f;
		
		int start = 0;// (1 << 14) * -1;
		int end = 89; // (1 << 14) * -1 + 200; // (1 << 14) - 1;
		
		for(int i = start; i < end; ++i) {
			byte[] bytepair = ConvertToBytes.SignedBytePairFromInt(i);
			int from = ConvertToBytes.IntFromSignedBytePair(bytepair);
//			if (from != i) {
				B.bugln("not eq: from: " + from + " i: " +i);
				B.bugln(ConvertToBytes.intAsBinaryString(from));
				B.bugln(ConvertToBytes.intAsBinaryString(i));
				B.bugln("byte 0: " + bytepair[0]);
				B.bugln("byte 1: " + bytepair[1]);
				B.bugln(ConvertToBytes.byteAsBinaryString((byte)0) + ConvertToBytes.byteAsBinaryString((byte)0) +ConvertToBytes.byteAsBinaryString(bytepair[0]) + ConvertToBytes.byteAsBinaryString(bytepair[1]));
//			}
			assertEquals(from, i, eps);
		}
	}
}
