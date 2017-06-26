package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.Action;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.component.Timer;
import com.pignic.spacegrinder.factory.complex.ProjectileFactory;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Weapon;
import com.pignic.spacegrinder.screen.AbstractScreen;

public class WeaponFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		final Weapon config = (Weapon) part;
		final float scl = 1f / SpaceGrinder.WORLD_SCALE * 15f;
		entity.add(new Position(new Vector2(), new Vector2(6 * scl, 4 * scl)));
		entity.add(getPhysicalComponent(world, entity, config, position, angle));
		entity.add(new Renderable(part.textureRegion, config.textureScale));
		entity.add(new Timer(config.reloadTime));
		entity.add(new Controllable(new Action() {
			@Override
			public Object call() throws Exception {
				final Physical physical = entity.getComponent(Physical.class);
				final Timer timer = entity.getComponent(Timer.class);
				if (timer.done()) {
					((AbstractScreen) SpaceGrinder.game.getScreen()).getEngine()
							.addEntity(ProjectileFactory.buildProjectile(world, physical.getBody(),
									config.projectileImpulse, config.range, config.damage, config.projectileSize));
					timer.reset();
				}
				return null;
			}
		}, 1, new int[0]));
		return entity;
	}

}
