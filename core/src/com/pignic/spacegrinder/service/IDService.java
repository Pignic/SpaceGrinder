package com.pignic.spacegrinder.service;

import java.util.UUID;

public class IDService {

	public static long getId() {
		return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
	}
}
