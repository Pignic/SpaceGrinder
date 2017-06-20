package com.pignic.spacegrinder.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.RenderHelper;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Configurable;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.basic.ShipPartFactory;
import com.pignic.spacegrinder.factory.basic.StructureFactory;
import com.pignic.spacegrinder.factory.complex.ShipFactory;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;
import com.pignic.spacegrinder.system.ControlSystem;
import com.pignic.spacegrinder.system.PhysicSystem;
import com.pignic.spacegrinder.system.RenderSystem;

public class BuilderScreen extends AbstractScreen {

	private final SpriteBatch batch;
	private final Camera camera;
	private Actor currentButton;
	private Entity currentPiece;
	private ShipFactory.PART_TYPE currentType;
	private final Box2DDebugRenderer debugRenderer;
	private final PooledEngine engine;
	private final List<Entity> entities = new ArrayList<Entity>();
	private final SpaceGrinder game;
	private final Texture grid;
	private Body lastPickedBody;
	private Table menuTable;
	private final Vector3 mouse = new Vector3();
	QueryCallback mouseClickCallback = new QueryCallback() {
		@Override
		public boolean reportFixture(final Fixture fixture) {
			if (fixture.testPoint(mouse.x, mouse.y)) {
				pickedBody = fixture.getBody();
				return false;
			} else {
				return true;
			}
		}
	};
	protected MouseJoint mouseJoint = null;
	private Body originBody;
	private boolean paused = true;
	private Body pickedBody;
	private Table propertiesTable;
	private float rotation = 0;
	private boolean runningSimulation = false;

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
		originBody = world.createBody(new BodyDef());
		stage = new Stage() {

			@Override
			public boolean mouseMoved(final int screenX, final int screenY) {
				if (!super.mouseMoved(screenX, screenY)) {
					camera.unproject(mouse.set(screenX, screenY, 0));
					if (mouseJoint != null) {
						mouseJoint.setTarget(new Vector2(mouse.x, mouse.y));
					}
				}
				return true;
			}

			@Override
			public boolean scrolled(final int amount) {
				rotation += Math.PI * amount / 20;
				if (mouseJoint != null && mouseJoint.getBodyB() != null) {
					mouseJoint.getBodyB().setTransform(mouseJoint.getBodyB().getWorldCenter(), rotation);
				}
				return super.scrolled(amount);
			}

			@Override
			public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
				if (!super.touchDown(screenX, screenY, pointer, button)) {
					camera.unproject(mouse.set(screenX, screenY, 0));
					lastPickedBody = pickedBody;
					pickedBody = null;
					world.QueryAABB(mouseClickCallback, mouse.x - 0.0001f, mouse.y - 0.0001f, mouse.x + 0.0001f,
							mouse.y + 0.0001f);
					if (button == Buttons.LEFT) {
						if (currentType != null) {
							final Entity createdEntity = buildPart(currentType,
									mouseJoint == null ? null : mouseJoint.getBodyB().getWorldCenter());
							if (createdEntity != null) {
								if (createdEntity != null) {
									final Physical physical = createdEntity.getComponent(Physical.class);
									if (physical != null) {
										physical.getBody().setType(BodyType.StaticBody);
									}
								}
								engine.addEntity(createdEntity);
								entities.add(createdEntity);
							}
						} else if (pickedBody != null) {
							// Show properties
							setupProperties((Entity) pickedBody.getUserData());
						} else {
							propertiesTable.setVisible(false);
						}
					} else if (button == Buttons.RIGHT) {
						clearTempEntity();
					}
				}
				return true;
			}
		};
	}

	public Table buildMenu(final BuilderScreen screen) {
		final Table table = new Table(style.skin);
		final Button runButton = new Button(style.skin);
		runButton.add(new Label("run", style.skin));
		runButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				setSimulation(!runningSimulation);
			}
		});
		table.add(runButton).width(100).left();
		table.row();

		for (final ShipFactory.PART_TYPE type : ShipFactory.PART_TYPE.values()) {
			final Button partButton = new Button(style.skin);
			partButton.add(new Label(type.name(), style.skin));
			partButton.setUserObject(type);
			partButton.addListener(new ChangeListener() {
				@Override
				public void changed(final ChangeEvent event, final Actor actor) {
					clearTempEntity();
					currentType = (PART_TYPE) actor.getUserObject();
					currentButton = actor;
					final Entity entity = buildPart(currentType, new Vector2());
					Physical physical;
					if (entity != null && (physical = entity.getComponent(Physical.class)) != null) {
						final MouseJointDef def = new MouseJointDef();
						physical.getBody().setFixedRotation(true);
						physical.getBody().setUserData(entity);
						def.bodyA = originBody;
						def.bodyB = physical.getBody();
						def.collideConnected = true;
						def.bodyB.setTransform(new Vector2(mouse.x, mouse.y), rotation);
						def.target.set(mouse.x, mouse.y);
						def.maxForce = 10000.0f * physical.getBody().getMass();
						mouseJoint = (MouseJoint) world.createJoint(def);
						physical.getBody().setAwake(true);
					}
				}
			});
			table.add(partButton).width(100).left();
			table.row();
		}
		table.bottom().left();
		return table;
	}

	private Entity buildPart(final PART_TYPE type, final Vector2 position) {
		Entity createdEntity = null;
		if (ShipFactory.PART_TYPE.STRUCTURE.equals(currentType)) {
			if (pickedBody != null && lastPickedBody != null && pickedBody != lastPickedBody) {
				createdEntity = StructureFactory.build(world,
						((Entity) pickedBody.getUserData()).getComponent(Physical.class),
						((Entity) lastPickedBody.getUserData()).getComponent(Physical.class));
				lastPickedBody = null;
				pickedBody = null;
			}
		} else {
			createdEntity = ShipPartFactory.build(world, new Vector2(position.x, position.y), rotation,
					currentType.clazz);
		}
		return createdEntity;
	}

	public Table buildProperties(final BuilderScreen screen) {
		final Table table = new Table();
		table.setWidth(300);
		table.setVisible(false);
		table.top();
		table.right();
		return table;
	}

	private void clearTempEntity() {
		currentType = null;
		currentButton = null;
		lastPickedBody = null;
		pickedBody = null;
		if (mouseJoint != null) {
			final Body tempBody = mouseJoint.getBodyB();
			final Entity tempEntity = (Entity) tempBody.getUserData();
			world.destroyJoint(mouseJoint);
			world.destroyBody(tempBody);
			engine.removeEntity(tempEntity);
			mouseJoint = null;
		}
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
		batch.end();
		debugRenderer.render(world, camera.combined);
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
		propertiesTable.setPosition(stage.getWidth() - propertiesTable.getWidth(), stage.getHeight());
	}

	@Override
	public void resume() {
		paused = false;
	}

	private boolean setSimulation(final boolean active) {
		for (final Entity entity : entities) {
			final Physical physical = entity.getComponent(Physical.class);
			if (physical != null) {
				physical.getBody().setType(active ? BodyType.DynamicBody : BodyType.StaticBody);
			}
		}
		runningSimulation = active;
		return active;
	}

	private void setupProperties(final Entity entity) {
		propertiesTable.clear();
		propertiesTable.setVisible(true);
		propertiesTable.setPosition(stage.getWidth() - propertiesTable.getWidth(), stage.getHeight());
		for (final Component component : entity.getComponents()) {
			if (component instanceof Configurable) {
				((Configurable) component).getConfiguration(propertiesTable).row();
			}
		}
	}

	@Override
	public void show() {
		paused = false;
		stage.clear();
		stage.addActor(menuTable = buildMenu(this));
		stage.addActor(propertiesTable = buildProperties(this));
		Gdx.input.setInputProcessor(stage);
	}

}
