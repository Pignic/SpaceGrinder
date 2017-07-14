package com.pignic.spacegrinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.Timer;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.pignic.spacegrinder.net.pojo.Message;
import com.pignic.spacegrinder.system.CollisionSystem;
import com.pignic.spacegrinder.system.DurabilitySystem;
import com.pignic.spacegrinder.system.LinkSystem;
import com.pignic.spacegrinder.system.PhysicSystem;
import com.pignic.spacegrinder.system.ProjectileSystem;
import com.pignic.spacegrinder.system.TimerSystem;

public class SpaceGrinderServer implements Runnable {

	public static void main(final String... args) {
		new Thread(new SpaceGrinderServer()).start();
	}

	private final PooledEngine engine;

	private int fps = 0;

	private final World world;

	public SpaceGrinderServer() {
		world = new World(new Vector2(0, 0), true);
		engine = new PooledEngine();
		final SpaceGrinderServer instance = this;
		engine.addSystem(new LinkSystem(world));
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new ProjectileSystem());
		engine.addSystem(new TimerSystem());
		engine.addSystem(new CollisionSystem(world));
		engine.addSystem(new DurabilitySystem());
		final Timer timer = new Timer((int) (1000f / 60f), new ActionListener() {
			private long lastFpsTime = 0;
			private long lastLoopTime = System.currentTimeMillis();
			private long now;
			private int updateCount = 0;
			private long updateLength;

			@Override
			public void actionPerformed(final ActionEvent event) {
				now = event.getWhen();
				updateLength = now - lastLoopTime;
				lastLoopTime = now;
				lastFpsTime += updateLength;
				++updateCount;
				if (lastFpsTime >= 1000) {
					instance.fps = updateCount;
					lastFpsTime = 0;
					updateCount = 0;
				}
				instance.update(updateLength);
			}
		});
		timer.start();
	}

	@Override
	public void run() {
		final ServerSocketHints serverSocketHint = new ServerSocketHints();
		serverSocketHint.acceptTimeout = 5000;
		final ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP, 9021, serverSocketHint);
		final Json json = new Json();
		while (true) {
			final Socket socket = serverSocket.accept(null);
			final BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = "";
			String data = "";
			try {
				while ((line = buffer.readLine()) != null) {
					data += line;
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
			final Message message = json.fromJson(Message.class, data);
			switch (message.type) {
			case BEAT:
				break;
			default:
				break;
			}
		}
	}

	private void update(final float ms) {
		engine.update(ms);
	}

}
