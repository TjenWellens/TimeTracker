/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro;

import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author Tjen
 */
public interface MacroI
{

    public Kalender getKalender();

    public String getKalenderName();

    public int getID();

    public void setKalender(Kalender kalender);

    public String getActiviteitTitle();

    public void setTitle(String title);
}
