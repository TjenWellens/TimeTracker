/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro;

import android.content.Context;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author Tjen
 */
public interface MacroI
{

    Kalender getKalender();

    String getKalenderName();

    int getId();

    void setKalender(Kalender kalender);

    String getActiviteitTitle();

    void setTitle(String title);

    void deleteDBMacro(Context context);

    void updateDBMacro(Context context);
}
