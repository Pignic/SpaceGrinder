package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pignic.spacegrinder.component.Durability;
import com.pignic.spacegrinder.component.Physical;

public class DurabilitySystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Durability> durability = ComponentMapper.getFor(Durability.class);
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
	}

	public DurabilitySystem() {
		super(Family.all(Durability.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		if (Mapper.durability.get(entity).getCurrentDurability() <= 0) {
			final Physical physical = Mapper.physical.get(entity);
			if (physical != null) {
				physical.destroyBody();
			}
			getEngine().removeEntity(entity);
		}
	}

}
