package eu.tjenwellens.timetracker.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 *
 * @author Tjen
 */
class Post4Overlay extends VersionedCalendarOverlay {
    private Context context;

    public Post4Overlay(Context context) {
        this.context = context;
    }

    @Override
    public Kalender[] retrieveKalenders() {
        ArrayList<Kalender> kalenders = new ArrayList<Kalender>();
        Cursor cursor;
        ContentResolver cr = context.getContentResolver();
        String projection[] = new String[]{
            Calendars._ID, // 0
            Calendars.CALENDAR_DISPLAY_NAME // 1
        };
        Uri uri = Calendars.CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;
        cursor = cr.query(uri, projection, selection, selectionArgs, null);

        while (cursor.moveToNext()) {
            long calID = cursor.getLong(0);
            String displayName = cursor.getString(1);
            kalenders.add(new Kalender(displayName, (int) calID));
        }
        cursor.close();
        return kalenders.toArray(new Kalender[0]);
    }

    @Override
    void post(Evenement e) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(Events.CONTENT_URI, contentValues(e));
    }

    private static ContentValues contentValues(Evenement e) {

//        values.put(Events.DTSTART, startMillis);
//        values.put(Events.DTEND, endMillis);
//        values.put(Events.TITLE, "Jazzercise");
//        values.put(Events.DESCRIPTION, "Group workout");
//        values.put(Events.CALENDAR_ID, calID);
//        values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
        
        ContentValues contentValues = new ContentValues();
        // obligated
        contentValues.put(Events.TITLE, e.getTitle());
        contentValues.put(Events.CALENDAR_ID, e.getCalendarId());
        contentValues.put(Events.DTSTART, e.getStartTimeMillis());
        contentValues.put(Events.DTEND, e.getEndTimeMillis());
        contentValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        // default-able
        String description = e.getDescription();
        if (description != null) {
            contentValues.put(Events.DESCRIPTION, description);
        }
        String eventLocation = e.getEventLocation();
        if (eventLocation != null) {
            contentValues.put(Events.EVENT_LOCATION, eventLocation);
        }
        int allDay = e.getAllDay();
        if (allDay >= 0) {
            contentValues.put(Events.ALL_DAY, allDay);
        }
        int eventStatus = e.getEventStatus();
        if (eventStatus >= 0) {
            contentValues.put(Events.STATUS, eventStatus);
        }
        int visibility = e.getVisibility();
        if (visibility >= 0) {
            contentValues.put(Events.VISIBLE, visibility);
        }
        int hasAlarm = e.getHasAlarm();
        if (hasAlarm >= 0) {
            contentValues.put(Events.HAS_ALARM, hasAlarm);
        }
        return contentValues;
    }
}
