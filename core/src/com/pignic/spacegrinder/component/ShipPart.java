package com.pignic.spacegrinder.component;

import com.badlogic.ashley.core.Component;

public class ShipPart implements Component {

	private final long shipId;

	public ShipPart(final long shipId) {
		this.shipId = shipId;
	}

	public long getShipId() {
		return shipId;
	}
}
