package com.pignic.spacegrinder.pojo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public abstract class ShipPart implements JSONObject {

	public float cost;
	public Deflection[] deflection;
	public float density;
	public int hitpoints;
	public float input;
	public Material[] materials;
	public String name;
	public SimpleVector[] shape;
	public String texture;
	public float textureScale;

	public Vector2[] getShape() {
		return getShape(1);
	}

	public Vector2[] getShape(final float scale) {
		final List<Vector2> list = new ArrayList<Vector2>(shape.length);
		for (final SimpleVector v : shape) {
			list.add(new Vector2(v.x * scale, v.y * scale));
		}
		return list.toArray(new Vector2[0]);
	}
}
