package com.pignic.spacegrinder.factory.complex;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Collectible;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.pojo.Material;
import com.pignic.spacegrinder.pojo.Resource;

public class CollectibleFactory {

	public static Entity buildCollectible(final World world, final Material content, final Vector2 position) {
		return buildCollectible(world, content, position, new Vector2());
	}

	public static Entity buildCollectible(final World world, final Material content, final Vector2 position,
			final Vector2 speed) {
		final Resource resource = Resource.Resources.get(content.type);
		final Entity collectible = new Entity();
		collectible.add(new Position());
		collectible.add(new Renderable(resource.getTexture()));
		collectible.add(new Collectible(content));
		final BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.linearVelocity.set(speed);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = resource.density;
		fixtureDef.shape = new CircleShape();
		fixtureDef.shape.setRadius(0.2f);
		collectible.add(new Physical(world, collectible, bodyDef, fixtureDef));
		return collectible;
	}
}
