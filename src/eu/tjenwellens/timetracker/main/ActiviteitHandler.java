package eu.tjenwellens.timetracker.main;

/**
 *
 * @author tjen
 */
public interface ActiviteitHandler
{
    void saveActiviteit(ActiviteitPanel a);

    void shortClick(ActiviteitPanel a);

    void longClick(ActiviteitPanel a);
}
