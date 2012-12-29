package eu.tjenwellens.timetracker.main;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.macro.MacroI;

/**
 *
 * @author tjen
 */
public class ActiviteitPanel extends LinearLayout implements ActiviteitI
{
    private LinearLayout pnlText;
    private TextView tvName, tvCalendar, tvStartTime, tvDuration;
    private Button saveButton, stopButton;
    private ActiviteitI activiteit;
    private ActiviteitHandler activiteitHandler;

    public ActiviteitPanel(Context context, ActiviteitHandler activiteitHandler)
    {
        super(context);
        this.activiteit = ActiviteitFactory.createActiviteit(context);
        this.activiteitHandler = activiteitHandler;
        initGUI(context);
    }

    public ActiviteitPanel(Context context, ActiviteitHandler activiteitHandler, ActiviteitI activiteit)
    {
        super(context);
        this.activiteit = activiteit;
        this.activiteitHandler = activiteitHandler;
        initGUI(context);
    }

    public ActiviteitPanel(Context context, ActiviteitHandler activiteitHandler, MacroI macro)
    {
        super(context);
        this.activiteit = ActiviteitFactory.createActiviteit(context, macro);
        this.activiteitHandler = activiteitHandler;
        initGUI(context);
    }

    private void initGUI(Context context)
    {
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);
        setLayoutParams(lp);
        setBackgroundColor(Color.LTGRAY);
        {
            pnlText = new LinearLayout(context);
            pnlText.setOrientation(LinearLayout.VERTICAL);
            pnlText.setClickable(true);
            pnlText.setOnClickListener(new OnClickListener()
            {
                public void onClick(View arg0)
                {
                    if (activiteitHandler != null)
                    {
                        activiteitHandler.shortClick(ActiviteitPanel.this);
                    }
                }
            });
            pnlText.setLongClickable(true);
            pnlText.setOnLongClickListener(new OnLongClickListener()
            {
                public boolean onLongClick(View arg0)
                {
                    if (activiteitHandler != null)
                    {
                        activiteitHandler.longClick(ActiviteitPanel.this);
                        return true;
                    }
                    return false;
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
//                saveButton.setEnabled(!activiteit.isRunning());
//                stopButton.setEnabled(activiteit.isRunning());
                updateButtons();
                saveButton.setOnClickListener(new OnClickListener()
                {
                    public void onClick(View arg0)
                    {
                        if (activiteitHandler != null && !isRunning())
                        {
                            activiteitHandler.saveActiviteit(ActiviteitPanel.this);
                        }
                    }
                });
                stopButton.setOnClickListener(new OnClickListener()
                {
                    public void onClick(View arg0)
                    {
                        stopRunning();
                    }
                });
            }
            // bepaalt hoogte: 
            this.addView(pnlText, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5f));
            this.addView(saveButton, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            this.addView(stopButton, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        }
    }

    public boolean stopRunning()
    {
        if (activiteit.stopRunning())
        {
            updateButtons();
            updateTVDuration();
            return true;
        }
        return false;
    }

    public boolean resumeRunning()
    {
        if (activiteit.resumeRunning())
        {
            updateButtons();
            updateTVDuration();
            return true;
        }
        return false;
    }

    private void updateButtons()
    {
        saveButton.setEnabled(!activiteit.isRunning());
        stopButton.setEnabled(activiteit.isRunning());
    }

    public void updateActiviteit(ActiviteitI a)
    {
        this.activiteit = a;
        updateAll();
    }

    private void updateAll()
    {
        updateButtons();
        updateTVCalendar();
        updateTVDuration();
        updateTVName();
        updateTVStart();
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

    public int getActiviteitId()
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

    public void setDescription(String[] description)
    {
        activiteit.setDescription(description);
    }

    public String getDescription()
    {
        return activiteit.getDescription();
    }

    public String[] getDescriptionEntries()
    {
        return activiteit.getDescriptionEntries();
    }

    public void deleteDBActiviteit(Context context)
    {
        activiteit.deleteDBActiviteit(context);
    }

    public void updateDBActiviteit(Context context)
    {
        activiteit.updateDBActiviteit(context);
    }

    public long getBeginMillis()
    {
        return activiteit.getBeginMillis();
    }

    public long getEndMillis()
    {
        return activiteit.getEndMillis();
    }
}
