package com.pignic.spacegrinder.component;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Physical implements SerializableComponent {

	private Body body;

	private BodyDef bodyDef;

	private FixtureDef fixtureDef;

	public Physical() {

	}

	public Physical(final World world, final Entity entity, final BodyDef bodyDef, final FixtureDef fixtureDef) {
		this.bodyDef = bodyDef;
		this.fixtureDef = fixtureDef;
		build(world, entity);
	}

	public Body build(final World world, final Entity entity) {
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(entity);
		return body;
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		bodyDef = new BodyDef();
		bodyDef.angle = jsonData.getFloat("angle");
		bodyDef.position.x = jsonData.getFloat("x");
		bodyDef.position.y = jsonData.getFloat("y");
		fixtureDef = new FixtureDef();
		fixtureDef.density = jsonData.getFloat("density");
		final PolygonShape shape = new PolygonShape();
		final List<Vector2> vertices = new ArrayList<Vector2>();
		final JsonValue jsonVertices = jsonData.get("shape");
		for (int i = 0; i < jsonVertices.size; ++i) {
			final JsonValue jsonVertice = jsonVertices.get(i);
			vertices.add(new Vector2(jsonVertice.getFloat("x"), jsonVertice.getFloat("y")));
		}
		shape.set(vertices.toArray(new Vector2[0]));
		fixtureDef.shape = shape;
	}

	public void destroyBody() {
		if (body != null) {
			body.getWorld().destroyBody(body);
			body = null;
		}
	}

	public Body getBody() {
		return body;
	}

	@Override
	public void serialize(final Json json) {
		json.writeValue("angle", body.getAngle());
		json.writeValue("x", body.getPosition().x);
		json.writeValue("y", body.getPosition().y);
		final Fixture fixture = body.getFixtureList().get(0);
		json.writeValue("density", fixture.getDensity());
		json.writeArrayStart("shape");
		final Vector2 vertex = new Vector2();
		final PolygonShape shape = (PolygonShape) fixture.getShape();
		for (int i = 0; i < shape.getVertexCount(); ++i) {
			shape.getVertex(i, vertex);
			json.writeObjectStart();
			json.writeValue("x", vertex.x);
			json.writeValue("y", vertex.y);
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
	}

}
