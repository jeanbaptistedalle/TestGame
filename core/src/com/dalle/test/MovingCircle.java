package com.dalle.test;

import static com.dalle.test.GeneralVariables.HEIGHT;
import static com.dalle.test.GeneralVariables.WIDTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MovingCircle extends Sprite implements Poolable {

	private float radius;
	private float speed;

	public MovingCircle() {
		super();
		this.radius = 0;
		this.speed = 0;
	}

	public MovingCircle(final Texture texture, float radius, float speed) {
		super(texture, texture.getWidth(), texture.getHeight());
		this.radius = radius;
		this.speed = speed;
	}

	private void move(float longueur) {
		float ox = getX();
		float oy = getY() + longueur;

		float newX = (float) (getX() + Math.cos(getRadiansRotation()) * (ox - getX()) - Math.sin(getRadiansRotation()) * (oy - getY()));
		float newY = (float) (getY() + Math.sin(getRadiansRotation()) * (ox - getX()) + Math.cos(getRadiansRotation()) * (oy - getY()));

		if (getX() < 0)
			newX = 0;
		if (getX() + radius * 2 > WIDTH)
			newX = WIDTH - radius * 2;
		if (getY() < 0)
			newY = 0;
		if (getY() + radius * 2 > HEIGHT)
			newY = HEIGHT - radius * 2;
		setPosition(newX, newY);
	}

	public void moveAhead(float delta) {
		float longueur = getSpeed() * delta;
		move(longueur);
	}

	public void moveBack(float delta) {
		float longueur = -getSpeed() * delta;
		move(longueur);
	}

	public void changeRotation(float headingDelta) {
		setRotation(getRotation() + headingDelta);
		while (getRotation() <= 0 || getRotation() > 360) {
			if (getRotation() <= 0) {
				setRotation(getRotation() + 360);
			}
			if (getRotation() > 360) {
				setRotation(getRotation() - 360);
			}
		}
	}

	public float getRadiansRotation() {
		return getRotation() * MathUtils.degreesToRadians;
	}

	public void setRadiansRotation(float rotation) {
		setRotation(rotation * MathUtils.radiansToDegrees);
	}

	public void backRandomHeading() {
		changeRotation(MathUtils.random((float) Math.PI / 2, (float) (3 * Math.PI) / 2));
	}

	public boolean isTouchingBoard() {
		return getX() <= 0 || getX() + getRadius() * 2 >= WIDTH || getY() <= 0 || getY() + getRadius() * 2 >= HEIGHT;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public boolean overlaps(final MovingCircle other) {
		double distance = Math.pow(
				(this.getX() - other.getX()) * (this.getX() - other.getX()) + (this.getY() - other.getY()) * (this.getY() - other.getY()),
				0.5);
		if (other.getRadius() >= this.getRadius() && distance <= (other.getRadius() - this.getRadius())) {
			return true;
		} else if (this.getRadius() >= other.getRadius() && distance <= (this.getRadius() - other.getRadius())) {
			return true;
		} else if (distance > (this.getRadius() + other.getRadius())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void reset() {
		super.setTexture(null);
		super.setRotation(0);
		super.setPosition(0, 0);
		this.radius = 0;
		this.speed = 0;
	}
}
