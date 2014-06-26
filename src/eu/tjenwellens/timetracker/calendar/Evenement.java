/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.calendar;

import android.content.Context;
import eu.tjenwellens.timetracker.TimeTrackerApplication;

/**
 *
 * @author tjen
 */
public class Evenement {
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

    public Evenement(int calendarId, String title, long startTimeMillis, long endTimeMillis) {
        this.calendarId = calendarId;
        this.title = title;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
    }

    public static void post(Context c, Evenement e) {
        ((TimeTrackerApplication) c.getApplicationContext()).getVco().post(e);
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public void setHasAlarm(int hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    int getCalendarId() {
        return calendarId;
    }

    String getTitle() {
        return title;
    }

    long getStartTimeMillis() {
        return startTimeMillis;
    }

    long getEndTimeMillis() {
        return endTimeMillis;
    }

    String getDescription() {
        return description;
    }

    String getEventLocation() {
        return eventLocation;
    }

    int getAllDay() {
        return allDay;
    }

    int getEventStatus() {
        return eventStatus;
    }

    int getVisibility() {
        return visibility;
    }

    int getTransparency() {
        return transparency;
    }

    int getHasAlarm() {
        return hasAlarm;
    }
}
