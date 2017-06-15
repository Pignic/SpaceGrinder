package com.pignic.spacegrinder.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.ACTION;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Particle.EFFECT;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.pojo.Armor;
import com.pignic.spacegrinder.pojo.Beacon;
import com.pignic.spacegrinder.pojo.Cockpit;
import com.pignic.spacegrinder.pojo.Collector;
import com.pignic.spacegrinder.pojo.Computer;
import com.pignic.spacegrinder.pojo.Connector;
import com.pignic.spacegrinder.pojo.Container;
import com.pignic.spacegrinder.pojo.Ejector;
import com.pignic.spacegrinder.pojo.Generator;
import com.pignic.spacegrinder.pojo.Gravity;
import com.pignic.spacegrinder.pojo.LandingGear;
import com.pignic.spacegrinder.pojo.Light;
import com.pignic.spacegrinder.pojo.Mass;
import com.pignic.spacegrinder.pojo.Sensor;
import com.pignic.spacegrinder.pojo.Shield;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Structure;
import com.pignic.spacegrinder.pojo.Thruster;
import com.pignic.spacegrinder.pojo.Weapon;

public class ShipFactory {

	public static enum PART_TYPE {
		//
		ARMOR("Armor", "armor", Armor.class),
		//
		BEACON("Beacon", "beacon", Beacon.class),
		//
		COCKPIT("Cockpit", "cockpit", Cockpit.class),
		//
		COLLECTOR("Collector", "collector", Collector.class),
		//
		COMPUTER("Computer", "computer", Computer.class),
		//
		CONNECTOR("Connector", "connector", Connector.class),
		//
		CONTAINER("Container", "container", Container.class),
		//
		EJECTOR("Ejector", "ejector", Ejector.class),
		//
		GENERATOR("Generator", "generator", Generator.class),
		//
		GRAVITY("Gravity", "gravity", Gravity.class),
		//
		LANDING_GEAR("Landing gear", "landing_gear", LandingGear.class),
		//
		LIGHT("Light", "light", Light.class),
		//
		MASS("Mass", "mass", Mass.class),
		//
		SENSOR("Sensor", "sensor", Sensor.class),
		//
		SHIELD("Shield", "shield", Shield.class),
		//
		STRUCTURE("Structure", "structure", Structure.class),
		//
		THRUSTER("Thruster", "thruster", Thruster.class),
		//
		WEAPON("Weapon", "weapon", Weapon.class);

		public Class<? extends ShipPart> clazz;
		public List<ShipPart> config;
		public String configFile;
		public String typeName;

