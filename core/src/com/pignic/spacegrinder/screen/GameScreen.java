package com.pignic.spacegrinder.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.RenderHelper;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.ShipFactory;
import com.pignic.spacegrinder.system.ControlSystem;
import com.pignic.spacegrinder.system.HierarchySystem;
import com.pignic.spacegrinder.system.PhysicSystem;
import com.pignic.spacegrinder.system.RenderSystem;

public class GameScreen implements Screen {

	private final Texture background;
	private final SpriteBatch batch;
	private final Camera camera;
	private final Box2DDebugRenderer debugRenderer;
	private final Engine engine;
	private final SpaceGrinder game;
	private final Texture parallax1;
	private final Texture parallax2;
	private boolean paused = true;
	private final Entity ship;
	private final World world;

	public GameScreen(final SpaceGrinder game) {
		this.game = game;
		engine = new Engine();
		world = new World(new Vector2(), false);
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawVelocities(true);
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / SpaceGrinder.WORLD_SCALE,
				Gdx.graphics.getHeight() / SpaceGrinder.WORLD_SCALE);
		batch = new SpriteBatch();
		ship = ShipFactory.buildShip(world);
		engine.addEntity(ship);
		engine.addSystem(new HierarchySystem());
		engine.addSystem(new ControlSystem());
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new RenderSystem(batch));
		background = new Texture(Constants.TEXTURE_PATH + "bg.png");
		parallax1 = new Texture(Constants.TEXTURE_PATH + "parallax1.png");
		parallax2 = new Texture(Constants.TEXTURE_PATH + "parallax2.png");
	}

	@Override
	public void dispose() {
		batch.dispose();
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
		// batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		final Body shipBody = ship.getComponent(Physical.class).getBody();
		camera.position.set(new Vector2(shipBody.getWorldCenter()).add(shipBody.getLinearVelocity()), 10);
		batch.begin();
		RenderHelper.drawTiledParalax(background, batch, 1, 0, camera);
		RenderHelper.drawTiledParalax(parallax1, batch, 1, 0.3f, camera);
		RenderHelper.drawTiledParalax(parallax2, batch, 1, 1, camera);
		engine.update(Gdx.graphics.getDeltaTime());
		batch.end();
		debugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(final int width, final int height) {
		camera.viewportWidth = width / SpaceGrinder.WORLD_SCALE;
		camera.viewportHeight = height / SpaceGrinder.WORLD_SCALE;
		camera.update();
	}

	@Override
	public void resume() {
		paused = false;
	}

	@Override
	public void show() {
		paused = false;
	}

}
