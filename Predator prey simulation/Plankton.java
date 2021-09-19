import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a plankton.
 * Plankton age, move, can get infected, breed and die.
 *
 * @author Adam Tlemsani and Rakshika Kodeswaran
 * @version 2021.03.03
 */
public class Plankton extends Animal
{
    // Characteristics shared by all plankton (class variables).

    // The age at which a plankton can start to breed.
    private static final int BREEDING_AGE = 8;
    // The age to which a plankton can live.
    private static final int MAX_AGE = 60;
    // The likelihood of a plankton breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of a single plant. In effect, this is the
    // number of steps a plankton can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The plankton's age.
    private int age;
    private int foodLevel;

    /**
     * Create a new plankton. A plankton may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the plankton will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plankton(boolean randomAge, Field field, Location location)
    {
        super(field, location, 3);
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
     * This is what the plankton does most of the time - it swims 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Actor> newPlankton)
    {
        incrementAge();
        if(isInfected() && infectionKills()) {
             setDead();
        }
        else {
            cureInfection();
        }// The probability that a plankton will be created in any given grid position.

        if(isAlive()) {
            giveBirth(newPlankton);            
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
     * This could result in the plankton's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this plankton more hungry. 
     * This could result in the plankton's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this plankton is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlankton A list to return newly born plankton.
     */
    private void giveBirth(List<Actor> newPlankton)
    {
        // New plankton are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shrimp young = new Shrimp(false, field, loc);
            newPlankton.add(young);
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
     * A plankton can breed if it has reached the breeding age.
     * @return true if the plankton can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Check for plastic adjacent to the current location.
     * If food is plastic then plankton will die.
     * @return Where plastic was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if(object instanceof Plastic) {
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
            if(animal instanceof Plankton){
                Plankton plankton = (Plankton) animal;
                if(plankton.isMale() != isMale()){
                    return true;
                }
            }
        }

        return false;
    }
}
