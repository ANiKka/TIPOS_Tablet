package tipsystem.utils;

import java.text.NumberFormat;

public class StringFormat {
	public static String convertToIntNumberFormat(String number ) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		double n = Double.valueOf(number);
		
		return String.format("%s", numberFormat.format((int)n));
	}
	
	public static String convertToNumberFormat(String number ) {
		if (number.equals("")) number = "0";
		
		double n = Double.valueOf(number);
		
		return convertToNumberFormat(n);
	}
	
	public static String convertToNumberFormat(Double number ) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		return String.format("%s", numberFormat.format(number));
	}
	
}
