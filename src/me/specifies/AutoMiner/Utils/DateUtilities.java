package me.specifies.AutoMiner.Utils;

import java.util.Date;

@SuppressWarnings("deprecation")
public class DateUtilities {
	
	public int getMinuteDifference(String timestampQuery) {
		
		long timestamp = Long.parseLong(timestampQuery);
		
		Date now = new Date();
		
		long currentTimestamp = now.getTime() / 1000;
		
		long difference = currentTimestamp - timestamp;
		
		Date calculated = new Date(difference * 1000);
		
		return calculated.getMinutes();
		
	}

}
