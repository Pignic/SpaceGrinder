package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.Binding;

public class ControlSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Controllable> controllable = ComponentMapper.getFor(Controllable.class);
	}

	public ControlSystem() {
		super(Family.all(Controllable.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Controllable c = Mapper.controllable.get(entity);
		for (final Binding binding : c.getBindings()) {
			if (Gdx.input.isKeyPressed(binding.getKeycode())) {
				c.getAction().binding = binding;
				try {
					c.getAction().call();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
