/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro;

import android.content.Context;
import android.widget.Button;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author Tjen
 */
public class MacroButton extends Button implements MacroI
{

    private MacroI macro;

    public MacroButton(Context context, MacroI macro)
    {
        super(context);
        this.macro = macro;
        updateText();
    }

    public Kalender getKalender()
    {
        return macro.getKalender();
    }

    public void setKalender(Kalender kalender)
    {
        macro.setKalender(kalender);
        updateText();
    }

    public String getActiviteitTitle()
    {
        return macro.getActiviteitTitle();
    }

    public void setTitle(String title)
    {
        macro.setTitle(title);
        updateText();
    }

    private void updateText()
    {
        this.setText(macro.getActiviteitTitle() + "\n" + macro.getKalenderName());
    }

    public String getKalenderName()
    {
        return macro.getKalenderName();
    }

    public int getID()
    {
        return macro.getID();
    }
}
