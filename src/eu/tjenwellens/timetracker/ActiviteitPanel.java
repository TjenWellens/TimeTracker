/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import android.content.Context;
import android.view.View;
import android.widget.*;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author tjen
 */
public class ActiviteitPanel extends LinearLayout implements ActiviteitI
{

    private RadioButton radioButton;
    private LinearLayout pnlText;
    private TextView tvName, tvCalendar, tvStartTime, tvDuration;
    private Button saveButton, stopButton;
    private Activiteit activiteit;
    private ActiviteitHandler activiteitHandler;

    public ActiviteitPanel(Context context, ActiviteitHandler activiteitHandler)
    {
        super(context);
        activiteit = new Activiteit();
        this.activiteitHandler = activiteitHandler;
        initGUI(context);
    }

    private void initGUI(Context context)
    {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        {
            radioButton = new RadioButton(context);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {

                public void onCheckedChanged(CompoundButton cb, boolean isChecked)
                {
                    if (isChecked && activiteitHandler != null)
                    {
                        activiteitHandler.radioButtonChecked(ActiviteitPanel.this);
                    }
                }
            });
            pnlText = new LinearLayout(context);
            pnlText.setOrientation(LinearLayout.VERTICAL);
            pnlText.setClickable(true);
            pnlText.setOnClickListener(new OnClickListener()
            {

                public void onClick(View arg0)
                {
                    if (activiteitHandler != null)
                    {
                        activiteitHandler.activiteitEdit(ActiviteitPanel.this);
                    }
                }
            });
            {
                tvName = new TextView(context);
                updateTVName();
                tvCalendar = new TextView(context);
                updateTVCalendar();
                tvStartTime = new TextView(context);
                updateTVStart();
                tvDuration = new TextView(context);
                updateTVDuration();
                //
                pnlText.addView(tvName, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                pnlText.addView(tvCalendar, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                pnlText.addView(tvStartTime, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                pnlText.addView(tvDuration, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }
            saveButton = new Button(context);
            stopButton = new Button(context);
            {
                saveButton.setText(R.string.save);
                stopButton.setText(R.string.stop);
                saveButton.setEnabled(false);
                stopButton.setEnabled(true);
                saveButton.setOnClickListener(new OnClickListener()
                {

                    public void onClick(View arg0)
                    {
                        if (activiteitHandler != null && !isRunning())
                        {
                            activiteitHandler.activiteitSave(ActiviteitPanel.this);
                        }
                    }
                });
                stopButton.setOnClickListener(new OnClickListener()
                {

                    public void onClick(View arg0)
                    {
                        if (stopRunning() && activiteitHandler != null)
                        {
                            activiteitHandler.activiteitStop(ActiviteitPanel.this);
                        }
                    }
                });
            }
            //
            this.addView(radioButton, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            // bepaalt hoogte: 
            this.addView(pnlText, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5f));
            this.addView(saveButton, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f));
            this.addView(stopButton, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f));
        }
    }

    public boolean stopRunning()
    {
        if (activiteit.stopRunning())
        {
            // enable save, disable stop
            saveButton.setEnabled(true);
            stopButton.setEnabled(false);
            updateTVDuration();
            return true;
        }
        return false;
    }

    public boolean resumeRunning()
    {
        if (activiteit.resumeRunning())
        {
            // disable save, enable stop
            saveButton.setEnabled(false);
            stopButton.setEnabled(true);
            updateTVDuration();
            return true;
        }
        return false;
    }

    public Evenement getEvenement()
    {
        return activiteit.getEvenement();
    }

    public void setKalender(Kalender kalender)
    {
        activiteit.setKalender(kalender);
        updateTVCalendar();
    }

    public void setEndTimeMillis(long endTimeMillis)
    {
        activiteit.setEndTimeMillis(endTimeMillis);
        updateTVDuration();
    }

    public void setStartTimeMillis(long startTimeMillis)
    {
        activiteit.setStartTimeMillis(startTimeMillis);
        updateTVStart();
        updateTVDuration();
    }

    public void setActiviteitTitle(String title)
    {
        activiteit.setActiviteitTitle(title);
        updateTVName();
    }

    public String getDuration()
    {
        return activiteit.getDuration();
    }

    public String getKalenderName()
    {
        return activiteit.getKalenderName();
    }

    public String getStartTime()
    {
        return activiteit.getStartTime();
    }

    public String getStopTime()
    {
        return activiteit.getStopTime();
    }

    public String getActiviteitTitle()
    {
        return activiteit.getActiviteitTitle();
    }

    public long getActiviteitId()
    {
        return activiteit.getActiviteitId();
    }

    public boolean isRunning()
    {
        return activiteit.isRunning();
    }

    private void updateTVName()
    {
        tvName.setText(activiteit.getActiviteitTitle());
    }

    private void updateTVCalendar()
    {
        tvCalendar.setText(activiteit.getKalenderName());
    }

    private void updateTVStart()
    {
        tvStartTime.setText(activiteit.getStartTime());
    }

    private void updateTVDuration()
    {
        tvDuration.setText(activiteit.getDuration());
    }

    public void checkRadioButton()
    {
        radioButton.setChecked(true);
    }

    public void uncheckRadioButton()
    {
        radioButton.setChecked(false);
    }
}
