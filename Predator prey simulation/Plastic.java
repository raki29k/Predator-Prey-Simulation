import java.util.Random;
import java.util.List;

/**
 * A simple model of plastic.
 * Plastic represents the pollution in the sea. It does not move or die.
 *
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public class Plastic implements Actor
{
    private Field field;
    private Location location;

    private static final Random rand = Randomizer.getRandom();

    private boolean alive;

    /**
     * Create new plastic.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plastic(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Place the plastic at the new location in the given field.
     * @param newLocation The plastic's new location.
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
     * Check whether the plastic is alive or not.
     * @return true if the plastic is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    public void act(List<Actor> newPlastic)
    {

    }

    /**
     * Indicate that the plastic is no longer alive.
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
     * Return the plastic's location.
     * @return The plastic's location.
     */
    private Location getLocation()
    {
        return location;
    }

    /**
     * Return the plastic's field.
     * @return Field the plastic's field.
     */
    private Field getField() 
    {
        return field;
    }

}
