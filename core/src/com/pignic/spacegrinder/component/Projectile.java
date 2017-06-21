package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class Projectile implements Component {

	private final float damage;

	private float distance = 0;

	private final float range;

	private final float speed;

	public Projectile(final float range, final float speed) {
		this(range, speed, 0);
	}

	public Projectile(final float range, final float speed, final float damage) {
		this.range = range;
		this.speed = speed;
		this.damage = damage;
	}

	public float getDamage() {
		return damage;
	}

	public boolean update(final float delta) {
		distance += speed * delta;
		return distance > range;
	}
}
