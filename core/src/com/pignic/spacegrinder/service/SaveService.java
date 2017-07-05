package com.pignic.spacegrinder.service;

import java.io.BufferedReader;
import java.io.IOException;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;

public class SaveService {

	public static Entity[] load(final String name, final World world) {
		final FileHandle saveFolder = Gdx.files.external("spacegrinder/save");
		final FileHandle file = saveFolder.child(name + ".json");
		final BufferedReader br = new BufferedReader(file.reader());
		String line = new String();
		String data = new String();
		try {
			while ((line = br.readLine()) != null) {
				data += line;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return new EntitySerializer().read(data, world);
	}

	public static void save(final String name, final ImmutableArray<Entity> entities) {
		final FileHandle saveFolder = Gdx.files.external("spacegrinder/save");
		saveFolder.mkdirs();
		final FileHandle file = saveFolder.child(name + ".json");
		try {
			final String save = new EntitySerializer().write(entities);
			System.out.println(save);
			file.write(false).write(save.getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
