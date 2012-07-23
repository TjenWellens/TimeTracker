package eu.tjenwellens.timetracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.gestures.SimpleGestureFilter;
import eu.tjenwellens.timetracker.gestures.SimpleGestureListener;
import java.util.ArrayList;
import java.util.List;

public class TimeTrackerActivity extends Activity implements GetContent, ActiviteitHandler, SimpleGestureListener
{

    public static final String CONTENT_CALENDARS = "content://com.android.calendar/calendars";
    public static final String CONTENT_EVENTS = "content://com.android.calendar/events";
    public static final String KEY_PREFERENCE_DEFAULT_CALENDAR = "default_calendar";
    public static final String APP_DEFAULT_CALENDAR = "default_calendar";
    //
    private LinearLayout scrollPanel;
    private ActiviteitPanel currentSelectedAP = null;
    // gestures
    private SimpleGestureFilter detector;
    // flippering
    private ViewFlipper flipper;
    //
    private List<ActiviteitPanel> activiteiten = new ArrayList<ActiviteitPanel>();
    //
    private Kalender pref_kal = null;
    //
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // GUI
        initFlipper();
        initMainGUI();
        // Initialize preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // load prev state (if any)
        loadActiviteiten();
        // init gestures
        detector = new SimpleGestureFilter(this, this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // load preferences
        loadPreferences();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveActiviteiten();
    }

    private void loadActiviteiten()
    {
        // load activiteiten, remove activiteiten from database
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (Activiteit activiteit : dbh.getAllActiviteiten()) {
            addActiviteit(new ActiviteitPanel(this, this, activiteit));
        }
        dbh.clearActiviteiten();
    }

