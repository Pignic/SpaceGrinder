package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Projectile;

public class ProjectileSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
		public static final ComponentMapper<Projectile> projectile = ComponentMapper.getFor(Projectile.class);
	}

	public ProjectileSystem() {
		super(Family.all(Projectile.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Projectile projectile = Mapper.projectile.get(entity);
		if (projectile.update(deltaTime)) {
			final Physical physical = Mapper.physical.get(entity);
			if (physical != null) {
				physical.destroyBody();
				getEngine().removeEntity(entity);
			}
		}
	}

}
