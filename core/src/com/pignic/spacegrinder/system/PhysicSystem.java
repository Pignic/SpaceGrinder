package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;

public class PhysicSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	ComponentMapper<Physical> phm = ComponentMapper.getFor(Physical.class);

	ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

	private final World world;

	public PhysicSystem(final World world) {
		this.world = world;
	}

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Position.class, Physical.class).get());
	}

	@Override
	public void update(final float deltaTime) {
		world.step(1 / 60f, 16, 12);
		// world.clearForces();
		for (final Entity entity : entities) {
			pm.get(entity).setScaled(phm.get(entity).getBody().getWorldCenter(), SpaceGrinder.WORLD_SCALE);
			// pm.get(entity).set(phm.get(entity).getBody().getWorldCenter());
			pm.get(entity).setAngle(phm.get(entity).getBody().getAngle());
		}
		super.update(deltaTime);
	}
}
