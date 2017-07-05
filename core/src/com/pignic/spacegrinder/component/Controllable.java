package com.pignic.spacegrinder.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.screen.AbstractScreen;

public class Controllable extends Configurable implements SerializableComponent {

	public static abstract class Action implements Callable<Object> {
		public Binding binding;
		public Entity entity;
		public ShipPart shipPart;

		public Action(final Entity entity) {
			this.entity = entity;
		}
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

	private Action action;

	private final List<Binding> bindings = new ArrayList<Binding>(1);

	private Action cancelAction = null;

	private float maxAmout = 1;

	private Action onPress;

	private ShipPart shipPart;

	public Controllable() {

	}

	public Controllable(final Action action, final float maxAmount, final Binding... bindings) {
		maxAmout = maxAmount;
		this.action = action;
		this.bindings.addAll(Arrays.asList(bindings));
	}

	public Controllable(final Action action, final float amount, final int... keycodes) {
		this.action = action;
		maxAmout = amount;
		for (final int keycode : keycodes) {
			bindings.add(new Binding(keycode, amount));
		}
	}

	public Controllable(final int keycode, final Action action, final float amount) {
		this.action = action;
		maxAmout = amount;
		bindings.add(new Binding(keycode, amount));
	}

	public Controllable addBinding(final int keycode, final float amount) {
		bindings.add(new Binding(keycode, amount));
		return this;
	}

	private void addBindingRow(final Table table, final Binding binding) {
		final TextField input = new TextField(binding.keycode <= 0 ? "" : Input.Keys.toString(binding.keycode),
				AbstractScreen.style.skin);
		input.setUserObject(binding);
		input.addListener(new InputListener() {
			@Override
			public boolean keyDown(final InputEvent event, final int keycode) {
				binding.keycode = keycode;
				input.setText(Input.Keys.toString(keycode));
				return true;
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
		table.add(input).width(100);
		table.add(scaleSlider);
		table.add(removeButton);
		table.row();
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		maxAmout = jsonData.getFloat("maxAmout");
		final JsonValue jsonBindings = jsonData.get("bindings");
		for (int i = 0; i < jsonBindings.size; ++i) {
			final JsonValue jsonBinding = jsonBindings.get(i);
			addBinding(jsonBinding.getInt("keycode"), jsonBinding.getFloat("amount"));
		}
		shipPart = PART_TYPE.valueOf(jsonData.getString("type")).config.get(jsonData.getInt("typeIndex"));
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

	public Action getCancelAction() {
		return cancelAction;
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

	public Action getOnPress() {
		return onPress;
	}

	public ShipPart getShipPart() {
		return shipPart;
	}

	@Override
	public void serialize(final Json json) {
		json.writeValue("maxAmout", maxAmout);
		json.writeArrayStart("bindings");
		for (final Binding binding : bindings) {
			json.writeObjectStart();
			json.writeValue("amount", binding.amount);
			json.writeValue("keycode", binding.keycode);
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		json.writeValue("type", shipPart.getPartType().name());
		json.writeValue("typeIndex", shipPart.getPartIndex());
	}

	public Controllable setAction(final Action action) {
		this.action = action;
		this.action.shipPart = shipPart;
		return this;
	}

	public Controllable setCancelAction(final Action cancelAction) {
		this.cancelAction = cancelAction;
		this.cancelAction.shipPart = shipPart;
		return this;
	}

	public void setOnPress(final Action onPress) {
		this.onPress = onPress;
		this.onPress.shipPart = shipPart;
	}

	public Controllable setShipPart(final ShipPart shipPart) {
		this.shipPart = shipPart;
		if (action != null) {
			action.shipPart = shipPart;
		}
		if (cancelAction != null) {
			cancelAction.shipPart = shipPart;
		}
		if (onPress != null) {
			onPress.shipPart = shipPart;
		}
		return this;
	}

}
