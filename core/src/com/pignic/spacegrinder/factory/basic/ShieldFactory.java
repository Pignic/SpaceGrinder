package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.pojo.Shield;
import com.pignic.spacegrinder.pojo.ShipPart;

public class ShieldFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		final Shield config = (Shield) part;
		entity.add(new Particle(EFFECT.SHIELD).setActive(true).setRotating(false));
		return entity;
	}
}
