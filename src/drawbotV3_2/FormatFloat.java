package drawbotV3_2;

public class FormatFloat {

	public static String FloatStringOneDecimal(float f) {
		return String.format("%.1f", f);
	}
	
	public static String FloatStringFourDecimals(float f) {
		return String.format("%.4f", f);
	}
}
