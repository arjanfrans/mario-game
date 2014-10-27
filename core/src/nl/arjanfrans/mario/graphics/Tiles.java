package nl.arjanfrans.mario.graphics;

import nl.arjanfrans.mario.debug.D;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

public class Tiles {
	private static TextureAtlas atlas = new TextureAtlas("data/tiles/mario_tileset.atlas"); 

	public static Array<StaticTiledMapTile> getAnimatedTile(String name) {
		Array<AtlasRegion> regions = atlas.findRegions(name);
		Array<StaticTiledMapTile> frames = new Array<StaticTiledMapTile>();
		for(int i = 0; i < regions.size; i++) {
			frames.add(new StaticTiledMapTile(regions.get(i)));
		}
		return frames;
	}
	
	public static Animation getAnimation(float speed, String name) {
		Array<AtlasRegion> regions = atlas.findRegions(name);
		TextureRegion[] frames = new TextureRegion[regions.size];
		for(int i = 0; i < regions.size; i++) {
			frames[i] = regions.get(i);
		}
		return new Animation(speed, frames);
	}
	
	public static TextureRegion getTile(String name) {
		AtlasRegion ar = atlas.findRegion(name);
		TextureRegion[] tr = ar.split(ar.getRegionWidth(), ar.getRegionHeight())[0];
		return tr[0];
	}
	
	public static TextureRegion getTile8(String name) {
		AtlasRegion ar = atlas.findRegion(name);
		TextureRegion[] tr = ar.split(8, 8)[0];
		return tr[0];
	}
	
	public void dispose() {
		atlas.dispose();
	}

}
