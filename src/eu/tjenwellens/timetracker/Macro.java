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
public class Macro implements MacroI
{

    private String title;
    private Kalender kalender;

    public Macro(String title, Kalender kalender)
    {
        this.title = title;
        this.kalender = kalender;
    }

    public Kalender getKalender()
    {
        return kalender;
    }

    public void setKalender(Kalender kalender)
    {
        this.kalender = kalender;
    }

    public String getActiviteitTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getKalenderName()
    {
        String kalName = "";
        if (kalender != null) {
            kalName = kalender.getName();
        }
        return kalName;
    }
}
