package com.pignic.spacegrinder.factory.basic;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.pojo.ShipPart;

public abstract class ShipPartFactory {

	private static Map<Class<? extends ShipPart>, ShipPartFactory> factories = new HashMap<Class<? extends ShipPart>, ShipPartFactory>();

	public static Entity build(final World world, final Vector2 position, final float angle,
			final Class<? extends ShipPart> partClass) {
		return getFactory(partClass).buildPart(world, position, angle);
	}

	@SuppressWarnings("unchecked")
	public static ShipPartFactory getFactory(final Class<? extends ShipPart> partClass) {
		ShipPartFactory instance;
		if ((instance = factories.get(partClass)) == null) {
			try {
				final Class<? extends ShipPartFactory> factoryClass = (Class<? extends ShipPartFactory>) Class
						.forName(ShipPartFactory.class.getPackage().getName().concat(".")
								.concat(partClass.getSimpleName()).concat("Factory"));
				instance = factoryClass.newInstance();
				factories.put(partClass, instance);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	protected static Physical getPhysicalComponent(final World world, final Entity entity, final ShipPart partConfig,
			final Vector2 position, final float angle) {
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.add(position);
		bodyDef.angle = angle;
		final PolygonShape shape = new PolygonShape();
		shape.set(partConfig.getShape(1f / SpaceGrinder.WORLD_SCALE));
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = partConfig.density;
		fixtureDef.shape = shape;
		return new Physical(world, entity, bodyDef, fixtureDef);
	}

	public abstract Entity buildPart(final World world, final Vector2 position, final float angle);

}