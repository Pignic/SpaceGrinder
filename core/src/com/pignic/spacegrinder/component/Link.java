package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Link implements SerializableComponent {

	private boolean built = false;
	private boolean dirty = true;
	private Entity entityA;
	private Entity entityB;
	private long entityIdA;
	private long entityIdB;

	public Link() {

	}

	public Link(final Entity entityA, final Entity entityB) {
		this.entityA = entityA;
		this.entityB = entityB;
		entityIdA = entityA.getComponent(Id.class).getId();
		entityIdB = entityB.getComponent(Id.class).getId();
		dirty = false;
	}

	public Link(final long entityIdA, final long entityIdB) {
		this.entityIdA = entityIdA;
		this.entityIdB = entityIdB;
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		entityIdA = jsonData.getLong("entityIdA");
		entityIdB = jsonData.getLong("entityIdB");
	}

	public Entity getEntityA() {
		return entityA;
	}

	public Entity getEntityB() {
		return entityB;
	}

	public long getEntityIdA() {
		return entityIdA;
	}

	public long getEntityIdB() {
		return entityIdB;
	}

	public boolean isBuilt() {
		return built;
	}

	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void serialize(final Json json) {
		json.writeValue("entityIdA", entityIdA);
		json.writeValue("entityIdB", entityIdB);
	}

	public void setBuilt(final boolean built) {
		this.built = built;
	}

	public void setDirty(final boolean dirty) {
		this.dirty = dirty;
	}

	public void setEntityA(final Entity entityA) {
		this.entityA = entityA;
		entityIdA = entityA.getComponent(Id.class).getId();
	}

	public void setEntityB(final Entity entityB) {
		this.entityB = entityB;
		entityIdB = entityB.getComponent(Id.class).getId();
	}

	public void setEntityIdA(final long entityIdA) {
		this.entityIdA = entityIdA;
		dirty = true;
	}

	public void setEntityIdB(final long entityIdB) {
		this.entityIdB = entityIdB;
		dirty = true;
	}

}
