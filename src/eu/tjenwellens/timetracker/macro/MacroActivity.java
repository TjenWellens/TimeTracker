/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import eu.tjenwellens.timetracker.ActivityResults;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.macro.macrosettings.MacroSettingsActivity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tjen
 */
public class MacroActivity extends Activity implements MacroHandler
{

    public static int MACRO_START = 659244;
    public static String MACRO_TITLE = "macro_title";
    public static String MACRO_CALENDAR = "macro_calendar";
    private MacroButtonPanel macroButtonPanel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // GUI
        initMacroGUI(loadMacros());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveMacros();
    }

    private void initMacros(List<MacroI> macros)
    {
        macroButtonPanel.reset();
        macroButtonPanel.addAllButtons(macros);
    }

    private void initMacroGUI(List<MacroI> macros)
    {
        setContentView(R.layout.macro);
        final LinearLayout macroContainer = (LinearLayout) findViewById(R.id.pnlMacro);
        if (macroButtonPanel == null) {
            macroButtonPanel = new MacroButtonPanel(this, this);
            macroContainer.addView(macroButtonPanel);
        }
        initMacros(macros);
    }

    private List<MacroI> loadMacros()
    {
        // load macros
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        List<MacroI> macros = dbh.getAllMacros();
        dbh.clearMacros();
        return macros;
    }

    private void saveMacros()
    {
        // save macros
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (MacroI macro : macroButtonPanel.getMacros()) {
            dbh.addMacro(macro);
        }
    }

    @Override
    public void startActiviteit(MacroI macro)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MACRO_TITLE, macro.getActiviteitTitle());
        returnIntent.putExtra(MACRO_CALENDAR, macro.getKalenderName());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void btnMacroBack(View button)
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void btnMacroAdd(View button)
    {
        Intent i = new Intent(this, MacroSettingsActivity.class);
        macrosToIntent(i, macroButtonPanel.getMacros(), ActivityResults.KEY_MACRO_SETTINGS);
        startActivityForResult(i, ActivityResults.MACRO_DETAILS_START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == ActivityResults.MACRO_DETAILS_START) {
            List<MacroI> macros = intentToMacros(this, data, ActivityResults.KEY_MACRO_SETTINGS);
            initMacros(macros);
        }
    }

    public static void macrosToIntent(Intent intent, List<MacroI> macros, String key)
    {
        if (macros == null) {
            intent.putExtra(key + "_amount", 0);
            return;
        }
        intent.putExtra(key + "_amount", macros.size());
        for (int i = 0; i < macros.size(); i++) {
            intent.putExtra(key + "title" + i, macros.get(i).getActiviteitTitle());
            intent.putExtra(key + "kalender" + i, macros.get(i).getKalenderName());
        }
    }

    public static List<MacroI> intentToMacros(Context c, Intent intent, String key)
    {
        int amount = intent.getIntExtra(key + "_amount", -1);
        if (amount <= 0) {
            // error
            return null;
        }
        List<MacroI> macros = new ArrayList<MacroI>(amount);
        for (int i = 0; i < amount; i++) {
            String title = intent.getStringExtra(key + "title" + i);
            Kalender k = Kalender.getKalenderByName(c, intent.getStringExtra(key + "kalender" + i));
            macros.add(new Macro(title, k));
        }
        return macros;
    }
}
