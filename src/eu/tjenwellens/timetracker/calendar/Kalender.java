package eu.tjenwellens.timetracker.calendar;

import android.database.Cursor;
import android.net.Uri;
import eu.tjenwellens.timetracker.GetContent;

public class Kalender
{
    
    public static final String CONTENT_CALENDARS = "content://com.android.calendar/calendars";
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

    private static Kalender[] arraysToKalenders(String[] names, int[] ids)
    {
        Kalender[] calendars = new Kalender[Math.min(names.length, ids.length)];
        for (int i = 0; i < calendars.length; i++)
        {
            calendars[i] = new Kalender(names[i], ids[i]);
        }
        return calendars;
    }

    public static Kalender[] retrieveKalenders(GetContent c, boolean currentCalendarsOnly)
    {
        Kalender[] kalenders = null;
        Cursor cursor;
        String projection[] = new String[2];
        projection[0] = "_id";
        projection[1] = "displayName";
        String args = null;
        if (currentCalendarsOnly)
        {
            args = "selected=1";
        }
        cursor = c.getContentResolver().query(Uri.parse(CONTENT_CALENDARS), projection, args, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            String[] calNames = new String[cursor.getCount()];
            int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++)
            {
                // retrieve the calendar names and ids
                // at this stage you can print out the display names to get an
                // idea of what calendars the user has
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
            cursor.close();
            if (calIds.length > 0)
            {
                // we're safe here to do any further work
                kalenders = arraysToKalenders(calNames, calIds);
            }
        }
        return kalenders;
    }
}
