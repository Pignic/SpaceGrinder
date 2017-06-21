package com.pignic.spacegrinder.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pignic.spacegrinder.screen.AbstractScreen;

public class Controllable extends Configurable implements Component {

	public static abstract class Action implements Callable<Object> {
		public Binding binding;
		public Controllable controllable;
	}

	public static class Binding {

		private float amount;

		private int keycode;

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

	private final Action action;

	private final List<Binding> bindings = new ArrayList<Binding>(1);

	private float maxAmout = 1;

	public Controllable(final Action action, final float maxAmount, final Binding... bindings) {
		maxAmout = maxAmount;
		this.action = action;
		action.controllable = this;
		this.bindings.addAll(Arrays.asList(bindings));
	}

	public Controllable(final Action action, final float amount, final int... keycodes) {
		this.action = action;
		action.controllable = this;
		maxAmout = amount;
		for (final int keycode : keycodes) {
			bindings.add(new Binding(keycode, amount));
		}
	}

	public Controllable(final int keycode, final Action action, final float amount) {
		this.action = action;
		action.controllable = this;
		maxAmout = amount;
		bindings.add(new Binding(keycode, amount));
	}

	public Binding addBinding(final int keycode, final float amount) {
		final Binding b = new Binding(keycode, amount);
		bindings.add(b);
		return b;
	}

	private void addBindingRow(final Table table, final Binding binding) {
		final TextField input = new TextField(binding.keycode <= 0 ? "" : Input.Keys.toString(binding.keycode),
				AbstractScreen.style.skin);
		input.setMaxLength(1);
		input.setUserObject(binding);
		input.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				binding.keycode = Input.Keys.valueOf(((TextField) actor).getText().toUpperCase());
			}
		});
		final Slider scaleSlider = new Slider(0f, maxAmout, maxAmout / 100f, false, AbstractScreen.style.skin);
		scaleSlider.setValue(binding.amount);
		scaleSlider.setUserObject(binding);
		scaleSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				binding.amount = scaleSlider.getValue();
				return true;
			}
		});
		final TextButton removeButton = new TextButton("-", AbstractScreen.style.skin);
		removeButton.setUserObject(new Actor[] { input, scaleSlider, removeButton });
		removeButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				final Actor[] actors = (Actor[]) event.getTarget().getUserObject();
				bindings.remove(actors[0].getUserObject());
				for (final Actor actorToRemove : actors) {
					actorToRemove.remove();
				}
			}
		});
		table.add(input).width(20);
		table.add(scaleSlider);
		table.add(removeButton);
		table.row();
	}

	public Action getAction() {
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

	@Override
	public Table getConfiguration(final Table table) {
		final Label title = new Label("Controls", AbstractScreen.style.skin, "light");
		table.add(title).row();
		final TextButton addButton = new TextButton("+", AbstractScreen.style.skin);
		addButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				final Binding binding = new Binding(0, maxAmout);
				bindings.add(binding);
				addBindingRow(table, binding);
			}
		});
		table.add(addButton);
		table.row();
		for (final Binding binding : bindings) {
			addBindingRow(table, binding);
		}
		return table;
	}

	public float getMaxAmout() {
		return maxAmout;
	}

}
