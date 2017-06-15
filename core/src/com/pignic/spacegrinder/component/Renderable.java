package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pignic.spacegrinder.SpaceGrinder;

public class Renderable implements Component {

	private Color color = new Color(1, 0, 0, 1);

	private boolean rotate = true;

	private float scale = 1f;

	private final Sprite sprite;

	private TextureRegion texture;

	private boolean tiled = false;

	public Renderable(final TextureRegion texture) {
		this(texture, 1);
	}

	public Renderable(final TextureRegion texture, final float scale) {
		this.texture = texture;
		this.scale = scale;
		sprite = new Sprite(texture);
		sprite.setOriginCenter();
		sprite.setScale(this.scale / SpaceGrinder.WORLD_SCALE);
	}

	public Color getColor() {
		return color;
	}

	public float getScale() {
		return scale;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public TextureRegion getTexture() {
		return texture;
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
		sprite.setScale(getScale() / SpaceGrinder.WORLD_SCALE);
	}

	public void setTexture(final TextureRegion texture) {
		this.texture = texture;
	}

	public void setTiled(final boolean tiled) {
		this.tiled = tiled;
	}
}
