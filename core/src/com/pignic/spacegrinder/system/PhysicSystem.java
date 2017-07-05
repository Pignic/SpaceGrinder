package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;

public class PhysicSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
		public static final ComponentMapper<Position> position = ComponentMapper.getFor(Position.class);
	}

	private final World world;

	public PhysicSystem(final World world) {
		super(Family.all(Position.class, Physical.class).get());
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Physical physical = Mapper.physical.get(entity);
		Mapper.position.get(entity).setScaled(physical.getBody().getPosition(), SpaceGrinder.WORLD_SCALE);
		Mapper.position.get(entity).setAngle(physical.getBody().getAngle());
	}

	@Override
	public void update(final float deltaTime) {
		super.update(deltaTime);
		world.step(1 / 60f, 8, 6);
	}
}
