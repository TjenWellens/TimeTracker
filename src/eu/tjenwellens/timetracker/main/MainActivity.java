package eu.tjenwellens.timetracker.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import eu.tjenwellens.timetracker.ActivityResults;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.detail.DetailActivity;
import eu.tjenwellens.timetracker.macro.MacroActivity;
import eu.tjenwellens.timetracker.macro.MacroFactory;
import eu.tjenwellens.timetracker.macro.MacroI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tjen
 */
public class MainActivity extends Activity implements ActiviteitHandler
{
    private ActiviteitPanel currentActiviteit = null;
    private ArrayList<ActiviteitPanel> activiteiten = new ArrayList<ActiviteitPanel>();
    // <editor-fold defaultstate="collapsed" desc="Folding">
    // </editor-fold>

    /*
     * Adds an activiteit to the GUI
     * TODO?: add to database? (vs activiteit zelf die db afhandelt)
     */
    private void addActiviteit(ActiviteitPanel a)
    {
        // add to list
        activiteiten.add(a);
        // add to gui
        final ViewGroup activiteitContainer = ((ViewGroup) findViewById(R.id.pnlMainActiviteiten));
        activiteitContainer.addView(a);
        a.setLongClickable(true);
        registerForContextMenu(a);
    }

    /*
     * Deletes activiteit completely(GUI, DB) if not null
     */
    private void deleteActiviteit(ActiviteitPanel a)
    {
        if (a != null)
        {
            removeActiviteitfromGUI(a);
            // remove from GUI
            a.deleteDBActiviteit(this);
        }
    }

    /*
     * removes an activity from the GUI
     */
    private void removeActiviteitfromGUI(ActiviteitPanel a)
    {
        // remove activiteit
        if (activiteiten.remove(a))
        {
            // stop -> GUI
            final ViewGroup activiteitContainer = ((ViewGroup) findViewById(R.id.pnlMainActiviteiten));
            activiteitContainer.removeView(a);
            unregisterForContextMenu(a);
            a.setClickable(false);
        }
    }

    /*
     * Resumes activiteit
     */
    private void resumeActiviteit(ActiviteitPanel a)
    {
        if (a != null)
        {
            a.resumeRunning();
        }
    }

    /*
     * Handles the add button, start new activiteit
     */
    public void btnMainAdd(View button)
    {
        addActiviteit(new ActiviteitPanel(this, this));
    }

    // <editor-fold defaultstate="collapsed" desc="Activity switching">
    /*
     * Starts Edit Activity
     */
    private void launchEdit(ActiviteitPanel a)
    {
        final Dialog dialog = new DialogActiviteit(this, a);
        dialog.show();
    }

    /*
     * Starts Macro Activity
     */
    private void launchMacros()
    {
        Intent i = new Intent(this, MacroActivity.class);
        startActivityForResult(i, ActivityResults.MACRO_START);
    }

    /*
     * Starts Detail Activity
     */
    private void launchDetail(ActiviteitPanel a)
    {
        currentActiviteit = a;
        // TODO: fix cross activity data
        Intent intent = new Intent(this, DetailActivity.class);
        arrayToIntent(intent, a.getDescriptionEntries(), ActivityResults.KEY_ACTIVITEIT_DETAIL);
        startActivityForResult(intent, ActivityResults.DETAIL_START);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Database saving, loading">
    /*
     * Saves all activiteiten
     */
    private void saveActiviteiten()
    {// TODO: should not be nessecary because everything is saved on the go
        // save activiteiten
        List<ActiviteitPanel> an = new ArrayList<ActiviteitPanel>(activiteiten);
        for (ActiviteitPanel ap : an)
        {
            ap.updateDBActiviteit(this);
            removeActiviteitfromGUI(ap);
        }
    }

    /*
     * Loads all activiteiten
     */
    private void loadActiviteiten()
    {
        // load activiteiten from database
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (ActiviteitI activiteit : dbh.getAllActiviteiten())
        {
            addActiviteit(new ActiviteitPanel(this, this, activiteit));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ActviteitHandler implementation">
    /*
     * Sends activiteit to Calendar
     */
    @Override
    public void saveActiviteit(ActiviteitPanel a)
    {
        // only save if activiteit has a calendar
        if (a.getKalenderName() == null)
        {
            return;
        }
        // create evenement
        Evenement e = a.getEvenement();
        if (e == null)
        {
            return;
        }
        // upload evenement to calendar
        Evenement.post(this, e);
        // remove activiteit
        deleteActiviteit(a);
    }

    private void launchContextMenu(ActiviteitPanel a)
    {
        currentActiviteit = a;
        // start context menu
        openContextMenu(a);
    }

    /*
     * Called when the activiteitpanel is clicked
     */
    @Override
    public void shortClick(ActiviteitPanel a)
    {
        launchDetail(a);
    }

    /*
     * opens context menu and saves the panel it was called from
     */
    @Override
    public void longClick(ActiviteitPanel a)
    {
        launchContextMenu(a);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Detail Activity Communication">
    /*
     * Starts the DetailActivity
     */
    public static void arrayToIntent(Intent intent, String[] array, String key)
    {
        if (array == null)
        {
            intent.putExtra(key + "_amount", 0);
            return;
        }
        intent.putExtra(key + "_amount", array.length);
        for (int i = 0; i < array.length; i++)
        {
            intent.putExtra(key + i, array[i]);
        }
    }

    public static String[] intentToArray(Intent intent, String key)
    {
        int amount = intent.getIntExtra(key + "_amount", -1);
        if (amount <= 0)
        {
            // error
            return null;
        }
        String[] array = new String[amount];
        for (int i = 0; i < amount; i++)
        {
            array[i] = intent.getStringExtra(key + i);
        }
        return array;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Activity Override code">
    /*
     * Creates the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // GUI
        setContentView(R.layout.main);
        // Initialize preferences
        loadActiviteiten();
    }

    /*
     * Handles window rotation
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        // catch window rotation
        super.onConfigurationChanged(newConfig);
    }

    /*
     * Creates menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
     * Creates context menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    /*
     * Handles menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_settings:
                // TODO: open settings
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_macros:
                launchMacros();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Handles context-menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (currentActiviteit == null)
        {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId())
        {
            // Edit
            case R.id.menu_edit:
                launchEdit(currentActiviteit);
                return true;

            // Resume
            case R.id.menu_resume:
                resumeActiviteit(currentActiviteit);
                return true;

            // Details
            case R.id.menu_details:
                launchDetail(currentActiviteit);
                return true;

            // Delete
            case R.id.menu_delete:
                deleteActiviteit(currentActiviteit);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Handles input from other activities that were lauched: Macros, Details 
     * TODO: Edit general activiteit settings
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == ActivityResults.MACRO_START)
            {
                // start the choosen macro
                String title = data.getStringExtra(MacroActivity.MACRO_TITLE);
                String calendar = data.getStringExtra(MacroActivity.MACRO_CALENDAR);
                MacroI m = MacroFactory.loadMacro(this, -1, title, Kalender.getKalenderByName(this, calendar));
                addActiviteit(new ActiviteitPanel(this, this, m));
            } else if (requestCode == ActivityResults.DETAIL_START)
            {
                // update detail
                String[] details = intentToArray(data, ActivityResults.KEY_ACTIVITEIT_DETAIL);
                if (details != null)
                {
                    this.currentActiviteit.setDescription(details);
                } else
                {
                    Toast.makeText(this, "updating activity failed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*
     * Destroys the activity
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveActiviteiten();
    }
    // </editor-fold>
}
