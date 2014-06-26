package eu.tjenwellens.timetracker.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 *
 * @author Tjen
 */
public class Pre4Overlay extends VersionedCalendarOverlay {
    public static final String CONTENT_EVENTS = "content://com.android.calendar/events";
    public static final String CONTENT_CALENDARS = "content://com.android.calendar/calendars";
    private Context context;

    public Pre4Overlay(Context context) {
        this.context = context;
    }

    @Override
    public Kalender[] retrieveKalenders() {
        Kalender[] kalenders = null;
        Cursor cursor;
        String projection[] = new String[2];
        projection[0] = "_id";
        projection[1] = "displayName";
        String args = null;
//        if (currentCalendarsOnly) {
//            args = "selected=1";
//        }
        cursor = context.getContentResolver().query(Uri.parse(CONTENT_CALENDARS), projection, args, null, null);

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

    private static Kalender[] arraysToKalenders(String[] names, int[] ids) {
        Kalender[] calendars = new Kalender[Math.min(names.length, ids.length)];
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = new Kalender(names[i], ids[i]);
        }
        return calendars;
    }

    @Override
    public void post(Evenement e) {
        context.getContentResolver().insert(Uri.parse(CONTENT_EVENTS), contentValues(e));
    }
// hard-coded text
    public static final String TITLE = "title";
    public static final String CALENDAR_ID = "calendar_id";
    public static final String DESCRIPTION = "description";
    public static final String EVENT_LOCATION = "eventLocation";
    public static final String START = "dtstart";
    public static final String END = "dtend";
    public static final String ALL_DAY = "allDay";
    public static final String EVENT_STATUS = "eventStatus";
    public static final String VISIBILITY = "visibility";
    public static final String TRANSPARANCY = "transparency";
    public static final String HAS_ALARM = "hasAlarm";

    private static ContentValues contentValues(Evenement e) {
        ContentValues contentValues = new ContentValues();
        // obligated
        contentValues.put(TITLE, e.getTitle());
        contentValues.put(CALENDAR_ID, e.getCalendarId());
        contentValues.put(START, e.getStartTimeMillis());
        contentValues.put(END, e.getEndTimeMillis());
        // default-able
        String description = e.getDescription();
        if (description != null) {
            contentValues.put(DESCRIPTION, description);
        }
        String eventLocation = e.getEventLocation();
        if (eventLocation != null) {
            contentValues.put(EVENT_LOCATION, eventLocation);
        }
        int allDay = e.getAllDay();
        if (allDay >= 0) {
            contentValues.put(ALL_DAY, allDay);
        }
        int eventStatus = e.getEventStatus();
        if (eventStatus >= 0) {
            contentValues.put(EVENT_STATUS, eventStatus);
        }
        int visibility = e.getVisibility();
        if (visibility >= 0) {
            contentValues.put(VISIBILITY, visibility);
        }
        int transparency = e.getTransparency();
        if (transparency >= 0) {
            contentValues.put(TRANSPARANCY, transparency);
        }
        int hasAlarm = e.getHasAlarm();
        if (hasAlarm >= 0) {
            contentValues.put(HAS_ALARM, hasAlarm);
        }
        return contentValues;
    }
}
