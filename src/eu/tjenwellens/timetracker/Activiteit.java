/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author tjen
 */
public class Activiteit
{

    private static long activityCounter = 0;
    //
    private boolean running;
    //
    private Kalender kalender = null;
    private long startTimeMillis;
    private long endTimeMillis;
    private String title;
    private long id;

    public Activiteit()
    {
        this.startTimeMillis = System.currentTimeMillis();
        endTimeMillis = -1;
        Activiteit.activityCounter++;
        this.title = "Leren" + Activiteit.activityCounter;
        this.id = activityCounter;
        this.running = true;
    }

    public boolean stop()
    {
        if (running)
        {
            endTimeMillis = System.currentTimeMillis();
            running = false;
            return true;
        } else
        {
            return false;
        }
    }

    public boolean resume()
    {
        if (!running)
        {
            endTimeMillis = -1;
            running = true;
            return true;
        } else
        {
            return false;
        }
    }

    public Evenement getEvent()
    {
        if (!running && kalender != null)
        {
            return new Evenement(kalender.getId(), title, startTimeMillis, endTimeMillis);
        } else
        {
            return null;
        }
    }

    public void setKalender(Kalender kalender)
    {
        this.kalender = kalender;
    }

    public void setEndTimeMillis(long endTimeMillis)
    {
        this.endTimeMillis = endTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis)
    {
        this.startTimeMillis = startTimeMillis;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDuration()
    {
        if (endTimeMillis < 0)
        {
            return "running";
        }
        long temp = (endTimeMillis - startTimeMillis) / 1000;
        int sec = (int) temp % 60;
        temp /= 60;
        int min = (int) temp % 60;
        temp /= 60;
        int hour = (int) temp % 24;
        temp /= 24;
        int day = (int) temp % 7;
        temp /= 7;
        int week = (int) temp;

        String s = "0 s";
        if (week > 0)
        {
            s = week + " w";
        } else if (day > 0)
        {
            s = day + " d";
        } else if (hour > 0)
        {
            s = hour + " h";
        } else if (min > 0)
        {
            s = min + " m";
        } else if (sec > 0)
        {
            s = sec + " s";
        }
        return s;
    }

    public String getKalenderName()
    {
        if (kalender == null)
        {
            return null;
        }
        return kalender.getName();
    }

    public String getStart()
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(startTimeMillis));
        String s = "";
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        String mins = String.valueOf(minutes);
        if (minutes < 10)
        {
            mins = "0" + mins;
        }
        return String.valueOf(hours) + ":" + mins;
    }

    public String getTitle()
    {
        return title;
    }

    public long getId()
    {
        return id;
    }

    public boolean isEnded()
    {
        return endTimeMillis > 0;
    }
}
