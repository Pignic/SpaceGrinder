package com.pignic.spacegrinder;

import com.badlogic.gdx.Game;
import com.pignic.spacegrinder.screen.BuilderScreen;
import com.pignic.spacegrinder.screen.GameScreen;
import com.pignic.spacegrinder.screen.HomeScreen;
import com.pignic.spacegrinder.screen.MenuScreen;
import com.pignic.spacegrinder.screen.OptionsScreen;

public class SpaceGrinder extends Game {

	public static enum SCENE {
		BUILDER, GAME, HOME, MENU, OPTIONS
	}

	public static final float WORLD_SCALE = 10;

	private BuilderScreen builderScreen;
	private final Configuration configurations = new Configuration();
	private GameScreen gameScreen;
	private HomeScreen homeScreen;
	private MenuScreen menuScreen;
	private OptionsScreen optionsScreen;

	@Override
	public void create() {
		builderScreen = new BuilderScreen(this);
		gameScreen = new GameScreen(this);
		homeScreen = new HomeScreen(this);
		menuScreen = new MenuScreen(this);
		optionsScreen = new OptionsScreen(this);
		setScreen(homeScreen);
	}

	@Override
	public void dispose() {
		builderScreen.dispose();
		menuScreen.dispose();
		gameScreen.dispose();
		homeScreen.dispose();
		optionsScreen.dispose();
	}

	public Configuration getConfigurations() {
		return configurations;
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(final int width, final int height) {
		super.resize(width, height);
	}

	public boolean switchScene(final SCENE scene) {
		switch (scene) {
		case BUILDER:
			setScreen(builderScreen);
			break;
		case HOME:
			setScreen(homeScreen);
			break;
		case MENU:
			setScreen(menuScreen);
			break;
		case GAME:
			setScreen(gameScreen);
			break;
		case OPTIONS:
			setScreen(optionsScreen);
			break;
		default:
			dispose();
		}
		return true;
	}
}
