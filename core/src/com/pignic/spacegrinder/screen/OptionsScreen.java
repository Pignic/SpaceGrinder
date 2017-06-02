package com.pignic.spacegrinder.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pignic.spacegrinder.SpaceGrinder;

public class OptionsScreen implements Screen {
	private final SpaceGrinder game;
	private Label musicOnOffLabel;
	private final String skinName = "glassy";
	private Label soundOnOffLabel;
	private final Stage stage;
	// our new fields
	private Label titleLabel;
	private Label volumeMusicLabel;

	private Label volumeSoundLabel;

	public OptionsScreen(final SpaceGrinder game) {
		this.game = game;
		stage = new Stage(new ScreenViewport());
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

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
		final Table table = new Table();
		table.setFillParent(true);
		// table.setDebug(true);
		stage.addActor(table);
		final Skin skin = new Skin(Gdx.files.internal("skins/" + skinName + "/skin/" + skinName + "-ui.json"));
		final Slider volumeMusicSlider = new Slider(0f, 1f, 0.1f, false, skin);
		volumeMusicSlider.setValue(game.getConfigurations().getMusicVolume());
		volumeMusicSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				game.getConfigurations().setMusicVolume(volumeMusicSlider.getValue());
				return false;
			}
		});
		final CheckBox musicCheckbox = new CheckBox(null, skin);
		musicCheckbox.setChecked(game.getConfigurations().isMusicEnabled());
		musicCheckbox.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				final boolean enabled = musicCheckbox.isChecked();
				game.getConfigurations().setMusicEnabled(enabled);
				return false;
			}
		});

		final Slider soundMusicSlider = new Slider(0f, 1f, 0.1f, false, skin);
		soundMusicSlider.setValue(game.getConfigurations().getMusicVolume());
		soundMusicSlider.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				game.getConfigurations().setSoundVolume(soundMusicSlider.getValue());
				return false;
			}
		});
		final CheckBox soundEffectsCheckbox = new CheckBox(null, skin);
		soundEffectsCheckbox.setChecked(game.getConfigurations().isMusicEnabled());
		soundEffectsCheckbox.addListener(new EventListener() {
			@Override
			public boolean handle(final Event event) {
				final boolean enabled = soundEffectsCheckbox.isChecked();
				game.getConfigurations().setSoundEffectsEnabled(enabled);
				return false;
			}
		});

		final TextButton backButton = new TextButton("Back", skin, "small");
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.switchScene(SpaceGrinder.SCENE.HOME);
			}
		});

		titleLabel = new Label("Preferences", skin);
		volumeMusicLabel = new Label("Music Volume", skin);
		volumeSoundLabel = new Label("Music", skin);
		musicOnOffLabel = new Label("Sound Volume", skin);
		soundOnOffLabel = new Label("Sound Effect", skin);

		table.add(titleLabel).left();
		table.row();
		table.add(volumeMusicLabel).left();
		table.add(volumeMusicSlider);
		table.row();
		table.add(musicOnOffLabel).left();
		table.add(musicCheckbox);
		table.row();
		table.add(volumeSoundLabel).left();
		table.add(soundMusicSlider);
		table.row();
		table.add(soundOnOffLabel).left();
		table.add(soundEffectsCheckbox);
		table.row();
		table.add(backButton);
		Gdx.input.setInputProcessor(stage);
	}
}
