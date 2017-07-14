package com.pignic.spacegrinder.pojo;

public class Material {

	public float quantity;

	public String type;

	public Material() {

	}

	public Material(final String type, final float quantity) {
		this.type = type;
		this.quantity = quantity;
	}
}
