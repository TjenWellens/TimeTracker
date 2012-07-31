package eu.tjenwellens.timetracker.calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Kalender implements CharSequence
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
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = new Kalender(names[i], ids[i]);
        }
        return calendars;
    }

    public static Kalender[] retrieveKalenders(Context c)
    {
        Kalender[] kalenders = null;
        Cursor cursor;
        String projection[] = new String[2];
        projection[0] = "_id";
        projection[1] = "displayName";
        String args = null;
//        if (currentCalendarsOnly) {
//            args = "selected=1";
//        }
        cursor = c.getContentResolver().query(Uri.parse(CONTENT_CALENDARS), projection, args, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String[] calNames = new String[cursor.getCount()];
            int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                // retrieve the calendar names and ids
                // at this stage you can print out the display names to get an
                // idea of what calendars the user has
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
            cursor.close();
            if (calIds.length > 0) {
                // we're safe here to do any further work
                kalenders = arraysToKalenders(calNames, calIds);
            }
        }
        return kalenders;
    }

    public static Kalender getKalenderByName(Context c, String name)
    {
        if (name == null) {
            return null;
        }
        Kalender kalender = null;
        for (Kalender k : retrieveKalenders(c)) {
            if (k.getName().equals(name)) {
                kalender = k;
                break;
            }
        }
        return kalender;
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
