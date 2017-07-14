package com.pignic.spacegrinder.component;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.AssetManager;
import com.pignic.spacegrinder.SpaceGrinder;

public class Renderable implements SerializableComponent {

	private PolygonSprite polygonSprite;

	private boolean rotate = true;

	private float scale = 1f;

	private Sprite sprite;

	private TextureRegion texture;

	private boolean tiled = false;

	public Renderable() {

	}

	public Renderable(final TextureRegion texture) {
		this(texture, 1);
	}

	public Renderable(final TextureRegion texture, final float scale) {
		this.texture = texture;
		this.scale = scale;
		setSprite();
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		rotate = jsonData.getBoolean("rotate");
		scale = jsonData.getFloat("scale");
		tiled = jsonData.getBoolean("tiled");
		texture = AssetManager.getInstance().getTexture(jsonData.getString("texture"));
		setSprite();
	}

	public PolygonSprite getPolygonSprite() {
		return polygonSprite;
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

	@Override
	public void serialize(final Json json) {
		json.writeValue("rotate", rotate);
		json.writeValue("scale", scale);
		json.writeValue("tiled", tiled);
		json.writeValue("texture", AssetManager.getInstance().getTexturePath(texture));
	}

	public Renderable setPolygon(final float[] vertices, final short[] triangles) {
		final float[] updated = new float[vertices.length];
		float minx = 0;
		float miny = 0;
		for (int i = 0; i < vertices.length; ++i) {
			final float val = vertices[i];
			if (i % 2 == 0) {
				if (val < minx) {
					minx = val;
				}
			} else {
				if (val < miny) {
					miny = val;
				}
			}
		}
		for (int i = 0; i < vertices.length; i += 2) {
			updated[i] = vertices[i] - minx;
			updated[i + 1] = vertices[i + 1] - miny;
		}
		polygonSprite = new PolygonSprite(new PolygonRegion(texture, updated, triangles));
		polygonSprite.setOrigin(polygonSprite.getBoundingRectangle().width / 2f,
				polygonSprite.getBoundingRectangle().height / 2f);
		return this;
	}

	public void setRotate(final boolean rotate) {
		this.rotate = rotate;
	}

	public void setScale(final float scale) {
		this.scale = scale;
		sprite.setScale(getScale() / SpaceGrinder.WORLD_SCALE);
	}

	private void setSprite() {
		sprite = new Sprite(texture);
		sprite.setOriginCenter();
		sprite.setScale(scale / SpaceGrinder.WORLD_SCALE);
	}

	public void setTexture(final TextureRegion texture) {
		this.texture = texture;
	}

	public Renderable setTiled(final boolean tiled) {
		this.tiled = tiled;
		return this;
	}
}
