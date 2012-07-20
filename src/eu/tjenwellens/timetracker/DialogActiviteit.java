/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author tjen
 */
public class DialogActiviteit extends Dialog
{

    private LinearLayout mainpanel, buttonPanel;
    private TextView tvName, tvCal, tvBegin, tvEnd;
    private EditText txtName, txtCal;
    private Button btnBegin, btnEnd, btnCancel, btnOK;
    private ActiviteitI activiteitI;
    private GetKalenders getKalenders;

    public DialogActiviteit(Context context, ActiviteitI activiteitI, GetKalenders getKalenders)
    {
        super(context);
        this.activiteitI = activiteitI;
        this.getKalenders = getKalenders;
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
            tvName.setText("Name");
            mainpanel.addView(tvName);
            txtName = new EditText(context);
            txtName.setText(activiteitI.getActiviteitTitle());
            mainpanel.addView(txtName);
            // CALENDAR
            tvCal = new TextView(context);
            tvCal.setText("Calendar");
            mainpanel.addView(tvCal);
            txtCal = new EditText(context);
            txtCal.setText(activiteitI.getKalenderName());
            mainpanel.addView(txtCal);
            // BEGIN
            tvBegin = new TextView(context);
            tvBegin.setText("Begin");
            mainpanel.addView(tvBegin);
            btnBegin = new Button(context);
            btnBegin.setText(activiteitI.getStartTime());
            // TODO: time picker
            mainpanel.addView(btnBegin);
            // END
            tvEnd = new TextView(context);
            tvEnd.setText("End");
            mainpanel.addView(tvEnd);
            btnEnd = new Button(context);
            String stoptime = activiteitI.getStopTime();
            if (stoptime != null)
            {
                btnEnd.setText(activiteitI.getStopTime());
                // TODO: time picker
            } else
            {
                btnEnd.setText("running");
                btnEnd.setEnabled(false);
            }
            // TODO: add listener
            mainpanel.addView(btnEnd);
            // BUTTON
            buttonPanel = new LinearLayout(context);
            {
                // CANCEL
                btnCancel = new Button(context);
                btnCancel.setText("Cancel");
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
                btnOK.setText("OK");
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
        String kName = txtCal.getText().toString();
        Kalender k = getKalenders.getKalendarByName(kName);
        if (title != null)
        {
            activiteitI.setActiviteitTitle(title);
        }
        if (k != null)
        {
            activiteitI.setKalender(k);
        }
        dismiss();
    }

    private void btnCancel()
    {
        dismiss();
    }
}
