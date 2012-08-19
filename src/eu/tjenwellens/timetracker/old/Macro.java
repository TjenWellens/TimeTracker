/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.old;

import android.content.Context;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.macro.MacroI;

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

    public int getId()
    {
        return this.id;
    }

    public void deleteDBMacro(Context context)
    {
        DatabaseHandler.getInstance(context).deleteMacro(this);
    }

    public void saveMacro(Context context)
    { //TODO: call on create
        DatabaseHandler.getInstance(context).addMacro(this);
    }

    public void updateDBMacro(Context context)
    {
        DatabaseHandler.getInstance(context).updateMacro(this);
    }
}
