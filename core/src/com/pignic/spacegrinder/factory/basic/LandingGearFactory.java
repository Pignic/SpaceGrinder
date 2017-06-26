package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.pojo.LandingGear;
import com.pignic.spacegrinder.pojo.ShipPart;

public class LandingGearFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		final LandingGear config = (LandingGear) part;
		return entity;
	}

}
