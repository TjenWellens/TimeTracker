/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.macro.macrosettings;

import eu.tjenwellens.timetracker.macro.MacroI;

/**
 *
 * @author Tjen
 */
public interface MacroSettingsHandler
{

    void deleteMacro(MacroSettingsPanel macroSettingsPanel);
    void updateMacro(MacroI macro);
}
