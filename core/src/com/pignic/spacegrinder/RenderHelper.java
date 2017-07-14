package com.pignic.spacegrinder;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;

public class RenderHelper {

	public static void drawTiledParalax(final Texture texture, final PolygonSpriteBatch batch,
			final float textureFactor, final float positionFactor, final Camera camera) {
		final float paralaxTextureWidth = texture.getWidth() / SpaceGrinder.WORLD_SCALE * textureFactor;
		final float paralaxTextureHeight = texture.getHeight() / SpaceGrinder.WORLD_SCALE * textureFactor;
		final float camX = camera.position.x;
		final float camY = camera.position.y;
		for (float x = -camX * positionFactor % paralaxTextureWidth
				- paralaxTextureWidth; x < camera.viewportWidth; x += paralaxTextureWidth) {
			for (float y = -camY * positionFactor % paralaxTextureHeight
					- paralaxTextureHeight; y < camera.viewportHeight; y += paralaxTextureHeight) {
				batch.draw(texture, x - camera.viewportWidth / 2f + camX, y - camera.viewportHeight / 2f + camY,
						paralaxTextureWidth, paralaxTextureHeight);
			}
		}
	}
}
