import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a shrimp.
 * Shrimps age, move, breed, eat plants, can get infected and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 * 
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Shrimp extends Animal
{
    // Characteristics shared by all shrimps (class variables).

    // The age at which a shrimp can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a shrimp can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a shrimp breeding.
    private static final double BREEDING_PROBABILITY = 0.26;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of a single plant. In effect, this is the
    // number of steps a shrimp can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The shrimp's age.
    private int age;
    private int foodLevel;

    /**
     * Create a new shrimp. A shrimp may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the shrimp will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shrimp(boolean randomAge, Field field, Location location)
    {
        super(field, location, 6);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
        }
    }

    /**
     * This is what the shrimp does most of the time - it swims 
     * around and eats plants. Sometimes it will breed or die of old age.
     * @param newShrimps A list to return newly born shrimps.
     */
    public void act(List<Actor> newShrimps)
    {
        incrementAge();
        if(isInfected() && infectionKills()) {
            setDead();
        }
        else {
            cureInfection();
        }

        if(isAlive()) {
            giveBirth(newShrimps); 
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null && getField() != null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the shrimp's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this shrimp more hungry. 
     * This could result in the shrimp's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for plants adjacent to the current location.
     * Only the first live plant is eaten.
     * Check if food is plastic which will kill the shrimp when eaten.
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
            if(object instanceof Plant) {
                Plant plant = (Plant) object;
                if(plant.isAlive()) { 
                    plant.setDead();
                    return where;
                }
            }
            else if(object instanceof Plastic) {
                Plastic plastic = (Plastic) object;
                if(plastic.isAlive()) {
                    plastic.setDead();
                    setDead();
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this shrimp is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newShrimps A list to return newly born shrimps.
     */
    private void giveBirth(List<Actor> newShrimps)
    {
        // New shrimps are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shrimp young = new Shrimp(false, field, loc);
            newShrimps.add(young);
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
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY &&
        availablePartner()) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A shrimp can breed if it has reached the breeding age.
     * @return true if the shrimp can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
      * Look for a partner adjacent to the current location.
     * Only the first female is chosen.
     * @return true if partner is found, false otherwise.
     */
    private boolean availablePartner()
    {
        Field field = getField();
        List<Location> adjacent =  field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location location = it.next();
            Object animal = field.getObjectAt(location);
            if(animal instanceof Shrimp){
                Shrimp shrimp = (Shrimp) animal;
                if(shrimp.isMale() != isMale()){
                    return true;
                }
            }
        }

        return false;
    }
}
