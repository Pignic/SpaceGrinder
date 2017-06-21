package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pignic.spacegrinder.screen.AbstractScreen;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;

public class LightSource extends Configurable implements Component {

	public static enum TYPE {
		Cone, Point
	}

	private float angle;

	private float arc;

	private Body body;

	private boolean built = false;

	private final Color color;

	private PositionalLight light;

	private final float maxArc;

	private final float maxRange;

	private Vector2 position;

	private final float range;

	private final TYPE type;

	public LightSource(final TYPE type, final Color color, final Body body, final float range, final float arc,
			final float maxRange, final float maxArc) {
		this(type, color, range, maxRange, maxArc);
		this.body = body;
		this.arc = arc;
	}

	private LightSource(final TYPE type, final Color color, final float range, final float maxRange,
			final float maxArc) {
		this.type = type;
		this.color = color;
		this.range = range;
		this.maxArc = maxArc;
		this.maxRange = maxRange;
	}

	public LightSource(final TYPE type, final Color color, final float range, final Vector2 position, final float angle,
			final float arc, final float maxRange, final float maxArc) {
		this(type, color, range, maxRange, maxArc);
		this.angle = angle;
		this.position = position;
		this.arc = arc;
	}

	public PositionalLight build(final RayHandler rayHandler) {
		if (built) {
			return light;
		}
		if (body != null) {
			switch (type) {
			case Cone:
				light = new ConeLight(rayHandler, (int) Math.ceil((float) (arc / (Math.PI / 48d))), color, range,
						body.getWorldCenter().x, body.getWorldCenter().y, (float) Math.toDegrees(body.getAngle()),
						(float) Math.toDegrees(arc));
				break;
			case Point:
				light = new PointLight(rayHandler, 48, color, range, body.getWorldCenter().x, body.getWorldCenter().y);
				break;
			default:
				break;
			}
			light.attachToBody(body);
		} else {
			switch (type) {
			case Cone:
				light = new ConeLight(rayHandler, (int) Math.ceil((float) (arc / (Math.PI / 48d))), color, range,
						position.x, position.y, (float) Math.toDegrees(angle), (float) Math.toDegrees(arc));
				break;
			case Point:
				light = new PointLight(rayHandler, 48, color, range, position.x, position.y);
				break;
			default:
				break;
			}
		}
		built = true;
		return light;
	}

	@Override
	public Table getConfiguration(final Table table) {
		final Label title = new Label("Light", AbstractScreen.style.skin, "light");
		table.add(title).row();
		final Label powerLabel = new Label("Power", AbstractScreen.style.skin, "light");
		final Slider powerSlider = new Slider(0f, maxRange, maxRange / 100f, false, AbstractScreen.style.skin);
		powerSlider.setValue(light.getDistance());
		powerSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				light.setDistance(powerSlider.getValue());
				return true;
			}
		});
		table.add(powerLabel);
		table.add(powerSlider).row();

		final Label redLabel = new Label("Red", AbstractScreen.style.skin, "light");
		final Slider redSlider = new Slider(0f, 1, 0.01f, false, AbstractScreen.style.skin);
		redSlider.setValue(light.getColor().r);
		redSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				light.getColor().r = redSlider.getValue();
				light.setColor(light.getColor());
				return true;
			}
		});
		table.add(redLabel);
		table.add(redSlider).row();

		final Label greenLabel = new Label("Green", AbstractScreen.style.skin, "light");
		final Slider greenSlider = new Slider(0f, 1, 0.01f, false, AbstractScreen.style.skin);
		greenSlider.setValue(light.getColor().g);
		greenSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				light.getColor().g = greenSlider.getValue();
				light.setColor(light.getColor());
				return true;
			}
		});
		table.add(greenLabel);
		table.add(greenSlider).row();

		final Label blueLabel = new Label("Blue", AbstractScreen.style.skin, "light");
		final Slider blueSlider = new Slider(0f, 1, 0.01f, false, AbstractScreen.style.skin);
		blueSlider.setValue(light.getColor().b);
		blueSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				light.getColor().b = blueSlider.getValue();
				light.setColor(light.getColor());
				return true;
			}
		});
		table.add(blueLabel);
		table.add(blueSlider).row();
		return table;
	}

	public PositionalLight getLight() {
		return light;
	}

	public boolean isBuilt() {
		return built;
	}

}
