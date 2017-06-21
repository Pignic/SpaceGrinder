package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pignic.spacegrinder.component.LightSource;

import box2dLight.RayHandler;

public class LightSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<LightSource> light = ComponentMapper.getFor(LightSource.class);
	}

	private final RayHandler rayHandler;

	public LightSystem(final SpriteBatch batch, final RayHandler rayHandler) {
		super(Family.all(LightSource.class).get());
		this.rayHandler = rayHandler;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final LightSource light = Mapper.light.get(entity);
		if (!light.isBuilt()) {
			light.build(rayHandler);
		}
	}

}
