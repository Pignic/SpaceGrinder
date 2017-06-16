package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.AssetManager;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;
import com.pignic.spacegrinder.pojo.Generator;

public class GeneratorFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final Vector2 position, final float angle) {
		final Generator config = (Generator) PART_TYPE.GENERATOR.config.get(0);
		final Entity armor = new Entity();
		final float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		armor.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl)));
		armor.add(getPhysicalComponent(world, config, position, angle));
		armor.add(new Renderable(AssetManager.shipPartsTextures.get(PART_TYPE.GENERATOR.clazz).get(0),
				config.textureScale));
		return armor;
	}

}
