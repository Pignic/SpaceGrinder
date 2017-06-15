package com.pignic.spacegrinder.screen;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.RenderHelper;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.factory.ShipFactory;
import com.pignic.spacegrinder.factory.ShipFactory.PART_TYPE;
import com.pignic.spacegrinder.system.ControlSystem;
import com.pignic.spacegrinder.system.PhysicSystem;
import com.pignic.spacegrinder.system.RenderSystem;

public class BuilderScreen implements Screen {

	private final SpriteBatch batch;

	private final Camera camera;

	private Actor currentButton;

	private ShipFactory.PART_TYPE currentType;
	private final Box2DDebugRenderer debugRenderer;
	private final PooledEngine engine;
	private final SpaceGrinder game;
	private final Texture grid;
	private boolean paused = true;
	private float rotation = 0;
	private final Stage stage;
	private final World world;

	public BuilderScreen(final SpaceGrinder game) {
		this.game = game;
		engine = new PooledEngine();
		world = new World(new Vector2(), false);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / SpaceGrinder.WORLD_SCALE,
				Gdx.graphics.getHeight() / SpaceGrinder.WORLD_SCALE);
		batch = new SpriteBatch();
		engine.addSystem(new ControlSystem());
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new RenderSystem(batch));
		grid = new Texture(Constants.TEXTURE_PATH + "grid.png");
		stage = new Stage() {
			@Override
			public boolean scrolled(final int amount) {
				rotation += Math.PI * amount / 20;
				return super.scrolled(amount);
			}

			@Override
			public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
				if (!super.touchDown(screenX, screenY, pointer, button)) {
					final Ray ray = camera.getPickRay(screenX, screenY);
					if (ShipFactory.PART_TYPE.THRUSTER.equals(currentType)) {
						engine.addEntity(ShipFactory.buildThruster(world, new Vector2(ray.origin.x, ray.origin.y),
								rotation, Input.Keys.Z));
					} else if (ShipFactory.PART_TYPE.COCKPIT.equals(currentType)) {

					}
				}
				return true;
			}
		};
	}

	public Table buildMenu(final BuilderScreen screen) {
		final BitmapFont font = new BitmapFont();
		font.setUseIntegerPositions(false);

		final ButtonStyle style = new ButtonStyle();
		final LabelStyle lStyle = new LabelStyle();
		lStyle.font = font;

		final Table table = new Table();
		for (final ShipFactory.PART_TYPE type : ShipFactory.PART_TYPE.values()) {
			final Button partButton = new Button(style);
			partButton.add(new Label(type.name(), lStyle));
			partButton.setUserObject(type);
			partButton.addListener(new ChangeListener() {
				@Override
				public void changed(final ChangeEvent event, final Actor actor) {
					if (actor == currentButton) {
						currentType = null;
						currentButton = null;
					} else {
						currentType = (PART_TYPE) actor.getUserObject();
						currentButton = actor;
					}
				}
			});
			table.add(partButton).left();
			table.row();
		}
		table.bottom().left();
		return table;
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
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		RenderHelper.drawTiledParalax(grid, batch, 1, 1, camera);
		engine.update(delta);
		if (currentType != null) {
			final Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			final Sprite sprite = new Sprite(ShipFactory.textures.get(currentType.clazz).get(0));
			sprite.setOriginCenter();
			sprite.setRotation((float) Math.toDegrees(rotation));
			sprite.setScale(1f / SpaceGrinder.WORLD_SCALE);
			sprite.setCenter(ray.origin.x, ray.origin.y);
			sprite.draw(batch);
		}
		batch.end();
		stage.draw();
		// debugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(final int width, final int height) {
		camera.viewportWidth = width / SpaceGrinder.WORLD_SCALE;
		camera.viewportHeight = height / SpaceGrinder.WORLD_SCALE;
		stage.getViewport().setWorldHeight(height);
		camera.update();
	}

	@Override
	public void resume() {
		paused = false;
	}

	@Override
	public void show() {
		paused = false;
		stage.clear();
		stage.addActor(buildMenu(this));
		Gdx.input.setInputProcessor(stage);
	}

}
