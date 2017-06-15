package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.ACTION;
import com.pignic.spacegrinder.component.Controllable.Binding;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Physical;

public class ControlSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Controllable> controllable = ComponentMapper.getFor(Controllable.class);
		public static final ComponentMapper<Particle> particle = ComponentMapper.getFor(Particle.class);
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
	}

	public ControlSystem() {
		super(Family.all(Controllable.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Controllable c = Mapper.controllable.get(entity);
		for (final Binding binding : c.getBindings()) {
			final Physical p = Mapper.physical.get(entity);
			final Particle part = Mapper.particle.get(entity);
			if (Gdx.input.isKeyPressed(binding.getKeycode())) {
				if (c.getAction().equals(ACTION.THRUST)) {
					// p.getBody().applyLinearImpulse(
					// p.getBody().getWorldVector(new Vector2(1, 0).scl(binding.getAmount() / 100f)),
					// p.getBody().getWorldCenter(), true);
					p.getBody().applyForceToCenter(
							p.getBody().getWorldVector(new Vector2(1, 0).scl(binding.getAmount())), false);
					if (part != null) {
						part.setActive(true);
					}
				}
			} else {
				if (c.getAction().equals(ACTION.THRUST)) {
					part.setActive(false);
				}
			}
		}
	}
}
