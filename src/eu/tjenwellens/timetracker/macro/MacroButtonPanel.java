/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import eu.tjenwellens.timetracker.calendar.Kalender;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tjen
 */
public class MacroButtonPanel extends LinearLayout
{

    // gui
//    private EditText txtTitle, txtKalender;
//    private LinearLayout bottomPanel, buttonPanel;
//    private Button btnAdd, btnClear, btnReset;
    //
    private Context context;
    private MacroHandler macroHandler;
    private OnClickListener macroButtonhandler = new OnClickListener()
    {

        public void onClick(View view)
        {
            if (view instanceof MacroI) {
                macroHandler.startActiviteit((MacroI) view);
            }
        }
    };
    //
    private List<MacroButton> macros = new ArrayList<MacroButton>();
    private ArrayList<LinearLayout> onderverdeling = new ArrayList<LinearLayout>();
    private int sqrt = 0;

    public MacroButtonPanel(Context context, MacroHandler macroHandler)
    {
        super(context);
        this.context = context;
        this.macroHandler = macroHandler;
        initGUI(context);
    }

    public void addAllButtons(List<MacroI> macros)
    {
        if (macros != null) {
            for (MacroI macroI : macros) {
                addButtonSilent(macroI);
            }
        }
        updateSqrt();
        resetButtons();
    }

    private void addButtonSilent(MacroI macro)
    {
        MacroButton macroButton = new MacroButton(context, macro);
        macroButton.setOnClickListener(macroButtonhandler);
        macros.add(macroButton);
    }

    public void addButton(MacroI macro)
    {
        addButtonSilent(macro);

        int newSqrt = (int) Math.ceil(Math.sqrt(macros.size()));

//        if (newSqrt == sqrt) {
//            onderverdeling.get(onderverdeling.size() - 1).addView(t, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
//        } else {
        updateSqrt();
        resetButtons();
//        }
    }

    private void updateSqrt()
    {
        this.sqrt = (int) Math.ceil(Math.sqrt(macros.size()));
    }

    private void resetButtons()
    {
        // delete all
        for (LinearLayout ll : onderverdeling) {
            ll.removeAllViews();
            this.removeView(ll);
        }
        // add per onderverdeling knoppen
        if (sqrt == 0) {
            return;
        }
        for (int i = 0; i < sqrt; i++) {
            // add ondervedeling
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(HORIZONTAL);
            for (int j = 0; j < sqrt; j++) {
                if (i * sqrt + j < macros.size()) {
                    ll.addView(macros.get(i * sqrt + j), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
                }
            }
            this.addView(ll, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
            onderverdeling.add(ll);
        }
    }

    private void initGUI(Context context)
    {
        setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        lp.setMargins(10, 5, 10, 5);
        setLayoutParams(lp);
    }

    public void add(String title, Kalender kalender)
    {
        addButton(MacroFactory.createMacro(context, title, kalender));
    }

    public void reset()
    {
        for (MacroI macro : macros) {
            //TODO: klopt dit wel?
            macro.deleteDBMacro(context);
        }
        macros.clear();
        sqrt = 0;
        this.removeAllViews();
        resetButtons();
    }

    public List<MacroI> getMacros()
    {
        return new ArrayList<MacroI>(macros);
    }
}
