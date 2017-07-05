package com.pignic.spacegrinder.component;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Timer implements SerializableComponent {

	private float currentTime = 0;

	private boolean destroyOnTimeout = false;

	private float time;

	public Timer() {
	}

	public Timer(final float time) {
		this.time = time;
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		time = jsonData.getFloat("time");
		currentTime = jsonData.getFloat("currentTime");
		destroyOnTimeout = jsonData.getBoolean("destroyOnTimeout");
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

	@Override
	public void serialize(final Json json) {
		json.writeValue("currentTime", currentTime);
		json.writeValue("time", time);
		json.writeValue("destroyOnTimeout", destroyOnTimeout);
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
