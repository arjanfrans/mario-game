package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.actions.MarioActions;
import nl.arjanfrans.mario.actions.MoveableActions;
import nl.arjanfrans.mario.audio.Audio;
import nl.arjanfrans.mario.graphics.MarioAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class Mario extends Creature {
	protected MarioAnimation gfx = new MarioAnimation();
	private float jump_boost = 40f, width, height;
	private boolean immume;
	protected Rectangle rect = new Rectangle();
	private boolean controlsEnabled = true;
	
	public Mario(World world, float positionX, float positionY) {
		super(world, positionX, positionY, 8f);
		immume = false;
		moving = true;
		level = 1;
		updateSize();
	}

	protected void updateSize() {
		this.setSize(gfx.getDimensions(state, level).x, gfx.getDimensions(state, level).y);
	}

	private void hitByEnemy() {	
		if(immume == false) level--;
		if(level < 1 && !immume) {
			state = State.Dying;
			velocity.set(0, 0);
			this.setWidth(gfx.getDimensions(state, level).x);
			this.setHeight(gfx.getDimensions(state, level).y);
			this.addAction(Actions.sequence(Actions.moveBy(0, 1, 0.2f, Interpolation.linear),
					Actions.delay(0.6f),
					Actions.moveBy(0, -10, 0.6f, Interpolation.linear),
					Actions.delay(1.6f),
					MoveableActions.DieAction(this)));
			Audio.stopSong();
			Audio.playSong("lifelost", false);
		}
		else {
			if(!immume) Audio.powerDown.play();
			immume = true;
			this.addAction(Actions.sequence(Actions.parallel(Actions.alpha(0f, 2f, Interpolation.linear),
					Actions.fadeIn(0.4f, Interpolation.linear),
					Actions.fadeOut(0.4f, Interpolation.linear),
					Actions.fadeIn(0.4f, Interpolation.linear),
					Actions.fadeOut(0.4f, Interpolation.linear),
					Actions.fadeIn(0.4f, Interpolation.linear)),
					Actions.alpha(1f),
					MarioActions.stopImmumeAction(this)));
		}
	}
	

	/**
	 * 
	 * @param flag Flag object.
	 * @param endX X-position of ending point.
	 * @param endY Y-position of ending point.
	 */
	public void captureFlag(Flag flag, float endX, float endY) {
		Rectangle flagRect = flag.rect();
		state = State.FlagSlide;
		//TODO Flip mario sprite in sliding pose when at bottom
		this.addAction(Actions.sequence(
				Actions.delay(0.2f),
				Actions.parallel(
						Actions.moveTo(this.getX(), flagRect.y, 0.5f, Interpolation.linear), 
						MarioActions.flagTakeDownAction(flag)),
				MarioActions.setStateAction(this, State.Walking),
				MarioActions.walkToAction(this, endX, endY),
				MarioActions.setStateAction(this, State.Pose),
				MarioActions.finishLevelAction())
			);
		Audio.stopSong();
		Audio.flag.play();
	}

	protected void dieByFalling() {
		if(this.getY() < -3f) {
			//TODO create some kind of sensor for dying
			state = State.Dying;
			velocity.set(0, 0);
			this.addAction(Actions.sequence(Actions.delay(3f),
					MoveableActions.DieAction(this)));
			Audio.stopSong();
			Audio.playSong("lifelost", false);
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if(state != State.Dying) {
			if(state != State.FlagSlide) {
				if(controlsEnabled) {
					if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.75f, 1)) && grounded) {
						jump();
					}
					if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f)) {
						move(Direction.LEFT);
					}
					if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
						move(Direction.RIGHT);
					}
				}
					
				width = gfx.getFrameWidth(level, width);
				height = gfx.getFrameHeight(level, height);
				rect.set(this.getX(), this.getY(), width, height);
				
				collisionWithEnemy();
				collisionWithMushroom();
                if(state != State.Dying) applyPhysics(rect);
			}
			
		}
	}



	@Override
	public void draw(Batch batch, float parentAlpha) {
		TextureRegion frame = gfx.getAnimation(state, level).getKeyFrame(stateTime);
		updateSize();
		Color oldColor = batch.getColor();
		batch.setColor(this.getColor());
		if(state == State.Dying) {
			batch.draw(frame, getX(), getY(), 
					getX()+this.getWidth()/2, getY() + this.getHeight()/2,
					this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
		}
		else {
			if(facesRight) {
				batch.draw(frame, this.getX(), this.getY(), 
						this.getX()+this.getWidth()/2, this.getY() + this.getHeight()/2,
						this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
			}
			else {
				batch.draw(frame, this.getX() + this.getWidth(), this.getY(), 
						this.getX()+this.getWidth()/2, this.getY() + this.getHeight()/2,
						-this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
			}
		}
		batch.setColor(oldColor);
	}




	private boolean isTouched(float startX, float endX)
	{
		// check if any finge is touch the area between startX and endX
		// startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
		for (int i = 0; i < 2; i++)
		{
			float x = Gdx.input.getX() / (float) Gdx.graphics.getWidth();
			if (Gdx.input.isTouched(i) && (x >= startX && x <= endX))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Jump
	 */
	private void jump() {
		if(grounded) {
			velocity.y += jump_velocity;
			state = MovingActor.State.Jumping;
			grounded = false;
			Audio.jump.play();
		}	
	}

	@Override
	protected void collisionXAction() {
		velocity.x = 0;
	}


	/**
	 * Check for collision with an enemy.
	 */
	protected void collisionWithEnemy() {
		Array<Goomba> goombas = world.getEnemies();
		Rectangle marioRect = rectangle();
		marioRect.set(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		for(Goomba goomba : goombas) {
			Rectangle eRect = goomba.rectangle();

			if(eRect.overlaps(marioRect) && goomba.state != State.Dying) {
				if(velocity.y < 0 && this.getY() > goomba.getY()) {
					goomba.deadByTrample(); //enemies dies mario stamped on his head
					Audio.stomp.play();
					velocity.y += jump_boost;
					grounded = false;
				}
				else if(goomba.state != State.Dying){
					hitByEnemy(); //mario dies because he came in from aside or below
				}
			}
		}
	}
	
	float maxheight;
	@Override
	protected void applyPhysics(Rectangle rect) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (deltaTime == 0) return;
		stateTime += deltaTime;
		
		velocity.add(0, World.GRAVITY * deltaTime); // apply gravity if we are falling

		if (Math.abs(velocity.x) < 1) {	// clamp the velocity to 0 if it's < 1, and set the state to standing
			velocity.x = 0;
			if (grounded && controlsEnabled) {
				state = State.Standing;
			}
		}

		velocity.scl(deltaTime); // multiply by delta time so we know how far we go in this frame

		if(collisionX(rect)) collisionXAction();
		rect.x = this.getX();
		collisionY(rect);
		

		this.setPosition(this.getX() + velocity.x, this.getY() +velocity.y); 
		velocity.scl(1 / deltaTime); // unscale the velocity by the inverse delta time and set the latest position
		
		velocity.x *= damping; // Apply damping to the velocity on the x-axis so we don't walk infinitely once a key was pressed
		
		dieByFalling();
	}

	/**
	 * Upgrade mario to level 2.
	 * @param mushroom
	 */
	private void big_mario(Mushroom mushroom) {
		level = 2;
		World.objectsToRemove.add(mushroom);
		Audio.powerUp.play();
	}

	/**
	 * Check for collision with a mushroom/powerup
	 */
	protected void collisionWithMushroom() {
		//TODO Make it more generic, for all powerups and not just mushrooms
		Array<Mushroom> mushrooms = world.getMushrooms();
		Rectangle marioRect = rectangle();
		marioRect.set(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		for(Mushroom mushroom : mushrooms) {
			Rectangle eRect = mushroom.rectangle();
			if(mushroom.isVisible() && eRect.overlaps(marioRect) && mushroom.state != State.Dying) {
				if(level == 1) big_mario(mushroom);
			}
		}
	}

	@Override
	public Animation getAnimation() {
		return gfx.getAnimation(state, level);
	}


	public void dispose() {
		gfx.dispose();
	}

	public void setImmume(boolean immume) {
		this.immume = immume;
	}
	
	@Override
	public void move(Direction dir) {
		if(state != State.Dying && moving) {
			if(dir == Direction.LEFT) {
				velocity.x = -max_velocity;
				facesRight = false;
			}
			else {
				velocity.x = max_velocity;
				facesRight = true;
			}
			direction = dir;
			if (grounded) {
				state = MovingActor.State.Walking;
			}
		}
	}

	public boolean isControlsEnabled() {
		return controlsEnabled;
	}

	public void setControlsEnabled(boolean controlsEnabled) {
		this.controlsEnabled = controlsEnabled;
	}





}
