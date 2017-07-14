package com.pignic.spacegrinder.component;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.pignic.spacegrinder.pojo.Material;

public class StellarObject implements Component {

	private final List<Material> content = new ArrayList<Material>();

	private float contentVolume = 0;

	private final List<Vector2> outline = new ArrayList<Vector2>();

	public StellarObject() {

	}

	public StellarObject(final List<Vector2> outline) {
		this.outline.addAll(outline);
	}

	public StellarObject(final List<Vector2> outline, final List<Material> content) {
		this(outline);
		this.content.addAll(content);
		updateContentVolume();
	}

	public List<Material> getContent() {
		return content;
	}

	public List<Vector2> getOutline() {
		return outline;
	}

	public Material pickResource(final float ammount) {
		float accumulator = 0;
		if (contentVolume <= 0) {
			return new Material("Stone", ammount);
		}
		final float index = (float) (Math.random() * contentVolume);
		final Material picked = new Material();
		for (final Material material : content) {
			accumulator += material.quantity;
			if (accumulator >= index) {
				picked.type = material.type;
				if (material.quantity <= ammount) {
					picked.quantity = material.quantity;
					content.remove(material);
				} else {
					picked.quantity = ammount;
					material.quantity -= ammount;
					contentVolume -= ammount;
				}
				break;
			}
		}
		return picked;
	}

	private float updateContentVolume() {
		contentVolume = 0;
		for (final Material material : content) {
			contentVolume += material.quantity;
		}
		return contentVolume;
	}

}
