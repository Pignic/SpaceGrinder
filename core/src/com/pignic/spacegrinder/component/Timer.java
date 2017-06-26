package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class Timer implements Component {

	private float currentTime = 0;

	private boolean destroyOnTimeout = false;

	private final float time;

	public Timer(final float time) {
		this.time = time;
	}

	public boolean done() {
		return currentTime > time;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public boolean isDestroyOnTimeout() {
		return destroyOnTimeout;
	}

	public void reset() {
		currentTime = 0;
	}

	public Timer setDestroyOnTimeout(final boolean destroyOnTimeout) {
		this.destroyOnTimeout = destroyOnTimeout;
		return this;
	}

	public boolean update(final float delta) {
		currentTime += delta * 1000;
		return currentTime > time;
	}

}
