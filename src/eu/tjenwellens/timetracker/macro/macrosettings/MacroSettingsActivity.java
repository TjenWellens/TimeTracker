/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro.macrosettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.macro.MacroActivity;
import eu.tjenwellens.timetracker.macro.MacroFactory;
import eu.tjenwellens.timetracker.macro.MacroI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tjen
 */
public class MacroSettingsActivity extends Activity implements MacroSettingsHandler
{
    private List<MacroSettingsPanel> macroPanels = new ArrayList<MacroSettingsPanel>();
    private Kalender selectedKalender;
    private OnItemSelectedListener spinnerListener = new OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            Object selected = parent.getItemAtPosition(pos);
            if (selected instanceof Kalender)
            {
                selectedKalender = (Kalender) selected;
            } else
            {
                Toast.makeText(MacroSettingsActivity.this, "Failed to recognize  calender", Toast.LENGTH_LONG).show();
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            selectedKalender = null;
            Toast.makeText(MacroSettingsActivity.this, "No selection made", Toast.LENGTH_LONG).show();
        }
    };
    // save when shutdown improper
    private boolean saveOnExit = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        List<MacroI> macros = MacroActivity.intentToMacros(this, getIntent(), ActivityResults.KEY_MACRO_SETTINGS);
//        List<MacroI> macros = MacroActivity.loadDBMacros(this);
        // GUI
        initMacroSettingsGUI(MacroActivity.loadDBMacros(this));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (saveOnExit)
        {
            save();
        }
    }

    private void exit(boolean save)
    {
        if (save)
        {
            save();
        }
        this.saveOnExit = false;
        finish();
    }

    private void save()
    {
        Toast.makeText(this, "SAVE", Toast.LENGTH_SHORT).show();
        MacroActivity.saveMacros(this, new ArrayList<MacroI>(macroPanels));
        setResult(RESULT_OK, new Intent());
    }

    private void addMacro(MacroI m)
    {
        final LinearLayout macroSettingsContainer = (LinearLayout) findViewById(R.id.pnlMacroSettings);
        MacroSettingsPanel mp = new MacroSettingsPanel(this, this, m);
        macroSettingsContainer.addView(mp);
        macroPanels.add(mp);
    }

    public void removeMacro(MacroSettingsPanel m)
    {
        final LinearLayout macroSettingsContainer = (LinearLayout) findViewById(R.id.pnlMacroSettings);
        macroSettingsContainer.removeView(m);
        macroPanels.remove(m);
        m.deleteDBMacro(this);
    }

    private void initMacroSettingsGUI(List<MacroI> macros)
    {
        setContentView(R.layout.macro_settings);
        initMacros(macros);
        initSpinner();
    }

    private void initMacros(List<MacroI> macros)
    {
        final LinearLayout macroSettingsContainer = (LinearLayout) findViewById(R.id.pnlMacroSettings);
        macroSettingsContainer.removeAllViews();
        macroPanels.clear();
        if (macros == null)
        {
            return;
        }
        addAllMacros(macros);
    }

    private void addAllMacros(List<MacroI> macros)
    {
        for (MacroI macro : macros)
        {
            addMacro(macro);
        }
    }

    private void initSpinner()
    {
        Kalender[] kalenders = Kalender.retrieveKalenders(this);
        if (kalenders == null)
        {
            Toast.makeText(this, "Error initializing calendars", Toast.LENGTH_LONG).show();
            return;
        }
        this.selectedKalender = kalenders[0];

        Spinner spinner = (Spinner) findViewById(R.id.spinnerMacroSettingsCalendar);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, kalenders);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add listener
        spinner.setOnItemSelectedListener(spinnerListener);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        // save when backing
        exit(true);
//        super.onBackPressed();
    }

    public void deleteMacro(MacroSettingsPanel macroSettingsPanel)
    {
        removeMacro(macroSettingsPanel);
    }

    public void updateMacro(MacroI macro)
    {
        DatabaseHandler.getInstance(this).updateMacro(macro);
    }

    public void btnMacroSettingsClear(View button)
    {
        final EditText txtTitle = (EditText) findViewById(R.id.txtMacroSettingsTitle);
        txtTitle.setText(R.string.none);
    }

    public void btnMacroSettingsAdd(View button)
    {
        final EditText txtTitle = (EditText) findViewById(R.id.txtMacroSettingsTitle);
        String title = txtTitle.getText().toString();
        MacroI m = MacroFactory.createMacro(this, title, selectedKalender);
        addMacro(m);
        // clear text
        txtTitle.setText(R.string.none);
    }

    public void btnMacroSettingsSave(View button)
    {
//        Intent i = new Intent();
////        MacroActivity.macrosToIntent(i, new ArrayList<MacroI>(macroPanels), ActivityResults.KEY_MACRO_SETTINGS);
//        MacroActivity.saveMacros(this, new ArrayList<MacroI>(macroPanels));
//        setResult(RESULT_OK, i);
        launchSave();
    }

    public void btnMacroSettingsCancel(View button)
    {
        launchCancel();
    }

    private void launchSave()
    {
        exit(true);
    }

    private void launchCancel()
    {
        // dont save macro's
        setResult(RESULT_CANCELED, new Intent());
        exit(false);
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.macro_settings_menu, menu);
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
            case R.id.menu_save:
                launchSave();
                return true;
            case R.id.menu_cancel:
                launchCancel();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
