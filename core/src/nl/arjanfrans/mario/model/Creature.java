package nl.arjanfrans.mario.model;

import com.badlogic.gdx.graphics.g2d.Animation;

public abstract class Creature extends MovingActor {

	public Creature(World world, float positionX, float positionY, float f) {
		super(world, positionX, positionY, f);
	}

	public abstract Animation getAnimation();

	protected abstract void dieByFalling();

	@Override
	protected abstract void collisionXAction();

}
