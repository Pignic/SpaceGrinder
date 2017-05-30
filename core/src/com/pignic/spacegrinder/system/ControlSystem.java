package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.ACTION;
import com.pignic.spacegrinder.component.Controllable.Binding;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Physical;

public class ControlSystem extends EntitySystem {

	ComponentMapper<Controllable> cm = ComponentMapper.getFor(Controllable.class);

	private ImmutableArray<Entity> entities;

	ComponentMapper<Particle> parm = ComponentMapper.getFor(Particle.class);
	ComponentMapper<Physical> pm = ComponentMapper.getFor(Physical.class);

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Controllable.class).get());
	}

	@Override
	public void update(final float deltaTime) {
		for (final Entity entity : entities) {
			final Controllable c = cm.get(entity);
			for (final Binding binding : c.getBindings()) {
				final Physical p = pm.get(entity);
				final Particle part = parm.get(entity);
				if (Gdx.input.isKeyPressed(binding.getKeycode())) {
					if (c.getAction().equals(ACTION.THRUST)) {
						// p.getBody().applyLinearImpulse(p.getBody().getWorldVector(new Vector2(1,
						// 0).scl(c.getAmount())),
						// p.getBody().getWorldCenter(), true);
						p.getBody().applyForceToCenter(
								p.getBody().getWorldVector(new Vector2(1, 0).scl(binding.getAmount())), true);
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
		super.update(deltaTime);
	}
}
