/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author Tjen
 */
public interface MacroI
{

    public Kalender getKalender();
    public String getKalenderName();

    public void setKalender(Kalender kalender);

    public String getActiviteitTitle();

    public void setTitle(String title);
}
