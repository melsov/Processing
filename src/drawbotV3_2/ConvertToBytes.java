package drawbotV3_2;

public class ConvertToBytes {
	
	public static byte[] SignedBytePairFromInt(int i) {
		if (i == 0) {
			return new byte[]{ 0, 0 };
		}
		byte smaller = (byte) (i & 127);
		byte larger =  (byte) ((i >> 7) & 127);
		if (i < 0) {
			if (i > -129) {
				larger = 0;
				smaller = (byte) i;
				return new byte[] { larger, smaller };
			} 
			smaller = (byte) ((Math.abs(i) & 127) * -1);
//			smaller = (byte) (((i ^ -1) & 127) * -1);
			larger = (byte) ((Math.abs(i) >> 7) * -1);
		}
		
//		int negativeMask = i & Integer.MIN_VALUE;
//		byte largerEightBits = (byte)( (negativeMask >>> 24) | larger );
		return new byte[] { larger, smaller};
	}
	public static int IntFromSignedBytePair(byte[] byp) {
		int larger = byp[0];
		int negMask = (larger & Integer.MIN_VALUE); 
		larger = (byp[0] << 7);
		larger = negMask | larger;
		return (larger) + ( byp[1]); // (byp[0] << 8) | ( byp[1] & 0xFF);
	}
	public static String byteAsBinaryString(byte b) {
		char[] cs = new char[8];
		for(int i=0; i < 8; ++i) {
			cs[7 - i] = ((b >> i) & 1 ) == 0 ? '0' : '1';
		}
		return new String(cs);
	}
	public static String intAsBinaryString(int ii) {
		char[] cs = new char[32];
		for(int i=0; i < 32; ++i) {
			cs[31 - i] = ((ii >> i) & 1 ) == 0 ? '0' : '1';
		}
		return new String(cs);
	}

}
