package com.pignic.spacegrinder.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Component;

public class Controllable implements Component {
	public static enum ACTION {
		SHOOT, THRUST
	}

	public static class Binding {

		private final float amount;

		private final int keycode;

		public Binding(final int keycode, final float amount) {
			this.keycode = keycode;
			this.amount = amount;
		}

		public float getAmount() {
			return amount;
		}

		public int getKeycode() {
			return keycode;
		}
	}

	private final ACTION action;

	private final List<Binding> bindings = new ArrayList<Binding>(1);

	public Controllable(final ACTION action, final Binding... bindings) {
		this.action = action;
		this.bindings.addAll(Arrays.asList(bindings));
	}

	public Controllable(final ACTION action, final float amount, final int... keycodes) {
		this.action = action;
		for (final int keycode : keycodes) {
			bindings.add(new Binding(keycode, amount));
		}
	}

	public Controllable(final int keycode, final ACTION action, final float amount) {
		this.action = action;
		bindings.add(new Binding(keycode, amount));
	}

	public Binding addBinding(final int keycode, final float amount) {
		final Binding b = new Binding(keycode, amount);
		bindings.add(b);
		return b;
	}

	public ACTION getAction() {
		return action;
	}

	public Binding getBinding(final int index) {
		return bindings.get(index);
	}

	public List<Binding> getBindings() {
		return bindings;
	}

	public int getBindingsCount() {
		return bindings.size();
	}

}
