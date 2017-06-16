package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.AssetManager;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.ACTION;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;
import com.pignic.spacegrinder.pojo.Thruster;

public class ThrusterFactory extends ShipPartFactory {

	public Entity build(final World world, final Vector2 position, final float angle, final int... keycodes) {
		final Thruster config = (Thruster) PART_TYPE.THRUSTER.config.get(0);
		final Entity shipPart = new Entity();
		final float scl = 1 / SpaceGrinder.WORLD_SCALE * 5;
		shipPart.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl), 0, 1));
		shipPart.add(getPhysicalComponent(world, config, position, angle));
		shipPart.add(new Controllable(ACTION.THRUST, config.maxThrust, keycodes));
		shipPart.add(new Renderable(AssetManager.shipPartsTextures.get(PART_TYPE.THRUSTER.clazz).get(0),
				config.textureScale));
		final Particle particle = new Particle(EFFECT.THRUSTER);
		particle.setRotation((float) Math.PI);
		shipPart.add(particle);
		return shipPart;
	}

	@Override
	public Entity buildPart(final World world, final Vector2 position, final float angle) {
		return build(world, position, angle, new int[0]);
	}
}
