package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Renderable implements Component {

	private Color color = new Color(1, 0, 0, 1);

	private boolean rotate = true;

	private float scale = 0.5f;

	private Texture texture;

	private boolean tiled = false;

	private int zIndex = 0;

	public Renderable(final Texture texture) {
		this.texture = texture;
	}

	public Renderable(final Texture texture, final int zIndex) {
		this.texture = texture;
		this.zIndex = zIndex;
	}

	public Color getColor() {
		return color;
	}

	public float getScale() {
		return scale;
	}

	public Texture getTexture() {
		return texture;
	}

	public int getzIndex() {
		return zIndex;
	}

	public boolean isRotate() {
		return rotate;
	}

	public boolean isTiled() {
		return tiled;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public void setRotate(final boolean rotate) {
		this.rotate = rotate;
	}

	public void setScale(final float scale) {
		this.scale = scale;
	}

	public void setTexture(final Texture texture) {
		this.texture = texture;
	}

	public void setTiled(final boolean tiled) {
		this.tiled = tiled;
	}

	public void setzIndex(final int zIndex) {
		this.zIndex = zIndex;
	}
}
