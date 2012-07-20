package eu.tjenwellens.timetracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import java.util.ArrayList;
import java.util.List;

public class TimeTrackerActivity extends Activity implements GetContent, ActiviteitHandler, GetKalenders
{/*
     * final Button btnStart = (Button) findViewById(R.id.btnStart), btnStop =
     * (Button) findViewById(R.id.btnStop), btnResume = (Button)
     * findViewById(R.id.btnResume); final TextView txtName = (TextView)
     * findViewById(R.id.txtName), txtCalendar = (TextView)
     * findViewById(R.id.txtCalendar), txtTime = (TextView)
     * findViewById(R.id.txtTime), txtDuration = (TextView)
     * findViewById(R.id.txtDuration);
     */


    public static final String CONTENT_CALENDARS = "content://com.android.calendar/calendars";
    public static final String CONTENT_EVENTS = "content://com.android.calendar/events";
    //
    private LinearLayout scrollPanel;
    private ActiviteitPanel currentSelectedAP = null;
    //
    private List<ActiviteitPanel> activiteiten = new ArrayList<ActiviteitPanel>();
    //
    private Kalender def_cal = null;
    private Kalender[] kalenders = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        scrollPanel = (LinearLayout) findViewById(R.id.activiteitenLinearLayout);
        initButtons();
        // TODO: reset/reload

        initDefaultKalender("test");
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

    private void removeActiviteit(ActiviteitPanel a)
    {
        // remove activiteit
        if (activiteiten.remove(a))
        {
            // stop -> GUI
            scrollPanel.removeView(a);
        }

        // reset selection
        if (currentSelectedAP == a)
        {
            setORresetSelectionMade(null);
        }
    }

    public void btnStart(View arg0)
    {
        // start new activiteit
        ActiviteitPanel a = new ActiviteitPanel(this, this);
        a.setKalender(def_cal);
        activiteiten.add(a);
        // start -> GUI
        scrollPanel.addView(a);
        // check the new radiobutton
        radioButtonChecked(a);
    }

    public void btnResume(View arg0)
    {// resume selected activiteit
        // find selected activiteit
        ActiviteitI a = currentSelectedAP;

        if (a != null)
        {
            a.resumeRunning();
            setORresetSelectionMade(currentSelectedAP);
        }
    }

    @Override
    public void activiteitEdit(ActiviteitPanel a)
    {
        final Dialog dialog = new DialogActiviteit(this, a, this);
        dialog.show();
    }

    public void activiteitStop(ActiviteitPanel a)
    {
        if (a == currentSelectedAP)
        {
            setORresetSelectionMade(a);
        }
    }

    public void btnCancel(View arg0)
    {// cancel selected activiteit
        // find selected activiteit
        ActiviteitPanel a = currentSelectedAP;
        if (a != null)
        {
            removeActiviteit(a);
            // resetSelection available buttons
            setORresetSelectionMade(null);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main);
        // TODO: load gui
    }

    private Kalender[] getKalenders(boolean currentCalendarsOnly)
    {
        if (kalenders == null)
        {
            kalenders = Kalender.retrieveKalenders(this, currentCalendarsOnly);
        }
        return kalenders;
    }

    private void initDefaultKalender(String name)
    {
        def_cal = getKalendarByName(name);
        if (def_cal == null)
        {
            Toast.makeText(this, "No calendar named '" + name + "' found, please check your settings", Toast.LENGTH_SHORT).show();
        }
    }

    public Kalender getKalendarByName(String name)
    {
        Kalender kalender = null;
        Kalender[] ks = getKalenders(true);
        for (Kalender k : ks)
        {
            if (k.getName().equals(name))
            {
                kalender = k;
                Log.d("Gevonden " + k.getId(), k.getName());
                break;
            } else
            {
                Log.d("Zoeken naar: " + name, k.getName());
            }
        }
        return kalender;
    }

    private void initButtons()
    {
        Button detail = (Button) findViewById(R.id.btnDetails);
        Button macro1 = (Button) findViewById(R.id.btnMacro1);
        Button macro2 = (Button) findViewById(R.id.btnMacro2);
        Button cancel = (Button) findViewById(R.id.btnCancel);
        Button resume = (Button) findViewById(R.id.btnResume);
        detail.setEnabled(false);
        macro1.setEnabled(false);
        macro2.setEnabled(false);
        cancel.setEnabled(false);
        resume.setEnabled(false);
    }

    private void setORresetSelectionMade(ActiviteitPanel a)
    {
        Button detail = (Button) findViewById(R.id.btnDetails);
        Button cancel = (Button) findViewById(R.id.btnCancel);
        Button resume = (Button) findViewById(R.id.btnResume);
        if (a == null)
        {
            detail.setEnabled(false);
            cancel.setEnabled(false);
            resume.setEnabled(false);
        } else
        {
            detail.setEnabled(true);
            cancel.setEnabled(true);
            if (a.isRunning())
            {
                resume.setEnabled(false);
            } else
            {
                resume.setEnabled(true);
            }
        }
        currentSelectedAP = a;
    }
}
