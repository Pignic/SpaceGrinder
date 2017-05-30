package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.pignic.spacegrinder.component.Parent;

public class HierarchySystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	ComponentMapper<Parent> pm = ComponentMapper.getFor(Parent.class);

	@Override
	public void addedToEngine(final Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Parent.class).get());
		for (final Entity entity : entities) {
			for (final Entity child : pm.get(entity).getChilds()) {
				if (!entities.contains(child, true)) {
					engine.addEntity(child);
				}
			}
		}
		super.addedToEngine(engine);
	}

}
