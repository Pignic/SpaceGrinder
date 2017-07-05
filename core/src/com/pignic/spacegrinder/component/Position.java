package com.pignic.spacegrinder.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Position implements SerializableComponent {

	private float angle = 0;

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

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		angle = jsonData.getFloat("angle");
		z = jsonData.getFloat("z");
		position.set(jsonData.getFloat("x"), jsonData.getFloat("y"));
		bounds.set(jsonData.getFloat("width"), jsonData.getFloat("height"));
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

	@Override
	public void serialize(final Json json) {
		json.writeValue("angle", angle);
		json.writeValue("x", position.x);
		json.writeValue("y", position.y);
		json.writeValue("z", z);
		json.writeValue("width", bounds.x);
		json.writeValue("height", bounds.y);
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
