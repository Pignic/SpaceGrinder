package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pignic.spacegrinder.component.Timer;

public class TimerSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Timer> timer = ComponentMapper.getFor(Timer.class);
	}

	private final Engine engine;

	public TimerSystem(final Engine engine) {
		super(Family.all(Timer.class).get());
		this.engine = engine;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Timer timer = Mapper.timer.get(entity);
		if (timer.update(deltaTime) && timer.isDestroyOnTimeout()) {
			engine.removeEntity(entity);
		}
	}

}
