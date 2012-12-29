package eu.tjenwellens.timetracker.detail.detailsettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    /*
     * Init details
     */
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

    /*
     * Exit & save
     */
    private void launchSave()
    {
        final EditText txtDetail = (EditText) findViewById(R.id.txtDetailSettingsDetail);
        detailString = txtDetail.getText().toString();
        Intent returnIntent = new Intent();
        MainActivity.arrayToIntent(returnIntent, ActiviteitFactory.splitDetailToEntries(detailString), ActivityResults.KEY_ACTIVITEIT_DETAIL_SETTINGS);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /*
     * Exit, do not save
     */
    private void launchCancel()
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /*
     * Handles back button
     */
    @Override
    public void onBackPressed()
    {
        launchSave();
    }

    /*
     * Handles activity creation
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String[] detail = MainActivity.intentToArray(getIntent(), ActivityResults.KEY_ACTIVITEIT_DETAIL_SETTINGS);
        // GUI
        setContentView(R.layout.detail_settings);
        initDetails(detail);
    }

    /*
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detail_settings_menu, menu);
        return true;
    }

    /*
     * Handle menu
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
