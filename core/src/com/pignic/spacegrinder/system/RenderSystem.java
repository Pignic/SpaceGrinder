package com.pignic.spacegrinder.system;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;

public class RenderSystem extends SortedIteratingSystem {

	private static class Mapper {
		private final static ComponentMapper<Particle> particle = ComponentMapper.getFor(Particle.class);
		// private final static ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
		public final static ComponentMapper<Position> position = ComponentMapper.getFor(Position.class);
		private final static ComponentMapper<Renderable> renderable = ComponentMapper.getFor(Renderable.class);
	}

	// private final SpriteBatch batch;
	private PolygonSpriteBatch batch;

	public RenderSystem(final PolygonSpriteBatch batch) {
		super(Family.all(Position.class).one(Renderable.class, Particle.class).get(), new Comparator<Entity>() {
			@Override
			public int compare(final Entity entityA, final Entity entityB) {
				return (int) (Mapper.position.get(entityA).getZ() - Mapper.position.get(entityB).getZ());
			}
		});
		// this.batch = batch;
		this.batch = batch;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Position position = Mapper.position.get(entity);
		final Renderable renderable = Mapper.renderable.get(entity);
		final Particle particle = Mapper.particle.get(entity);
		if (particle != null) {
			final ParticleEmitter emitter = particle.getEmitter();
			if (particle.isActive()) {
				emitter.setPosition(position.get().x / SpaceGrinder.WORLD_SCALE,
						position.get().y / SpaceGrinder.WORLD_SCALE);
				if (particle.isRotating()) {
					final ScaledNumericValue angle = emitter.getAngle();
					angle.setLowMin((float) Math.toDegrees(position.getAngle() + particle.getRotation())
							+ particle.getAngleLowMin());
					angle.setLowMax((float) Math.toDegrees(position.getAngle() + particle.getRotation())
							+ particle.getAngleLowMax());
					angle.setHigh((float) Math.toDegrees(position.getAngle() + particle.getRotation())
							+ particle.getAngleHigh());
				}
				emitter.getScale().setHigh(particle.getScale());
				emitter.getScale().setLow(particle.getScale());
			}
			emitter.draw(batch, deltaTime);
		}
		if (renderable != null) {
			if (renderable.isTiled()) {
				renderable.getPolygonSprite().setRotation((float) Math.toDegrees(position.getAngle()));
				renderable.getPolygonSprite().setPosition(
						position.get().x / SpaceGrinder.WORLD_SCALE - renderable.getPolygonSprite().getOriginX(),
						position.get().y / SpaceGrinder.WORLD_SCALE - renderable.getPolygonSprite().getOriginY());
				renderable.getPolygonSprite().draw(batch);
			} else {
				renderable.getSprite().setRotation((float) Math.toDegrees(position.getAngle()));
				renderable.getSprite().setCenter(position.get().x / SpaceGrinder.WORLD_SCALE,
						position.get().y / SpaceGrinder.WORLD_SCALE);
				renderable.getSprite().draw(batch);
			}
		}
	}
}
