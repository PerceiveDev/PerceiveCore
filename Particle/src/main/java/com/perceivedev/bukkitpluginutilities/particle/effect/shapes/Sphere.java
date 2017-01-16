package com.perceivedev.bukkitpluginutilities.particle.effect.shapes;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.perceivedev.bukkitpluginutilities.particle.math.RotationMatrices;
import com.perceivedev.bukkitpluginutilities.particle.math.SphericalCoordinates;


/**
 * A particle sphere
 */
public class Sphere extends AbstractParticleShape {

    private double granularityPhi;
    private double radius;

    /**
     * @param orientation The {@link Orientation} of the effect
     * @param granularityTheta The granularity for theta. The distance between each spawned particle
     * @param granularityPhi The granularity for phi. Phi is the angle between the z axis and the line OP
     * @param radius The radius of the sphere
     * @param particle The Particle to use
     */
    @SuppressWarnings("unused")
    public Sphere(Orientation orientation, double granularityTheta, double granularityPhi, double radius, Particle
            particle) {
        super(orientation, granularityTheta, particle);

        this.radius = radius;
        this.granularityPhi = granularityPhi;
    }

    /**
     * Returns the Granularity for {@code theta}
     *
     * @return The Granularity for {@code theta}
     */
    @Override
    public double getGranularity() {
        return super.getGranularity();
    }

    @Override
    public void display(Location center) {
        SphericalCoordinates coordinates = new SphericalCoordinates(radius, getGranularity(), granularityPhi);
        World world = center.getWorld();

        for (double phi = 0; phi < Math.PI; phi += granularityPhi) {
            coordinates.setPhi(phi);
            for (double theta = 0; theta < 2 * Math.PI; theta += getGranularity()) {
                coordinates.setTheta(theta);

                Vector cartesian = coordinates.toCartesian();

                if (getOrientation() == Orientation.VERTICAL) {
                    cartesian = RotationMatrices.rotateRadian(cartesian, 0, Math.PI / 2);
                }

                Location point = center.clone().add(cartesian);
                world.spawnParticle(getParticle(), point, 1, 0, 0, 0, 0);
            }
        }
    }
}
