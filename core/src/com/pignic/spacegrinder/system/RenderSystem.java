package com.pignic.spacegrinder.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;

public class RenderSystem extends EntitySystem {

	private final SpriteBatch batch;

	private List<Entity> entities;

	ComponentMapper<Particle> parm = ComponentMapper.getFor(Particle.class);

	ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

	ComponentMapper<Renderable> rm = ComponentMapper.getFor(Renderable.class);

	public RenderSystem(final SpriteBatch batch) {
		this.batch = batch;
	}

	@Override
	public void addedToEngine(final Engine engine) {
		Collections.sort(entities = Arrays
				.asList(engine.getEntitiesFor(Family.all(Position.class).one(Renderable.class, Particle.class).get())
						.<Entity> toArray(Entity.class)),
				new Comparator<Entity>() {
					@Override
					public int compare(final Entity entityA, final Entity entityB) {
						final Renderable renderableA = rm.get(entityA);
						final Renderable renderableB = rm.get(entityB);
						return (renderableA != null ? renderableA.getZIndex() : 0)
								- (renderableB != null ? renderableB.getZIndex() : 0);
					}
				});
	}

	@Override
	public void update(final float deltaTime) {
		for (final Entity entity : entities) {
			final Position position = pm.get(entity);
			final Renderable renderable = rm.get(entity);
			final Particle particle = parm.get(entity);
			if (particle != null) {
				final ParticleEmitter emitter = particle.getEmitter();
				emitter.setPosition(position.get().x / SpaceGrinder.WORLD_SCALE,
						position.get().y / SpaceGrinder.WORLD_SCALE);
				final ScaledNumericValue angle = emitter.getAngle();
				angle.setLow((float) Math.toDegrees(position.getAngle() + particle.getRotation()));
				angle.setHigh((float) Math.toDegrees(position.getAngle() + particle.getRotation()));
				emitter.draw(batch, deltaTime);
			}
			if (renderable != null) {
				renderable.getSprite().setRotation((float) Math.toDegrees(position.getAngle()));
				renderable.getSprite().setCenter(position.get().x / SpaceGrinder.WORLD_SCALE,
						position.get().y / SpaceGrinder.WORLD_SCALE);
				renderable.getSprite().draw(batch);
			}
		}
		super.update(deltaTime);
	}
}
