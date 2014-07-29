package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.actions.MoveableActions;
import nl.arjanfrans.mario.graphics.GoombaAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class Goomba extends Creature {
	protected float max_velocity = 1f;
	protected GoombaAnimation gfx = new GoombaAnimation();
	protected Rectangle rect = new Rectangle();

	public Goomba(World world, float positionX, float positionY) {
		super(world, positionX, positionY, 3f);
		float width = gfx.getDimensions(state).x;;
		float height = gfx.getDimensions(state).y;
		this.setSize(width, height);
		direction = Direction.LEFT;
		moving = false;
	}	
	
	private void dieByTrample() {
		state = State.Dying;
		velocity.set(0, 0);
		this.addAction(Actions.sequence(Actions.moveBy(0, -(2 * 1/16f) ),
				Actions.delay(0.5f),
				MoveableActions.DieAction(this)));
	}
	

	
	protected void deadByTrample() {
		dieByTrample();
	}
	
	protected void dieByFalling() {
		if(this.getY() < -3f) {
			setDead(true);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		TextureRegion frame = gfx.getAnimation(state).getKeyFrame(stateTime);
		if(state == State.Dying) {
			this.setWidth(gfx.getDimensions(State.Dying).x);
			this.setHeight(gfx.getDimensions(State.Dying).y);
			batch.draw(frame, getX(), getY(), 
					getX()+this.getWidth()/2, getY() + this.getHeight()/2,
	                this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
		}
		else {
			if(facesRight) {
				batch.draw(frame, this.getX(), this.getY(), 
						this.getX()+(this.getWidth()/2), this.getY() + this.getHeight()/2,
		                this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
			}
			else {
				batch.draw(frame, this.getX() + this.getWidth(), this.getY(), 
						this.getX()+(this.getWidth()/2), this.getY() + this.getHeight()/2,
		                -this.getWidth(), this.getHeight(), getScaleX(), getScaleY(), getRotation());
			}
		}
	}
	
	protected void collisionWithCreature() {
		Array<Goomba> goombas = world.getEnemies();
		Rectangle rect = rectangle();
		rect.set(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		for(Goomba goomba : goombas) {
			Rectangle eRect = goomba.rectangle();
			if(goomba != this && eRect.overlaps(rect) && goomba.state != State.Dying) {
				collisionXAction();
			}
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if(state != State.Dying) {
			move(direction);
			rect.set(this.getX(), this.getY(), this.getWidth(), this.getHeight());
			applyPhysics(rect);
			collisionWithCreature();
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
	public Animation getAnimation() {
		return gfx.getAnimation(state);
	}

	public void dispose() {
		gfx.dispose();
	}

	
}
