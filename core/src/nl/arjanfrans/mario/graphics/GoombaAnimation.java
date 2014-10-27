package nl.arjanfrans.mario.graphics;

import nl.arjanfrans.mario.model.MovingActor.State;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GoombaAnimation extends CharacterAnimation {
	private Animation walking;
	private Animation trampled;

	public GoombaAnimation() {
		Array<AtlasRegion> regions = atlas.findRegions("goomba_walking");
		walking = new Animation(0.2f, regions);
		walking.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		trampled = new Animation(1f, atlas.findRegion("goomba_trampled"));
	}
	
	public Animation getAnimation(State state) {
		switch(state) {
			case Walking:
				return walking;
			case Dying:
				return trampled;
			default:
				return walking;
		}
	}
	
	public Vector2 getDimensions(State state) {
		switch(state) {
			case Walking:
				return new Vector2(walking.getKeyFrame(0).getRegionWidth() * scale, 
						walking.getKeyFrame(0).getRegionHeight() * scale);
			case Dying:
				return new Vector2(trampled.getKeyFrame(0).getRegionWidth() * scale, 
						trampled.getKeyFrame(0).getRegionHeight() * scale);
			default:
				return new Vector2(walking.getKeyFrame(0).getRegionWidth() * scale, 
						walking.getKeyFrame(0).getRegionHeight() * scale);
		}
	}
	
	
}
