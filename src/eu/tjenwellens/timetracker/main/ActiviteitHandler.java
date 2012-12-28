/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.main;

/**
 *
 * @author tjen
 */
public interface ActiviteitHandler
{
    void activiteitSave(ActiviteitPanel a);

    void activiteitEdit(ActiviteitPanel a);

//    void radioButtonChecked(ActiviteitPanel a);
//    void activiteitStop(ActiviteitPanel a);

    void longClick(ActiviteitPanel a);
}
