package eu.tjenwellens.timetracker.main;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Tjen
 */
public class Time
{

    public static String timeToString(long millis)
    {
        if (millis < 0) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(millis));
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        String mins = String.valueOf(minutes);
        if (minutes < 10) {
            mins = "0" + mins;
        }
        return String.valueOf(hours) + ":" + mins;
    }
    
    public static int getHours(long millis)
    {
        if (millis < 0) {
            return 0;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(millis));
        return c.get(Calendar.HOUR_OF_DAY);
    }
    
    public static int getMinutes(long millis)
    {
        if (millis < 0) {
            return 0;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(millis));
        return c.get(Calendar.MINUTE);
    }
    
    public static long updateTime(long originalMillis, int hours, int minutes){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(originalMillis));
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes);
        return c.getTimeInMillis();
    }
}
