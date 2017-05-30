package com.pignic.spacegrinder;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.ShipFactory;
import com.pignic.spacegrinder.system.ControlSystem;
import com.pignic.spacegrinder.system.HierarchySystem;
import com.pignic.spacegrinder.system.PhysicSystem;
import com.pignic.spacegrinder.system.RenderSystem;

public class SpaceGrinder extends ApplicationAdapter {

	public static final float WORLD_SCALE = 10;

	Texture background;

	SpriteBatch batch;
	private Camera camera;
	Box2DDebugRenderer debugRenderer;
	Engine engine;
	private long lastUpdate = 0;
	Texture parallax1;
	Texture parallax2;
	private Entity ship;
	World world;

	@Override
	public void create() {
		engine = new Engine();
		world = new World(new Vector2(), false);
		lastUpdate = System.currentTimeMillis();
		debugRenderer = new Box2DDebugRenderer();
		// debugRenderer.setDrawVelocities(true);
		camera = new OrthographicCamera(800 / WORLD_SCALE, 600 / WORLD_SCALE);
		batch = new SpriteBatch();
		ship = ShipFactory.buildShip(world);
		engine.addEntity(ship);
		engine.addSystem(new HierarchySystem());
		engine.addSystem(new ControlSystem());
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new RenderSystem(batch, camera));
		background = new Texture("bg.png");
		parallax1 = new Texture("parallax1.png");
		parallax2 = new Texture("parallax2.png");
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {
		camera.update();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		final Body shipBody = ship.getComponent(Physical.class).getBody();
		camera.position.set(new Vector2(shipBody.getWorldCenter()).add(shipBody.getLinearVelocity()), 10);
		batch.begin();
		RenderHelper.drawTiledParalax(background, batch, 1, 0, camera);
		RenderHelper.drawTiledParalax(parallax1, batch, 1, 0.3f, camera);
		RenderHelper.drawTiledParalax(parallax2, batch, 1, 1, camera);
		engine.update(-(lastUpdate - (lastUpdate = System.currentTimeMillis())));
		batch.end();
		debugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(final int width, final int height) {
		camera.viewportWidth = width / WORLD_SCALE;
		camera.viewportHeight = height / WORLD_SCALE;
	}
}
