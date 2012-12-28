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
//    private ActiviteitPanel currentActiviteit = null;
    private ActiviteitPanel currentContextActiviteit = null;
    private ArrayList<ActiviteitPanel> activiteiten = new ArrayList<ActiviteitPanel>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // GUI
        initMainGUI();
        // Initialize preferences
        loadActiviteiten();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveActiviteiten();
    }

    private void saveActiviteiten()
    {
        // save activiteiten
        List<ActiviteitPanel> an = new ArrayList<ActiviteitPanel>(activiteiten);
//        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (ActiviteitPanel ap : an)
        {
//            dbh.addActiviteit(ap);
            ap.updateDBActiviteit(this);
//            ap.deleteDBActiviteit(this);
            removeActiviteitGUI(ap);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {// window rotated
        super.onConfigurationChanged(newConfig);
    }

    private void loadActiviteiten()
    {
        // load activiteiten, remove activiteiten from database
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (ActiviteitI activiteit : dbh.getAllActiviteiten())
        {
            addActiviteit(new ActiviteitPanel(this, this, activiteit));
        }
//        dbh.clearActiviteiten();
    }

    private void addActiviteit(ActiviteitPanel a)
    {
        // add to list
        activiteiten.add(a);
        // add to gui
        final ViewGroup activiteitContainer = ((ViewGroup) findViewById(R.id.pnlMainActiviteiten));
        activiteitContainer.addView(a);
        // check the new radiobutton
//        radioButtonChecked(a);
        a.setLongClickable(true);
        registerForContextMenu(a);
    }

    private void initMainGUI()
    {
        setContentView(R.layout.main);
        initGUIButtons();
    }

//    public void radioButtonChecked(ActiviteitPanel a)
//    {
//        for (ActiviteitPanel ap : activiteiten)
//        {
//            ap.uncheckRadioButton();
//        }
//        if (a != null)
//        {
//            a.checkRadioButton();
//            setORresetSelectionMade(a);
//        }
//    }
    private void initGUIButtons()
    {
//        Button details = (Button) findViewById(R.id.btnMainDetails);
//        details.setEnabled(false);
//        setORresetSelectionMade(currentActiviteit);
    }

//    private void updateCurrentActiviteit()
//    {
//        if (!activiteiten.isEmpty())
//        {
//            currentActiviteit = activiteiten.get(activiteiten.size() - 1);
//        }
////        Button detail = (Button) findViewById(R.id.btnMainDetails);
////        Button cancel = (Button) findViewById(R.id.btnMainDelete);
////        Button resume = (Button) findViewById(R.id.btnMainResume);
////        if (a == null)
////        {
//////            detail.setEnabled(false);
////            cancel.setEnabled(false);
////            resume.setEnabled(false);
////        } else
////        {
//////            detail.setEnabled(true);
////            cancel.setEnabled(true);
////            resume.setEnabled(!a.isRunning());
////        }
////        currentActiviteit = a;
//    }

    @Override
    public void activiteitSave(ActiviteitPanel a)
    {
        // handle all UI except the Activiteitpanel
        if (a.getKalenderName() == null)
        {
            return;
        }
        Evenement e = a.getEvenement();
        if (e == null)
        {
            return;
        }
        Evenement.post(this, e);
        removeActiviteitGUI(a);
    }

    @Override
    public void activiteitEdit(ActiviteitPanel a)
    {
        final Dialog dialog = new DialogActiviteit(this, a);
        dialog.show();
    }

//    public void activiteitStop(ActiviteitPanel a)
//    {
//        if (a == currentActiviteit)
//        {
//            // update resume button
//            setORresetSelectionMade(a);
//        }
//    }

    private void removeActiviteitGUI(ActiviteitPanel a)
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

        // reset selection
//        if (currentActiviteit == a)
//        {
//            updateCurrentActiviteit();
//        }
    }

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

    private void startDetail(ActiviteitI a)
    {
        // TODO: fix cross activity data
        Intent intent = new Intent(this, DetailActivity.class);
        arrayToIntent(intent, a.getDescriptionEntries(), ActivityResults.KEY_ACTIVITEIT_DETAIL);
        startActivityForResult(intent, ActivityResults.DETAIL_START);
    }

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
                MacroI m = MacroFactory.createMacro(this, title, Kalender.getKalenderByName(this, calendar));
                addActiviteit(new ActiviteitPanel(this, this, m));
            } else if (requestCode == ActivityResults.DETAIL_START)
            {
                // update detail
                String[] details = intentToArray(data, ActivityResults.KEY_ACTIVITEIT_DETAIL);
                if (details != null)
                {
                    this.currentContextActiviteit.setDescription(details);
                } else
                {
                    Toast.makeText(this, "updating activity failed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//    public void btnMainDelete(View button)
//    {// cancel selected activiteit
//        deleteActiviteit(currentActiviteit);
//    }

    /*
     * removes activiteit completely if not null
     * from GUI
     * from Database
     */
    private void deleteActiviteit(ActiviteitPanel a)
    {
        if (a != null)
        {
            removeActiviteitGUI(a);
            a.deleteDBActiviteit(this);
        }
    }

    /*
     * Resumes activiteit if not null
     */
    private void resumeActiviteit(ActiviteitPanel a)
    {
        if (a != null)
        {
            a.resumeRunning();
        }
    }

//    public void btnMainResume(View button)
//    {
//        // resume selected activiteit
//        resumeActiviteit(currentActiviteit);
////        if (currentActiviteit != null)
////        {
////            // update resume button
//////            updateCurrentActiviteit(currentActiviteit);
////        }
//    }

    public void btnMainAdd(View button)
    {// start new activiteit
        addActiviteit(new ActiviteitPanel(this, this));
    }
//
//    public void btnMainMacros(View button)
//    {
//        launchMacros();
//    }

    private void launchMacros()
    {
        Intent i = new Intent(this, MacroActivity.class);
        startActivityForResult(i, ActivityResults.MACRO_START);
    }

    private void launchDetail(ActiviteitI a)
    {
        startDetail(a);
    }
//
//    public void btnMainDetails(View button)
//    {
//        launchDetails();
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    public void longClick(ActiviteitPanel a)
    {
        currentContextActiviteit = a;
        // start context menu
        openContextMenu(a);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (currentContextActiviteit == null)
        {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId())
        {
            // Edit
            case R.id.menu_edit:
                Toast.makeText(this, "Edit " + currentContextActiviteit.getActiviteitTitle(), Toast.LENGTH_SHORT).show();
                return true;

            // Resume
            case R.id.menu_resume:
                resumeActiviteit(currentContextActiviteit);
                return true;

            // Details
            case R.id.menu_details:
                launchDetail(currentContextActiviteit);
                return true;

            // Delete
            case R.id.menu_delete:
                deleteActiviteit(currentContextActiviteit);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    
//    // Initiating Menu XML file (menu.xml)
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu)
//    {
//        if (isFinalized)
//        {
//            menu.getItem(1).setEnabled(false);
//        }
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            // TODO: remove details from main menu
//            case R.id.menu_details:
//                launchDetail(currentActiviteit);
//                return true;
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
//    private boolean updateActivity(ActiviteitI a)
//    {
//        if (a == null)
//        {
//            return false;
//        }
//        int id = a.getActiviteitId();
//        for (ActiviteitPanel ap : activiteiten)
//        {
//            if (ap.getActiviteitId() == id)
//            {
//                ap.updateActiviteit(a);
//                return true;
//            }
//        }
//        return false;
//    }
}
