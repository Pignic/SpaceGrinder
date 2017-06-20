package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class Container implements Component {

	private float volume = 0;

	public Container(final float volume) {
		this.volume = volume;
	}

	public float getVolume() {
		return volume;
	}
}
