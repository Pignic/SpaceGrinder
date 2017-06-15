package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pignic.spacegrinder.component.Animation;
import com.pignic.spacegrinder.component.Renderable;

public class AnimationSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Animation> animation = ComponentMapper.getFor(Animation.class);
		public static final ComponentMapper<Renderable> renderable = ComponentMapper.getFor(Renderable.class);
	}

	public AnimationSystem() {
		super(Family.all(Renderable.class, Animation.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Animation animation = Mapper.animation.get(entity);
		final Renderable renderable = Mapper.renderable.get(entity);
		renderable.setTexture(animation.getKeyFrame());
		animation.update(deltaTime);
	}
}
