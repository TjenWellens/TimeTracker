package eu.tjenwellens.timetracker.old;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.detail.DetailHandler;
import eu.tjenwellens.timetracker.detail.DetailPanel;
import eu.tjenwellens.timetracker.gestures.SimpleGestureFilter;
import eu.tjenwellens.timetracker.gestures.SimpleGestureListener;
import eu.tjenwellens.timetracker.macro.MacroButtonPanel;
import eu.tjenwellens.timetracker.macro.MacroHandler;
import eu.tjenwellens.timetracker.macro.MacroI;
import eu.tjenwellens.timetracker.main.*;
import eu.tjenwellens.timetracker.old.preferences.PreferencesActivity;
import java.util.ArrayList;
import java.util.List;

public class TimeTrackerActivity extends Activity implements ActiviteitHandler, SimpleGestureListener, DetailHandler, MacroHandler
{

    public static final String CONTENT_CALENDARS = "content://com.android.calendar/calendars";
    public static final String CONTENT_EVENTS = "content://com.android.calendar/events";
    public static final String KEY_PREFERENCE_DEFAULT_CALENDAR = "default_calendar";
    public static final String APP_DEFAULT_CALENDAR = "test";
    // main screen selection
    private ViewGroup activiteitViewContainer;
    private ActiviteitPanel currentActiviteit = null;
    // detail screen selection
    private ArrayList<DetailPanel> currentDetails = new ArrayList<DetailPanel>();
    // macro
    private MacroButtonPanel macroPanel;
    // gestures
    private SimpleGestureFilter detector;
    // flippering
    private ViewFlipper flipper;
    //
    private ArrayList<ActiviteitPanel> activiteiten = new ArrayList<ActiviteitPanel>();
    //
    private Kalender pref_kal = null;
    //
    private SharedPreferences preferences;

    private void spinnerTest()
    {
        Spinner s = new Spinner(this);
        s.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
//        ArrayAdapter<String>aa=new ArrayAdapter<String>(this, 0, null);
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // GUI
        initFlipper();
        initMainGUI();
        initDetailGUI();
        initMacroGUI();
        // Initialize preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // load prev state (if any)
        loadActiviteiten();
        loadMacros();
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
        saveMacros();
    }

    private void loadActiviteiten()
    {
        // load activiteiten, remove activiteiten from database
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (ActiviteitI activiteit : dbh.getAllActiviteiten()) {
            addActiviteit(new ActiviteitPanel(this, this, activiteit));
        }
//        dbh.clearActiviteiten();
    }

