package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Collision;
import com.pignic.spacegrinder.component.Durability;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Projectile;
import com.pignic.spacegrinder.component.Timer;

public class CollisionSystem extends IteratingSystem implements ContactListener {

	private static class Mapper {
		public static final ComponentMapper<Collision> collision = ComponentMapper.getFor(Collision.class);
		public static final ComponentMapper<Durability> durability = ComponentMapper.getFor(Durability.class);
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
		if (contact.getFixtureA().getUserData() != null
				&& contact.getFixtureB().getBody().equals(contact.getFixtureA().getUserData())) {
			contact.setEnabled(false);
		} else if (contact.getFixtureB().getUserData() != null
				&& contact.getFixtureA().getBody().equals(contact.getFixtureB().getUserData())) {
			contact.setEnabled(false);
		} else {
			final Entity entity = new Entity();
			entity.add(new Collision((Entity) contact.getFixtureA().getBody().getUserData(),
					(Entity) contact.getFixtureB().getBody().getUserData(), contact));
			final Entity particleEntity = new Entity();
			particleEntity.add(new Timer(100).setDestroyOnTimeout(true));
			particleEntity.add(new Particle(EFFECT.IMPACT).setLoop(false).setActive(true));
			particleEntity.add(
					new Position(new Vector2(contact.getWorldManifold().getPoints()[0]).scl(SpaceGrinder.WORLD_SCALE),
							new Vector2(), contact.getWorldManifold().getNormal().angle()));
			engine.addEntity(entity);
			engine.addEntity(particleEntity);
		}
	}

	@Override
	public void endContact(final Contact contact) {

	}

	private void handlePartToPart(final Entity partA, final Entity partB) {

	}

	private void handleProjectileToPart(final Entity projectileEntity, final Entity partEntity) {
		final Projectile projectile = Mapper.projectile.get(projectileEntity);
		final Durability durability = Mapper.durability.get(partEntity);
		durability.hit(projectile.getDamage(), projectile.getDamageTypes().values()
				.toArray(new com.pignic.spacegrinder.component.Durability.Damage[0]));
		world.destroyBody(projectileEntity.getComponent(Physical.class).getBody());
		engine.removeEntity(projectileEntity);
	}

	private void handleProjectileToProjectile(final Entity projectileA, final Entity ProjectileB) {

	}

	@Override
	public void postSolve(final Contact contact, final ContactImpulse impulse) {

	}

	@Override
	public void preSolve(final Contact contact, final Manifold oldManifold) {

	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Collision collision = Mapper.collision.get(entity);
		final Projectile projectileA = Mapper.projectile.get(collision.getEntityA());
		final Projectile projectileB = Mapper.projectile.get(collision.getEntityB());
		if (projectileA != null && projectileB != null) {
			handleProjectileToProjectile(collision.getEntityA(), collision.getEntityB());
		} else if (projectileA != null) {
			handleProjectileToPart(collision.getEntityA(), collision.getEntityB());
		} else if (projectileB != null) {
			handleProjectileToPart(collision.getEntityB(), collision.getEntityA());
		} else {
			handlePartToPart(collision.getEntityA(), collision.getEntityB());
		}
		engine.removeEntity(entity);
	}

}
