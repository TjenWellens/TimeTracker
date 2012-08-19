/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro.macrosettings;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.macro.MacroButtonPanel;
import eu.tjenwellens.timetracker.macro.MacroI;

/**
 *
 * @author Tjen
 */
public class MacroSettingsPanel extends LinearLayout implements MacroI
{

    private MacroI macro;
    private MacroSettingsHandler macroSettingsHandler;
    //
    private LinearLayout pnlText;
    private Button delete;
    private TextView tvTitle, tvCalendar;

    public MacroSettingsPanel(Context context, MacroSettingsHandler macroSettingsHandler, MacroI macro)
    {
        super(context);
        this.macroSettingsHandler = macroSettingsHandler;
        this.macro = macro;
        initGUI(context);
    }

    private void initGUI(Context context)
    {
        this.setOrientation(HORIZONTAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);
        setLayoutParams(lp);
        setBackgroundColor(Color.LTGRAY);
        {
            // Text panel
            pnlText = new LinearLayout(context);
            pnlText.setOrientation(VERTICAL);
            pnlText.setOnClickListener(new OnClickListener()
            {

                public void onClick(View arg0)
                {
                    // TODO: edit dialog for macro
                }
            });
            {
                // Title
                tvTitle = new TextView(context);
                tvTitle.setText(macro.getActiviteitTitle());
                pnlText.addView(tvTitle, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                // Calendar
                tvCalendar = new TextView(context);
                tvCalendar.setText(macro.getKalenderName());
                pnlText.addView(tvCalendar, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
            this.addView(pnlText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            // Delete button
            delete = new Button(context);
            delete.setText(R.string.delete);
            delete.setOnClickListener(new OnClickListener()
            {

                public void onClick(View arg0)
                {
                    macroSettingsHandler.deleteMacro(MacroSettingsPanel.this);
                }
            });
            this.addView(delete, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        }
    }

    public Kalender getKalender()
    {
        return macro.getKalender();
    }

    public String getKalenderName()
    {
        return macro.getKalenderName();
    }

    public void setKalender(Kalender kalender)
    {
        macro.setKalender(kalender);
    }

    public String getActiviteitTitle()
    {
        return macro.getActiviteitTitle();
    }

    public void setTitle(String title)
    {
        macro.setTitle(title);
    }

    public int getId()
    {
        return macro.getId();
    }

    public void deleteDBMacro(Context context)
    {
        macro.deleteDBMacro(context);
    }

    public void updateDBMacro(Context context)
    {
        macro.updateDBMacro(context);
    }
}
