package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;

public class Collision implements Component {

	private final Contact contact;

	private final Entity entityA;

	private final Entity entityB;

	public Collision(final Entity entityA, final Entity entityB, final Contact contact) {
		this.entityA = entityA;
		this.entityB = entityB;
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	public Entity getEntityA() {
		return entityA;
	}

	public Entity getEntityB() {
		return entityB;
	}
}
