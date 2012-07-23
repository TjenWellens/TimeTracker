/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker;

/**
 *
 * @author tjen
 */
public interface ActiviteitHandler
{

    void activiteitSave(ActiviteitPanel a);

    void activiteitEdit(ActiviteitPanel a);

    void radioButtonChecked(ActiviteitPanel a);

    void activiteitStop(ActiviteitPanel a);
}
