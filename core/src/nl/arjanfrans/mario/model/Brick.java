package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.actions.ActorActions;
import nl.arjanfrans.mario.audio.Audio;
import nl.arjanfrans.mario.graphics.Tiles;
import nl.arjanfrans.mario.model.brick.BrickShatter;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class Brick extends StaticActor {
	private TextureRegion texture;
	private TextureRegion empty_texture;

	private float stateTime;
	private static Animation bonus_animation;
	private int hitcount = 0;
	private int maxhits = 1;
	private boolean destructable;
	private Array<Actor> items;
	private boolean bonus; //Whether this is a bonus tile or not
	
	private BrickShatter shatter;

	public Brick(World world, float x, float y, String color, boolean bonus, boolean destructable) {
		super(world);
		this.bonus = bonus;
		this.destructable = destructable;
		this.setOrigin(x, y);
		this.setBounds(x, y, 16 * (1/16f), 16 * (1/16f));
		bonus_animation = Tiles.getAnimation(0.15f, "bonus_block");
		bonus_animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		empty_texture = Tiles.getTile("brick_empty");
		items = new Array<Actor>();
		if(color.equals("brown")) {
			texture = Tiles.getTile("brick");
			empty_texture = Tiles.getTile("brick_empty");
			shatter =  new BrickShatter(this.getX(), this.getY());
		}
		else if(color.equals("blue")) {
			//TODO make blue tile
		}
	}
	
	@Override
	public void act(float delta) {
		stateTime += delta;
		super.act(delta);
	}
	
	/**
	 * Shatter the brick in pieces.
	 */
	private void shatter() {
		this.addAction(Actions.sequence(Actions.delay(0.3f), Actions.alpha(0, 0.1f), ActorActions.removeActor(this)));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(destroyed) {
			shatter.draw(batch);
			shatter();
		}
		else {
			if(items.size < 1 && hitcount > 0) {
				batch.draw(empty_texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			}
			else if(bonus) {
				batch.draw(bonus_animation.getKeyFrame(stateTime), this.getX(), this.getY(), this.getWidth(), this.getHeight());
			}
			else {
				batch.draw(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			}
		}
	}

	public void hit(int mario_level) {
		if(items.size > 0) {
			if(items.peek() instanceof Mushroom) {
				((Mushroom) items.pop()).appear();
			}
		}
		if(mario_level > 1) {
			hitcount++;
			if(items.size < 1) {
				if(destructable) {
					destroyed = true;
				}
			}
			this.addAction(Actions.sequence(Actions.moveTo(this.getOriginX(), this.getOriginY() + 0.2f, 0.1f, Interpolation.linear),
					Actions.moveTo(this.getOriginX(), this.getOriginY(), 0.1f, Interpolation.linear)));
			Audio.bump.play();
		}
		else {
			this.addAction(Actions.sequence(Actions.moveTo(this.getOriginX(), this.getOriginY() + 0.2f, 0.1f, Interpolation.linear),
					Actions.moveTo(this.getOriginX(), this.getOriginY(), 0.1f, Interpolation.linear)));
			Audio.bump.play();
		}
	}
	
	

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return Math.round(super.getX() * 100.0f) / 100.0f;
	}


	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return Math.round(super.getY()  * 100.0f) / 100.0f;
	}



	public Array<Actor> getItems() {
		return items;
	}
	
	public Actor popItem() {
		return items.pop();
	}


	public void addItem(Actor item) {
		items.add(item);
	}




}
