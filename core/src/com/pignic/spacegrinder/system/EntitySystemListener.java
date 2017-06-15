package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

public abstract class EntitySystemListener extends EntitySystem implements EntityListener {

	protected Array<Entity> entities;

	public EntitySystemListener() {
		super();
	}

	@Override
	public void addedToEngine(final Engine engine) {
		entities = new Array<Entity>((Entity[]) engine.getEntitiesFor(getFamily()).toArray(Entity.class));
	}

	@Override
	public void entityAdded(final Entity entity) {
		entities.add(entity);
	}

	@Override
	public void entityRemoved(final Entity entity) {
		entities.removeValue(entity, true);
	}

	public abstract Family getFamily();

	public void preUpdate(final float deltaTime) {

	}

	public EntitySystemListener register(final Engine engine) {
		engine.addSystem(this);
		// engine.addEntityListener(this);
		return this;
	}

	@Override
	public final void update(final float deltaTime) {
		preUpdate(deltaTime);
		for (final Entity entity : entities) {
			updateEntity(entity, deltaTime);
		}
		super.update(deltaTime);
	}

	public void updateEntity(final Entity entity, final float deltaTime) {

	}

}
