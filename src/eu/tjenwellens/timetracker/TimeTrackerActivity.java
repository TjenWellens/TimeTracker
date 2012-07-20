package eu.tjenwellens.timetracker;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;

public class TimeTrackerActivity extends Activity implements GetContent
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
    private Activiteit a = null;
    private Kalender def_cal = null;
    private Kalender[] kalenders = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        reset();

        initDefaultKalender("test");
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

    private Kalender getKalendarByName(String name)
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

    private void editCalendar(int calendarId)
    {
        String title = "jwei's birthday";
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS;

        Evenement e = new Evenement(calendarId, title, startTime, endTime);
        e.setDescription("Time to celebrate jwei's birthday.");
        e.setEventLocation("Palo Alto, CA");
        e.setAllDay(0);
        e.setHasAlarm(0);

        Evenement.post(this, e);
    }

    public void btnStart(View view)
    {
        if (a != null)
        {
            return;
        }
        a = new Activiteit();
        a.setKalender(def_cal);
        Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();

        final TextView txtStart = (TextView) findViewById(R.id.txtTime);
        final TextView txtTitle = (TextView) findViewById(R.id.txtName);
        final TextView txtCalendar = (TextView) findViewById(R.id.txtCalendar);
        final TextView txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtTitle.setText(a.getTitle());
        txtCalendar.setText(a.getKalenderName());
        txtStart.setText(a.getStart());
        txtDuration.setText("timer running");

        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnStop = (Button) findViewById(R.id.btnStop);
        final Button btnResume = (Button) findViewById(R.id.btnResume);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        btnResume.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(true);

        Log.d("+++ Started Activity", "title: " + a.getTitle());
        Log.d("+++ Started Activity", "id   : " + a.getId());
        Log.d("+++ Started Activity", "start: " + a.getStart());
        Log.d("+++ Started Activity", "calen: " + a.getKalenderName());
//        calendar();
    }

    public void btnStop(View view)
    {
        if (!a.stop())
        {
            return;
        }
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();

        final TextView txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtDuration.setText(a.getDuration());

        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnStop = (Button) findViewById(R.id.btnStop);
        final Button btnResume = (Button) findViewById(R.id.btnResume);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnResume.setEnabled(true);
        btnSave.setEnabled(true);

        Log.d("+++ Stopped Activity", "title: " + a.getTitle());
        Log.d("+++ Stopped Activity", "id   : " + a.getId());
        Log.d("+++ Stopped Activity", "start: " + a.getStart());
        Log.d("+++ Stopped Activity", "calen: " + a.getKalenderName());
        Log.d("+++ Stopped Activity", "durat: " + a.getDuration());
    }

    public void btnResume(View view)
    {
        if (!a.resume())
        {
            return;
        }
        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();

        final TextView txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtDuration.setText("timer continues");

        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnStop = (Button) findViewById(R.id.btnStop);
        final Button btnResume = (Button) findViewById(R.id.btnResume);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        btnResume.setEnabled(false);
        btnSave.setEnabled(false);


        Log.d("+++ Resumed Activity", "title: " + a.getTitle());
        Log.d("+++ Resumed Activity", "id   : " + a.getId());
        Log.d("+++ Resumed Activity", "start: " + a.getStart());
        Log.d("+++ Resumed Activity", "calen: " + a.getKalenderName());
//        calendar();
    }

    public void btnSave(View view)
    {
        final EditText txtTitle = (EditText) findViewById(R.id.txtName);
        final EditText txtCalendar = (EditText) findViewById(R.id.txtCalendar);

        String calendarName = txtCalendar.getText().toString();
        if (calendarName != null && calendarName.length() > 0 && !calendarName.equals(a.getKalenderName()))
        {
            Kalender kalender = getKalendarByName(calendarName);
            if (kalender != null)
            {
                a.setKalender(kalender);
            }
        }
        if (a.getKalenderName() == null)
        {
            Toast.makeText(this, "Error with Calendars", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = txtTitle.getText().toString();
        if (title != null)
        {
            a.setTitle(title);
        }

        Evenement e = a.getEvent();
        if (e == null)
        {
            return;
        }
        Evenement.post(this, e);
        Log.d("+++ Posting Activity", "title: " + a.getTitle());
        Log.d("+++ Posting Activity", "id   : " + a.getId());
        Log.d("+++ Posting Activity", "start: " + a.getStart());
        Log.d("+++ Posting Activity", "calen: " + a.getKalenderName());
        Log.d("+++ Stopped Activity", "durat: " + a.getDuration());
        reset();
    }

    public void btnCancel(View view)
    {
        Log.d("+++ Cancelling Activity", "title: " + a.getTitle());
        reset();
    }

    private void reset()
    {
        Log.d("+++ RESET Activity", "All values reset");

        a = null;
        resetTextFields();
        resetButtons();
    }

    private void resetTextFields()
    {
        final TextView txtStart = (TextView) findViewById(R.id.txtTime);
        final TextView txtTitle = (TextView) findViewById(R.id.txtName);
        final TextView txtCalendar = (TextView) findViewById(R.id.txtCalendar);
        final TextView txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtTitle.setText(R.string.empty);
        txtCalendar.setText(R.string.empty);
        txtStart.setText(R.string.empty);
        txtDuration.setText(R.string.empty);
    }

    private void resetButtons()
    {
        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnStop = (Button) findViewById(R.id.btnStop);
        final Button btnResume = (Button) findViewById(R.id.btnResume);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        btnResume.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
    }

    private void loadGUI()
    {
        if (a == null)
        {
            // state is NONE
            resetButtons();
            resetTextFields();
            return;
        }
        // state is niet NONE
        boolean running = false;
        boolean ended = false;

        final Button btnStart = (Button) findViewById(R.id.btnStart);
        final Button btnStop = (Button) findViewById(R.id.btnStop);
        final Button btnResume = (Button) findViewById(R.id.btnResume);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnResume.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(true);
        if (a.isEnded())
        {
            // state is ENDED
            btnResume.setEnabled(true);
            btnSave.setEnabled(true);
        } else
        {
            // state is RUNNING
            btnStop.setEnabled(true);
        }

        final TextView txtStart = (TextView) findViewById(R.id.txtTime);
        final TextView txtTitle = (TextView) findViewById(R.id.txtName);
        final TextView txtCalendar = (TextView) findViewById(R.id.txtCalendar);
        final TextView txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtTitle.setText(a.getTitle());
        txtCalendar.setText(a.getKalenderName());
        txtStart.setText(a.getStart());
        txtDuration.setText(a.getDuration());
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
        loadGUI();
    }
}
