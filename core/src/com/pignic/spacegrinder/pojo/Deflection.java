package com.pignic.spacegrinder.pojo;

public class Deflection {

	public static enum TYPE {
		ELECTRIC, HEAT, KINETIC, LIGHT, PLASMA, SONIC
	}

	public float factor = 1;

	public TYPE type;

	public Deflection() {

	}

	public Deflection(final TYPE type) {
		this(type, 1);
	}

	public Deflection(final TYPE type, final float factor) {
		this.type = type;
		this.factor = factor;
	}
}
