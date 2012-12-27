package eu.tjenwellens.timetracker.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
            removeActiviteit(ap);
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
        radioButtonChecked(a);

    }

    private void initMainGUI()
    {
        setContentView(R.layout.main);
        initGUIButtons();
    }

    public void radioButtonChecked(ActiviteitPanel a)
    {
        for (ActiviteitPanel ap : activiteiten)
        {
            ap.uncheckRadioButton();
        }
        if (a != null)
        {
            a.checkRadioButton();
            setORresetSelectionMade(a);
        }
    }

    private void initGUIButtons()
    {
        Button details = (Button) findViewById(R.id.btnMainDetails);
        details.setEnabled(false);
        setORresetSelectionMade(currentActiviteit);
    }

    private void setORresetSelectionMade(ActiviteitPanel a)
    {
        if (a == null && !activiteiten.isEmpty())
        {
            a = activiteiten.get(activiteiten.size() - 1);
        }
        Button detail = (Button) findViewById(R.id.btnMainDetails);
        Button cancel = (Button) findViewById(R.id.btnMainDelete);
        Button resume = (Button) findViewById(R.id.btnMainResume);
        if (a == null)
        {
            detail.setEnabled(false);
            cancel.setEnabled(false);
            resume.setEnabled(false);
        } else
        {
            detail.setEnabled(true);
            cancel.setEnabled(true);
            resume.setEnabled(!a.isRunning());
        }
        currentActiviteit = a;
    }

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
        removeActiviteit(a);
    }

    @Override
    public void activiteitEdit(ActiviteitPanel a)
    {
        final Dialog dialog = new DialogActiviteit(this, a);
        dialog.show();
    }

    public void activiteitStop(ActiviteitPanel a)
    {
        if (a == currentActiviteit)
        {
            // update resume button
            setORresetSelectionMade(a);
        }
    }

    private void removeActiviteit(ActiviteitPanel a)
    {
        // remove activiteit
        if (activiteiten.remove(a))
        {
            // stop -> GUI
            final ViewGroup activiteitContainer = ((ViewGroup) findViewById(R.id.pnlMainActiviteiten));
            activiteitContainer.removeView(a);
        }

        // reset selection
        if (currentActiviteit == a)
        {
            setORresetSelectionMade(null);
        }
    }

    public void btnMainDetails(View button)
    {
        startDetail(currentActiviteit);
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

    public void btnMainMacros(View button)
    {
        Intent i = new Intent(this, MacroActivity.class);
        startActivityForResult(i, ActivityResults.MACRO_START);
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
                    this.currentActiviteit.setDescription(details);
                } else
                {
                    Toast.makeText(this, "updating activity failed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void btnMainDelete(View button)
    {// cancel selected activiteit
        ActiviteitPanel a = currentActiviteit;
        if (a != null)
        {
            removeActiviteit(a);
            a.deleteDBActiviteit(this);
        }
    }

    public void btnMainResume(View button)
    {// resume selected activiteit
        ActiviteitI a = currentActiviteit;
        if (a != null)
        {
            a.resumeRunning();
            // update resume button
            setORresetSelectionMade(currentActiviteit);
        }
    }

    public void btnMainAdd(View button)
    {// start new activiteit
        addActiviteit(new ActiviteitPanel(this, this));
    }

    private boolean updateActivity(ActiviteitI a)
    {
        if (a == null)
        {
            return false;
        }
        int id = a.getActiviteitId();
        for (ActiviteitPanel ap : activiteiten)
        {
            if (ap.getActiviteitId() == id)
            {
                ap.updateActiviteit(a);
                return true;
            }
        }
        return false;
    }
}
