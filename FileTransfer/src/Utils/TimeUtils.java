package Utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	
	public static Date getCurrentDate() {
		   Date currentTime = new Date();
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String dateString = formatter.format(currentTime);
		   try {
			Date res = formatter.parse(dateString);
			return res;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		   return null;
	}
	
	public static String getCurrentDateString() {
		   Date currentTime = new Date();
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String dateString = formatter.format(currentTime);
		   return dateString;
	}
	
	public static String getStringByDate(Date date) {
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String dateString = formatter.format(date);
		   return dateString;
	}
	
	
	public static Date stringToDate(String string) {
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   ParsePosition pos = new ParsePosition(0);
		   Date date = formatter.parse(string, pos);
		   return date;
	}
}
