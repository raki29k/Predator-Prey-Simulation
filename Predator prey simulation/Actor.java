import java.util.List;

/**
 * A class representing shared characteristics of actors.
 *
 * @author Rakshika Kodeswaran and Adam Tlemsani
 * @version 2021.03.03
 */
public interface Actor 
{
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param  newActors A list to receive new actors.
     */
    void act(List<Actor> newActors);

    /**
     * Check whether the actor is alive or not.
     * @return true if the actor is still alive.
     */
    boolean isAlive();

    /**
     * Place the actor at the new location in the given field.
     * @param newLocation The actor's new location.
     */
    void setLocation(Location newLocation);
}
