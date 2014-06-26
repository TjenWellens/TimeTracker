package eu.tjenwellens.timetracker.calendar;

import android.content.Context;
import android.os.Build;

/**
 *
 * @author Tjen
 */
public abstract class VersionedCalendarOverlay {
    public static VersionedCalendarOverlay getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static VersionedCalendarOverlay makeInstance(Context context) {
        return SingletonHolder.INSTANCE = newInstance(context);
    }

    abstract Kalender[] retrieveKalenders();

    abstract void post(Evenement e);

    private static VersionedCalendarOverlay newInstance(Context context) {
        final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        VersionedCalendarOverlay vco = null;
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            vco = new Pre4Overlay(context);
        } else {
            vco = new Post4Overlay(context);
        }
        return vco;
    }

    private static class SingletonHolder {
        private static VersionedCalendarOverlay INSTANCE;
    }
}