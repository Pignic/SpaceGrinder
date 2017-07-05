package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public interface SerializableComponent extends Component {

	public void deserialize(final Json json, final JsonValue jsonData);

	public void serialize(final Json json);
}
