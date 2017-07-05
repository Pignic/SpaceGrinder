package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.factory.complex.ActionFactory;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Thruster;

public class ThrusterFactory extends ShipPartFactory {

	public Entity build(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity, final int... keycodes) {
		final Thruster config = (Thruster) part;
		final Particle particle = new Particle(EFFECT.THRUSTER).setActive(false);
		entity.add(new Controllable(ActionFactory.getThrusterAction(entity), config.maxThrust, keycodes)
				.setShipPart(part).setCancelAction(ActionFactory.getThrusterCancelAction(entity)));
		particle.setRotation((float) Math.PI);
		entity.add(particle);
		return entity;
	}

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		return build(world, part, position, angle, entity, new int[0]);
	}
}
