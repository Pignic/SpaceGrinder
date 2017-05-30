package com.pignic.spacegrinder.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.ACTION;
import com.pignic.spacegrinder.component.Parent;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;

public class ShipFactory {

	private static Texture cockpitTexture;
	private static Texture structureTexture;
	private static Texture thrusterTexture;

	public static Entity buildShip(final World world) {
		loadTextures();
		final Entity cockpit = new Entity();
		cockpit.add(new Position());
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.add(new Vector2(0, 0));
		final PolygonShape shape = new PolygonShape();
		float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		shape.set(new Vector2[] { new Vector2(-2 * scl, -2 * scl), new Vector2(2 * scl, -2 * scl),
				new Vector2(4 * scl, -1 * scl), new Vector2(4 * scl, 1 * scl), new Vector2(2 * scl, 2 * scl),
				new Vector2(-2 * scl, 2 * scl) });
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 2;
		fixtureDef.shape = shape;
		final Physical physical = new Physical(world, bodyDef, fixtureDef);
		cockpit.add(physical);
		scl = 1f / SpaceGrinder.WORLD_SCALE * 10f;
		final Entity shipPartA = buildThruster(world, new Vector2(-2.5f * scl, 1.0f * scl), 0, Input.Keys.Z);
		final Entity shipPartB = buildThruster(world, new Vector2(-2.5f * scl, -1.0f * scl), 0, Input.Keys.Z);
		final Entity shipPartD = buildThruster(world, new Vector2(7.8f * scl, 0f * scl), (float) Math.PI, Input.Keys.S);
		final Entity shipPartC = buildThruster(world, new Vector2(3.5f * scl, -3.7f * scl), (float) Math.PI / 2,
				Input.Keys.Q);
		final Entity shipPartE = buildThruster(world, new Vector2(3.5f * scl, 3.7f * scl), (float) (3 * Math.PI / 2),
				Input.Keys.D);
		final Entity shipPartF = buildThruster(world, new Vector2(1.0f * scl, -3.5f * scl), (float) Math.PI / 2,
				Input.Keys.A);
		final Entity shipPartG = buildThruster(world, new Vector2(1.0f * scl, 3.5f * scl), (float) (3 * Math.PI / 2),
				Input.Keys.E);
		final Entity structureA = buildStructure(world, physical, shipPartA.getComponent(Physical.class));
		final Entity structureB = buildStructure(world, physical, shipPartB.getComponent(Physical.class));
		final Entity structureC = buildStructure(world, physical, shipPartC.getComponent(Physical.class));
		final Entity structureD = buildStructure(world, physical, shipPartD.getComponent(Physical.class));
		final Entity structureE = buildStructure(world, physical, shipPartE.getComponent(Physical.class));
		final Entity structureF = buildStructure(world, physical, shipPartF.getComponent(Physical.class));
		final Entity structureG = buildStructure(world, physical, shipPartG.getComponent(Physical.class));
		cockpit.add(new Parent(shipPartA, shipPartB, shipPartC, shipPartD, shipPartE, shipPartF, shipPartG, structureA,
				structureB, structureC, structureD, structureE, structureF, structureG));
		cockpit.add(new Renderable(cockpitTexture));
		return cockpit;
	}

	public static Entity buildStructure(final World world, final Physical partA, final Physical partB) {
		final Entity structure = new Entity();
		structure.add(new Position());
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		final PolygonShape shape = new PolygonShape();
		final Vector2 structureSize = new Vector2(
				partA.getBody().getWorldCenter().dst(partB.getBody().getWorldCenter()), 10 / SpaceGrinder.WORLD_SCALE);
		shape.setAsBox(structureSize.x / 2f, structureSize.y / 2f);
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
		structure.add(new Renderable(structureTexture, -1));
		return structure;
	}

	public static Entity buildThruster(final World world, final Vector2 position, final float angle,
			final int keycode) {
		final Entity shipPart = new Entity();
		shipPart.add(new Position());
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.add(position);
		bodyDef.angle = angle;
		final PolygonShape shape = new PolygonShape();
		final float scl = 1 / SpaceGrinder.WORLD_SCALE * 5;
		shape.set(new Vector2[] { new Vector2(-2 * scl, -2 * scl), new Vector2(2 * scl, -2 * scl),
				new Vector2(2 * scl, 2 * scl), new Vector2(-2 * scl, 2 * scl), new Vector2(-4 * scl, 1 * scl),
				new Vector2(-4 * scl, -1 * scl) });
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		final Physical physical = new Physical(world, bodyDef, fixtureDef);
		shipPart.add(physical);
		shipPart.add(new Controllable(keycode, ACTION.THRUST, 1000));
		shipPart.add(new Renderable(thrusterTexture, 1));
		return shipPart;
	}

	private static void loadTextures() {
		if (cockpitTexture == null) {
			cockpitTexture = new Texture("cockpit.png");
			thrusterTexture = new Texture("thruster.png");
			structureTexture = new Texture("structure.png");
		}
	}
}
