package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.pojo.Gravity;
import com.pignic.spacegrinder.pojo.ShipPart;

public class GravityFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		final Gravity config = (Gravity) part;
		final float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		entity.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl)));
		entity.add(getPhysicalComponent(world, entity, config, position, angle));
		entity.add(new Renderable(part.textureRegion, config.textureScale));
		return entity;
	}

}
