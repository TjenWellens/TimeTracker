/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.main;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.*;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author tjen
 */
public class DialogActiviteit extends Dialog
{

    private LinearLayout mainpanel, buttonPanel;
    private TextView tvName, tvCal, tvBegin, tvEnd;
    private EditText txtName;
    private Button btnBegin, btnEnd, btnCancel, btnOK;
    private ActiviteitI activiteitI;
//    private Context context;
    private Spinner spnCal;
    private Kalender selectedKalender;
    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener()
    {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            Object selected = parent.getItemAtPosition(pos);
            if (selected instanceof Kalender) {
                selectedKalender = (Kalender) selected;
//                Toast.makeText(MacroSettingsActivity.this, "Success spinner to calender: " + selectedKalender, Toast.LENGTH_LONG).show();
            } else {
//                context.Toast.makeText(MacroSettingsActivity.this, "Failed to recognize  calender", Toast.LENGTH_LONG).show();
            }
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            selectedKalender = null;
//            Toast.makeText(MacroSettingsActivity.this, "No selection made", Toast.LENGTH_LONG).show();
        }
    };

    public DialogActiviteit(Context context, ActiviteitI activiteitI)
    {
        super(context);
//        this.context = context;
        this.activiteitI = activiteitI;
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
            txtName.setText(activiteitI.getActiviteitTitle());
            txtName.setSelection(txtName.getText().length());
            mainpanel.addView(txtName);
            // CALENDAR tv
            tvCal = new TextView(context);
            tvCal.setText(R.string.lblCalendar);
            mainpanel.addView(tvCal);
            // CALENDAR spinner
            spnCal = new Spinner(context);
            initSpinner(context, spnCal);
            mainpanel.addView(spnCal, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // BEGIN
            tvBegin = new TextView(context);
            tvBegin.setText(R.string.lblStartTime);
            mainpanel.addView(tvBegin);
            btnBegin = new Button(context);
            btnBegin.setText(activiteitI.getStartTime());
            // TODO: time picker
            mainpanel.addView(btnBegin);
            // END
            tvEnd = new TextView(context);
            tvEnd.setText(R.string.lblStopTime);
            mainpanel.addView(tvEnd);
            btnEnd = new Button(context);
            String stoptime = activiteitI.getStopTime();
            if (stoptime != null) {
                btnEnd.setText(activiteitI.getStopTime());
                // TODO: time picker
            } else {
                btnEnd.setText(R.string.running);
                btnEnd.setEnabled(false);
            }
            // TODO: add listener
            mainpanel.addView(btnEnd);
            // BUTTON
            buttonPanel = new LinearLayout(context);
            {
                // CANCEL
                btnCancel = new Button(context);
                btnCancel.setText(R.string.cancel);
                btnCancel.setOnClickListener(new android.view.View.OnClickListener()
                {

                    public void onClick(View arg0)
                    {
                        DialogActiviteit.this.btnCancel();
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
                        DialogActiviteit.this.btnOK();
                    }
                });
                buttonPanel.addView(btnOK, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            }
            mainpanel.addView(buttonPanel, new ViewGroup.LayoutParams(300, LayoutParams.WRAP_CONTENT));

            this.setContentView(mainpanel, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void btnOK()
    {
        String title = txtName.getText().toString();
        Kalender k = selectedKalender;
        if (title != null) {
            activiteitI.setActiviteitTitle(title);
        }
        activiteitI.setKalender(k);
        dismiss();
    }

    private void btnCancel()
    {
        dismiss();
    }

    private void initSpinner(Context context, Spinner spinner)
    {
        Kalender[] kalenders = Kalender.retrieveKalenders(context);
        if (kalenders == null) {
//            Toast.makeText(this, "Error initializing calendars", Toast.LENGTH_LONG).show();
            return;
        }
        this.selectedKalender = kalenders[0];

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_dropdown_item, kalenders);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add listener
        spinner.setOnItemSelectedListener(spinnerListener);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
}
