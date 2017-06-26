package com.pignic.spacegrinder.factory.complex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.basic.ShipPartFactory;
import com.pignic.spacegrinder.factory.basic.StructureFactory;
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
				config = new ArrayList<ShipPart>(list.size());
				for (final JsonValue JsonValue : list) {
					final ShipPart shipPart = json.readValue(clazz, JsonValue);
					shipPart.textureRegion = new TextureRegion(
							new Texture(Constants.SHIP_PART_TEXTURE_PATH + shipPart.texture + ".png"));
					config.add(shipPart);
				}
			}
			this.typeName = typeName;
		}
	}

	public static List<Entity> buildShip(final World world) {
		final Entity cockpit = ShipPartFactory.build(world, PART_TYPE.COCKPIT.config.get(0), new Vector2(), 0);
		final Physical physical = cockpit.getComponent(Physical.class);
		final float scl = 1f / SpaceGrinder.WORLD_SCALE * 10f;
		final float thrust = ((Thruster) PART_TYPE.THRUSTER.config.get(0)).maxThrust;
		final Entity shipPartA = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(-6.7f * scl, 1.5f * scl), 0);
		shipPartA.getComponent(Controllable.class).addBinding(Keys.Z, thrust);
		final Entity shipPartB = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(-6.7f * scl, -1.5f * scl), 0);
		shipPartB.getComponent(Controllable.class).addBinding(Keys.Z, thrust);
		final Entity shipPartC = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(0.5f * scl, -4.5f * scl), (float) Math.PI / 2);
		shipPartC.getComponent(Controllable.class).addBinding(Keys.Q, thrust).addBinding(Keys.A, thrust);
		final Entity shipPartD = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(6.1f * scl, 0f * scl), (float) Math.PI);
		shipPartD.getComponent(Controllable.class).addBinding(Keys.S, thrust);
		final Entity shipPartE = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(0.5f * scl, 4.5f * scl), (float) (3 * Math.PI / 2));
		shipPartE.getComponent(Controllable.class).addBinding(Keys.D, thrust).addBinding(Keys.E, thrust);
		final Entity shipPartF = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(-2.5f * scl, -4.5f * scl), (float) Math.PI / 2);
		shipPartF.getComponent(Controllable.class).addBinding(Keys.A, thrust).addBinding(Keys.D, thrust);
		final Entity shipPartG = ShipPartFactory.build(world, PART_TYPE.THRUSTER.config.get(0),
				new Vector2(-2.5f * scl, 4.5f * scl), (float) (3 * Math.PI / 2));
		shipPartG.getComponent(Controllable.class).addBinding(Keys.E, thrust).addBinding(Keys.Q, thrust);
		final Entity structureA = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartA.getComponent(Physical.class));
		final Entity structureB = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartB.getComponent(Physical.class));
		final Entity structureC = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartC.getComponent(Physical.class));
		final Entity structureD = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartD.getComponent(Physical.class));
		final Entity structureE = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartE.getComponent(Physical.class));
		final Entity structureF = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartF.getComponent(Physical.class));
		final Entity structureG = StructureFactory.build(world, PART_TYPE.STRUCTURE.config.get(0), physical,
				shipPartG.getComponent(Physical.class));
		return Arrays.asList(new Entity[] { cockpit, shipPartA, shipPartB, shipPartC, shipPartD, shipPartE, shipPartF,
				shipPartG, structureA, structureB, structureC, structureD, structureE, structureF, structureG });
	}
}
