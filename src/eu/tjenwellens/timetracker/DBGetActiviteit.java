/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

/**
 *
 * @author Tjen
 */
public interface DBGetActiviteit
{

    String getKalenderName();

    long getBeginMillis();

    long getEndMillis();

    String getActiviteitTitle();

    String getDescription();
}
