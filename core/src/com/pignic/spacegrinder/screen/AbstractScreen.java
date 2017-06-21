package com.pignic.spacegrinder.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class AbstractScreen implements Screen {

	public static final class Style {

		public final Skin skin;

		public Style(final String skinName) {
			skin = new Skin(Gdx.files.internal("skins/" + skinName + "/skin/" + skinName + "-ui.json"));
		}

	}

	public static final Style style = new Style("default");

	public abstract Engine getEngine();
}
