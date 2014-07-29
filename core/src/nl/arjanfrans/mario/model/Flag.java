package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.graphics.Tiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;


public class Flag extends Actor {
	private Animation animation;
	private float stateTime;
	private float endX;
	private float endY;
	private boolean down = false;
	private float bottomY;
	private float slideOffset = 2;
	
	public Flag(float x, float y, float width, float height, float endX, float endY) {
		animation = Tiles.getAnimation(0.15f, "evil_flag");
		animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		this.setOrigin(width / 2, height);
		this.setBounds(x + (8 * World.scale), y, 2 * World.scale, height);
		this.setTouchable(Touchable.disabled);
		this.endX = endX;
		this.endY = endY;
		this.bottomY = y - (height - slideOffset);
	}
	
	@Override
	public void act(float delta) {
		stateTime += delta;
		super.act(delta);
	}
	
	public void takeDown() {
		this.addAction(Actions.sequence(
				Actions.delay(0.2f),
				Actions.moveBy(0, -(this.getHeight() - slideOffset), 2f))
			);
	}

	

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(animation.getKeyFrame(stateTime), this.getX() - (1 * World.scale),  this.getY() + (this.getHeight() - slideOffset), 
				animation.getKeyFrame(stateTime).getRegionWidth() * World.scale, animation.getKeyFrame(stateTime).getRegionHeight() * World.scale);
	}
	
	public Rectangle rect() {
		return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	public boolean isDown() {
		return Math.round(this.getY()) == bottomY;
	}

	public void setDown(boolean down) {
		this.down = down;
	}
	
	


}
