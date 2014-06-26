package eu.tjenwellens.timetracker;

import android.app.Application;
import eu.tjenwellens.timetracker.calendar.VersionedCalendarOverlay;

/**
 *
 * @author Tjen
 */
public class TimeTrackerApplication extends Application {
    private VersionedCalendarOverlay vco = VersionedCalendarOverlay.makeInstance(this);

    public VersionedCalendarOverlay getVco() {
        return vco;
    }
}
