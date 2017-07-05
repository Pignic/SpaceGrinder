package com.pignic.spacegrinder.component;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Id implements SerializableComponent {

	private long id;

	public Id() {

	}

	public Id(final long id) {
		this.id = id;
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		id = jsonData.getLong("id");
	}

	public long getId() {
		return id;
	}

	@Override
	public void serialize(final Json json) {
		json.writeValue("id", id);
	}

}
