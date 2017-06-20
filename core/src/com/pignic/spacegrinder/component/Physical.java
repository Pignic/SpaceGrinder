package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Physical implements Component {

	private final Body body;

	public Physical(final World world, final Entity entity, final BodyDef bodyDef, final FixtureDef fixtureDef) {
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(entity);
	}

	public Body getBody() {
		return body;
	}

}
