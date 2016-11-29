package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.graphics.Tiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Super extends Mushroom {
	private static TextureRegion texture;
	protected Rectangle rect = new Rectangle();
	
	public Super (World world, float x, float y, float max_velocity) {
		super(world, x, y, max_velocity);
		texture = Tiles.getTile("mushroom_super");
		this.setBounds(x, y, texture.getRegionWidth() * World.scale, texture.getRegionHeight() * World.scale);
		direction = Direction.RIGHT;
		moving = false;
		this.setVisible(false);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		move(direction);
		rect.set(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		if(moving) applyPhysics(rect);
	}

	@Override
	protected void dieByFalling() {
		if(this.getY() < -3f) {
			setDead(true);
		}
	}

	@Override
	protected void collisionXAction() {
		if(direction == Direction.LEFT) {
			direction = Direction.RIGHT;
		}
		else {
			direction = Direction.LEFT;
		}
	}

	@Override
	public void dispose() {
		texture.getTexture().dispose();
	}


}
