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
import com.pignic.spacegrinder.component.Physical;

public class ControlSystem extends EntitySystem {

	ComponentMapper<Controllable> cm = ComponentMapper.getFor(Controllable.class);

	private ImmutableArray<Entity> entities;

	ComponentMapper<Physical> pm = ComponentMapper.getFor(Physical.class);

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Controllable.class).get());
	}

	@Override
	public void update(final float deltaTime) {
		for (final Entity entity : entities) {
			final Controllable c = cm.get(entity);
			if (Gdx.input.isKeyPressed(c.getKeycode())) {
				if (c.getAction().equals(ACTION.THRUST)) {
					final Physical p = pm.get(entity);
					// p.getBody().applyLinearImpulse(p.getBody().getWorldVector(new Vector2(1, 0).scl(c.getAmount())),
					// p.getBody().getWorldCenter(), true);
					p.getBody().applyForceToCenter(p.getBody().getWorldVector(new Vector2(1, 0).scl(c.getAmount())),
							true);
				}
			}
		}
		super.update(deltaTime);
	}
}
