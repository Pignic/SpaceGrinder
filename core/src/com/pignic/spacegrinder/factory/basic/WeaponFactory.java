package com.pignic.spacegrinder.factory.basic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Controllable;
import com.pignic.spacegrinder.component.Timer;
import com.pignic.spacegrinder.factory.complex.ActionFactory;
import com.pignic.spacegrinder.pojo.ShipPart;
import com.pignic.spacegrinder.pojo.Weapon;

public class WeaponFactory extends ShipPartFactory {

	@Override
	public Entity buildPart(final World world, final ShipPart part, final Vector2 position, final float angle,
			final Entity entity) {
		final Weapon config = (Weapon) part;
		entity.add(new Timer(config.reloadTime));
		// entity.add(new Controllable(ActionFactory.getWeaponAction(entity, world, config), 1, new int[0]));
		entity.add(new Controllable().setShipPart(config).setAction(ActionFactory.getWeaponAction(entity, world)));
		return entity;
	}

}
