import java.util.Random;
import java.util.List;

/**
 * A class representing plants. Plants can age and reproduce but do not move.
 * They can be eaten by other animals.
 *
 * @author Adam Tlemsani and Rakshika Kodeswaran.
 * @version 2021.03.03
 */
public class Plant implements Actor
{
    private static final Random rand = Randomizer.getRandom(); 

    private static double BREEDING_PROBABILITY = 0.12;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int MAX_AGE = 40;
    private boolean alive;
    private Field field;
    private Location location;
    private int age;

    /**
     * Create a new plant. A plant may be created with age zero
     * (a new born) or with a random age.
     * 
     * @param randomAge If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge){
            age = rand.nextInt(MAX_AGE);
        }
        else{
            age = 0;
        }
    }

    /**
     * This is what the plant does most of the time - it grows
     * with age and produces offspring. Sometimes it will die of old age or 
     * when eaten by an animal.
     * @param newPlant A list to return newly born plants. 
     */
    public void act(List<Actor> newPlant)
    {
        incrementAge();
        if(isAlive() && Weather.getCondition().equals("sunny")){
            growPlant(newPlant);
        }
        else {
            setDead();
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
     * Check whether or not this plant is to produce offspring at this step.
     * New plants will be made into free adjacent locations.
     * @param newPlants A list to return newly born plants.
     */
    public void growPlant(List<Actor> newPlants)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Plant(false, field, loc);
            newPlants.add(young);
        } 
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Increase the age. 
     * This could result in the plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Indicate that the plant is no longer alive.
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

    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
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
     * Return the plant's field.
     * @return Field the plant's field.
     */
    public Field getField()
    {
        return field;
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }
}
