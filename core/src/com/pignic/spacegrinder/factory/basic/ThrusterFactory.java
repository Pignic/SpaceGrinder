package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.Action;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Thruster;

public class ThrusterFactory extends ShipPartFactory {

	public Entity build(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity, final int... keycodes) {
		final Thruster config = (Thruster) part;
		final Physical physical = entity.getComponent(Physical.class);
		final Particle particle = new Particle(EFFECT.THRUSTER).setActive(false);
		entity.add(new Controllable(new Action() {
			@Override
			public Object call() throws Exception {
				physical.getBody().applyForceToCenter(
						physical.getBody().getWorldVector(new Vector2(1, 0).scl(binding.getAmount())), false);
				if (particle != null) {
					particle.setActive(true);
					particle.setScale(binding.getAmount() / controllable.getMaxAmout() * 3);
				}
				return null;
			}

		}, config.maxThrust, keycodes).setCancelAction(new Action() {
			@Override
			public Object call() throws Exception {
				entity.getComponent(Particle.class).setActive(false);
				return null;
			}
		}));
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
