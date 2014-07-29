package nl.arjanfrans.mario.graphics;

import nl.arjanfrans.mario.model.MovingActor.State;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public abstract class CharacterAnimation {
	protected TextureAtlas atlas = new TextureAtlas("data/characters/characters.atlas");
	public static final float scale = 1/16f;
	
	public void dispose() {
		atlas.dispose();
	}

}
