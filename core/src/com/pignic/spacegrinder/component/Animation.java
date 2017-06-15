package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;

@SuppressWarnings("rawtypes")
public class Animation implements Component {

	public static enum STATE {
		DEFAULT(0);

		private int code;

		STATE(final int code) {
			this.code = code;
		}
	}

	private final IntMap<com.badlogic.gdx.graphics.g2d.Animation> animations;
	private final boolean isLooping = false;
	private STATE state = STATE.DEFAULT;
	private float time = 0.0f;

	public Animation(final IntMap<com.badlogic.gdx.graphics.g2d.Animation> animations) {
		this.animations = animations;
	}

	public com.badlogic.gdx.graphics.g2d.Animation getAnimation() {
		return getAnimation(state);
	}

	public com.badlogic.gdx.graphics.g2d.Animation getAnimation(final STATE state) {
		return getAnimations().get(state.code);
	}

	public IntMap<com.badlogic.gdx.graphics.g2d.Animation> getAnimations() {
		return animations;
	}

	public TextureRegion getKeyFrame() {
		return (TextureRegion) getAnimation().getKeyFrame(time, isLooping);
	}

	public STATE getState() {
		return state;
	}

	public void set(final STATE newState) {
		state = newState;
		time = 0.0f;
	}

	public float update(final float delta) {
		return time += delta;
	}
}
