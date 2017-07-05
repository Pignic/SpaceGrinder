package com.pignic.spacegrinder.pojo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;

public abstract class ShipPart implements JSONObject {

	public float cost;
	public Deflection[] deflection;
	public float density;
	public int hitpoints;
	public float input;
	public Material[] materials;
	public String name;
	private int partIndex = -1;
	private PART_TYPE partType;
	public SimpleVector[] shape;
	public String texture;
	public TextureRegion textureRegion;
	public float textureScale;

	public int getPartIndex() {
		final PART_TYPE type = getPartType();
		if (partIndex >= 0) {
			return partIndex;
		}
		for (int i = 0; i < type.config.size(); ++i) {
			if (type.config.get(i).name.equals(name)) {
				partIndex = i;
				return i;
			}
		}
		return -1;
	}

	public PART_TYPE getPartType() {
		if (partType != null) {
			return partType;
		}
		for (final PART_TYPE type : PART_TYPE.values()) {
			if (type.clazz.equals(this.getClass())) {
				partType = type;
				return type;
			}
		}
		return null;
	}

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
