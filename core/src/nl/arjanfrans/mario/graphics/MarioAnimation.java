package nl.arjanfrans.mario.graphics;

import nl.arjanfrans.mario.model.MovingActor.State;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MarioAnimation extends CharacterAnimation {
	private static Animation walking;
	private static Animation standing;
	private static Animation jumping;
	private static Animation dying;
	private static Animation walking_big;
	private static Animation standing_big;
	private static Animation jumping_big;
	private static Animation crouch_big;
	private static Animation flagslide_small;
	private static Animation flagslide_big;
	private static Animation pose_small;
	private static Animation pose_big;
	

	public MarioAnimation() {
		Array<AtlasRegion> regions = atlas.findRegions("mario_mini_walking");
		standing = new Animation(0, regions.get(0));
		walking = new Animation(0.1f, regions);
		walking.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
					
		jumping = new Animation(0, atlas.findRegion("mario_mini_jump"));
		
		dying = new Animation(0, atlas.findRegion("mario_mini_dead"));

		// For animation with more than 1 frames use 'regions = atlas.findRegions("...").
		regions = atlas.findRegions("mario_big_walking");
		standing_big = new Animation(0, regions.get(0));
		walking_big = new Animation(0.1f, regions);
		walking_big.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		jumping_big = new Animation(0, atlas.findRegion("mario_big_jump"));
		
		crouch_big = new Animation(0, atlas.findRegion("mario_big_crouch"));
		
		regions = atlas.findRegions("mario_big_flagslide");
		flagslide_big = new Animation(1f, regions);
		
		regions = atlas.findRegions("mario_mini_flagslide");
		flagslide_small = new Animation(1f, regions);
		
		regions = atlas.findRegions("mario_big_pose");
		pose_big = new Animation(3.5f, regions);
		
		
		//TODO add mini mario pose
		regions = atlas.findRegions("mario_mini_jump");
		pose_small = new Animation(3.5f, regions);
		
	}


	public Animation getAnimation(State state, int level) {
		switch(state) {
			case Walking:
				if(level == 1) {
					return walking;
				}
				else if(level == 2) {
					return walking_big;
				}
			case Standing:
				if(level == 1) {
					return standing;
				}
				else if(level == 2) {
					return standing_big;
				}
			case Jumping:
				if(level == 1) {
					return jumping;
				}
				else if(level == 2) {
					return jumping_big;
				}
			case FlagSlide:
				if(level == 1) {
					return flagslide_small;
				}
				else if(level == 2) {
					return flagslide_big;
				}
			case Dying:
				return dying;
			case Pose:
				if(level == 1) {
					return pose_small;
				}
				else if(level == 2) {
					return pose_big;
				}
			default:
					return standing;
		}
	}
	
	public Vector2 getDimensions(State state, int level) {
		switch(state) {
			case Walking:
				if(level == 1) {
					return new Vector2(walking.getKeyFrame(0).getRegionWidth() * scale, 
							walking.getKeyFrame(0).getRegionHeight() * scale);
				}
				else if(level == 2) {
					return new Vector2(walking_big.getKeyFrame(0).getRegionWidth() * scale, 
							walking_big.getKeyFrame(0).getRegionHeight() * scale);
				}
				break;
			case Standing:
				if(level == 1) {
					return new Vector2(standing.getKeyFrame(0).getRegionWidth() * scale, 
							standing.getKeyFrame(0).getRegionHeight() * scale);
				}
				else if(level == 2) {
					return new Vector2(standing_big.getKeyFrame(0).getRegionWidth() * scale, 
							standing_big.getKeyFrame(0).getRegionHeight() * scale);
				}
				break;
			case Jumping:
				if(level == 1) {
					//TODO define sizes in constructor
					return new Vector2(jumping.getKeyFrame(0).getRegionWidth() * scale, 
							jumping.getKeyFrame(0).getRegionHeight() * scale);
				}
				else if(level == 2) {
					return new Vector2(jumping_big.getKeyFrame(0).getRegionWidth() * scale, 
						jumping_big.getKeyFrame(0).getRegionHeight() * scale);
				}
				break;
			case Pose:
				if(level == 1) {
					return new Vector2(pose_small.getKeyFrame(0).getRegionWidth() * scale, 
							pose_small.getKeyFrame(0).getRegionHeight() * scale);
				}
				else if(level == 2) {
					return new Vector2(pose_big.getKeyFrame(0).getRegionWidth() * scale, 
						pose_big.getKeyFrame(0).getRegionHeight() * scale);
				}
				break;
			case FlagSlide:
				if(level == 1) {
					return new Vector2(flagslide_small.getKeyFrame(0).getRegionWidth() * scale, 
							flagslide_small.getKeyFrame(0).getRegionHeight() * scale);
				}
				else if(level == 2) {
					return new Vector2(flagslide_big.getKeyFrame(0).getRegionWidth() * scale, 
							flagslide_big.getKeyFrame(0).getRegionHeight() * scale);
				}
				break;
			case Dying:
				return new Vector2(dying.getKeyFrame(0).getRegionWidth() * scale, 
						dying.getKeyFrame(0).getRegionHeight() * scale);
			default:
				if(level == 1) {
					return new Vector2(walking.getKeyFrame(0).getRegionWidth() * scale, 
							walking.getKeyFrame(0).getRegionHeight() * scale);
				}
				else if(level == 2) {
					return new Vector2(walking_big.getKeyFrame(0).getRegionWidth() * scale, 
							walking_big.getKeyFrame(0).getRegionHeight() * scale);
				}
				break;
		}
		return new Vector2(dying.getKeyFrame(0).getRegionWidth() * scale, 
				dying.getKeyFrame(0).getRegionHeight() * scale);
	}
	
	public float getFrameWidth(int level, float width){
		if (level == 1) width = walking.getKeyFrame(0).getRegionWidth() * scale;
		else width = walking_big.getKeyFrame(0).getRegionWidth() * scale;
		
		return width;
	}
	
	public float getFrameHeight(int level, float height){
		if (level == 1) height = walking.getKeyFrame(0).getRegionHeight() * scale;
		else height = walking_big.getKeyFrame(0).getRegionHeight() * scale;
		
		return height;
	}
}
