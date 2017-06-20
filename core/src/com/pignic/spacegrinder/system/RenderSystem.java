package com.pignic.spacegrinder.system;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;

public class RenderSystem extends SortedIteratingSystem {

	private static class Mapper {
		private final static ComponentMapper<Particle> particle = ComponentMapper.getFor(Particle.class);
		public final static ComponentMapper<Position> position = ComponentMapper.getFor(Position.class);
		private final static ComponentMapper<Renderable> renderable = ComponentMapper.getFor(Renderable.class);
	}

	private final SpriteBatch batch;

	public RenderSystem(final SpriteBatch batch) {
		super(Family.all(Position.class).one(Renderable.class, Particle.class).get(), new Comparator<Entity>() {
			@Override
			public int compare(final Entity entityA, final Entity entityB) {
				return (int) (Mapper.position.get(entityA).getZ() - Mapper.position.get(entityB).getZ());
			}
		});
		this.batch = batch;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Position position = Mapper.position.get(entity);
		final Renderable renderable = Mapper.renderable.get(entity);
		final Particle particle = Mapper.particle.get(entity);
		if (particle != null) {
			final ParticleEmitter emitter = particle.getEmitter();
			emitter.setPosition(position.get().x / SpaceGrinder.WORLD_SCALE,
					position.get().y / SpaceGrinder.WORLD_SCALE);
			final ScaledNumericValue angle = emitter.getAngle();
			angle.setLow((float) Math.toDegrees(position.getAngle() + particle.getRotation()));
			angle.setHigh((float) Math.toDegrees(position.getAngle() + particle.getRotation()));
			emitter.getScale().setHigh(particle.getScale());
			emitter.getScale().setLow(particle.getScale());
			emitter.draw(batch, deltaTime);
		}
		if (renderable != null) {
			renderable.getSprite().setRotation((float) Math.toDegrees(position.getAngle()));
			renderable.getSprite().setCenter(position.get().x / SpaceGrinder.WORLD_SCALE,
					position.get().y / SpaceGrinder.WORLD_SCALE);
			renderable.getSprite().draw(batch);
		}
	}
}
