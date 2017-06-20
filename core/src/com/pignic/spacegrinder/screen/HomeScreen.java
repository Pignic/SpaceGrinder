package com.pignic.spacegrinder.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pignic.spacegrinder.SpaceGrinder;
import com.pignic.spacegrinder.SpaceGrinder.SCENE;

public class HomeScreen extends AbstractScreen {

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
		final TextButton gameButton = new TextButton("Play", style.skin);
		table.add(gameButton).width(100);
		gameButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SCENE.GAME);
			}
		});
		table.row();
		final TextButton buildButton = new TextButton("Build", style.skin);
		table.add(buildButton).width(100);
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SCENE.BUILDER);
			}
		});
		table.row();
		final TextButton optionsButton = new TextButton("Options", style.skin);
		table.add(optionsButton).width(100);
		optionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SCENE.OPTIONS);
			}
		});
		table.row();
		final TextButton exitButton = new TextButton("Exit", style.skin);
		table.add(exitButton).width(100);
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.app.exit();
			}
		});
		Gdx.input.setInputProcessor(stage);
	}

}
