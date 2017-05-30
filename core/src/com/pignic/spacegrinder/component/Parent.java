package com.pignic.spacegrinder.component;

import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class Parent implements Component {

	private List<Entity> childs;

	public Parent(final Entity... entities) {
		childs = Arrays.asList(entities);
	}

	public List<Entity> getChilds() {
		return childs;
	}

	public void setChilds(final List<Entity> childs) {
		this.childs = childs;
	}
}
