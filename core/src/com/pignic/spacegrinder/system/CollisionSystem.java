package com.pignic.spacegrinder.system;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
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
import com.pignic.spacegrinder.component.Collectible;
import com.pignic.spacegrinder.component.Collision;
import com.pignic.spacegrinder.component.Durability;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Projectile;
import com.pignic.spacegrinder.component.ShipPart;
import com.pignic.spacegrinder.component.StellarObject;
import com.pignic.spacegrinder.component.Timer;
import com.pignic.spacegrinder.factory.complex.AsteroidFactory;
import com.pignic.spacegrinder.pojo.Deflection;

public class CollisionSystem extends IteratingSystem implements ContactListener {

	private static class CollisionHandler {

		public static abstract class Callback implements Callable<Object> {
			public Contact contact;
			public Entity entityA;
			public Entity entityB;
		}

		public Callback callback;
		public Class<? extends Component> classA;
		public Class<? extends Component> classB;

		public CollisionHandler(final Class<? extends Component> classA, final Class<? extends Component> classB,
				final Callback callback) {
			this.classA = classA;
			this.classB = classB;
			this.callback = callback;
		}
	}

	private static class Mapper {
		public static final ComponentMapper<Collision> collision = ComponentMapper.getFor(Collision.class);
		public static final ComponentMapper<Durability> durability = ComponentMapper.getFor(Durability.class);
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
		public static final ComponentMapper<Projectile> projectile = ComponentMapper.getFor(Projectile.class);
	}

	List<CollisionHandler> collisionHandlers = new ArrayList<CollisionHandler>();

	private final World world;

	public CollisionSystem(final World world) {
		super(Family.all(Collision.class).get());
		this.world = world;
		this.world.setContactListener(this);
		collisionHandlers.add(new CollisionHandler(Projectile.class, Projectile.class, new CollisionHandler.Callback() {
			@Override
			public Object call() throws Exception {
				entityA.getComponent(Physical.class).destroyBody();
				getEngine().removeEntity(entityA);
				entityB.getComponent(Physical.class).destroyBody();
				getEngine().removeEntity(entityB);
				return null;
			}
		}));
		collisionHandlers.add(new CollisionHandler(Projectile.class, ShipPart.class, new CollisionHandler.Callback() {
			@Override
			public Object call() throws Exception {
				final Projectile projectile = Mapper.projectile.get(entityA);
				final Durability durability = Mapper.durability.get(entityB);
				durability.hit(projectile.getDamage(), projectile.getDamageTypes().values()
						.toArray(new com.pignic.spacegrinder.component.Durability.Damage[0]));
				entityA.getComponent(Physical.class).destroyBody();
				getEngine().removeEntity(entityA);
				return null;
			}
		}));
		collisionHandlers
				.add(new CollisionHandler(Projectile.class, StellarObject.class, new CollisionHandler.Callback() {
					@Override
					public Object call() throws Exception {
						final Projectile projectile = Mapper.projectile.get(entityA);
						final Physical projectilePhysical = entityA.getComponent(Physical.class);
						if (projectilePhysical.getBody() != null) {
							if (projectile.getDamageTypes().get(Deflection.TYPE.MINING) != null) {
								AsteroidFactory.hitAsteroid(world, getEngine(), entityB,
										entityA.getComponent(Physical.class).getBody().getWorldCenter(), 2);
							}
							projectilePhysical.destroyBody();
						}
						getEngine().removeEntity(entityA);
						return null;
					}
				}));
		collisionHandlers.add(new CollisionHandler(ShipPart.class, ShipPart.class, new CollisionHandler.Callback() {
			@Override
			public Object call() throws Exception {
				// Damage the ship parts
				return null;
			}
		}));
		collisionHandlers
				.add(new CollisionHandler(ShipPart.class, StellarObject.class, new CollisionHandler.Callback() {
					@Override
					public Object call() throws Exception {
						// Damage the ship part
						return null;
					}
				}));
		collisionHandlers
				.add(new CollisionHandler(Collectible.class, Collectible.class, new CollisionHandler.Callback() {
					@Override
					public Object call() throws Exception {
						// Merge the collectible of the same type
						return null;
					}
				}));
	}

	@Override
	public void beginContact(final Contact contact) {
		// This is to avoid contact between projectile and emitter
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
			getEngine().addEntity(entity);
			getEngine().addEntity(particleEntity);
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
		final Collision collision = Mapper.collision.get(entity);
		for (final CollisionHandler collisionHandler : collisionHandlers) {
			if (collision.getEntityA().getComponent(collisionHandler.classA) != null) {
				if (collision.getEntityB().getComponent(collisionHandler.classB) != null) {
					collisionHandler.callback.entityA = collision.getEntityA();
					collisionHandler.callback.entityB = collision.getEntityB();
					collisionHandler.callback.contact = collision.getContact();
					try {
						collisionHandler.callback.call();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			} else if (collision.getEntityA().getComponent(collisionHandler.classB) != null) {
				if (collision.getEntityB().getComponent(collisionHandler.classA) != null) {
					collisionHandler.callback.entityA = collision.getEntityB();
					collisionHandler.callback.entityB = collision.getEntityA();
					collisionHandler.callback.contact = collision.getContact();
					try {
						collisionHandler.callback.call();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		getEngine().removeEntity(entity);
	}
}
