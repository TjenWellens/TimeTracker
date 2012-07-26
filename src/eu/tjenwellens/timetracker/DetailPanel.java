/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * @author Tjen
 */
public class DetailPanel extends LinearLayout
{

    private DetailHandler detailHandler;
    private String detailText;
    //
    private TextView tvDetailEntry;
    private Button deleteButton;

    public DetailPanel(Context context, DetailHandler detailHandler, String detailText)
    {
        super(context);
        this.detailHandler = detailHandler;
        this.detailText = detailText;
        initGUI(context);
    }

    private void initGUI(Context context)
    {
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);
        setLayoutParams(lp);
        setBackgroundColor(Color.LTGRAY);
        {
            tvDetailEntry = new TextView(context);
            tvDetailEntry.setText(detailText);
            this.addView(tvDetailEntry, new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            deleteButton = new Button(context);
            deleteButton.setText("Delete");
            this.addView(deleteButton, new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            deleteButton.setOnClickListener(new OnClickListener()
            {

                public void onClick(View arg0)
                {
                    detailHandler.deleteDetail(DetailPanel.this);
                }
            });
        }
    }

    public String getDetailText()
    {
        return detailText;
    }
}