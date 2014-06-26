package eu.tjenwellens.timetracker.calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import eu.tjenwellens.timetracker.TimeTrackerApplication;

public class Kalender implements CharSequence
{
    private String name;
    private int id;

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Kalender(String name, int id)
    {
        this.name = name;
        this.id = id;
    }

    public static Kalender getDefaultKalender(Context c)
    {
        Kalender[] kalenders = retrieveKalenders(c);
        if (kalenders == null || kalenders.length <= 0)
        {
            return null;
        }
        return kalenders[0];
    }

    public static Kalender[] retrieveKalenders(Context c)
    {
        return ((TimeTrackerApplication) c.getApplicationContext()).getVco().retrieveKalenders();
    }

    public static Kalender getKalenderByName(Context c, String kalenderName)
    {
        if (kalenderName == null)
        {
            return null;
        }
        for (Kalender k : retrieveKalenders(c))
        {
            if (k.getName().equals(kalenderName))
            {
                return k;
            }
        }
        return null;
    }

    public static Kalender getKalenderByName(Kalender[] kalenders, String kalenderName)
    {
        for (Kalender k : kalenders)
        {
            if (k.getName().equals(kalenderName))
            {
                return k;
            }
        }
        return null;
    }

    public int length()
    {
        return name.length();
    }

    public char charAt(int arg0)
    {
        return name.charAt(arg0);
    }

    public CharSequence subSequence(int arg0, int arg1)
    {
        return name.subSequence(arg0, arg1);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
