/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import eu.tjenwellens.timetracker.ActivityResults;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.macro.macrosettings.MacroSettingsActivity;
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
        initMacroGUI(loadDBMacros(this));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveMacros(this, macroButtonPanel.getMacros());
    }

    private void initMacros(List<MacroI> macros)
    {
        macroButtonPanel.addAllButtons(macros);
    }

    private void reloadMacros()
    {
        if (macroButtonPanel != null)
        {
            macroButtonPanel.reset();
            final LinearLayout macroContainer = (LinearLayout) findViewById(R.id.pnlMacro);
            macroContainer.removeAllViews();
            macroButtonPanel = null;
        }
        initMacroGUI(loadDBMacros(this));
    }

    private void initMacroGUI(List<MacroI> macros)
    {
        setContentView(R.layout.macro);
        final LinearLayout macroContainer = (LinearLayout) findViewById(R.id.pnlMacro);
        if (macroButtonPanel == null)
        {
            macroButtonPanel = new MacroButtonPanel(this, this);
            macroContainer.addView(macroButtonPanel);
        }
        initMacros(macros);
    }

    public static List<MacroI> loadDBMacros(Context context)
    {
        // load macros
        DatabaseHandler dbh = DatabaseHandler.getInstance(context);
        List<MacroI> macros = dbh.getAllMacros();
//        dbh.clearMacros();
        return macros;
    }

    public static void saveMacros(Context context, List<MacroI> macros)
    {
        // save macros
//        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (MacroI macro : macros)
        {
            macro.updateDBMacro(context);
//            dbh.addMacro(macro);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == ActivityResults.MACRO_DETAILS_START)
        {

            reloadMacros();
//            if (macros == null)
//            {
//                Toast.makeText(this, "Activity result ok, macro's NULL !!!", Toast.LENGTH_LONG).show();
//            } else
//            {
//                Toast.makeText(this, "Activity result ok, macro's not null, length: " + macros.size(), Toast.LENGTH_LONG).show();
//            }
//            initMacroGUI(macros);
//            resetMacros();
//            List<MacroI> macros = intentToMacros(this, data, ActivityResults.KEY_MACRO_SETTINGS);
//            initMacros(macros);
        }
//        else if (resultCode == Activity.RESULT_CANCELED)
//        {
//            Toast.makeText(this, "Activity cancelled ", Toast.LENGTH_LONG).show();
//        } else
//        {
//            Toast.makeText(this, "ERROR wrong resultcode", Toast.LENGTH_LONG).show();
//        }
    }

    public void btnMacroBack(View button)
    {
        launchCancel();
    }

    public void btnMacroAdd(View button)
    {
        launchMacroSettings();
    }

    private void launchCancel()
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void launchMacroSettings()
    {
        Intent i = new Intent(this, MacroSettingsActivity.class);
        saveMacros(this, macroButtonPanel.getMacros());
//        macrosToIntent(i, macroButtonPanel.getMacros(), ActivityResults.KEY_MACRO_SETTINGS);
        startActivityForResult(i, ActivityResults.MACRO_DETAILS_START);
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.macro_menu, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.menu_edit:
                launchMacroSettings();
                return true;
            case R.id.menu_cancel:
                launchCancel();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    public static void macrosToIntent(Intent intent, List<MacroI> macros, String key)
//    {
//        if (macros == null)
//        {
//            intent.putExtra(key + "_amount", 0);
//            return;
//        }
//        intent.putExtra(key + "_amount", macros.size());
//        for (int i = 0; i < macros.size(); i++)
//        {
//            intent.putExtra(key + "title" + i, macros.get(i).getActiviteitTitle());
//            intent.putExtra(key + "kalender" + i, macros.get(i).getKalenderName());
//        }
//    }
//
//    public static List<MacroI> intentToMacros(Context context, Intent intent, String key)
//    {
//        int amount = intent.getIntExtra(key + "_amount", -1);
//        if (amount <= 0)
//        {
//            // error
//            return null;
//        }
//        List<MacroI> macros = new ArrayList<MacroI>(amount);
//        for (int i = 0; i < amount; i++)
//        {
//            String title = intent.getStringExtra(key + "title" + i);
//            Kalender k = Kalender.getKalenderByName(context, intent.getStringExtra(key + "kalender" + i));
//            macros.add(MacroFactory.createMacro(context, title, k));
//        }
//        return macros;
//    }
}
