import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 *  A simple model of a human.
 *  Humans age, eat fish and die.
 *
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Human implements Actor 
{
    // The human's field.
    private Field field;
    // The human's position in the field.
    private Location location;
    // The age to which a human can live.
    private static final int MAX_AGE = 150;

    private static final Random rand = Randomizer.getRandom();

    private int age;
    private int foodLevel;
    private boolean alive;

    /**
     * Create a new human. A human may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the human will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Human(boolean randomAge,Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge){
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(35);
        }
        else{
            age = 0;
            foodLevel = 15;
        }
    }

    /**
     * This is what the human does most of the time - it hunts 
     * for fish and eats them. Sometimes it will breed or die of old age.
     * @param newHumans A list to return newly born humans. 
     */
    public void act(List<Actor> newHumans)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && !Time.isNight()) { 
            if(!Weather.getCondition().equals("raining")) {
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null && getField() != null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }

            }
        }
    }

    /**
     * Check whether the human is alive or not.
     * @return true if the human is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * Check if fish is infected which will kill the human when eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if(object instanceof Animal) {
                Animal animal = (Animal) object;
                if(animal.isAlive()) { 
                    animal.setDead();
                    foodLevel = animal.getFoodLevel();
                    return where;
                }
                else if(animal.isInfected()) {
                    animal.setDead();
                    setDead();
                }
            }
        }
        return null;
    }

    /**
     * Increase the age.
     * This could result in the human's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this human more hungry. 
     * This could result in the human's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Return the human's location.
     * @return The human's location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Place the human at the new location in the given field.
     * @param newLocation The human's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the human's field.
     * @return Field the human's field.
     */
    public Field getField()
    {
        return field;
    }

    /**
     * Indicate that the human is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
}
