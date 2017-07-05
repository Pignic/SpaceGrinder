package com.pignic.spacegrinder.screen;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.RenderHelper;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Configurable;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.basic.ShipPartFactory;
import com.pignic.spacegrinder.factory.basic.StructureFactory;
import com.pignic.spacegrinder.factory.complex.ShipFactory;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.service.SaveService;
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

public class BuilderScreen extends AbstractScreen {

	private final static float cameraSpeed = 0.5f;

	private Table actionsTable;

	private final SpriteBatch batch;
	private final OrthographicCamera camera;
	private ControlSystem controlSystem;
	private Actor currentButton;
	private Entity currentPiece;
	private ShipPart currentType;
	private final Box2DDebugRenderer debugRenderer;
	private final PooledEngine engine;
	private final SpaceGrinder game;
	private final Texture grid;
	private Body lastPickedBody;
	private RayHandler lightsRayHandler;
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

	private float zoomLevel = 1;

	public BuilderScreen(final SpaceGrinder game) {
		this.game = game;
		engine = new PooledEngine();
		world = new World(new Vector2(), false);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / SpaceGrinder.WORLD_SCALE,
				Gdx.graphics.getHeight() / SpaceGrinder.WORLD_SCALE);
		batch = new SpriteBatch();
		lightsRayHandler = new RayHandler(world);
		controlSystem = new ControlSystem();
		engine.addSystem(controlSystem);
		engine.addSystem(new LinkSystem(world));
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new RenderSystem(batch));
		engine.addSystem(new LightSystem(batch, lightsRayHandler));
		engine.addSystem(new ProjectileSystem());
		engine.addSystem(new TimerSystem(engine));
		engine.addSystem(new CollisionSystem(world));
		engine.addSystem(new DurabilitySystem());

		grid = new Texture(Constants.TEXTURE_PATH + "grid.png");
		originBody = world.createBody(new BodyDef());
		stage = new Stage() {

			@Override
			public boolean keyDown(final int keyCode) {
				if (!super.keyDown(keyCode)) {
					if (keyCode == Keys.DEL) {
						if (pickedBody != null) {
							final Entity entity = (Entity) pickedBody.getUserData();
							entity.getComponent(Physical.class).destroyBody();
							engine.removeEntity(entity);
							pickedBody = null;
							propertiesTable.setVisible(false);
						}
					}
					controlSystem.setKeyState(keyCode, true);
				}
				return true;
			}

			@Override
			public boolean keyUp(final int keyCode) {
				controlSystem.setKeyState(keyCode, false);
				return super.keyUp(keyCode);
			}

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
				if (mouseJoint != null && mouseJoint.getBodyB() != null) {
					rotation += Math.PI * amount / 20;
					mouseJoint.getBodyB().setTransform(mouseJoint.getBodyB().getWorldCenter(), rotation);
				} else {
					zoomLevel += 0.1 * amount;
					camera.viewportWidth = stage.getWidth() * zoomLevel / SpaceGrinder.WORLD_SCALE;
					camera.viewportHeight = stage.getHeight() * zoomLevel / SpaceGrinder.WORLD_SCALE;
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
								final Physical physical = createdEntity.getComponent(Physical.class);
								if (physical != null) {
									physical.getBody().setType(BodyType.StaticBody);
								}
								engine.addEntity(createdEntity);
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
		setSimulation(false);
	}

	private Table buildActions(final BuilderScreen screen) {
		final Table table = new Table(style.skin);
		table.setWidth(128);
		final ImageButton runButton = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("texture/ui/run.png")))));
		runButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				setSimulation(!runningSimulation);
			}
		});
		final ImageButton saveButton = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("texture/ui/save.png")))));
		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				SaveService.save("test", engine.getEntities());
			}
		});
		final ImageButton loadButton = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("texture/ui/load.png")))));
		loadButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				final Entity[] entities = SaveService.load("test", world);
				for (final Entity entity : entities) {
					engine.addEntity(entity);
				}
			}
		});
		table.add(runButton).width(32).height(32);
		table.add(saveButton).width(32).height(32);
		table.add(loadButton).width(32).height(32);
		return table;
	}

	public Table buildMenu(final BuilderScreen screen) {
		final Table table = new Table(style.skin);
		final Tree tree = new Tree(style.skin);
		for (final ShipFactory.PART_TYPE type : ShipFactory.PART_TYPE.values()) {
			final Label label = new Label(type.name(), style.skin, "light");
			final Node node = new Node(label);
			tree.add(node);
			for (final ShipPart part : type.config) {
				final Button partButton = new Button(style.skin);
				partButton.add(new Label(part.name, style.skin));
				partButton.setUserObject(part);
				partButton.addListener(new ChangeListener() {
					@Override
					public void changed(final ChangeEvent event, final Actor actor) {
						clearTempEntity();
						currentType = (ShipPart) actor.getUserObject();
						currentButton = actor;
						final Entity entity = buildPart(currentType, new Vector2(mouse.x, mouse.y));
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
						if (entity != null) {
							engine.addEntity(entity);
						}
					}
				});
				node.add(new Node(partButton));
			}
		}
		table.add(tree);
		table.bottom().left();
		return table;
	}

	private Entity buildPart(final ShipPart type, final Vector2 position) {
		Entity createdEntity = null;
		if (ShipFactory.PART_TYPE.STRUCTURE.equals(currentType.getPartType())) {
			if (pickedBody != null && lastPickedBody != null && pickedBody != lastPickedBody) {
				createdEntity = StructureFactory.build(world, currentType, (Entity) pickedBody.getUserData(),
						(Entity) lastPickedBody.getUserData());
				lastPickedBody = null;
				pickedBody = null;
			}
		} else {
			createdEntity = ShipPartFactory.build(world, currentType, new Vector2(position.x, position.y), rotation);
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
		update(delta);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		lightsRayHandler.setCombinedMatrix(camera);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lightsRayHandler.updateAndRender();
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
		camera.viewportWidth = width * zoomLevel / SpaceGrinder.WORLD_SCALE;
		camera.viewportHeight = height * zoomLevel / SpaceGrinder.WORLD_SCALE;
		camera.update();
		stage.getViewport().setScreenSize(width, height);
		stage.getViewport().setWorldSize(width, height);
		stage.getViewport().apply(true);
		propertiesTable.setPosition(stage.getWidth() - propertiesTable.getWidth(), stage.getHeight());
		actionsTable.setPosition(stage.getWidth() - actionsTable.getWidth(), 32);
	}

	@Override
	public void resume() {
		paused = false;
	}

	private boolean setSimulation(final boolean active) {
		final ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(Physical.class).get());
		for (final Entity entity : entities) {
			entity.getComponent(Physical.class).getBody().setType(active ? BodyType.DynamicBody : BodyType.StaticBody);
		}
		runningSimulation = active;
		controlSystem.setProcessing(active);
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
		stage.addActor(actionsTable = buildActions(this));
		Gdx.input.setInputProcessor(stage);
	}

	private void update(final float delta) {
		camera.position.add(
				((controlSystem.isPressed(Keys.LEFT) ? -1 : 0) + (controlSystem.isPressed(Keys.RIGHT) ? 1 : 0))
						* cameraSpeed * zoomLevel,
				((controlSystem.isPressed(Keys.UP) ? 1 : 0) + (controlSystem.isPressed(Keys.DOWN) ? -1 : 0))
						* cameraSpeed * zoomLevel,
				0);
	}

}