		<T extends ShipPart> PART_TYPE(final String typeName, final String configFile, final Class<T> clazz) {
			this.configFile = Constants.DATA_PATH + configFile + ".json";
			this.clazz = clazz;
			final Json json = new Json();
			final ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(this.configFile));
			if (list != null) {
				textures.put(this.clazz, new ArrayList<TextureRegion>(list.size()));
				config = new ArrayList<ShipPart>(list.size());
				for (final JsonValue JsonValue : list) {
					final ShipPart shipPart = json.readValue(clazz, JsonValue);
					config.add(shipPart);
					textures.get(this.clazz)
							.add(new TextureRegion(new Texture(Constants.TEXTURE_PATH + shipPart.texture + ".png")));
				}
			}
			this.typeName = typeName;
		}
	}

	private static TextureRegion cockpitTexture;
	private static TextureRegion structureTexture;
	public static Map<Class<? extends ShipPart>, List<TextureRegion>> textures = new HashMap<Class<? extends ShipPart>, List<TextureRegion>>();

	public static List<Entity> buildShip(final World world) {
		final Cockpit config = (Cockpit) PART_TYPE.COCKPIT.config.get(0);
		loadTextures();
		final Entity cockpit = new Entity();
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.add(new Vector2(0, 0));
		final PolygonShape shape = new PolygonShape();
		float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		shape.set(config.getShape(1f / SpaceGrinder.WORLD_SCALE));
		cockpit.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl)));
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = config.density;
		fixtureDef.shape = shape;
		final Physical physical = new Physical(world, bodyDef, fixtureDef);
		cockpit.add(physical);
		scl = 1f / SpaceGrinder.WORLD_SCALE * 10f;
		final Entity shipPartA = buildThruster(world, new Vector2(-2.5f * scl, 1.0f * scl), 0, Input.Keys.Z);
		final Entity shipPartB = buildThruster(world, new Vector2(-2.5f * scl, -1.0f * scl), 0, Input.Keys.Z);
		final Entity shipPartD = buildThruster(world, new Vector2(7.8f * scl, 0f * scl), (float) Math.PI, Input.Keys.S);
		final Entity shipPartC = buildThruster(world, new Vector2(3.5f * scl, -3.7f * scl), (float) Math.PI / 2,
				Input.Keys.Q, Input.Keys.A);
		final Entity shipPartE = buildThruster(world, new Vector2(3.5f * scl, 3.7f * scl), (float) (3 * Math.PI / 2),
				Input.Keys.D, Input.Keys.E);
		final Entity shipPartF = buildThruster(world, new Vector2(1.0f * scl, -3.5f * scl), (float) Math.PI / 2,
				Input.Keys.A, Input.Keys.D);
		final Entity shipPartG = buildThruster(world, new Vector2(1.0f * scl, 3.5f * scl), (float) (3 * Math.PI / 2),
				Input.Keys.E, Input.Keys.Q);
		final Entity structureA = buildStructure(world, physical, shipPartA.getComponent(Physical.class));
		final Entity structureB = buildStructure(world, physical, shipPartB.getComponent(Physical.class));
		final Entity structureC = buildStructure(world, physical, shipPartC.getComponent(Physical.class));
		final Entity structureD = buildStructure(world, physical, shipPartD.getComponent(Physical.class));
		final Entity structureE = buildStructure(world, physical, shipPartE.getComponent(Physical.class));
		final Entity structureF = buildStructure(world, physical, shipPartF.getComponent(Physical.class));
		final Entity structureG = buildStructure(world, physical, shipPartG.getComponent(Physical.class));
		// cockpit.add(new Parent(shipPartA, shipPartB, shipPartC, shipPartD, shipPartE, shipPartF, shipPartG,
		// structureA,
		// structureB, structureC, structureD, structureE, structureF, structureG));
		cockpit.add(new Renderable(textures.get(PART_TYPE.COCKPIT.clazz).get(0), 0.75f));
		return Arrays.asList(new Entity[] { cockpit, shipPartA, shipPartB, shipPartC, shipPartD, shipPartE, shipPartF,
				shipPartG, structureA, structureB, structureC, structureD, structureE, structureF, structureG });
	}

	public static Entity buildStructure(final World world, final Physical partA, final Physical partB) {
		final Entity structure = new Entity();
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		final PolygonShape shape = new PolygonShape();
		final Vector2 structureSize = new Vector2(
				partA.getBody().getWorldCenter().dst(partB.getBody().getWorldCenter()), 10 / SpaceGrinder.WORLD_SCALE);
		shape.setAsBox(structureSize.x / 2f, structureSize.y / 2f);
		structure.add(new Position(new Vector2(), new Vector2(structureSize), 0, -1));
		bodyDef.angle = new Vector2(partB.getBody().getWorldCenter()).sub(partA.getBody().getWorldCenter()).angleRad();
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		final Physical physical = new Physical(world, bodyDef, fixtureDef);
		structure.add(physical);
		final WeldJointDef jointDef = new WeldJointDef();
		jointDef.collideConnected = false;
		jointDef.bodyA = physical.getBody();
		jointDef.localAnchorA.set(-structureSize.x / 2f, 0);
		jointDef.localAnchorB.set(new Vector2(0, 0));
		jointDef.bodyB = partA.getBody();
		jointDef.referenceAngle = partA.getBody().getAngle() - bodyDef.angle;
		world.createJoint(jointDef);
		jointDef.localAnchorA.set(structureSize.x / 2f, 0);
		jointDef.bodyB = partB.getBody();
		jointDef.referenceAngle = partB.getBody().getAngle() - bodyDef.angle;
		world.createJoint(jointDef);
		structure.add(new Renderable(structureTexture, 0.1f));
		return structure;
	}

	public static Entity buildThruster(final World world, final Vector2 position, final float angle,
			final int... keycodes) {
		final Thruster config = (Thruster) PART_TYPE.THRUSTER.config.get(0);
		final Entity shipPart = new Entity();
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.add(position);
		bodyDef.angle = angle;
		final PolygonShape shape = new PolygonShape();
		final float scl = 1 / SpaceGrinder.WORLD_SCALE * 5;
		shape.set(config.getShape(1f / SpaceGrinder.WORLD_SCALE));
		shipPart.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl), 0, 1));
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = config.density;
		final Physical physical = new Physical(world, bodyDef, fixtureDef);
		shipPart.add(physical);
		shipPart.add(new Controllable(ACTION.THRUST, config.maxThrust, keycodes));
		shipPart.add(new Renderable(textures.get(PART_TYPE.THRUSTER.clazz).get(0), 0.5f));
		final Particle particle = new Particle(EFFECT.THRUSTER);
		particle.setRotation((float) Math.PI);
		shipPart.add(particle);
		return shipPart;
	}

	private static void loadTextures() {
		if (cockpitTexture == null) {
			cockpitTexture = new TextureRegion(new Texture(Constants.TEXTURE_PATH + "cockpit.png"));
			structureTexture = new TextureRegion(new Texture(Constants.TEXTURE_PATH + "structure.png"));
		}
	}
}
