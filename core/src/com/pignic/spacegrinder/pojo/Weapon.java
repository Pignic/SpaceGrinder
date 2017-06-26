package com.pignic.spacegrinder.pojo;

public class Weapon extends ShipPart {

	public static class Damage {
		public float part;
		public Deflection.TYPE type;
	}

	public float damage;
	public Damage[] damageTypes;
	public float projectileImpulse;
	public float projectileSize;
	public float range;
	public float reloadTime;
	public boolean rotate;
	public boolean stream;

}
