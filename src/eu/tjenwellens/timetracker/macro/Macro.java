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
public class Macro implements MacroI
{

    private static int idCounter;
    private int id;
    private String title;
    private Kalender kalender;

    public Macro(String title, Kalender kalender)
    {
        idCounter++;
        this.id = idCounter;
        this.title = title;
        this.kalender = kalender;
    }

    public Macro(int id, String title, Kalender kalender)
    {
        this.id = id;
        if (idCounter < id) {
            idCounter = id;
        }
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

    public int getID()
    {
        return this.id;
    }
}
