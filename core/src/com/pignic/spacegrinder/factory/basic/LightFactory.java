package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.LightSource;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.pojo.Light;
import com.pignic.spacegrinder.pojo.ShipPart;

public class LightFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		final Light config = (Light) part;
		entity.add(new LightSource(new Color(Color.WHITE), entity.getComponent(Physical.class).getBody(),
				config.maxRange, (float) (Math.toRadians(config.maxArc) / 2f), config.maxRange,
				(float) (Math.toRadians(config.maxArc) / 2f)));
		return entity;
	}

}
