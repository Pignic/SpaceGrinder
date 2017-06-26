package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Durability;
import com.pignic.spacegrinder.component.Physical;

public class DurabilitySystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Durability> durability = ComponentMapper.getFor(Durability.class);
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
	}

	private final Engine engine;
	private final World world;

	public DurabilitySystem(final World world, final Engine engine) {
		super(Family.all(Durability.class).get());
		this.world = world;
		this.engine = engine;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		if (Mapper.durability.get(entity).getCurrentDurability() <= 0) {
			final Physical physical = Mapper.physical.get(entity);
			if (physical != null) {
				world.destroyBody(Mapper.physical.get(entity).getBody());
			}
			engine.removeEntity(entity);
		}
	}

}
