import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 * 
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.06;
    // The probability that a shrimp will be created in any given grid position.
    private static final double SHRIMP_CREATION_PROBABILITY = 0.08;    
    // The probability that a otter will be created in any given grid position.
    private static final double SEAOTTER_CREATION_PROBABILITY = 0.04;
    // The probability that a plankton will be created in any given grid position.
    private static final double PLANKTON_CREATION_PROBABILITY = 0.07;
    // The probability that a human will be created in any given grid position.
    private static final double HUMAN_CREATION_PROBABILITY = 0.10;
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.12;
    // The probability that plastic will be created in any given grid position.
    private static final double PLASTIC_CREATION_PROBABILITY = 0.14;

    // List of actors in the field.
    private List<Actor> actors;

    private Time time;
    
    private Weather weather;
    
    // The current state of the field.
    private Field field;
    private Field plantField;
    // The current step of the simulation.
    private int step;
    // The current hour in the simulation.
    private int hour;
     // A graphical view of the simulation.
    private SimulatorView view;

    
    /**
      * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        
        
        field = new Field(depth, width);
        plantField = new Field(depth, width);
        
        time = new Time();
        weather = new Weather();
        
        
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Shrimp.class, Color.ORANGE);
        view.setColor(Shark.class, Color.BLACK);
        view.setColor(Human.class, Color.MAGENTA);
        view.setColor(Otter.class, Color.CYAN);
        view.setColor(Plankton.class, Color.RED);
        view.setColor(Plant.class, Color.GREEN);
        view.setColor(Plastic.class, Color.BLUE);
        
        

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        
        time.incrementHour(step);
        hour = time.getHours();
        weather.setWeather(hour);
        
        
        // Provide space for newborn actors.
        List<Actor> newActors = new ArrayList<>();        
        // Let all actors act.
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors);
            if(!actor.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born actors to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field, hour);
        
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        hour = 0;
        actors.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field, hour);
        
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        plantField.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    actors.add(shark);
                }
                else if(rand.nextDouble() <= SHRIMP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shrimp shrimp = new Shrimp(true, field, location);
                    actors.add(shrimp);
                }
                else if(rand.nextDouble() <= SEAOTTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Otter otter = new Otter(true, field, location);
                    actors.add(otter);
                }
                else if(rand.nextDouble() <= PLANKTON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plankton plankton = new Plankton(true, field, location);
                    actors.add(plankton);
                }
                else if(rand.nextDouble() <= HUMAN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Human human = new Human(true, field, location);
                    actors.add(human);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location);
                    actors.add(plant);
                }
                else if(rand.nextDouble() <= PLASTIC_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plastic plastic = new Plastic(field, location);
                    actors.add(plastic);
                }
                // else leave the location empty.
            }
        }   
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
