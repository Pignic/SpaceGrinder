package com.pignic.spacegrinder.component;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.pignic.spacegrinder.pojo.Deflection;

public class Durability implements Component {

	public static class Damage {

		private final float part;

		private final Deflection.TYPE type;

		public Damage(final Deflection.TYPE type, final float part) {
			this.type = type;
			this.part = part;
		}
	}

	private float currentDurability;

	private final Map<Deflection.TYPE, Deflection> deflections = new HashMap<Deflection.TYPE, Deflection>();

	private final float durability;

	public Durability(final float durability, final Deflection[] deflections) {
		this.durability = durability;
		currentDurability = durability;
		for (final Deflection deflection : deflections) {
			this.deflections.put(deflection.type, deflection);
		}
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
}
