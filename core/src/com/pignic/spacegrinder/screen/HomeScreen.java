package com.pignic.spacegrinder.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.SpaceGrinder.SCENE;

public class HomeScreen implements Screen {

	private final SpaceGrinder game;
	private final Stage stage;
	private Table table;

	public HomeScreen(final SpaceGrinder game) {
		this.game = game;
		stage = new Stage(new ScreenViewport());
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(final float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		stage.clear();
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		// table.setDebug(true);
		final TextButtonStyle style = new TextButtonStyle();
		style.font = new BitmapFont();
		final TextButton gameButton = new TextButton("Play", style);
		table.add(gameButton);
		gameButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SCENE.GAME);
			}
		});
		table.row();
		final TextButton buildButton = new TextButton("Build", style);
		table.add(buildButton);
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SCENE.BUILDER);
			}
		});
		table.row();
		final TextButton optionsButton = new TextButton("Options", style);
		table.add(optionsButton);
		optionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SCENE.OPTIONS);
			}
		});
		table.row();
		final TextButton exitButton = new TextButton("Exit", style);
		table.add(exitButton);
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.app.exit();
			}
		});
		Gdx.input.setInputProcessor(stage);
	}

}
