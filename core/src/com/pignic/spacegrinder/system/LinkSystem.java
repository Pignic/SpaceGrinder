package com.pignic.spacegrinder.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.World;
import com.pignic.spacegrinder.component.Id;
import com.pignic.spacegrinder.component.Link;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.factory.basic.StructureFactory;
import com.pignic.spacegrinder.factory.complex.ShipFactory.PART_TYPE;

public class LinkSystem extends IteratingSystem {

	private static class Mapper {
		public static final ComponentMapper<Id> id = ComponentMapper.getFor(Id.class);
		public static final ComponentMapper<Link> link = ComponentMapper.getFor(Link.class);
		public static final ComponentMapper<Physical> physical = ComponentMapper.getFor(Physical.class);
	}

	private final World world;

	public LinkSystem(final World world) {
		super(Family.all(Link.class).get());
		this.world = world;
	}

	private Entity getEntityById(final long id) {
		final ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Id.class).get());
		for (final Entity entity : entities) {
			if (id == Mapper.id.get(entity).getId()) {
				return entity;
			}
		}
		return null;
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final Link link = Mapper.link.get(entity);
		if (link.isDirty()) {
			link.setEntityA(getEntityById(link.getEntityIdA()));
			link.setEntityB(getEntityById(link.getEntityIdB()));
			link.setDirty(false);
		}
		if (!link.isBuilt()) {
			final Physical linkPhysical = entity.getComponent(Physical.class);
			if (linkPhysical != null && linkPhysical.getBody() != null) {
				StructureFactory.buildJoints(world, linkPhysical, link.getEntityA().getComponent(Physical.class),
						link.getEntityB().getComponent(Physical.class));
			} else {
				StructureFactory.build(world, entity, PART_TYPE.STRUCTURE.config.get(0),
						Mapper.physical.get(link.getEntityA()), Mapper.physical.get(link.getEntityB()));
			}
			link.setBuilt(true);
		}
	}
}
