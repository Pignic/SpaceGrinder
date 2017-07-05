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
import com.pignic.spacegrinder.component.Durability;
import com.pignic.spacegrinder.component.Id;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.service.IDService;

public abstract class ShipPartFactory {

	private static Map<Class<? extends ShipPart>, ShipPartFactory> factories = new HashMap<Class<? extends ShipPart>, ShipPartFactory>();

	public static Entity build(final World world, final ShipPart part, final Vector2 position, final float angle) {
		return getFactory(part.getClass()).buildPart(world, part, position, angle);
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

	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle) {
		final Entity entity = new Entity();
		entity.add(new com.pignic.spacegrinder.component.ShipPart(1));
		entity.add(new Id(IDService.getId()));
		final float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		entity.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl)));
		entity.add(getPhysicalComponent(world, entity, part, position, angle));
		entity.add(new Renderable(part.textureRegion, part.textureScale));
		entity.add(new Durability(part.hitpoints, part.deflection));
		return buildPart(world, part, position, angle, entity);
	}

	protected abstract Entity buildPart(final World world, final ShipPart part, final Vector2 position,
			final float angle, Entity entity);

}
