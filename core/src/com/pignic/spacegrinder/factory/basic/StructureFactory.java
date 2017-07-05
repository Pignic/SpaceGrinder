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
import com.pignic.spacegrinder.component.Id;
import com.pignic.spacegrinder.component.Link;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Structure;
import com.pignic.spacegrinder.service.IDService;

public class StructureFactory {

	/**
	 * This method give a physical body to an existing link
	 *
	 * @param world The box2d simulation
	 * @param structure the existing structure to build
	 * @param part The part configuration
	 * @param partA One part of the link
	 * @param partB The other part of the link
	 * @return The built structure
	 */
	public static Entity build(final World world, final Entity structure, final ShipPart part, final Physical partA,
			final Physical partB) {
		final Structure config = (Structure) part;
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
		buildJoints(world, physical, partA, partB);
		return structure;
	}

	public static Entity build(final World world, final ShipPart part, final Entity partA, final Entity partB) {
		final Structure config = (Structure) part;
		final Entity structure = new Entity();
		structure.add(new Id(IDService.getId()));
		structure.add(new Link(partA, partB));
		structure.add(new Renderable(config.textureRegion, config.textureScale));
		structure.add(new Durability(part.hitpoints, part.deflection));
		return structure;
	}

	public static void buildJoints(final World world, final Physical physical, final Physical partA,
			final Physical partB) {
		final float length = partA.getBody().getWorldCenter().dst(partB.getBody().getWorldCenter());
		final float angle = physical.getBody().getAngle();
		final WeldJointDef jointDef = new WeldJointDef();
		jointDef.collideConnected = false;
		jointDef.bodyA = physical.getBody();
		jointDef.localAnchorA.set(-length / 2f, 0);
		jointDef.localAnchorB.set(new Vector2(0, 0));
		jointDef.bodyB = partA.getBody();
		jointDef.referenceAngle = partA.getBody().getAngle() - angle;
		world.createJoint(jointDef);
		jointDef.localAnchorA.set(length / 2f, 0);
		jointDef.bodyB = partB.getBody();
		jointDef.referenceAngle = partB.getBody().getAngle() - angle;
		world.createJoint(jointDef);
	}
}
