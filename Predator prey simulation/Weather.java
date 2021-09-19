import java.util.Random;

/**
 * A class representing weather.
 * It keeps track of the temperature and different weather conditions
 * that can occur.
 *
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Weather
{
    private static Random rand = Randomizer.getRandom();
    private static int temperature;
    
    private boolean isSunny;
    private boolean isRaining;
    private boolean isCloudy;
    // stores the weather condition.
    private static String weatherCondition;
    /**
     * Create a weather object and randomly assign temperature 
     * to a value up to 30.
     */
    public Weather()
    {
        temperature = rand.nextInt(31);
        setCondition();
        setTemperature();
    }

    /**
     * Set the weather to one of four conditions:
     *    sunny, raining, cloudy and normal.
     */
    private void setCondition()
    {
        if(rand.nextDouble() <= 0.5) {
            isSunny = true;
            weatherCondition = "sunny";
        }
        else if(rand.nextDouble() <= 0.9) {
            isRaining = true;
            weatherCondition = "raining";
        }
        else if(rand.nextDouble() <= 0.2) {
            isCloudy = true;
            weatherCondition = "cloudy";
        }
        else{
            weatherCondition = "normal";
        }
    }

    /**
     * Set the temperature according to each weather condition.
     */
    private void setTemperature()
    {
        if(isSunny) {
            temperature = rand.nextInt(18);
        }
        else if(isRaining) {
            temperature = rand.nextInt(7);
        }
        else if(isCloudy) {
            temperature = rand.nextInt(5);
        }  
    }

    /**
     * @return The temperature.
     */
    public static int getTemperature()
    {
        return temperature;
    }

    /**
     * @return The weather condition.
     */
    public static String getCondition()
    {
        return weatherCondition;
    }

    /**
     * Set and update the weather every 8 hours.
     * @param hour Every hour that passes in the simulation.
     */
    public void setWeather(int hour)
    {
        if(hour % 8 == 0) {
            setCondition();
            setTemperature();
        }
    }
}