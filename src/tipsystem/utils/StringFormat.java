package tipsystem.utils;

import java.text.NumberFormat;

public class StringFormat {
	public static String convertToIntNumberFormat(String number ) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		double n = Double.valueOf(number);
		
		return String.format("%s", numberFormat.format((int)n));
	}
	
	public static String convertToNumberFormat(String number ) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		double n = Double.valueOf(number);
		
		return String.format("%s", numberFormat.format(n));
	}
}
