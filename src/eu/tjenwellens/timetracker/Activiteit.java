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
public class Activiteit implements ActiviteitI, DBGetActiviteit
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
    private String description = null;

    public Activiteit(long id, Kalender kalender, String title, long startTimeMillis, long endTimeMillis)
    {
        this.id = id;
        Activiteit.activityCounter = id + 1;
        this.kalender = kalender;
        this.title = title;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.running = endTimeMillis < 0;
        this.description = "";
    }

    public Activiteit(long id, Kalender kalender, String title, long startTimeMillis, long endTimeMillis, String description)
    {
        this.id = id;
        Activiteit.activityCounter = id + 1;
        this.kalender = kalender;
        this.title = title;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.running = endTimeMillis < 0;
        this.description = description;
    }

    public Activiteit()
    {
        this.startTimeMillis = System.currentTimeMillis();
        endTimeMillis = -1;
        Activiteit.activityCounter++;
        this.title = "Activiteit" + Activiteit.activityCounter;
        this.id = activityCounter;
        this.running = true;
        this.description = "";
    }

    @Override
    public boolean stopRunning()
    {
        if (running) {
            endTimeMillis = System.currentTimeMillis();
            running = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean resumeRunning()
    {
        if (!running) {
            endTimeMillis = -1;
            running = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Evenement getEvenement()
    {
        if (!running && kalender != null) {
            Evenement e = new Evenement(kalender.getId(), title, startTimeMillis, endTimeMillis);
            if (description != null) {
                e.setDescription(description);
            }
            return e;
        } else {
            return null;
        }
    }

    @Override
    public void setKalender(Kalender kalender)
    {
        this.kalender = kalender;
    }

    @Override
    public void setEndTimeMillis(long endTimeMillis)
    {
        this.endTimeMillis = endTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis)
    {
        this.startTimeMillis = startTimeMillis;
    }

    @Override
    public void setActiviteitTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getDuration()
    {
        if (endTimeMillis < 0) {
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
        if (week > 0) {
            s = week + " w";
        } else if (day > 0) {
            s = day + " d";
        } else if (hour > 0) {
            s = hour + " h";
        } else if (min > 0) {
            s = min + " m";
        } else if (sec > 0) {
            s = sec + " s";
        }
        return s;
    }

    @Override
    public String getKalenderName()
    {
        if (kalender == null) {
            return null;
        }
        return kalender.getName();
    }

    @Override
    public String getStartTime()
    {
        return timeToString(startTimeMillis);
    }

    @Override
    public String getStopTime()
    {
        return timeToString(endTimeMillis);
    }

    private String timeToString(long millis)
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

    @Override
    public String getActiviteitTitle()
    {
        return title;
    }

    @Override
    public long getActiviteitId()
    {
        return id;
    }

    @Override
    public boolean isRunning()
    {
        return running;
    }

    public String getDescription()
    {
        return description != null ? description : "";
    }

    public long getBeginMillis()
    {
        return startTimeMillis;
    }

    public long getEndMillis()
    {
        return endTimeMillis;
    }
}
