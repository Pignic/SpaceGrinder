package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Projectile;

public class ProjectileSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
		public static final ComponentMapper<Projectile> projectile = ComponentMapper.getFor(Projectile.class);
	}

	private final Engine engine;
	private final World world;

	public ProjectileSystem(final World world, final Engine engine) {
		super(Family.all(Projectile.class).get());
		this.world = world;
		this.engine = engine;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Projectile projectile = Mapper.projectile.get(entity);
		if (projectile.update(deltaTime)) {
			final Physical physical = Mapper.physical.get(entity);
			if (physical != null) {
				world.destroyBody(physical.getBody());
				engine.removeEntity(entity);
			}
		}
	}

}