    private void saveActiviteiten()
    {
        // save activiteiten
        List<ActiviteitPanel> an = new ArrayList<ActiviteitPanel>(activiteiten);
//        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (ActiviteitPanel ap : an) {
//            dbh.addActiviteit(ap);
            ap.updateDBActiviteit(this);
            removeActiviteit(ap);
        }
    }

    private void loadMacros()
    {
        // load macros
        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        Log.d("\n\n\n\n\n\n\n\n\n\n\n", "laad macros");
        macroPanel.addAllButtons(dbh.getAllMacros());
        Log.d("gelukt", "\n\n\n\n\n\n\n\n\n\n\n");
        dbh.clearMacros();
    }

    private void saveMacros()
    {
        // save macros
        List<MacroI> macros = macroPanel.getMacros();
//        DatabaseHandler dbh = DatabaseHandler.getInstance(this);
        for (MacroI macro : macros) {
//            dbh.addMacro(macro);
            macro.updateDBMacro(this);
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
    public void saveActiviteit(ActiviteitPanel a)
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
            activiteitViewContainer.removeView(a);
        }

        // reset selection
        if (currentActiviteit == a) {
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
        ActiviteitI a = currentActiviteit;

        if (a != null) {
            a.resumeRunning();
            // update resume button
            setORresetSelectionMade(currentActiviteit);
        }
    }

    public void btnCancel(View arg0)
    {// cancel selected activiteit
        // find selected activiteit
        ActiviteitPanel a = currentActiviteit;
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

    public void btnDetailSave(View arg0)
    {
        if (currentActiviteit != null) {
            int size = currentDetails.size();
            String[] description = new String[size];
            for (int i = 0; i < size; i++) {
                description[i] = currentDetails.get(i).getDetailText();
            }
            currentActiviteit.setDescription(description);
        }
        Toast.makeText(this, "Details saved", Toast.LENGTH_SHORT).show();
        nextScreen();
    }

    public void deleteDetail(DetailPanel dp)
    {
        final LinearLayout detailViewContainer = (LinearLayout) findViewById(R.id.pnlDetail);
        if (currentDetails.remove(dp)) {
            detailViewContainer.removeView(dp);
        }
        Toast.makeText(this, "Detail deleted", Toast.LENGTH_SHORT).show();
    }

    public void btnDetailCancel(View arg0)
    {
        EditText et = (EditText) findViewById(R.id.txtDetail);
        et.setText(R.string.none);
    }

    public void btnDetailAdd(View arg0)
    {
        EditText et = (EditText) findViewById(R.id.txtDetail);
        String text = et.getText().toString();
        DetailPanel dp = new DetailPanel(this, this, text);
        addDetail(dp);
        et.setText(R.string.none);
    }

    public void btnDetailTime(View arg0)
    {
        EditText et = (EditText) findViewById(R.id.txtDetail);
        et.setText(Time.timeToString(System.currentTimeMillis()) + " " + et.getText().toString());
    }

    public void btnMacroAdd(View arg0)
    {
        final EditText txtTitle = (EditText) findViewById(R.id.txtMacroSettingsTitle);
        final Spinner txtKal = (Spinner) findViewById(R.id.spinnerMacroSettingsCalendar);
        String kalenderString = txtKal.toString();
        String title = txtTitle.getText().toString();
        Kalender kalender = Kalender.getKalenderByName(this, kalenderString);
        macroPanel.add(title, kalender);

        txtTitle.setText(R.string.none);
        Log.d("----------------added new macro: ", title + " - " + kalender);
    }

    public void btnMacroReset(View arg0)
    {
        macroPanel.reset();
    }

    public void btnMacroClear(View arg0)
    {
        final EditText txtTitle = (EditText) findViewById(R.id.txtMacroSettingsTitle);
        txtTitle.setText(R.string.none);
    }

    public void btnMacroBack(View arg0)
    {
        previousScreen();
    }

    private void deleteDetails()
    {
        final LinearLayout detailViewContainer = (LinearLayout) findViewById(R.id.pnlDetail);
        detailViewContainer.removeAllViews();
        currentDetails.clear();
    }

    private void addDetail(DetailPanel dp)
    {
        final LinearLayout detailViewContainer = (LinearLayout) findViewById(R.id.pnlDetail);
        detailViewContainer.addView(dp);
        currentDetails.add(dp);
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
//        ActiviteitPanel a = new ActiviteitPanel(this, this);
//        a.setActiviteitTitle("pauze");
//        Kalender ritme_indeling = Kalender.getKalenderByName(this, "ritme/indeling");
//        if (ritme_indeling != null) {
//            a.setKalender(ritme_indeling);
//        } else {
//            a.setKalender(pref_kal);
//        }
//        addActiviteit(a);
        // go to macro screen
        nextScreen();
    }

//    public void btnMacro2(View arg0)
//    {
//        // start new activiteit
//        ActiviteitPanel a = new ActiviteitPanel(this, this);
//        a.setActiviteitTitle("leren");
//        Kalender leren = Kalender.getKalenderByName(this, "school/leren");
//        if (leren != null) {
//            a.setKalender(leren);
//        } else {
//            a.setKalender(pref_kal);
//        }
//        addActiviteit(a);
//    }
    private void addActiviteit(ActiviteitPanel a)
    {
        // add to list
        activiteiten.add(a);
        // add to gui
        activiteitViewContainer.addView(a);
        // check the new radiobutton
//        radioButtonChecked(a);

    }

    @Override
    public void shortClick(ActiviteitPanel a)
    {
        final Dialog dialog = new DialogActiviteit(this, a);
        dialog.show();
    }

    public void activiteitStop(ActiviteitPanel a)
    {
        if (a == currentActiviteit) {
            // update resume button
            setORresetSelectionMade(a);
        }
    }

//    public void radioButtonChecked(ActiviteitPanel a)
//    {
//        for (ActiviteitPanel ap : activiteiten) {
//            ap.uncheckRadioButton();
//        }
//        if (a != null) {
//            a.checkRadioButton();
//            setORresetSelectionMade(a);
//        }
//    }

    private void initMainGUI()
    {
//        setContentView(R.layout.main);
        activiteitViewContainer = ((ViewGroup) findViewById(R.id.pnlMainActiviteiten));
        initGUIButtons();
    }

    private void initGUIButtons()
    {
//        Button macros = (Button) findViewById(R.id.btnMainMacros);
//        Button btnDetailCancel = (Button) findViewById(R.id.btnDetailCancel);
//        macros.setEnabled(true);
//        btnDetailCancel.setEnabled(true);

        setORresetSelectionMade(currentActiviteit);
    }

    private void setORresetSelectionMade(ActiviteitPanel a)
    {
//        Button detail = (Button) findViewById(R.id.btnMainDetails);
//        Button cancel = (Button) findViewById(R.id.btnMainDelete);
//        Button resume = (Button) findViewById(R.id.btnMainResume);
        Button detailAdd = (Button) findViewById(R.id.btnDetailAdd);
//        Button detailSave = (Button) findViewById(R.id.btnDetailSave);
        if (a == null) {
//            detail.setEnabled(false);
//            cancel.setEnabled(false);
//            resume.setEnabled(false);
            detailAdd.setEnabled(false);
//            detailSave.setEnabled(false);
            deleteDetails();
        } else {
//            detail.setEnabled(true);
//            cancel.setEnabled(true);
//            resume.setEnabled(!a.isRunning());
            detailAdd.setEnabled(true);
//            detailSave.setEnabled(true);
            deleteDetails();
            loadDetails(a);
        }
        currentActiviteit = a;
    }

    private void loadDetails(ActiviteitPanel a)
    {
        if (a == null) {
            return;
        }
        String[] currentDescriptions = a.getDescriptionEntries();
        if (currentDescriptions == null) {
            return;
        }
        for (String string : currentDescriptions) {
            DetailPanel dp = new DetailPanel(this, this, string);
            addDetail(dp);
        }
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
                previousScreen();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                nextScreen();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                // do nothing
                break;
            case SimpleGestureFilter.SWIPE_UP:
                // do nothing
                break;

        }
    }

    private void previousScreen()
    {
        flipper.setInAnimation(inFromLeftAnimation());
        flipper.setOutAnimation(outToRightAnimation());
        flipper.showPrevious();
    }

    private void nextScreen()
    {
        flipper.setInAnimation(inFromRightAnimation());
        flipper.setOutAnimation(outToLeftAnimation());
        flipper.showNext();
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

    private void initDetailGUI()
    {
        // nothing to do yet
    }

    public void launchActiviteit(MacroI macro)
    {
        ActiviteitPanel a = new ActiviteitPanel(this, this, macro);
        addActiviteit(a);
        previousScreen();
    }

    private void initMacroGUI()
    {
        final LinearLayout macroContainer = (LinearLayout) findViewById(R.id.pnlMacro);
        if (macroContainer != null) {
            macroPanel = new MacroButtonPanel(this, this);
            macroContainer.addView(macroPanel);
        } else {
            Toast.makeText(this, "Macro's not initialized", Toast.LENGTH_LONG).show();
        }
    }

    public void editDetail(DetailPanel dp)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void longClick(ActiviteitPanel a)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
