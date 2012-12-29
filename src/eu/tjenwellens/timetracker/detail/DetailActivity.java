package eu.tjenwellens.timetracker.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import eu.tjenwellens.timetracker.ActivityResults;
import eu.tjenwellens.timetracker.R;
import eu.tjenwellens.timetracker.detail.detailsettings.DetailSettingsActivity;
import eu.tjenwellens.timetracker.main.MainActivity;
import eu.tjenwellens.timetracker.main.Time;
import java.util.ArrayList;

/**
 *
 * @author Tjen
 */
public class DetailActivity extends Activity implements DetailHandler
{
    private ArrayList<DetailPanel> currentDetails = new ArrayList<DetailPanel>();
    private String[] details;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String[] detail = MainActivity.intentToArray(getIntent(), ActivityResults.KEY_ACTIVITEIT_DETAIL);
        // GUI
        setContentView(R.layout.detail);
        if (detail != null)
        {
            initDetails(detail);
        }
    }

    private String[] detailsFromPanels()
    {
        String[] test = new String[currentDetails.size()];
        for (int i = 0; i < test.length; i++)
        {
            test[i] = currentDetails.get(i).getDetailText();
        }
        return test;
    }

    public void btnDetailTime(View button)
    {
        EditText et = (EditText) findViewById(R.id.txtDetail);
        et.setText(Time.timeToString(System.currentTimeMillis()) + " " + et.getText().toString());
        et.setSelection(et.getText().length());
    }

    public void btnDetailClear(View button)
    {
        EditText et = (EditText) findViewById(R.id.txtDetail);
        et.setText(R.string.none);
    }

    public void btnDetailAdd(View button)
    {
        EditText et = (EditText) findViewById(R.id.txtDetail);
        String text = et.getText().toString();
        addDetail(new DetailPanel(this, this, text));
        et.setText(R.string.none);
    }

    private void addDetail(DetailPanel dp)
    {
        final LinearLayout detailContainer = (LinearLayout) findViewById(R.id.pnlDetail);
        detailContainer.addView(dp);
        currentDetails.add(dp);
    }

    private void removeDetails()
    {
        final LinearLayout detailContainer = (LinearLayout) findViewById(R.id.pnlDetail);
        detailContainer.removeAllViews();
        currentDetails.clear();
    }

    private void initDetails(String[] s)
    {
        if (s == null)
        {
            return;
        }
        details = s;
        for (String string : details)
        {
            addDetail(new DetailPanel(this, this, string));
        }
    }

    /*
     * Deletes detail entry
     */
    @Override
    public void deleteDetail(DetailPanel dp)
    {
        final ViewGroup detailViewContainer = (ViewGroup) findViewById(R.id.pnlDetail);
        if (currentDetails.remove(dp))
        {
            detailViewContainer.removeView(dp);
        }
        Toast.makeText(this, "Detail deleted", Toast.LENGTH_SHORT).show();
    }

    /*
     * Launch MacroSettingsActivity
     */
    @Override
    public void editDetail(DetailPanel dp)
    {
        launchEdit();
    }

    /*
     * Launch MacroSettingsActivity
     */
    private void launchEdit()
    {
        Intent intent = new Intent(this, DetailSettingsActivity.class);
        MainActivity.arrayToIntent(intent, detailsFromPanels(), ActivityResults.KEY_ACTIVITEIT_DETAIL_SETTINGS);
        startActivityForResult(intent, ActivityResults.DETAIL_EDIT);
    }

    /*
     * Quit & save changes
     */
    private void launchSave()
    {
        details = detailsFromPanels();
        Intent returnIntent = new Intent();
        MainActivity.arrayToIntent(returnIntent, details, ActivityResults.KEY_ACTIVITEIT_DETAIL);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /*
     * Quit without saving changes
     */
    private void launchCancel()
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /*
     * Handle input from started activities
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ActivityResults.DETAIL_EDIT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // update detail
                String[] detail = MainActivity.intentToArray(data, ActivityResults.KEY_ACTIVITEIT_DETAIL_SETTINGS);
                removeDetails();
                initDetails(detail);
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Changes cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Handle back button
     */
    @Override
    public void onBackPressed()
    {
        launchSave();
    }

    /*
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
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
            case R.id.menu_edit:
                launchEdit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}