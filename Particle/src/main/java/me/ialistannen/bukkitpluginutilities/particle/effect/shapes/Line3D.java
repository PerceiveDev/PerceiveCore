package me.ialistannen.bukkitpluginutilities.particle.effect.shapes;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.ialistannen.bukkitpluginutilities.particle.math.SphericalCoordinates;

/**
 * A Line, which points from one Vector to another.
 * <p>
 * It doesn't care about pesky things like "axes"
 */
public class Line3D extends AxisAlignedLine {

    private SphericalCoordinates differenceCoordinates;
    private double distance;

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
    @SuppressWarnings("unused")
    public Line3D(double granularity, Particle particle, Location first, Location second) {
        super(granularity, particle, first, second);

        updateLocations();
    }

    /**
     * @param first The first location of the line
     *
     * @throws IllegalArgumentException If the worlds do not match
     */
    @Override
    public void setFirst(Location first) {
        ensureWoldIsSame(first, second);

        this.first = first.clone();

        updateLocations();
    }

    /**
     * @param second The second location of the line
     *
     * @throws IllegalArgumentException If the worlds do not match
     */
    @Override
    public void setSecond(Location second) {
        ensureWoldIsSame(first, second);

        this.second = second.clone();

        updateLocations();
    }

    private void updateLocations() {
        // God, THAT is dirty...
        if (first == null || second == null) {
            return;
        }
        Vector delta = getMaxVector().subtract(getMinVector());

        differenceCoordinates = SphericalCoordinates.fromCartesian(delta.getX(), delta.getY(), delta.getZ());

        distance = getFirst().distance(getSecond());
    }

    @Override
    public void display() {
        World world = first.getWorld();
        Vector min = getMinVector();
        Location minLocation = new Location(world, min.getX(), min.getY(), min.getZ());

        for (double rho = 0; rho < distance; rho += getGranularity()) {
            differenceCoordinates.setRho(rho);

            Location point = minLocation.clone().add(differenceCoordinates.toCartesian());

            world.spawnParticle(getParticle(), point, 1, 0, 0, 0, 0);
        }
    }
}
