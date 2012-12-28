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
        setContentView(R.layout.macro);
        reloadMacros();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void reloadMacros()
    {
        if (macroButtonPanel == null)
        {// init new panel
            final LinearLayout macroContainer = (LinearLayout) findViewById(R.id.pnlMacro);
            macroButtonPanel = new MacroButtonPanel(this, this);
            macroContainer.addView(macroButtonPanel);
        } else
        {// reset current panel
            macroButtonPanel.reset();
//            final LinearLayout macroContainer = (LinearLayout) findViewById(R.id.pnlMacro);
//            macroContainer.removeAllViews();
//            macroButtonPanel = null;
        }
        // add to panel
        macroButtonPanel.addAllButtons(loadDBMacros(this));
    }

    public static List<MacroI> loadDBMacros(Context context)
    {
        return DatabaseHandler.getInstance(context).getAllMacros();
    }

    @Override
    public void launchActiviteit(MacroI macro)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MACRO_TITLE, macro.getActiviteitTitle());
        returnIntent.putExtra(MACRO_CALENDAR, macro.getKalenderName());
        setResult(RESULT_OK, returnIntent);
        finish();
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
        startActivityForResult(i, ActivityResults.MACRO_DETAILS_START);
    }

    /*
     * 
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == ActivityResults.MACRO_DETAILS_START)
        {
            reloadMacros();
        }
    }

    /*
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.macro_menu, menu);
        return true;
    }

    /*
     * Handle menu
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
}
