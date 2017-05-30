package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class Controllable implements Component {
	public static enum ACTION {
		SHOOT, THRUST
	}

	private final ACTION action;

	private final float amount;

	private final int keycode;

	public Controllable(final int keycode, final ACTION action, final float amount) {
		this.keycode = keycode;
		this.action = action;
		this.amount = amount;
	}

	public ACTION getAction() {
		return action;
	}

	public float getAmount() {
		return amount;
	}

	public int getKeycode() {
		return keycode;
	}
}
