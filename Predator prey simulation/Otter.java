import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a otter.
 * Otters age, move, eat shrimp and plankton, can get infected,
 * breed and die.
 *
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Otter extends Animal
{
    // Characteristics shared by all otters (class variables).

    // The age at which an otter can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which an otter can live.
    private static final int MAX_AGE = 120;
    // The likelihood of an otter breeding.
    private static final double BREEDING_PROBABILITY = 0.40;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single shrimp. In effect, this is the
    // number of steps an otter can go before it has to eat again.
    private static final int SHRIMP_FOOD_VALUE = 18;
    // The food value of a single plankton.
    private static final int PLANKTON_FOOD_VALUE = 18;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The otter's age.
    private int age;
    // The otter's food level, which is increased by eating shrimp
    // and plankton.
    private int foodLevel;
    
    /**
     * Create a new otter. An otter may be created with age zero
     * (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Otter(boolean randomAge, Field field, Location location)
    {
        super(field, location, 9);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SHRIMP_FOOD_VALUE + PLANKTON_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SHRIMP_FOOD_VALUE + PLANKTON_FOOD_VALUE;
        }
    }

    /**
     * This is what the otter does most of the time - it swims 
     * around. Sometimes it will breed or die of old age.
     * If it catches a disease then there is a given chance it will
     * die from infection.
     * @param newOtters A list to return newly born otters.
     */
    public void act(List<Actor> newOtters)
    {
        incrementAge();
        incrementHunger();
        if(isInfected() && infectionKills()) {
            setDead();
        }
        else{
            cureInfection();
        }

        if(isAlive()) {
            giveBirth(newOtters);            
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

    /**
     * Increase the age. 
     * This could result in the otter's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this otter more hungry. 
     * This could result in the otter's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for shrimp or plankton adjacent to the current location.
     * Only the first live animal is eaten.
     * Check if food is plastic which will kill the otter when eaten.
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
            if(object instanceof Shrimp) {
                Shrimp shrimp = (Shrimp) object;
                if(shrimp.isAlive()) { 
                    shrimp.setDead();
                    foodLevel = SHRIMP_FOOD_VALUE;
                    return where;
                }
            }
            else if(object instanceof Plankton) {
                Plankton plankton = (Plankton) object;
                if(plankton.isAlive()) {
                    plankton.setDead();
                    foodLevel = PLANKTON_FOOD_VALUE;
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
     * Check whether or not this otter is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOtters A list to return newly born otters.
     */
    private void giveBirth(List<Actor> newOtters)
    {
        // New otters are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Otter young = new Otter(false, field, loc);
            newOtters.add(young);
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
     * An otter can breed if it has reached the breeding age.
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
            if(animal instanceof Otter){
                Otter otter = (Otter) animal;
                if(otter.isMale() != isMale()){
                    return true;
                }
            }
        }

        return false;
    }
}
