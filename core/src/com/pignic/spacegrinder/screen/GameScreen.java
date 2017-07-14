package com.pignic.spacegrinder.screen;

import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.RenderHelper;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.complex.ShipFactory;
import com.pignic.spacegrinder.system.CollisionSystem;
import com.pignic.spacegrinder.system.ControlSystem;
import com.pignic.spacegrinder.system.DurabilitySystem;
import com.pignic.spacegrinder.system.LightSystem;
import com.pignic.spacegrinder.system.LinkSystem;
import com.pignic.spacegrinder.system.PhysicSystem;
import com.pignic.spacegrinder.system.ProjectileSystem;
import com.pignic.spacegrinder.system.RenderSystem;
import com.pignic.spacegrinder.system.TimerSystem;

import box2dLight.RayHandler;

public class GameScreen extends AbstractScreen {

	private final Texture background;
	private PolygonSpriteBatch batch;
	private final Camera camera;
	private final Box2DDebugRenderer debugRenderer;
	private final PooledEngine engine;
	private final SpaceGrinder game;
	private RayHandler lightsRayHandler;
	private final Texture parallax1;
	private final Texture parallax2;
	private boolean paused = true;
	private Entity ship;
	private final Stage stage;
	private final World world;

	public GameScreen(final SpaceGrinder game) {
		this.game = game;
		engine = new PooledEngine();
		world = new World(new Vector2(), false);
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawVelocities(true);
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / SpaceGrinder.WORLD_SCALE,
				Gdx.graphics.getHeight() / SpaceGrinder.WORLD_SCALE);
		batch = new PolygonSpriteBatch();
		lightsRayHandler = new RayHandler(world);
		final ControlSystem controlSystem = new ControlSystem();
		engine.addSystem(controlSystem);
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new RenderSystem(batch));
		engine.addSystem(new LinkSystem(world));
		engine.addSystem(new LightSystem(lightsRayHandler));
		engine.addSystem(new ProjectileSystem());
		engine.addSystem(new TimerSystem());
		engine.addSystem(new CollisionSystem(world));
		engine.addSystem(new DurabilitySystem());
		final List<Entity> entities = ShipFactory.buildShip(world);
		ship = entities.get(0);
		for (final Entity entity : entities) {
			engine.addEntity(entity);
		}
		background = new Texture(Constants.TEXTURE_PATH + "bg.png");
		parallax1 = new Texture(Constants.TEXTURE_PATH + "parallax1.png");
		parallax2 = new Texture(Constants.TEXTURE_PATH + "parallax2.png");
		stage = new Stage() {

			@Override
			public boolean keyDown(final int keyCode) {
				if (!super.keyDown(keyCode)) {
					controlSystem.setKeyState(keyCode, true);
				}
				return true;
			}

			@Override
			public boolean keyUp(final int keyCode) {
				controlSystem.setKeyState(keyCode, false);
				return super.keyUp(keyCode);
			}
		};
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	public void hide() {
		paused = true;
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void render(final float delta) {
		if (paused) {
			return;
		}
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		final Body shipBody = ship.getComponent(Physical.class).getBody();
		camera.position.set(new Vector2(shipBody.getWorldCenter()).add(shipBody.getLinearVelocity()), 10);
		batch.begin();
		RenderHelper.drawTiledParalax(background, batch, 1, 0, camera);
		RenderHelper.drawTiledParalax(parallax1, batch, 1, 0.3f, camera);
		RenderHelper.drawTiledParalax(parallax2, batch, 1, 1, camera);
		engine.update(delta);
		batch.end();
		// debugRenderer.render(world, camera.combined);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		camera.viewportWidth = width / SpaceGrinder.WORLD_SCALE;
		camera.viewportHeight = height / SpaceGrinder.WORLD_SCALE;
		camera.update();
		stage.getViewport().setScreenSize(width, height);
		stage.getViewport().setWorldSize(width, height);
		stage.getViewport().apply(true);
	}

	@Override
	public void resume() {
		paused = false;
	}

	@Override
	public void show() {
		paused = false;
		stage.clear();
		Gdx.input.setInputProcessor(stage);
		if (ship == null) {

		}
	}

}
