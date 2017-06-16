package com.pignic.spacegrinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pignic.spacegrinder.pojo.ShipPart;

public class AssetManager {

	public static Map<Class<? extends ShipPart>, List<TextureRegion>> shipPartsTextures = new HashMap<Class<? extends ShipPart>, List<TextureRegion>>();
}
