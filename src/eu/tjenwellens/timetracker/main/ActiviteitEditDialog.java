package eu.tjenwellens.timetracker.main;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author tjen
 */
public class ActiviteitEditDialog extends Dialog
{
    // activiteit
    private ActiviteitI activiteit;
    // new values
    private String title;
    private Kalender selectedKalender;
    private long beginTime;
    private long endTime;
    //GUI
    private LinearLayout mainpanel, buttonPanel;
    private TextView tvName, tvCal, tvBegin, tvEnd;
    private EditText txtName;
    private Button btnBegin, btnEnd, btnCancel, btnOK;
    // time selection
    private TimePickerDialog tpd;
    private boolean choosingBegin = true;
    // Calendar selection
    private Spinner spnCal;
    private AdapterView.OnItemSelectedListener spinnerListener = new SpinnerListener();
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimeSetListener();

    public ActiviteitEditDialog(Context context, ActiviteitI activiteit)
    {
        super(context);
        this.activiteit = activiteit;
        this.title = activiteit.getActiviteitTitle();
        this.selectedKalender = null;
        this.beginTime = activiteit.getBeginMillis();
        this.endTime = activiteit.getEndMillis();
        initGUI(context);
    }

    private void initGUI(Context context)
    {
        this.setTitle("Editing event");
        mainpanel = new LinearLayout(context);
        mainpanel.setOrientation(LinearLayout.VERTICAL);
        {
            // NAME
            tvName = new TextView(context);
            tvName.setText(R.string.lblTitle);
            mainpanel.addView(tvName);
            txtName = new EditText(context);
            txtName.setText(title);
            txtName.setSelection(txtName.getText().length());
            mainpanel.addView(txtName);

            // CALENDAR
            // CALENDAR tv
            tvCal = new TextView(context);
            tvCal.setText(R.string.lblCalendar);
            mainpanel.addView(tvCal);
            // CALENDAR spinner
            spnCal = new Spinner(context);
            initSpinner(context, spnCal, activiteit.getKalenderName());
            mainpanel.addView(spnCal, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            // TIME PICKER
            tpd = new TimePickerDialog(context, timeSetListener, 0, 0, true);

            // BEGIN TIME
            tvBegin = new TextView(context);
            tvBegin.setText(R.string.lblStartTime);
            mainpanel.addView(tvBegin);
            btnBegin = new Button(context);
            btnBegin.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    btnBegin();
                }
            });
            mainpanel.addView(btnBegin);

            // END TIME
            tvEnd = new TextView(context);
            tvEnd.setText(R.string.lblStopTime);
            mainpanel.addView(tvEnd);
            btnEnd = new Button(context);
            btnEnd.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    btnEnd();
                }
            });
            mainpanel.addView(btnEnd);

            // Update time
            updateTime();

            // DIALOG BUTTONS
            buttonPanel = new LinearLayout(context);
            {
                // CANCEL
                btnCancel = new Button(context);
                btnCancel.setText(R.string.cancel);
                btnCancel.setOnClickListener(new android.view.View.OnClickListener()
                {
                    public void onClick(View arg0)
                    {
                        ActiviteitEditDialog.this.btnCancel();
                    }
                });
                buttonPanel.addView(btnCancel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
                // OK
                btnOK = new Button(context);
                btnOK.setText(R.string.ok);
                btnOK.setOnClickListener(new android.view.View.OnClickListener()
                {
                    public void onClick(View arg0)
                    {
                        ActiviteitEditDialog.this.btnOK();
                    }
                });
                buttonPanel.addView(btnOK, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            }
            mainpanel.addView(buttonPanel, new ViewGroup.LayoutParams(300, LayoutParams.WRAP_CONTENT));

            this.setContentView(mainpanel, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void btnBegin()
    {
        // pick start time
        choosingBegin = true;
        // TODO: start timepicker
        tpd.updateTime(Time.getHours(beginTime), Time.getMinutes(beginTime));
        tpd.show();
    }

    private void btnEnd()
    {
        if (endTime < 0)
        {
            // stop
            endTime = System.currentTimeMillis();
            updateTime();
        } else
        {
            // pick end time
            choosingBegin = false;
            tpd.updateTime(Time.getHours(endTime), Time.getMinutes(endTime));
            tpd.show();
        }
    }

    private void timeSet(int hour, int minute)
    {
        if (choosingBegin)
        {
            beginTime = Time.updateTime(beginTime, hour, minute);
        } else
        {
            endTime = Time.updateTime(endTime, hour, minute);
        }
        updateTime();
    }

    private void updateTime()
    {
        // begin button
        btnBegin.setText(Time.timeToString(beginTime));

        // end button
        String stoptime = Time.timeToString(endTime);
        if (stoptime != null)
        {
            btnEnd.setText(stoptime);
        } else
        {
            btnEnd.setText(R.string.stop);
        }
    }

    private void btnOK()
    {
        // update
        title = txtName.getText().toString();
        if (title != null)
        {
            activiteit.setActiviteitTitle(title);
        }
        activiteit.setKalender(selectedKalender);
        activiteit.setStartTimeMillis(beginTime);
        activiteit.setEndTimeMillis(endTime);
        // exit
        dismiss();
    }

    private void btnCancel()
    {
        // don't save
        // exit
        dismiss();
    }

    private void initSpinner(Context context, Spinner spinner, String kalenderName)
    {
        Kalender[] kalenders = Kalender.retrieveKalenders(context);
        if (kalenders == null)
        {
            return;
        }
        Kalender kalender = Kalender.getKalenderByName(context, kalenderName);
        if (kalenderName == null)
        {
            kalender = kalenders[0];
        }
        this.selectedKalender = kalender;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_dropdown_item, kalenders);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add listener
        spinner.setOnItemSelectedListener(spinnerListener);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private class TimeSetListener implements OnTimeSetListener
    {
        public void onTimeSet(TimePicker view, int hour, int minute)
        {
            timeSet(hour, minute);
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            Object selected = parent.getItemAtPosition(pos);
            if (selected instanceof Kalender)
            {
                selectedKalender = (Kalender) selected;
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            selectedKalender = null;
        }
    }
}
