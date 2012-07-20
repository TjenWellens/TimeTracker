/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

import eu.tjenwellens.timetracker.calendar.Kalender;

/**
 *
 * @author tjen
 */
interface GetKalenders
{
    Kalender getKalendarByName(String name);
}
