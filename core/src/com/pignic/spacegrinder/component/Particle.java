package com.pignic.spacegrinder.component;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.pignic.spacegrinder.SpaceGrinder;

public class Particle implements Component {

	public static enum EFFECT {
		THRUSTER
	}

	private static final Map<EFFECT, ParticleEffect> effects = new HashMap<EFFECT, ParticleEffect>();

	private boolean active = false;

	private final ParticleEffect effect = new ParticleEffect();

	private final ParticleEmitter emitter;

	private Vector2 offset = new Vector2();

	private float rotation = 0;

	private final int zIndex = 0;

	public Particle(final EFFECT effect) {
		if (effects.size() <= 0) {
			effects.put(EFFECT.THRUSTER, new ParticleEffect());
			effects.get(EFFECT.THRUSTER).load(Gdx.files.internal("thruster-particle.p"), Gdx.files.internal(""));
			effects.get(EFFECT.THRUSTER).scaleEffect(1 / SpaceGrinder.WORLD_SCALE);
		}
		emitter = new ParticleEmitter(effects.get(effect).getEmitters().first());
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

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
		if (this.active) {
			emitter.start();
			emitter.setContinuous(true);
		} else {
			emitter.duration = 0;
			emitter.setContinuous(false);
		}
	}

	public void setOffset(final Vector2 offset) {
		this.offset = offset;
	}

	public void setRotation(final float rotation) {
		this.rotation = rotation;
	}

	public boolean toggle() {
		setActive(!active);
		return active;
	}
}