    private void saveActiviteiten()
    {
        // TODO: save activiteiten
        List<ActiviteitPanel> an = new ArrayList<ActiviteitPanel>(activiteiten);
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (ActiviteitPanel ap : an) {
            dbh.addActiviteit(ap.getActiviteit());
            removeActiviteit(ap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

// This method is called once the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            // We have only one menu option
            case R.id.menu_settings:
                // Launch Preference activity
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                // Some feedback to the user
                Toast.makeText(this, "Enter default calendar.", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {// window rotated
        super.onConfigurationChanged(newConfig);
        // TODO: load gui
//        setContentView(R.layout.main);
    }

    private void loadPreferences()
    {// load default kalender 
        String kalenderName = preferences.getString(KEY_PREFERENCE_DEFAULT_CALENDAR, APP_DEFAULT_CALENDAR);
        Kalender newDefault;
        if (pref_kal == null || !pref_kal.getName().equals(kalenderName)) {
            newDefault = Kalender.getKalenderByName(this, kalenderName);
            // try from preferences
            if (newDefault == null) {
                // else use the app's default kalender
                newDefault = Kalender.getKalenderByName(this, APP_DEFAULT_CALENDAR);
            }
            pref_kal = (pref_kal != null && newDefault == null) ? pref_kal : newDefault;
        }
    }

    @Override
    public void activiteitSave(ActiviteitPanel a)
    {
        // handle all UI except the Activiteitpanel
        if (a.getKalenderName() == null) {
            return;
        }
        Evenement e = a.getEvenement();
        if (e == null) {
            return;
        }
        Evenement.post(this, e);
        removeActiviteit(a);
    }

    private void removeActiviteit(ActiviteitPanel a)
    {
        // remove activiteit
        if (activiteiten.remove(a)) {
            // stop -> GUI
            scrollPanel.removeView(a);
        }

        // reset selection
        if (currentSelectedAP == a) {
            setORresetSelectionMade(null);
        }
    }

    public void btnStart(View arg0)
    {
        // start new activiteit
        ActiviteitPanel a = new ActiviteitPanel(this, this);
        a.setKalender(pref_kal);
        addActiviteit(a);
    }

    public void btnResume(View arg0)
    {// resume selected activiteit
        // find selected activiteit
        ActiviteitI a = currentSelectedAP;

        if (a != null) {
            a.resumeRunning();
            // update resume button
            setORresetSelectionMade(currentSelectedAP);
        }
    }

    public void btnCancel(View arg0)
    {// cancel selected activiteit
        // find selected activiteit
        ActiviteitPanel a = currentSelectedAP;
        if (a != null) {
            removeActiviteit(a);
            // resetSelection available buttons
//            setORresetSelectionMade(null);
        }
    }

    public void btnDetails(View arg0)
    {
        flipper.setInAnimation(inFromLeftAnimation());
        flipper.setOutAnimation(outToRightAnimation());
        flipper.showPrevious();
    }

    public void btnDetailOK(View arg0)
    {
        ActiviteitPanel a = currentSelectedAP;
        if (a != null) {
            EditText txtDetail = (EditText) findViewById(R.id.txtDetail);
            a.setDescription(txtDetail.getText().toString());
        }
        Toast.makeText(this, "Details saved ", Toast.LENGTH_SHORT).show();
    }

    public void btnDetailCancel(View arg0)
    {
        ActiviteitPanel a = currentSelectedAP;
        if (a != null) {
            EditText txtDetail = (EditText) findViewById(R.id.txtDetail);
            txtDetail.setText(a.getDescription());
        }
    }

    private void showPrefs(String default_calendar)
    {
        if (default_calendar == null) {
            default_calendar = "No preference set";
        }
        Toast.makeText(this, "Input: " + default_calendar, Toast.LENGTH_LONG).show();
    }

    public void btnMacro1(View arg0)
    {
        // start new activiteit
        ActiviteitPanel a = new ActiviteitPanel(this, this);
        a.setActiviteitTitle("pauze");
        Kalender ritme_indeling = Kalender.getKalenderByName(this, "ritme/indeling");
        if (ritme_indeling != null) {
            a.setKalender(ritme_indeling);
        } else {
            a.setKalender(pref_kal);
        }
        addActiviteit(a);
    }

    public void btnMacro2(View arg0)
    {
        // start new activiteit
        ActiviteitPanel a = new ActiviteitPanel(this, this);
        a.setActiviteitTitle("leren");
        Kalender leren = Kalender.getKalenderByName(this, "school/leren");
        if (leren != null) {
            a.setKalender(leren);
        } else {
            a.setKalender(pref_kal);
        }
        addActiviteit(a);
    }

    private void addActiviteit(ActiviteitPanel a)
    {
        // add to list
        activiteiten.add(a);
        // add to gui
        scrollPanel.addView(a);
        // check the new radiobutton
        radioButtonChecked(a);

    }

    @Override
    public void activiteitEdit(ActiviteitPanel a)
    {
        final Dialog dialog = new DialogActiviteit(this, a);
        dialog.show();
    }

    public void activiteitStop(ActiviteitPanel a)
    {
        if (a == currentSelectedAP) {
            // update resume button
            setORresetSelectionMade(a);
        }
    }

    public void radioButtonChecked(ActiviteitPanel a)
    {
        for (ActiviteitPanel ap : activiteiten) {
            ap.uncheckRadioButton();
        }
        if (a != null) {
            a.checkRadioButton();
            setORresetSelectionMade(a);
        }
    }

    private void initMainGUI()
    {
//        setContentView(R.layout.main);
        scrollPanel = ((LinearLayout) findViewById(R.id.activiteitenLinearLayout));
        initGUIButtons();
    }

    private void initGUIButtons()
    {
        Button macro1 = (Button) findViewById(R.id.btnMacro1);
        Button macro2 = (Button) findViewById(R.id.btnMacro2);
        macro1.setEnabled(true);
        macro1.setText("Pauze");
        macro2.setEnabled(true);
        macro2.setText("Leren");

        setORresetSelectionMade(currentSelectedAP);
    }

    private void setORresetSelectionMade(ActiviteitPanel a)
    {
        Button detail = (Button) findViewById(R.id.btnDetails);
        Button cancel = (Button) findViewById(R.id.btnCancel);
        Button resume = (Button) findViewById(R.id.btnResume);
        Button detailOK = (Button) findViewById(R.id.btnDetailOk);
        Button detailCancel = (Button) findViewById(R.id.btnDetailCancel);
        EditText txtDetail = (EditText) findViewById(R.id.txtDetail);
        if (a == null) {
            detail.setEnabled(false);
            cancel.setEnabled(false);
            resume.setEnabled(false);
            detailOK.setEnabled(false);
            detailCancel.setEnabled(false);
            txtDetail.setText("");

        } else {
            detail.setEnabled(true);
            cancel.setEnabled(true);
            detailOK.setEnabled(true);
            detailCancel.setEnabled(true);
//            Log.d("+++++++++\n++++++++++\n" + a.getActiviteitTitle(), "running: " + a.isRunning());
            resume.setEnabled(!a.isRunning());
            txtDetail.setText(a.getDescription());
        }
        currentSelectedAP = a;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me)
    {
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction)
    {

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                flipper.setInAnimation(inFromLeftAnimation());
                flipper.setOutAnimation(outToRightAnimation());
                flipper.showPrevious();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                flipper.setInAnimation(inFromRightAnimation());
                flipper.setOutAnimation(outToLeftAnimation());
                flipper.showNext();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                // do nothing
                break;
            case SimpleGestureFilter.SWIPE_UP:
                // do nothing
                break;

        }
    }

    @Override
    public void onDoubleTap()
    {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    private void initFlipper()
    {
        setContentView(R.layout.flipper);

        flipper = (ViewFlipper) findViewById(R.id.flipper);
    }

    private Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private Animation inFromLeftAnimation()
    {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private Animation outToRightAnimation()
    {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
}
