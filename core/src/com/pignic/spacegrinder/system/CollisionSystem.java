package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Collision;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Projectile;

public class CollisionSystem extends IteratingSystem implements ContactListener {

	private static class Mapper {
		public static final ComponentMapper<Collision> collision = ComponentMapper.getFor(Collision.class);
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
		public static final ComponentMapper<Projectile> projectile = ComponentMapper.getFor(Projectile.class);
	}

	private final Engine engine;

	private final World world;

	public CollisionSystem(final World world, final Engine engine) {
		super(Family.all(Collision.class).get());
		this.world = world;
		this.engine = engine;
		this.world.setContactListener(this);
	}

	@Override
	public void beginContact(final Contact contact) {
		if (contact.getFixtureA().getUserData() != null) {
			if (contact.getFixtureB().getBody().equals(contact.getFixtureA().getUserData())) {
				contact.setEnabled(false);
			}
		} else if (contact.getFixtureB().getUserData() != null) {
			if (contact.getFixtureA().getBody().equals(contact.getFixtureB().getUserData())) {
				contact.setEnabled(false);
			}
		} else {
			// final Entity entity = new Entity();
			// entity.add(new Collision((Entity) contact.getFixtureA().getBody().getUserData(),
			// (Entity) contact.getFixtureB().getBody().getUserData(), contact));
			// engine.addEntity(entity);
		}
	}

	@Override
	public void endContact(final Contact contact) {

	}

	@Override
	public void postSolve(final Contact contact, final ContactImpulse impulse) {

	}

	@Override
	public void preSolve(final Contact contact, final Manifold oldManifold) {

	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		// final Collision collision = Mapper.collision.get(entity);
		// final Physical physicalA = Mapper.physical.get(collision.getEntityA());
		// final Physical physicalB = Mapper.physical.get(collision.getEntityB());
		// final Entity eA = (Entity) physicalA.getBody().getFixtureList().get(0).getUserData();
		// final Entity eB = (Entity) physicalA.getBody().getFixtureList().get(0).getUserData();
		// if (eA != null) {
		//
		// } else if (eB != null) {
		//
		// }
		// engine.removeEntity(entity);
	}

}
