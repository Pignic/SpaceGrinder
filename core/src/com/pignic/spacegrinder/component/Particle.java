package com.pignic.spacegrinder.component;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.pignic.spacegrinder.Constants;
import com.pignic.spacegrinder.SpaceGrinder;

public class Particle implements SerializableComponent {

	public static enum EFFECT {
		IMPACT, SHIELD, THRUSTER
	}

	private static final Map<EFFECT, ParticleEffect> effects = new HashMap<EFFECT, ParticleEffect>();

	private boolean active = false;

	private float angleHigh;

	private float angleLowMax;

	private float angleLowMin;

	private final ParticleEffect effect = new ParticleEffect();

	private EFFECT effectType;

	private ParticleEmitter emitter;

	private boolean loop = true;

	private Vector2 offset = new Vector2();

	private boolean rotating = true;

	private float rotation = 0;

	private float scale = 1;

	public Particle() {

	}

	public Particle(final EFFECT effect) {
		setEffect(effect);
	}

	@Override
	public void deserialize(final Json json, final JsonValue jsonData) {
		setEffect(EFFECT.valueOf(jsonData.getString("effect")));
		active = jsonData.getBoolean("active");
		loop = jsonData.getBoolean("loop");
		offset.set(jsonData.getFloat("offsetx"), jsonData.getFloat("offsety"));
		rotating = jsonData.getBoolean("rotating");
		rotation = jsonData.getFloat("rotation");
		scale = jsonData.getFloat("scale");
	}

	public float getAngleHigh() {
		return angleHigh;
	}

	public float getAngleLowMax() {
		return angleLowMax;
	}

	public float getAngleLowMin() {
		return angleLowMin;
	}

	public ParticleEffect getEffect() {
		return effect;
	}

	public ParticleEmitter getEmitter() {
		return emitter;
	}

	public Vector2 getOffset() {
		return offset;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isLoop() {
		return loop;
	}

	public boolean isRotating() {
		return rotating;
	}

	@Override
	public void serialize(final Json json) {
		json.writeValue("active", active);
		json.writeValue("loop", loop);
		json.writeValue("offsetx", offset.x);
		json.writeValue("offsety", offset.y);
		json.writeValue("rotating", rotating);
		json.writeValue("rotation", rotation);
		json.writeValue("scale", scale);
		json.writeValue("effect", effectType);
	}

	public Particle setActive(final boolean active) {
		this.active = active;
		if (this.active) {
			emitter.start();
			emitter.setContinuous(loop);
		} else {
			emitter.duration = 0;
			emitter.setContinuous(false);
		}
		return this;
	}

	private void setEffect(final EFFECT effect) {
		effectType = effect;
		if (effects.size() <= 0) {
			effects.put(EFFECT.THRUSTER, new ParticleEffect());
			effects.get(EFFECT.THRUSTER).load(Gdx.files.internal(Constants.PARTICLE_PATH + "thruster-particle.p"),
					Gdx.files.internal(""));
			effects.get(EFFECT.THRUSTER).scaleEffect(1 / SpaceGrinder.WORLD_SCALE);

			effects.put(EFFECT.IMPACT, new ParticleEffect());
			effects.get(EFFECT.IMPACT).load(Gdx.files.internal(Constants.PARTICLE_PATH + "impact-particle.p"),
					Gdx.files.internal(""));
			effects.get(EFFECT.IMPACT).scaleEffect(1 / SpaceGrinder.WORLD_SCALE);

			effects.put(EFFECT.SHIELD, new ParticleEffect());
			effects.get(EFFECT.SHIELD).load(Gdx.files.internal(Constants.PARTICLE_PATH + "shield-particle.p"),
					Gdx.files.internal(""));
			effects.get(EFFECT.SHIELD).scaleEffect(1 / SpaceGrinder.WORLD_SCALE);
		}
		emitter = new ParticleEmitter(effects.get(effect).getEmitters().first());
		angleLowMin = emitter.getAngle().getLowMin();
		angleLowMax = emitter.getAngle().getLowMax();
		angleHigh = emitter.getAngle().getHighMin();
	}

	public Particle setLoop(final boolean loop) {
		this.loop = loop;
		return this;
	}

	public void setOffset(final Vector2 offset) {
		this.offset = offset;
	}

	public Particle setRotating(final boolean rotating) {
		this.rotating = rotating;
		return this;
	}

	public void setRotation(final float rotation) {
		this.rotation = rotation;
	}

	public void setScale(final float scale) {
		this.scale = scale;
	}

	public boolean toggle() {
		setActive(!active);
		return active;
	}
}
