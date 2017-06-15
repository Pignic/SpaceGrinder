package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Position implements Component {

	private float angle;

	private final Vector2 bounds = new Vector2();

	private final Vector2 position = new Vector2();

	private float z = 0;

	public Position() {

	}

	public Position(final Vector2 position, final Vector2 bounds) {
		this(position, bounds, 0);
	}

	public Position(final Vector2 position, final Vector2 bounds, final float angle) {
		this(position, bounds, angle, 0);
	}

	public Position(final Vector2 position, final Vector2 bounds, final float angle, final float z) {
		this.position.set(position);
		this.bounds.set(bounds);
		this.angle = angle;
		this.z = z;
	}

	public Vector2 get() {
		return position;
	}

	public float getAngle() {
		return angle;
	}

	public Vector2 getBounds() {
		return bounds;
	}

	public float getZ() {
		return z;
	}

	public Vector2 set(final float x, final float y) {
		position.x = x;
		position.y = y;
		return position;
	}

	public Vector2 set(final Vector2 position) {
		return this.position.set(position);
	}

	public void setAngle(final float angle) {
		this.angle = angle;
	}

	public Vector2 setBounds(final float width, final float height) {
		return bounds.set(width, height);
	}

	public Vector2 setBounds(final Vector2 bounds) {
		return this.bounds.set(bounds);
	}

	public Vector2 setScaled(final Vector2 position, final float scale) {
		return this.position.set(position.x * scale, position.y * scale);
	}

	public void setZ(final float z) {
		this.z = z;
	}
}
