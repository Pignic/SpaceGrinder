package com.pignic.spacegrinder.factory.complex;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Controllable.Action;
import com.pignic.spacegrinder.component.Particle;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Timer;
import com.pignic.spacegrinder.pojo.Weapon;
import com.pignic.spacegrinder.screen.AbstractScreen;

public class ActionFactory {

	public static Action getThrusterAction(final Entity entity) {
		return new Action(entity) {
			@Override
			public Object call() throws Exception {
				final Physical physical = entity.getComponent(Physical.class);
				final Particle particle = entity.getComponent(Particle.class);
				// physical.getBody().applyForceToCenter(
				// physical.getBody().getWorldVector(new Vector2(1, 0).scl(binding.getAmount())), true);
				physical.getBody().applyLinearImpulse(
						physical.getBody().getWorldVector(new Vector2(1, 0).scl(binding.getAmount() / 60f)),
						physical.getBody().getWorldCenter(), true);
				if (particle != null) {
					particle.setActive(true);
					particle.setScale(binding.getAmount() / entity.getComponent(Controllable.class).getMaxAmout() * 3);
				}
				return null;
			}
		};
	}

	public static Action getThrusterCancelAction(final Entity entity) {
		return new Action(entity) {
			@Override
			public Object call() throws Exception {
				entity.getComponent(Particle.class).setActive(false);
				return null;
			}
		};
	}

	public static Action getWeaponAction(final Entity entity, final World world) {
		return new Action(entity) {
			@Override
			public Object call() throws Exception {
				final Weapon config = (Weapon) shipPart;
				final Physical physical = entity.getComponent(Physical.class);
				final Timer timer = entity.getComponent(Timer.class);
				if (timer.done()) {
					((AbstractScreen) SpaceGrinder.game.getScreen()).getEngine()
							.addEntity(ProjectileFactory.buildProjectile(world, physical.getBody(),
									config.projectileImpulse, config.range, config.damage, config.projectileSize,
									config.damageTypes));
					timer.reset();
				}
				return null;
			}
		};
	}
}
