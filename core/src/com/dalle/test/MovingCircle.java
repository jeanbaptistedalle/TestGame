package com.dalle.test;

import static com.dalle.test.GeneralVariables.HEIGHT;
import static com.dalle.test.GeneralVariables.MOUVEMENT;
import static com.dalle.test.GeneralVariables.RADIUS;
import static com.dalle.test.GeneralVariables.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MovingCircle extends Circle implements Poolable{
	private static final long serialVersionUID = -6813543139672608019L;

	float heading;
	
	public MovingCircle(){
		super();
		reset();
	}

	public void move(float longueur) {
		float ox = this.x;
		float oy = this.y + longueur;

		float newX = (float) (this.x + Math.cos(heading) * (ox - this.x) - Math.sin(heading) * (oy - this.y));
		float newY = (float) (this.y + Math.sin(heading) * (ox - this.x) + Math.cos(heading) * (oy - this.y));

		this.x = newX;
		this.y = newY;

		if (this.x < 0)
			this.x = 0;
		if (this.x + RADIUS * 2 > WIDTH)
			this.x = WIDTH - RADIUS * 2;
		if (this.y < 0)
			this.y = 0;
		if (this.y + RADIUS * 2 > HEIGHT)
			this.y = HEIGHT - RADIUS * 2;
	}

	public void move() {
		float longueur = MOUVEMENT * Gdx.graphics.getDeltaTime();
		move(longueur);
	}

	public void changeHeading(float headingDelta) {
		this.heading += headingDelta;
		while (this.heading <= 0 || this.heading > Math.PI * 2) {
			if (this.heading <= 0) {
				this.heading += Math.PI * 2;
			}
			if (this.heading > Math.PI * 2) {
				this.heading -= Math.PI * 2;
			}
		}
	}

	public void backRandomHeading() {
		changeHeading(MathUtils.random((float) Math.PI / 2, (float) (3 * Math.PI) / 2));
	}

	public boolean isTouchingBoard() {
		return this.x <= 0 || this.x + RADIUS * 2 >= WIDTH || this.y <= 0 || this.y + RADIUS * 2 >= HEIGHT;
	}

	@Override
	public void reset() {
		this.x = 0;
		this.y = 0;
		this.heading = 0;
	}
}
