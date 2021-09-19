
/**
 * A class representing time. 
 * It keeps track of the time and whether it is day or night.
 *
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Time
{
    // hour that passes for each step taken.
    private int hour;
    // Whether the time is night or not.
    private static boolean isNight;

    private static String time;
    /**
     * Represent time in the simulator.
     */
    public Time()
    {
        hour = 0;
        isNight = false;
        time = "night";
    }

    /**
     * @return true if it is night, false otherwise.
     */
    public static boolean isNight()
    {
        return isNight;
    }

    /**
     * @return the hours that have passed.
     */
    public int getHours()
    {
        return hour;
    }

    /**
     * Increase the hour as step increases. For one step an hour passes.
     * When hour reaches 24, set to zero.
     * @param step The step an actor takes.
     */
    public void incrementHour(int step)
    {
        if(step % 1 == 0) {
            hour++;
            hour = hour % 24;
            setNight();
        }
    }

    /**
     * Set the time between 9pm and 5am as night. 
     */
    private void setNight()
    {
        if(hour < 6 && hour > 20) {
            isNight = true;
            time = "night";
        }
        else {
            isNight = false;
            time = "day";
        }
    }

    /**
     *  @return The string time for day or night.
     */
    public static String getTime()
    {
        return time;
    }
}

