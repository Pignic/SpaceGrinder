package com.pignic.spacegrinder.component;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.pojo.Deflection;
import com.pignic.spacegrinder.pojo.Deflection.TYPE;

public class Durability implements SerializableComponent {

	public static class Damage {

		private final float part;

		private final Deflection.TYPE type;

		public Damage(final Deflection.TYPE type, final float part) {
			this.type = type;
			this.part = part;
		}

		public float getPart() {
			return part;
		}

		public Deflection.TYPE getType() {
			return type;
		}
	}

	private float currentDurability;

	private final Map<Deflection.TYPE, Deflection> deflections = new HashMap<Deflection.TYPE, Deflection>();

	private float durability = 1;

	public Durability() {

	}

	public Durability(final float durability, final Deflection[] deflections) {
		this.durability = durability;
		currentDurability = durability;
		if (deflections != null) {
			for (final Deflection deflection : deflections) {
				this.deflections.put(deflection.type, deflection);
			}
		}
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		currentDurability = jsonData.getFloat("currentDurability");
		durability = jsonData.getFloat("durability");
		final JsonValue jsonDeflections = jsonData.get("deflections");
		for (int i = 0; i < jsonDeflections.size; ++i) {
			final JsonValue jsonDeflection = jsonDeflections.get(i);
			final Deflection deflection = new Deflection(TYPE.valueOf(jsonDeflection.getString("type")),
					jsonDeflection.getFloat("factor"));
			deflections.put(deflection.type, deflection);
		}
	}

	public float getCurrentDurability() {
		return currentDurability;
	}

	public float getDurability() {
		return durability;
	}

	public float hit(final float damage, final Damage... damages) {
		float effectiveDamages = 0;
		for (final Damage damageType : damages) {
			final Deflection deflection = deflections.get(damageType.type);
			effectiveDamages += damageType.part * damage * (deflection != null ? deflection.factor : 1);
		}
		currentDurability -= effectiveDamages;
		return currentDurability;
	}

	public float repair(final float repair) {
		currentDurability += repair;
		if (currentDurability > durability) {
			currentDurability = durability;
		}
		return currentDurability;
	}

	@Override
	public void serialize(final Json json) {
		json.writeValue("currentDurability", currentDurability);
		json.writeValue("durability", durability);
		json.writeArrayStart("deflections");
		for (final Deflection deflection : deflections.values()) {
			json.writeObjectStart();
			json.writeValue("type", deflection.type.name());
			json.writeValue("factor", deflection.factor);
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
	}
}
