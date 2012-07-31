/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
}
