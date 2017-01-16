package me.ialistannen.bukkitpluginutilities.particle.effect.shapes;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Draws a line
 */
public class AxisAlignedLine extends AbstractParticleShape {

    @SuppressWarnings("WeakerAccess")
    protected Location first, second;

    /**
     * @param granularity The granularity. The granularity is the distance
     * between each spawned particle
     * @param particle The Particle to use
     * @param first The first end point of the line
     * @param second The second end point of the line
     *
     * @throws IllegalArgumentException If the worlds do not match or it covers
     *                                  more than one axis
     */
    @SuppressWarnings("WeakerAccess")
    public AxisAlignedLine(double granularity, Particle particle, Location first, Location second) {
        super(Orientation.HORIZONTAL, granularity, particle);

        // God, THAT is dirty...
        setFirst(first);
        setSecond(second);
    }

    /**
     * @param first The first location of the line
     *
     * @throws IllegalArgumentException If the worlds do not match
     */
    public void setFirst(Location first) {
        ensureWoldIsSame(first, second);
        ensureOneAxis(first, second);

        this.first = first.clone();
    }

    /**
     * @return The first point of the line. A clone.
     */
    @SuppressWarnings("WeakerAccess")
    public Location getFirst() {
        return first.clone();
    }

    /**
     * @param second The second location of the line
     *
     * @throws IllegalArgumentException If the worlds do not match
     */
    public void setSecond(Location second) {
        ensureWoldIsSame(first, second);
        ensureOneAxis(first, second);

        this.second = second.clone();
    }

    /**
     * @return The second point of the line. A clone.
     */
    @SuppressWarnings("WeakerAccess")
    public Location getSecond() {
        return second.clone();
    }

    /**
     * Throws an {@link IllegalArgumentException} if the two Locations are in
     * different worlds
     *
     * @param one The first Location
     * @param two The second location
     */
    @SuppressWarnings("WeakerAccess")
    protected void ensureWoldIsSame(Location one, Location two) {
        // God, THAT is dirty...
        if (one == null || two == null) {
            return;
        }
        if (!one.getWorld().equals(two.getWorld())) {
            throw new IllegalArgumentException(String.format("Worlds differ! One={%s}, Second={%s}", one.getWorld(), two
                    .getWorld()));
        }
    }

    private void ensureOneAxis(Location one, Location two) {
        if (Double.compare(one.getX(), two.getX()) != 0
                && Double.compare(one.getY(), two.getY()) != 0) {

            throw new IllegalArgumentException(String.format("It covers at least X and Y axes! One={%s}, " +
                    "Second={%s}", one
                    .toVector(), two.toVector()));
        }
        if (Double.compare(one.getX(), two.getX()) != 0
                && Double.compare(one.getZ(), two.getZ()) != 0) {

            throw new IllegalArgumentException(String.format("It covers at least X and Z axes! One={%s}, " +
                    "Second={%s}", one
                    .toVector(), two.toVector()));
        }
        if (Double.compare(one.getY(), two.getY()) != 0
                && Double.compare(one.getZ(), two.getZ()) != 0) {

            throw new IllegalArgumentException(String.format("It covers at least Y and Z axes! One={%s}, " +
                    "Second={%s}", one
                    .toVector(), two.toVector()));
        }
    }

    /**
     * @return The minimum of the two locations
     */
    @SuppressWarnings("WeakerAccess")
    protected Vector getMinVector() {
        return new Vector(
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ())
        );
    }

    /**
     * @return The maximum of the two locations
     */
    @SuppressWarnings("WeakerAccess")
    protected Vector getMaxVector() {
        return new Vector(
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );
    }

    @Override
    public void display(Location center) {
        display();
    }

    /**
     * Displays the line
     */
    public void display() {
        World world = first.getWorld();

        Vector min = getMinVector();
        Vector max = getMaxVector();

        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();

        for (double x = min.getX(); x <= maxX; x += getGranularity()) {
            for (double y = min.getY(); y <= maxY; y += getGranularity()) {
                for (double z = min.getZ(); z <= maxZ; z += getGranularity()) {
                    Location point = new Location(world, x, y, z);
                    world.spawnParticle(getParticle(), point, 1, 0, 0, 0, 0);
                }
            }
        }
    }
}
