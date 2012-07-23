/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author tjen
 */
public interface ActiviteitI
{

    boolean stopRunning();

    boolean resumeRunning();

    Evenement getEvenement();

    void setKalender(Kalender kalender);

    void setEndTimeMillis(long endTimeMillis);

    void setStartTimeMillis(long startTimeMillis);

    void setActiviteitTitle(String title);

    void setDescription(String description);

    String getDuration();

    String getKalenderName();

    String getStartTime();

    String getStopTime();

    String getActiviteitTitle();

    long getActiviteitId();

    boolean isRunning();

    String getDescription();
}
