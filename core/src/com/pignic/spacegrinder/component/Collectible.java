package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;
import com.pignic.spacegrinder.pojo.Material;

public class Collectible implements Component {

	private final Material content;

	public Collectible(final Material content) {
		this.content = content;
	}
}
