package nl.arjanfrans.mario.model.brick;

import nl.arjanfrans.mario.graphics.Tiles;
import nl.arjanfrans.mario.model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class BrickPiece extends Sprite {
	private TextureRegion texture = Tiles.getTile8("brick_piece");
	private Vector2 position;
	private Vector2 original_position;
	private static final float SIZE = 0.5f;
	private float angle = 0;
	private float speed = 0.25f;
	private float length = 0.4f;
	private int direction;
	private int rotation = 0;
	
	/**
	 * @param x
	 * @param y
	 * @param direction The direction/position the brick piece
	 */
	public BrickPiece(float x, float y, int direction) {
		this.position = new Vector2(x, y);
		original_position = new Vector2(x, y);
		this.direction = direction;
		this.setOrigin(this.getWidth()/2, this.getHeight()/2);
		switch(direction) {
			case 0:
				this.rotate(90);
			case 1:
				this.rotate(180);
			case 2:
				this.rotate(270);
			case 3:
				this.rotate(360);
		}
	}
	
	@Override
	public void draw(Batch batch) {      
		
		if(Math.toDegrees(angle) < 120) {
			switch(direction) {
				case 0:
					position.x = (float) ((length*1.5) * Math.cos(angle));
					position.y = (float) ((length*2) * Math.sin(angle));
					rotation = 7;
					break;
				case 1:
					position.x = (float) ((length*1.5) * Math.cos(angle)) * -1;
					position.y = (float) ((length*2) * Math.sin(angle));
					rotation = -7;
					break;
				case 2:
					position.x = (float) (length * Math.cos(angle));
					position.y = (float) (length * Math.sin(angle));
					rotation = 5;
					break;
				case 3:
					position.x = (float) (length * Math.cos(angle)) * -1;
					position.y = (float) (length * Math.sin(angle));
					rotation = -5;
					break;								
			}
			angle += speed;
			position.x += original_position.x;
			position.y += original_position.y + 1/16 * Gdx.graphics.getDeltaTime();
		}
		else {
			position.y -= speed * Gdx.graphics.getDeltaTime();
		}
		this.rotate(rotation);
			
		batch.draw(texture, position.x, position.y, getOriginX(), getOriginY(),
				SIZE, SIZE, getScaleX(), getScaleY(), getRotation());
		
	}
	
	

}
