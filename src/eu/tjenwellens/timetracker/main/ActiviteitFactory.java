/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.main;

import android.content.Context;
import eu.tjenwellens.timetracker.calendar.Evenement;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.database.DatabaseHandler;
import eu.tjenwellens.timetracker.macro.MacroI;

/**
 *
 * @author Tjen
 */
public class ActiviteitFactory
{

    private static final String SPLITTER = "\n";

    private ActiviteitFactory()
    {
    }

    public static String mergeDetailEntries(String[] strings)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i < strings.length - 1) {
                sb.append(SPLITTER);
            }
        }
        return sb.toString();
    }

    public static String[] splitDetailToEntries(String strings)
    {
        return strings.split(SPLITTER);
    }

    public static ActiviteitI createActiviteit(Context context, int id, Kalender kalender, String title, long startTimeMillis, long endTimeMillis)
    {
        Activiteit a = new Activiteit(id, kalender, title, startTimeMillis, endTimeMillis);
        a.saveDBActiviteit(context);
        return a;
    }

    public static ActiviteitI createActiviteit(Context context, int id, Kalender kalender, String title, long startTimeMillis, long endTimeMillis, String description)
    {
        Activiteit a = new Activiteit(id, kalender, title, startTimeMillis, endTimeMillis, description);
        a.saveDBActiviteit(context);
        return a;
    }

    public static ActiviteitI createActiviteit(Context context)
    {
        Activiteit a = new Activiteit();
        a.saveDBActiviteit(context);
        return a;
    }

    public static ActiviteitI createActiviteit(Context context, MacroI macro)
    {
        Activiteit a = new Activiteit(macro);
        a.saveDBActiviteit(context);
        return a;
    }

    private static class Activiteit implements ActiviteitI
    {

        private static int activityCounter = 0;
        //
        private boolean running;
        //
        private Kalender kalender = null;
        private long startTimeMillis;
        private long endTimeMillis;
        private String title;
        private int id;
        private String[] description = null;

        private Activiteit(int id, Kalender kalender, String title, long startTimeMillis, long endTimeMillis)
        {
            this.id = id;
            activityCounter = id + 1;
            this.kalender = kalender;
            this.title = title;
            this.startTimeMillis = startTimeMillis;
            this.endTimeMillis = endTimeMillis;
            this.running = endTimeMillis < 0;
            this.description = null;
        }

        private Activiteit(int id, Kalender kalender, String title, long startTimeMillis, long endTimeMillis, String description)
        {
            this.id = id;
            activityCounter = id + 1;
            this.kalender = kalender;
            this.title = title;
            this.startTimeMillis = startTimeMillis;
            this.endTimeMillis = endTimeMillis;
            this.running = endTimeMillis < 0;
            this.description = description.split(SPLITTER);
        }

        private Activiteit()
        {
            this.startTimeMillis = System.currentTimeMillis();
            this.endTimeMillis = -1;
            activityCounter++;
            this.title = "Activiteit" + activityCounter;
            this.id = activityCounter;
            this.running = true;
            this.description = null;
        }

        private Activiteit(MacroI macro)
        {
            this.startTimeMillis = System.currentTimeMillis();
            this.title = macro.getActiviteitTitle();
            this.kalender = macro.getKalender();
            this.endTimeMillis = -1;
            activityCounter++;
            this.id = activityCounter;
            this.running = true;
            this.description = null;
        }

        @Override
        public boolean stopRunning()
        {
            if (running) {
                endTimeMillis = System.currentTimeMillis();
                running = false;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean resumeRunning()
        {
            if (!running) {
                endTimeMillis = -1;
                running = true;
                return true;
            } else {
                return false;
            }
        }

        private String mergeDetails(String splitter)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < description.length; i++) {
                sb.append(description[i]);
                if (i < description.length - 1) {
                    sb.append(splitter);
                }
            }
            return sb.toString();
        }

        @Override
        public Evenement getEvenement()
        {
            if (!running && kalender != null) {
                Evenement e = new Evenement(kalender.getId(), title, startTimeMillis, endTimeMillis);
                if (description != null) {
                    e.setDescription(mergeDetails(SPLITTER));
                }
                return e;
            } else {
                return null;
            }
        }

        @Override
        public void setKalender(Kalender kalender)
        {
            this.kalender = kalender;
        }

        @Override
        public void setEndTimeMillis(long endTimeMillis)
        {
            this.endTimeMillis = endTimeMillis;
        }

        public void setStartTimeMillis(long startTimeMillis)
        {
            this.startTimeMillis = startTimeMillis;
        }

        @Override
        public void setActiviteitTitle(String title)
        {
            this.title = title;
        }

        public void setDescription(String[] description)
        {
            this.description = description;
        }

        @Override
        public String getDuration()
        {
            if (endTimeMillis < 0) {
                return "running";
            }
            long temp = (endTimeMillis - startTimeMillis) / 1000;
            int sec = (int) temp % 60;
            temp /= 60;
            int min = (int) temp % 60;
            temp /= 60;
            int hour = (int) temp % 24;
            temp /= 24;
            int day = (int) temp % 7;
            temp /= 7;
            int week = (int) temp;

            String s = "0 s";
            if (week > 0) {
                s = week + " w";
            } else if (day > 0) {
                s = day + " d";
            } else if (hour > 0) {
                s = hour + " h";
            } else if (min > 0) {
                s = min + " m";
            } else if (sec > 0) {
                s = sec + " s";
            }
            return s;
        }

        @Override
        public String getKalenderName()
        {
            if (kalender == null) {
                return null;
            }
            return kalender.getName();
        }

        @Override
        public String getStartTime()
        {
            return Time.timeToString(startTimeMillis);
        }

        @Override
        public String getStopTime()
        {
            return Time.timeToString(endTimeMillis);
        }

        @Override
        public String getActiviteitTitle()
        {
            return title;
        }

        @Override
        public int getActiviteitId()
        {
            return id;
        }

        @Override
        public boolean isRunning()
        {
            return running;
        }

        public String getDescription()
        {
            return description != null ? mergeDetails(SPLITTER) : "";
        }

        public String[] getDescriptionEntries()
        {
            return description;
        }

        public long getBeginMillis()
        {
            return startTimeMillis;
        }

        public long getEndMillis()
        {
            return endTimeMillis;
        }

        public void deleteDBActiviteit(Context context)
        {
            DatabaseHandler.getInstance(context).deleteActiviteit(this);
        }

        private void saveDBActiviteit(Context context)
        {
            DatabaseHandler.getInstance(context).addActiviteit(this);
        }

        public void updateDBActiviteit(Context context)
        {
            DatabaseHandler.getInstance(context).updateActiviteit(this);
        }
    }
}
