package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class Timer implements Component {

	private float currentTime = 0;

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

	public void reset() {
		currentTime = 0;
	}

	public boolean update(final float delta) {
		currentTime += delta * 1000;
		return currentTime > time;
	}

}
