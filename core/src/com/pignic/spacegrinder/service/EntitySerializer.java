package com.pignic.spacegrinder.service;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.SerializableComponent;
import com.pignic.spacegrinder.factory.complex.ActionFactory;

@SuppressWarnings("rawtypes")
public class EntitySerializer implements Serializer<Entity> {

	private World world;

	@Override
	public Entity read(final Json json, final JsonValue jsonData, final Class type) {
		final Entity entity = new Entity();
		for (int i = 0; i < jsonData.size; ++i) {
			final JsonValue jsonComponent = jsonData.get(i);
			final String componentName = jsonComponent.getString("component");
			try {
				final SerializableComponent component = (SerializableComponent) this.getClass().getClassLoader()
						.loadClass(componentName).newInstance();
				component.deserialize(json, jsonComponent);
				if (component instanceof Physical) {
					((Physical) component).build(world, entity);
				} else if (component instanceof Controllable) {
					((Controllable) component).setAction(ActionFactory.getThrusterAction(entity));
					((Controllable) component).setCancelAction(ActionFactory.getThrusterCancelAction(entity));
				}
				entity.add(component);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	public Entity[] read(final String data, final World world) {
		this.world = world;
		final Json json = new Json();
		json.setSerializer(Entity.class, this);
		return json.fromJson(Entity[].class, data);
	}

	public String write(final ImmutableArray<Entity> entities) {
		final Json json = new Json();
		json.setSerializer(Entity.class, this);
		return json.toJson(entities.toArray());
	}

	@Override
	public void write(final Json json, final Entity object, final Class knownType) {
		json.writeArrayStart();
		for (final Component component : object.getComponents()) {
			if (component instanceof SerializableComponent) {
				json.writeObjectStart();
				json.writeValue("component", component.getClass().getName());
				((SerializableComponent) component).serialize(json);
				json.writeObjectEnd();
			}
		}
		json.writeArrayEnd();
	}

}
