package com.pignic.spacegrinder.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.AssetManager;
import com.pignic.spacegrinder.Constants;

public class Resource {

	public static class Resources {

		private static final Map<String, Resource> resources = new HashMap<String, Resource>();

		public static Resource get(final String name) {
			if (resources.size() <= 0) {
				load();
			}
			return resources.get(name);
		}

		public static Collection<Resource> getResources() {
			if (resources.size() <= 0) {
				load();
			}
			return resources.values();
		}

		private static void load() {
			final Json json = new Json();
			final ArrayList<JsonValue> list = json.fromJson(ArrayList.class,
					Gdx.files.internal(Constants.DATA_PATH + "/resource/resource.json"));
			if (list != null) {
				for (final JsonValue jsonValue : list) {
					final Resource resource = json.readValue(Resource.class, jsonValue);
					resource.texture = AssetManager.getInstance()
							.getTexture(Constants.TEXTURE_PATH + "resource/" + resource.name.toLowerCase() + ".png");
					resources.put(resource.name, resource);
				}
			}
		}
	}

	public float density;

	public String name;

	public float rarity;

	private TextureRegion texture;

	public TextureRegion getTexture() {
		return texture;
	}
}
