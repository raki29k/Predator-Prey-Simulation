import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 * Sharks age, move, eat shrimp and plankton, can get infected,
 * breed and die. 
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 * 
 * @author Adam Tlemsani and Rakshika Kodeswaran
 * @version 2021.03.03
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).

    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a shark can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.24;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The food value of a single shrimp. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int SHRIMP_FOOD_VALUE = 12;
    // The food value of a single plankton. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int PLANKTON_FOOD_VALUE = 18;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields). 
    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating shrimps 
    // and plankton.
    private int foodLevel;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location, 15);
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
     * This is what the shark does most of the time: it hunts for
     * shrimp and plankton. In the process, it might breed, die of hunger,
     * or die of old age.
     * If it catches a disease then there is a given chance it will
     * die from infection.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Actor> newSharks)
    {
        incrementAge();
        incrementHunger();
        if(isInfected() && infectionKills()) {
            setDead();
        }
        else {
            cureInfection();
        }

        if(isAlive()) {
            giveBirth(newSharks);            
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
     * This could result in the shark's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this shark more hungry.
     * This could result in the shark's death.
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
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object food = field.getObjectAt(where);
            if(food instanceof Shrimp) {
                Shrimp shrimp = (Shrimp) food;
                if(shrimp.isAlive()) { 
                    shrimp.setDead();
                    foodLevel = SHRIMP_FOOD_VALUE;
                    return where;
                }
            }
            else if(food instanceof Plankton) {
                Plankton plankton = (Plankton) food;
                if(plankton.isAlive()) {
                    plankton.setDead();
                    foodLevel = PLANKTON_FOOD_VALUE;
                    return where;
                }
            }
            else if(food instanceof Plastic) {
                Plastic plastic = (Plastic) food;
                if(plastic.isAlive()) {
                    plastic.setDead();
                    setDead();
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSharks A list to return newly born sharks.
     */
    private void giveBirth(List<Actor> newSharks)
    {
        // New sharks are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newSharks.add(young);
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
     * A shark can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE; 
    }

     /**
     * Look for a partner adjacent to the current location.
     * Only the first female is chosen.
     * Check if food is plastic which will kill the shark when eaten.
     * @return true if partner is found, false otherwise.
     */
    private boolean availablePartner()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location location = it.next();
            Object animal = field.getObjectAt(location);
            if(animal instanceof Shark){
                Shark shark = (Shark) animal;
                if(shark.isMale() != isMale()){
                    return true;
                }
            }
        }
        return false;
    }
}