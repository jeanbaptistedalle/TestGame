package com.dalle.test;

import static com.dalle.test.GeneralVariables.BULLET_MOUVEMENT_SPEED;
import static com.dalle.test.GeneralVariables.BULLET_RADIUS;
import static com.dalle.test.GeneralVariables.HEIGHT;
import static com.dalle.test.GeneralVariables.PLAYER_MOUVEMENT_SPEED;
import static com.dalle.test.GeneralVariables.PLAYER_RADIUS;
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
	private Texture enemyTexture;
	private Texture playerTexture;
	private Texture bulletTexture;
	private OrthographicCamera camera;
	private MovingCircle player;
	private Array<MovingCircle> enemies;
	private Array<MovingCircle> bullets;
	private Pool<MovingCircle> circlePool;

	private long lastEnemyTime;
	private int enemyKilled;

	public GameScreen(final TestGame game) {
		this.game = game;
		circlePool = new Pool<MovingCircle>() {

			@Override
			protected MovingCircle newObject() {
				return new MovingCircle();
			}
		};
		enemyTexture = new Texture(Gdx.files.internal("enemy.png"));
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
		bullets = new Array<MovingCircle>();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		player = new MovingCircle();
		player.setTexture(playerTexture);
		float x = WIDTH / 2 - player.getRadius() / 2;
		float y = HEIGHT / 2 - player.getRadius() / 2;
		player.setPosition(x, y);
		player.setSpeed(PLAYER_MOUVEMENT_SPEED);
		player.setRadius(PLAYER_RADIUS);
		enemies = new Array<MovingCircle>();
		spawnEnemy(4);
	}

	private void spawnEnemy(int nb) {
		for (int i = 0; i < nb; i++) {
			spawnEnemy();
		}
	}

	private void spawnEnemy() {
		final MovingCircle enemy = circlePool.obtain();
		enemy.setSpeed(PLAYER_MOUVEMENT_SPEED);
		enemy.setRadius(PLAYER_RADIUS);
		enemy.setRotation(MathUtils.random(0, 360));
		enemy.setTexture(enemyTexture);
		float x = 0;
		float y = 0;
		if (MathUtils.randomBoolean()) {
			if (MathUtils.randomBoolean()) {
				x = MathUtils.random(enemy.getRadius(), WIDTH - enemy.getRadius());
				y = enemy.getRadius();
			} else {
				x = MathUtils.random(enemy.getRadius(), WIDTH - enemy.getRadius());
				y = HEIGHT - enemy.getRadius();
			}
		} else {
			if (MathUtils.randomBoolean()) {
				x = enemy.getRadius();
				y = MathUtils.random(enemy.getRadius(), HEIGHT - enemy.getRadius());
			} else {
				x = WIDTH - enemy.getRadius();
				y = MathUtils.random(enemy.getRadius(), HEIGHT - enemy.getRadius());
			}
		}
		enemy.setPosition(x, y);
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
		player.draw(game.batch);
		for (final MovingCircle enemy : enemies) {
			enemy.draw(game.batch);
		}
		for (final MovingCircle bullet : bullets) {
			bullet.draw(game.batch);
		}
		game.batch.end();

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			player.changeRotation(TURN_RATE * Gdx.graphics.getDeltaTime());
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			player.changeRotation(-TURN_RATE * Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			player.moveAhead(Gdx.graphics.getDeltaTime());
		} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			player.moveBack(Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			final MovingCircle bullet = circlePool.obtain();
			bullet.setTexture(bulletTexture);
			bullet.setRotation(player.getRotation());
			bullet.setSpeed(BULLET_MOUVEMENT_SPEED);
			bullet.setRadius(BULLET_RADIUS);
			float x = (float) (player.getX() + PLAYER_RADIUS + PLAYER_RADIUS * Math.cos(player.getRotation()));
			float y = (float) (player.getY() + PLAYER_RADIUS + PLAYER_RADIUS * Math.sin(player.getRotation()));
			bullet.setPosition(x, y);
		}

		if (TimeUtils.nanoTime() - lastEnemyTime > 100000000 && enemies.size < 10) {
			spawnEnemy();
		}

		final Iterator<MovingCircle> enemiesIt = enemies.iterator();
		while (enemiesIt.hasNext()) {
			final MovingCircle enemy = enemiesIt.next();
			enemy.moveAhead(Gdx.graphics.getDeltaTime());
			if (enemy.isTouchingBoard()) {
				enemy.backRandomHeading();
			}
			if (enemy.overlaps(player)) {
				enemyKilled++;
				enemiesIt.remove();
			}
		}
		final Iterator<MovingCircle> bulletsIt = bullets.iterator();
		while (bulletsIt.hasNext()) {
			final MovingCircle bullet = bulletsIt.next();
			bullet.moveAhead(Gdx.graphics.getDeltaTime());
			if (bullet.isTouchingBoard()) {
				bulletsIt.remove();
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
		enemyTexture.dispose();
		playerTexture.dispose();
		bulletTexture.dispose();
	}

}