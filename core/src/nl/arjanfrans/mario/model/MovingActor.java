package nl.arjanfrans.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public abstract class MovingActor extends Actor {
	//TODO Some states are for Mario only, they don't belong in this class.
	public static enum State {
		
		Standing, Walking, Jumping, Dying, Dead, FlagSlide, NoControl, Pose
	}
	public static enum Direction {
		LEFT, RIGHT;
	}
	protected Pool<Rectangle> rectPool = new Pool<Rectangle>()
	{
		@Override
		protected Rectangle newObject()
		{
			return new Rectangle();
		}
	};
	protected float max_velocity;
	protected float jump_velocity = 40f;
	protected float damping = 0.87f;
	protected Vector2 position;
	protected Vector2 velocity;
	protected World world;
	protected boolean dead;
	protected boolean moving;
	protected State state = State.Standing;
	protected float stateTime = 0;
	protected int level;
	protected boolean facesRight = true;
	protected Direction direction;
	
	protected boolean grounded = false;
	
	public MovingActor(World world, float x, float y, float max_velocity) {
		this.world = world;
		this.setPosition(x, y);
		this.max_velocity = max_velocity;
		velocity = new Vector2(0, 0);
		dead = false;
		moving = false;
		this.setTouchable(Touchable.disabled);
	}



	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
		world.removeActor(this);
	}
	
	
	protected Rectangle rectangle() {
		return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	

	protected void applyPhysics(Rectangle rect) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (deltaTime == 0) return;
		stateTime += deltaTime;
		
		velocity.add(0, World.GRAVITY * deltaTime); // apply gravity if we are falling

		if (Math.abs(velocity.x) < 1) {	// clamp the velocity to 0 if it's < 1, and set the state to standing
			velocity.x = 0;
			if (grounded) {
				state = State.Standing;
			}
		}

		velocity.scl(deltaTime); // multiply by delta time so we know how far we go in this frame

		if(collisionX(rect)) collisionXAction();
		rect.x = this.getX();
		collisionY(rect);

		this.setPosition(this.getX() + velocity.x, this.getY() + velocity.y); // unscale the velocity by the inverse delta time and set the latest position
		velocity.scl(1 / deltaTime);
		velocity.x *= damping; // Apply damping to the velocity on the x-axis so we don't walk infinitely once a key was pressed
		
		dieByFalling();
	}
	
	protected abstract void dieByFalling();
	
	protected abstract void collisionXAction();
	

	protected boolean collisionX(Rectangle rect) {
		int[] bounds = checkTiles(true);
		Array<Rectangle> tiles = world.getTiles(bounds[0], bounds[1], bounds[2], bounds[3]);
		rect.x += velocity.x;
		for (Rectangle tile : tiles) {
			if (rect.overlaps(tile)) {
				return true;
			}
		}
		for(StaticActor a : world.getStaticActors()) {
			if(rect.overlaps(a.rectangle()) && !a.isDestroyed()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check from where to where to check. Method is used to help finding tiles
	 * if the koala is moving right, check the tiles to the right of it's
	 * right bounding box edge, otherwise check the ones to the left
	 * if the koala is moving upwards, check the tiles to the top of it's
	 * top bounding box edge, otherwise check the ones to the bottom
	 * @param checkx Check x if true or if false check y
	 * @return startX, startY, endX, enY
	 */
	
	protected int[] checkTiles(boolean checkX) {
		int startX, startY, endX, endY;
		if(checkX) {
			if (velocity.x > 0) {
				startX = endX = (int) (this.getX() + this.getWidth() + velocity.x);
			}
			else {
				startX = endX = (int) (this.getX() + velocity.x);
			}
			startY = (int) (this.getY());
			endY = (int) (this.getY() + this.getHeight());
		}
		else {
			if (velocity.y > 0) {
				startY = endY = (int) (this.getY() + this.getHeight() + velocity.y); //
			}
			else {
				startY = endY = (int) (this.getY() + velocity.y);
			}
			startX = (int) (this.getX());
			endX = (int) (this.getX() + this.getWidth());
		}
		return new int[]{startX, startY, endX, endY};
	}
	
	protected void collisionY(Rectangle rect) {
		int[] bounds = checkTiles(false);
		Array<Rectangle> tiles = world.getTiles(bounds[0], bounds[1], bounds[2], bounds[3]);
		rect.y += velocity.y;
		if(velocity.y < 0 ) {	//dont allow jumping when falling/walking off an edge
			grounded = false;
		}
		for (Rectangle tile : tiles) {
			if (rect.overlaps(tile)) {
				if (velocity.y > 0) {
					this.setY(tile.y - this.getHeight()); //so it is just below/above the tile we collided with
				}
				else {
					this.setY(tile.y + tile.height);
					hitGround();
				}
				velocity.y = 0;
				break;
			}
		}
		
		for(StaticActor a : world.getStaticActors()) {
			if(rect.overlaps(a.rectangle()) && !a.isDestroyed()) {
				if (velocity.y > 0) {
					a.hit(level);
					this.setY(a.getOriginY() - this.getHeight());
				}
				else {
					this.setY(a.getY() + a.getHeight());
					hitGround();
				}
				velocity.y = 0;
				break;
			}
		}
		rectPool.free(rect);
	}

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
	
	protected void hitGround() {
		grounded = true;
	}
	
	public float getMax_velocity() {
		return max_velocity;
	}
	
	public float getJump_velocity() {
		return jump_velocity;
	}
	
	public float getDamping() {
		return damping;
	}

	public float getStateTime() {
		return stateTime;
	}

	public boolean isFacesRight() {
		return facesRight;
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}
	
	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	

}
