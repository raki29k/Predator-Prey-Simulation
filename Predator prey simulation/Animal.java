import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 * 
 * @author Adam Tlemsani and Rakshika Kodeswaran
 * @version 2021.03.03
 */
public abstract class Animal implements Actor
{
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // Whether the animal is male or not.
    private boolean isMale;
    // Amount of food gained by eating this animal.
    private int foodLevel;
    // Whether the animal is alive or not.
    private boolean alive;
    // Whether the animal is infected or not.
    private boolean isInfected;
    // Whether the infection will kill an animal or not.
    private boolean infectionKills;
    // The total number of infected animals.
    private static int numberOfInfected;
    // The probability of an animal infecting another one.
    private static final double INFECTION_PROBABILITY = 0.12;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param foodLevel The food gained by eating an animal.
     */
    public Animal(Field field, Location location, int foodLevel)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        this.foodLevel = foodLevel;
        Random random = new Random();
        isMale = (random.nextInt(2) == 0) ? true : false; 
        isInfected = (random.nextInt(5) < 2) ? true : false;
        infectionKills = (random.nextInt(4) == 0) ? true : false;
        numberOfInfected = 0;

    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Actor> newAnimals);

    /**
     * Check whether the animal is male or not.
     * @return true if the animal is male.
     */
    public boolean isMale()
    {
        return isMale;
    }

    /**
     * Return the animal's food level.
     * @return The animal's food level.
     */
    public int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        if(alive) {
            field.place(this, newLocation);
        }
    }

    /**
     * Check for an animal adjacent to the current location to
     * spread the infection.
     */
    protected void spreadInfection()
    {
        if(location != null) {
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            Random random = new Random();
            while(it.hasNext()) {
                Location location = it.next();
                Object object = field.getObjectAt(location);
                if(object instanceof Animal) {
                    Animal animal = (Animal) object;
                    if(animal.isAlive() && isInfected) {
                        if(random.nextDouble() <= INFECTION_PROBABILITY) {
                            animal.startInfection();
                        }
                    }
                }
            }
        }
    }

    /**
     * Start the infection at random whether animal is infected or not.
     */
    protected  void startInfection()
    {   
        isInfected = !isInfected;
        // increment the number of infected animals.
        if(isInfected()) {
            incrementInfected();
        }
    }

    /**
     * @return If animal is infected.
     */
    protected boolean isInfected()
    {
        return isInfected;
    }

    /**
     * @return If infection will kill animal.
     */
    protected boolean infectionKills()
    {
        return infectionKills;
    }

    /**
     * Increase the total number of animals infected.
     */
    protected void incrementInfected()
    {
        numberOfInfected++;
    }

    /**
     * Reset the infection total to zero.
     */
    protected void resetNumberOfInfected()
    {
        numberOfInfected = 0;
    }

    /**
     * @return The total number of infected animals.
     */
    public static int getNumberOfInfected()
    {
        return numberOfInfected;
    }

    /**
     * Each animal has a random chance to cure the infection 
     * by themselves.
     */
    protected void cureInfection()
    {
        Random random = new Random();
        if(isInfected) {
            infectionKills = (random.nextInt(11) == 1 ? false : true);
        }
    }

}