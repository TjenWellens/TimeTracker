/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 *
 * @author tjen
 */
public class Evenement implements Evenement_ContentValues, Evenement_values
{

    public static final String CONTENT_EVENTS = "content://com.android.calendar/events";
    // obligated
    private int calendarId;
    private String title;
    private long startTimeMillis;
    private long endTimeMillis;
    // free
    private String description = null;
    private String eventLocation = null;
    private int allDay = -1;        // allDay: ?-gegokt-? 0~ false; 1~ true
    private int eventStatus = -1;   // eventStatus: 0~ tentative; 1~ confirmed; 2~ canceled
    private int visibility = -1;    // visibility: 0~ default; 1~ confidential; 2~ private; 3~ public
    private int transparency = -1;  //0~ opaque, no timing conflict is allowed; 1~ transparency, allow overlap of scheduling
    private int hasAlarm = -1;      //0~ false; 1~ true

    public Evenement(int calendarId, String title, long startTimeMillis, long endTimeMillis)
    {
        this.calendarId = calendarId;
        this.title = title;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
    }
    
    public static void post(Context c, Evenement e)
    {
        c.getContentResolver().insert(Uri.parse(CONTENT_EVENTS), e.contentValues());
    }

    private ContentValues contentValues()
    {
        ContentValues contentValues = new ContentValues();
        // obligated
        contentValues.put(TITLE, title);
        contentValues.put(CALENDAR_ID, calendarId);
        contentValues.put(START, startTimeMillis);
        contentValues.put(END, endTimeMillis);
        // default-able
        if (description != null)
        {
            contentValues.put(DESCRIPTION, description);
        }
        if (eventLocation != null)
        {
            contentValues.put(EVENT_LOCATION, eventLocation);
        }
        if (allDay >= 0)
        {
            contentValues.put(ALL_DAY, allDay);
        }
        if (eventStatus >= 0)
        {
            contentValues.put(EVENT_STATUS, eventStatus);
        }
        if (visibility >= 0)
        {
            contentValues.put(VISIBILITY, visibility);
        }
        if (transparency >= 0)
        {
            contentValues.put(TRANSPARANCY, transparency);
        }
        if (hasAlarm >= 0)
        {
            contentValues.put(HAS_ALARM, hasAlarm);
        }
        return contentValues;
    }

    public void setAllDay(int allDay)
    {
        this.allDay = allDay;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setEventLocation(String eventLocation)
    {
        this.eventLocation = eventLocation;
    }

    public void setEventStatus(int eventStatus)
    {
        this.eventStatus = eventStatus;
    }

    public void setHasAlarm(int hasAlarm)
    {
        this.hasAlarm = hasAlarm;
    }

    public void setTransparency(int transparency)
    {
        this.transparency = transparency;
    }

    public void setVisibility(int visibility)
    {
        this.visibility = visibility;
    }
}
