package com.pignic.spacegrinder.component;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.pignic.spacegrinder.pojo.Deflection;
import com.pignic.spacegrinder.pojo.Weapon.Damage;

public class Projectile implements Component {

	private final float damage;

	private final Map<Deflection.TYPE, com.pignic.spacegrinder.component.Durability.Damage> damageTypes = new HashMap<Deflection.TYPE, com.pignic.spacegrinder.component.Durability.Damage>();

	private float distance = 0;

	private final Entity emitter;

	private final float range;

	private final float speed;

	public Projectile(final Entity emitter, final float range, final float speed) {
		this(emitter, range, speed, 0);
	}

	public Projectile(final Entity emitter, final float range, final float speed, final float damage,
			final Damage... damages) {
		this.range = range;
		this.speed = speed;
		this.damage = damage;
		this.emitter = emitter;
		for (final Damage damageType : damages) {
			damageTypes.put(damageType.type,
					new com.pignic.spacegrinder.component.Durability.Damage(damageType.type, damageType.part));
		}
	}

	public float getDamage() {
		return damage;
	}

	public Map<Deflection.TYPE, com.pignic.spacegrinder.component.Durability.Damage> getDamageTypes() {
		return damageTypes;
	}

	public Entity getEmitter() {
		return emitter;
	}

	public boolean update(final float delta) {
		distance += speed * delta;
		return distance > range;
	}
}
