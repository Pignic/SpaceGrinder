package com.pignic.spacegrinder.system;

import java.util.ArrayList;
import java.util.List;

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

	private final boolean[] keyState = new boolean[256];

	private final List<Integer> pressedKeys = new ArrayList<Integer>(5);

	private final List<Integer> releasedKeys = new ArrayList<Integer>(5);

	public ControlSystem() {
		super(Family.all(Controllable.class).get());
	}

	public boolean isPressed(final int key) {
		return keyState[key];
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Controllable c = Mapper.controllable.get(entity);
		for (final Binding binding : c.getBindings()) {
			if (c.getOnPress() != null && pressedKeys.contains(binding.getKeycode())) {
				try {
					c.getOnPress().call();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			if (Gdx.input.isKeyPressed(binding.getKeycode())) {
				c.getAction().binding = binding;
				try {
					c.getAction().call();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			} else if (c.getCancelAction() != null && releasedKeys.contains(binding.getKeycode())) {
				try {
					c.getCancelAction().call();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setKeyState(final int key, final boolean status) {
		keyState[key] = status;
		if (!status) {
			releasedKeys.add(key);
		} else {
			pressedKeys.add(key);
		}
	}

	@Override
	public void update(final float deltaTime) {
		super.update(deltaTime);
		releasedKeys.clear();
		pressedKeys.clear();
	}
}
