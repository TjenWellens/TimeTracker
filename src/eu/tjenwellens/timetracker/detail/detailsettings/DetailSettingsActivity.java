/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.detail.detailsettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import eu.tjenwellens.timetracker.ActivityResults;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.main.ActiviteitFactory;
import eu.tjenwellens.timetracker.main.MainActivity;

/**
 *
 * @author Tjen
 */
public class DetailSettingsActivity extends Activity
{
    private String detailString;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String[] detail = MainActivity.intentToArray(getIntent(), ActivityResults.KEY_ACTIVITEIT_DETAIL_SETTINGS);
        // GUI
        initDetailSettingsGUI();
        initDetails(detail);
    }

    private void initDetailSettingsGUI()
    {
        setContentView(R.layout.detail_settings);
    }

    private void initDetails(String[] detail)
    {
        if (detail == null)
        {
            return;
        }
        final EditText txtDetail = (EditText) findViewById(R.id.txtDetailSettingsDetail);
        txtDetail.setText(ActiviteitFactory.mergeDetailEntries(detail));
        txtDetail.setSelection(txtDetail.getText().length());
    }

    public void btnDetailSettingsCancel(View button)
    {
        launchCancel();
    }

    public void btnDetailSettingsSave(View button)
    {
        launchSave();
    }

    private void launchSave()
    {
        final EditText txtDetail = (EditText) findViewById(R.id.txtDetailSettingsDetail);
        detailString = txtDetail.getText().toString();
        Intent returnIntent = new Intent();
        MainActivity.arrayToIntent(returnIntent, ActiviteitFactory.splitDetailToEntries(detailString), ActivityResults.KEY_ACTIVITEIT_DETAIL_SETTINGS);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void launchCancel()
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detail_settings_menu, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.menu_save:
                launchSave();
                return true;
            case R.id.menu_cancel:
                launchCancel();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
