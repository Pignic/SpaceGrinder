package com.pignic.spacegrinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetManager {

	private static AssetManager instance;

	public static AssetManager getInstance() {
		if (instance == null) {
			instance = new AssetManager();
		}
		return instance;
	}

	private final Map<String, TextureRegion> textures = new HashMap<String, TextureRegion>();

	public TextureRegion getTexture(final String path) {
		TextureRegion region = textures.get(path);
		if (region == null) {
			region = new TextureRegion(new Texture(path));
			textures.put(path, region);
		}
		return region;
	}

	public String getTexturePath(final TextureRegion region) {
		for (final Entry<String, TextureRegion> entry : textures.entrySet()) {
			if (entry.getValue().equals(region)) {
				return entry.getKey();
			}
		}
		return "";
	}
}
