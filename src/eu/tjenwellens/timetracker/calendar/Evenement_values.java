/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.calendar;

/**
 *
 * @author tjen
 */
public interface Evenement_values
{

    // allDay: ?gegokt?
    public static final int ALL_DAY_TRUE = 1;
    public static final int ALL_DAY_FALSE = 0;
    // eventStatus: 0~ tentative; 1~ confirmed; 2~ canceled
    public static final int EVENT_STATUS_TENTATIVE = 0;
    public static final int EVENT_STATUS_CONFIRMED = 1;
    public static final int EVENT_STATUS_CANCELLED = 2;
    // visibility: 0~ default; 1~ confidential; 2~ private; 3~ public
    public static final int VISIBILITY_DEFAULT = 0;
    public static final int VISIBILITY_CONFIDENTIAL = 1;
    public static final int VISIBILITY_PRIVATE = 2;
    public static final int VISIBILITY_PUBLIC = 3;
    // transparancy: 0~ opaque, no timing conflict is allowed; 1~ transparency, allow overlap of scheduling
    public static final int TRANSPARANCY_NO_OVERLAP = 0;
    public static final int TRANSPARANCY_OVERLAP = 1;
    // hasAlarm: 0~ false; 1~ true
    public static final int HAS_ALARM_TRUE = 1;
    public static final int HAS_ALARM_FALSE = 0;
}
