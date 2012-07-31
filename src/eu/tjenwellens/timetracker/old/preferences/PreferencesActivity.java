package eu.tjenwellens.timetracker.old.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import eu.tjenwellens.timetracker.R;

public class PreferencesActivity extends PreferenceActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
