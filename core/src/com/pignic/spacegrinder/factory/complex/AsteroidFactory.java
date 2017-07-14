package com.pignic.spacegrinder.factory.complex;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.pignic.spacegrinder.AssetManager;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.component.Physical;
import com.pignic.spacegrinder.component.Position;
import com.pignic.spacegrinder.component.Renderable;
import com.pignic.spacegrinder.component.StellarObject;
import com.pignic.spacegrinder.pojo.Material;
import com.pignic.spacegrinder.pojo.Resource;
import com.pignic.spacegrinder.pojo.Resource.Resources;
import com.pignic.spacegrinder.system.PhysicSystem;

public class AsteroidFactory {

	public static Entity createAsteroid(final World world, final Vector2 position, final float radius,
			final int complexity, final float softness) {
		final Entity asteroid = new Entity();
		asteroid.add(new Position(new Vector2(), new Vector2()));
		final BodyDef bodydef = new BodyDef();
		bodydef.position.set(position);
		bodydef.type = BodyType.DynamicBody;
		final FixtureDef fixturedef = new FixtureDef();
		fixturedef.density = 100;
		final float[] floatVertices = new float[complexity * 2];
		final Vector2[] vertices = new Vector2[complexity];
		for (int i = 0; i < complexity; ++i) {
			final Vector2 vector = new Vector2((float) (Math.random() * (1f - softness) * radius + radius * softness),
					0f);
			vector.rotateRad((float) (Math.PI * 2f * (i / (float) complexity)));
			floatVertices[i * 2] = vector.x;
			floatVertices[i * 2 + 1] = vector.y;
			vertices[i] = new Vector2(vector.x, vector.y);
		}
		final Body body = world.createBody(bodydef);
		body.setUserData(asteroid);
		final ShortArray array = createFixtures(body, floatVertices, fixturedef);
		asteroid.add(new Renderable(AssetManager.getInstance().getTexture(Constants.TEXTURE_PATH + "rock.png"), 1f)
				.setTiled(true).setPolygon(floatVertices, array.items));
		asteroid.add(new Physical(asteroid, body));
		asteroid.add(new StellarObject(Arrays.asList(vertices), getMaterials(getPolygonSurface(vertices))));
		return asteroid;
	}

	public static Entity createAsteroid(final World world, final Vector2 position, final List<Vector2> outline) {
		final Entity asteroid = new Entity();
		final BodyDef bodydef = new BodyDef();
		bodydef.position.set(position);
		bodydef.type = BodyType.DynamicBody;
		final FixtureDef fixturedef = new FixtureDef();
		fixturedef.density = 100;
		final float[] floatVertices = new float[outline.size() * 2];
		final Body body = world.createBody(bodydef);
		body.setUserData(asteroid);
		final ShortArray array = createFixtures(body, floatVertices, fixturedef);
		asteroid.add(new Position(new Vector2(), new Vector2()));
		asteroid.add(new Renderable(AssetManager.getInstance().getTexture(Constants.TEXTURE_PATH + "rock.png"), 1f)
				.setTiled(true).setPolygon(floatVertices, array.items));
		asteroid.add(new Physical(asteroid, body));
		asteroid.add(new StellarObject(outline));
		return asteroid;
	}

	private static ShortArray createFixtures(final Body body, final float[] vertices, final FixtureDef fixtureDef) {
		final EarClippingTriangulator triangulator = new EarClippingTriangulator();
		final PolygonShape shape = new PolygonShape();
		final ShortArray array = triangulator.computeTriangles(vertices);
		fixtureDef.shape = shape;
		for (int i = 0; i < array.size; i += 3) {
			final Vector2 a = new Vector2(vertices[array.get(i) * 2], vertices[array.get(i) * 2 + 1]);
			final Vector2 b = new Vector2(vertices[array.get(i + 1) * 2], vertices[array.get(i + 1) * 2 + 1]);
			final Vector2 c = new Vector2(vertices[array.get(i + 2) * 2], vertices[array.get(i + 2) * 2 + 1]);
			if (a.dst(b) > 0.1 && b.dst(c) > 0.1 && c.dst(a) > 0.1) {
				shape.set(new Vector2[] { a, b, c });
				body.createFixture(fixtureDef);
			}
		}
		return array;
	}

	private static List<Area> getAreas(final Area area) {
		final List<Area> areas = new ArrayList<Area>();
		if (area.isSingular()) {
			areas.add(area);
			return areas;
		}
		final PathIterator iter = area.getPathIterator(null);
		final Path2D.Float poly = new Path2D.Float();
		while (!iter.isDone()) {
			final float point[] = new float[2];
			final int type = iter.currentSegment(point);
			if (type == PathIterator.SEG_MOVETO) {
				poly.moveTo(point[0], point[1]);
			} else if (type == PathIterator.SEG_CLOSE) {
				if (poly.getBounds().getHeight() * poly.getBounds().getWidth() > 1) {
					areas.add(new Area(poly));
				}
				poly.reset();
			} else {
				poly.lineTo(point[0], point[1]);
			}
			iter.next();
		}
		return areas;
	}

