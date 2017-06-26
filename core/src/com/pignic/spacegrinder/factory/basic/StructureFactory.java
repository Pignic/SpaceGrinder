package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Durability;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Structure;

public class StructureFactory {

	public static Entity build(final World world, final ShipPart part, final Physical partA, final Physical partB) {
		final Structure config = (Structure) part;
		final Entity structure = new Entity();
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.add(new Vector2(partA.getBody().getWorldCenter()).sub(partB.getBody().getWorldCenter())
				.scl(0.5f).add(partB.getBody().getWorldCenter()));
		final PolygonShape shape = new PolygonShape();
		final Vector2 structureSize = new Vector2(
				partA.getBody().getWorldCenter().dst(partB.getBody().getWorldCenter()),
				config.thickness / SpaceGrinder.WORLD_SCALE);
		shape.setAsBox(structureSize.x / 2f, structureSize.y / 2f);
		structure.add(new Position(new Vector2(), new Vector2(structureSize), 0, -1));
		bodyDef.angle = new Vector2(partB.getBody().getWorldCenter()).sub(partA.getBody().getWorldCenter()).angleRad();
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = config.density;
		final Physical physical = new Physical(world, structure, bodyDef, fixtureDef);
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
		structure.add(new Renderable(config.textureRegion, config.textureScale));
		structure.add(new Durability(part.hitpoints, part.deflection));
		return structure;
	}
}
