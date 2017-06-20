package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.AssetManager;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;
import com.pignic.spacegrinder.pojo.Sensor;

public class SensorFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final Vector2 position, final float angle) {
		final Sensor config = (Sensor) PART_TYPE.SENSOR.config.get(0);
		final Entity entity = new Entity();
		final float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		entity.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl)));
		entity.add(getPhysicalComponent(world, entity, config, position, angle));
		entity.add(
				new Renderable(AssetManager.shipPartsTextures.get(PART_TYPE.SENSOR.clazz).get(0), config.textureScale));
		return entity;
	}

}