	private static List<Material> getMaterials(final float surface) {
		final List<Material> materials = new ArrayList<Material>();
		for (final Resource resource : Resources.getResources()) {
			materials.add(new Material(resource.name, surface * resource.rarity * 100f));
		}
		return materials;
	}

	private static float getPolygonSurface(final Vector2[] outline) {
		final float[] points = new float[outline.length * 2];
		for (int i = 0; i < outline.length; ++i) {
			points[i * 2] = outline[i].x;
			points[i * 2 + 1] = outline[i].y;
		}
		return new com.badlogic.gdx.math.Polygon(points).area();
	}

	public static Entity hitAsteroid(final World world, final Engine engine, final Entity asteroid,
			final Vector2 impact, final float radius) {
		final StellarObject stellarObject = asteroid.getComponent(StellarObject.class);
		final Body body = asteroid.getComponent(Physical.class).getBody();
		final int[] stellarXs = new int[stellarObject.getOutline().size()];
		final int[] stellarYs = new int[stellarObject.getOutline().size()];
		final int[] impactXs = new int[12];
		final int[] impactYs = new int[12];
		for (int i = 0; i < stellarObject.getOutline().size(); ++i) {
			stellarXs[i] = (int) (stellarObject.getOutline().get(i).x * 100f);
			stellarYs[i] = (int) (stellarObject.getOutline().get(i).y * 100f);
		}
		final Vector2 impactCenter = new Vector2(impact).sub(body.getWorldCenter());
		final Vector2 tmpVector = new Vector2();
		for (int i = 0; i < 12; ++i) {
			tmpVector.set(radius, 0).rotateRad((float) (Math.PI * i / 6f));
			impactXs[i] = (int) ((tmpVector.x + impactCenter.x) * 100f);
			impactYs[i] = (int) ((tmpVector.y + impactCenter.y) * 100f);
		}
		final Polygon stellarPolygon = new Polygon(stellarXs, stellarYs, stellarObject.getOutline().size());
		final Polygon impactPolygon = new Polygon(impactXs, impactYs, 12);
		final Area stellarArea = new Area(stellarPolygon);
		final Area impactArea = new Area(impactPolygon);
		stellarArea.subtract(impactArea);
		final List<Area> areas = getAreas(stellarArea);
		boolean isMain = true;
		for (final Area ar : areas) {
			final PathIterator it = ar.getPathIterator(null);
			final List<Vector2> outline = new ArrayList<Vector2>();
			while (!it.isDone()) {
				final float vals[] = new float[2];
				it.currentSegment(vals);
				outline.add(new Vector2(vals[0] / 100f, vals[1] / 100f));
				it.next();
			}
			final float[] vertices = new float[outline.size() * 2];
			for (int i = 0; i < outline.size(); ++i) {
				vertices[i * 2] = outline.get(i).x;
				vertices[i * 2 + 1] = outline.get(i).y;
			}
			if (isMain) {
				stellarObject.getOutline().clear();
				stellarObject.getOutline().addAll(prune(outline, 0.1f));
				final PhysicSystem physicSystem = engine.getSystem(PhysicSystem.class);
				for (final Fixture fixture : body.getFixtureList()) {
					physicSystem.destroyFixture(fixture);
				}
				final FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.density = 100;
				final ShortArray array = createFixtures(body, vertices, fixtureDef);
				asteroid.getComponent(Renderable.class).setPolygon(vertices, array.items);
				isMain = false;
			} else {
				final Rectangle bounds = ar.getBounds();
				final Vector2 center = new Vector2((bounds.x + bounds.width / 2f) / 100f,
						(bounds.y + bounds.height / 2f) / 100f);
				engine.addEntity(createAsteroid(world, center, outline));
			}
		}
		for (int i = 0; i < 5; ++i) {
			engine.addEntity(CollectibleFactory.buildCollectible(world,
					stellarObject.pickResource((float) (Math.PI * radius * radius)), impact));
		}
		return asteroid;
	}

	private static List<Vector2> prune(final List<Vector2> input, final float scale) {
		final List<Vector2> output = new ArrayList<Vector2>(input);
		for (int i = 1; i < output.size(); ++i) {
			if (output.get(i - 1).dst(output.get(i)) < scale) {
				final Vector2 avg = new Vector2(output.get(i)).sub(output.get(i - 1)).scl(0.5f).add(output.get(i - 1));
				output.get(i - 1).set(avg);
				output.remove(i);
			}
		}
		return output;
	}
}
