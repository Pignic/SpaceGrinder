package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class ShipPart implements Component {

	private final int shipId;

	public ShipPart(final int shipId) {
		this.shipId = shipId;
	}

	public int getShipId() {
		return shipId;
	}
}
