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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;

public class RenderSystem extends EntitySystem {

	private final SpriteBatch batch;

	private final Camera camera;

	private List<Entity> entities;

	ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

	ComponentMapper<Renderable> rm = ComponentMapper.getFor(Renderable.class);

	public RenderSystem(final SpriteBatch batch, final Camera camera) {
		this.batch = batch;
		this.camera = camera;
	}

	@Override
	public void addedToEngine(final Engine engine) {
		Collections.sort(entities = Arrays.asList(engine
				.getEntitiesFor(Family.all(Position.class, Renderable.class).get()).<Entity> toArray(Entity.class)),
				new Comparator<Entity>() {

					@Override
					public int compare(final Entity arg0, final Entity arg1) {
						return rm.get(arg0).getzIndex() - rm.get(arg1).getzIndex();
					}

				});
	}

	@Override
	public void update(final float deltaTime) {
		for (final Entity entity : entities) {
			final Position position = pm.get(entity);
			final Renderable renderable = rm.get(entity);
			batch.draw(renderable.getTexture(), (position.get().x + camera.position.x) / SpaceGrinder.WORLD_SCALE,
					(position.get().y + camera.position.y) / SpaceGrinder.WORLD_SCALE,
					renderable.getTexture().getWidth() * renderable.getScale() / SpaceGrinder.WORLD_SCALE,
					renderable.getTexture().getHeight() * renderable.getScale() / SpaceGrinder.WORLD_SCALE);
		}
		super.update(deltaTime);
	}
}
