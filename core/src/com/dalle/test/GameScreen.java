package com.dalle.test;

import static com.dalle.test.GeneralVariables.HEIGHT;
import static com.dalle.test.GeneralVariables.MOUVEMENT;
import static com.dalle.test.GeneralVariables.RADIUS;
import static com.dalle.test.GeneralVariables.TURN_RATE;
import static com.dalle.test.GeneralVariables.WIDTH;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	private final TestGame game;
	private Texture enemyImage;
	private Texture playerImage;
	private OrthographicCamera camera;
	private MovingCircle player;
	private Array<MovingCircle> enemies;
	private Pool<MovingCircle> enemyPool;
	long lastEnemyTime;
	int enemyKilled;

	public GameScreen(final TestGame game) {
		this.game = game;

		// load the images for the droplet and the bucket, 64x64 pixels each
		enemyImage = new Texture(Gdx.files.internal("Enemy.png"));
		playerImage = new Texture(Gdx.files.internal("Player.png"));

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);

		// create a Rectangle to logically represent the bucket
		player = new MovingCircle();
		player.radius = RADIUS;
		player.x = WIDTH / 2 - RADIUS / 2;
		player.y = HEIGHT / 2 - RADIUS / 2;
		player.heading = 0;

		// create the raindrops array and spawn the first raindrop
		enemies = new Array<MovingCircle>();
		enemyPool = new Pool<MovingCircle>() {

			@Override
			protected MovingCircle newObject() {
				return new MovingCircle();
			}
		};
		spawnEnemy(3);

	}

	private void spawnEnemy(int nb) {
		for (int i = 0; i < nb; i++) {
			spawnEnemy();
		}
	}

	private void spawnEnemy() {
		final MovingCircle enemy = enemyPool.obtain();
		if (MathUtils.randomBoolean()) {
			if (MathUtils.randomBoolean()) {
				enemy.x = MathUtils.random(RADIUS, WIDTH - RADIUS);
				enemy.y = RADIUS;
			} else {
				enemy.x = MathUtils.random(RADIUS, WIDTH - RADIUS);
				enemy.y = HEIGHT - RADIUS;
			}
		} else {
			if (MathUtils.randomBoolean()) {
				enemy.x = RADIUS;
				enemy.y = MathUtils.random(RADIUS, HEIGHT - RADIUS);
			} else {
				enemy.x = WIDTH - RADIUS;
				enemy.y = MathUtils.random(RADIUS, HEIGHT - RADIUS);
			}
		}
		enemy.heading = MathUtils.random(0, 360);
		enemy.radius = RADIUS;
		enemies.add(enemy);
		lastEnemyTime = TimeUtils.nanoTime();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.font.draw(game.batch, "Enemy killed : " + enemyKilled, 0, 480);
		game.batch.draw(playerImage, player.x, player.y);

		for (final MovingCircle enemy : enemies) {
			game.batch.draw(enemyImage, enemy.x, enemy.y);
		}
		game.batch.end();

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			player.changeHeading(TURN_RATE * Gdx.graphics.getDeltaTime());
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			player.changeHeading(-TURN_RATE * Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			player.move(MOUVEMENT * Gdx.graphics.getDeltaTime());
		} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			player.move(-MOUVEMENT * Gdx.graphics.getDeltaTime());
		}

		if (TimeUtils.nanoTime() - lastEnemyTime > 100000000 && enemies.size < 10) {
			spawnEnemy();
		}

		Iterator<MovingCircle> iter = enemies.iterator();
		while (iter.hasNext()) {
			final MovingCircle enemy = iter.next();
			enemy.move();
			if (enemy.isTouchingBoard()) {
				enemy.backRandomHeading();
			}
			if (enemy.overlaps(player)) {
				enemyKilled++;
				iter.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		enemyImage.dispose();
		playerImage.dispose();
	}

}