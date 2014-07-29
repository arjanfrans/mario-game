package nl.arjanfrans.mario.model;

import java.util.Iterator;

import nl.arjanfrans.mario.audio.Audio;
import nl.arjanfrans.mario.debug.D;
import nl.arjanfrans.mario.graphics.Tiles;
import nl.arjanfrans.mario.view.WorldRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;


public class World {
	private Mario player;
	private TiledMap map;
	public static final float GRAVITY = -150;
	public static final float scale = 1/16f;
	private Array<Goomba> goombas;
	private Array<Mushroom> mushrooms;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>()
	{
		@Override
		protected Rectangle newObject()
		{
			return new Rectangle();
		}
	};
	private static Tiles tiles = new Tiles();
	private Stage stage;
	private WorldRenderer wr;
	
	// If true the world will reset
	public static boolean reset_flag = false;
	
	private boolean playing_finish_song = false;
	
	/**
	 * The flag at the end of the level.
	 */
	private Flag flag;
	
	public static Array<Actor> objectsToRemove = new Array<Actor>();
	
	public World() {
		reset();
	}
	
	private boolean level_ended = false;
	
	
	private Array<Goomba> generateEnemies() {
		Array<Goomba> goombas = new Array<Goomba>();
		MapLayer layer = map.getLayers().get("objects");
		MapObjects objects = layer.getObjects();
		Iterator<MapObject> objectIt = objects.iterator();
		while(objectIt.hasNext()) {
			MapObject obj = objectIt.next();
			String type = (String) obj.getProperties().get("type");
			if(type != null) {
				float x = (Float) obj.getProperties().get("x");
				float y = (Float) obj.getProperties().get("y");
				if(type.equals("goomba")) {
					Goomba goomba = new Goomba(this, x * (1/16f), y* (1/16f));
					goombas.add(goomba);
					stage.addActor(goomba);
				}
			}
		}
		return goombas;
	}
	
	public void removeActor(Actor a) {
		stage.getActors().removeValue(a, true);
	}
	
	private void reset() {
		reset_flag = false;
		map = new TmxMapLoader().load("data/level1.tmx");

		animateTiles((TiledMapTileLayer) map.getLayers().get("walls_background"));
		initTileset((TiledMapTileLayer) map.getLayers().get("walls_background"));
		animateTiles((TiledMapTileLayer) map.getLayers().get("walls"));
		initTileset((TiledMapTileLayer) map.getLayers().get("walls"));
		
		
		//Read the object named 'mario' from the tmx map. Read the starting position.
		MapObject mario = map.getLayers().get("objects").getObjects().get("mario");
		int marioX = (int) ((Float) mario.getProperties().get("x") * World.scale);
		int marioY = (int) ((Float) mario.getProperties().get("y") * World.scale);
		player = new Mario(this, marioX, marioY);
		
		stage = new Stage();
		goombas = generateEnemies();
		mushrooms = new Array<Mushroom>();
		generateBricks((TiledMapTileLayer) map.getLayers().get("walls"));
		
		generateFlag((MapLayer) map.getLayers().get("objects"));
		
		stage.addActor(player);
		String song = (String) map.getLayers().get("background").getObjects().get("background_image")
				.getProperties().get("audio");
		Audio.stopSong();
		Audio.playSong(song, true);
		wr = new WorldRenderer(this);
		wr.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void update() {
		//Check if the level has ended
		endLevel();
		
		Rectangle screen = rectPool.obtain();
		screen.set(wr.getCamera().position.x - wr.getCamera().viewportWidth/2, 
				wr.getCamera().position.y-wr.getCamera().viewportHeight/2,
				wr.getCamera().viewportWidth, wr.getCamera().viewportHeight);
		for(Goomba e : goombas) {
			if(screen.overlaps(e.rectangle())) {
				e.setMoving(true);
			}
			if(e.isDead()) {
				goombas.removeValue(e, true);
				stage.getActors().removeValue(e, true);
			}
		}
		
		for(Actor a : objectsToRemove) {
			stage.getActors().removeValue(a, true);
			objectsToRemove.removeValue(a, true);
		}
		
		rectPool.free(screen);
		stage.act(Gdx.graphics.getDeltaTime());
		if(player.isDead()) reset();
		
		wr.render();
	}
	
	/**
	 * Setup the flag at the end of the level
	 * @param layer Tmx map layer with the object named 'flag';
	 */
	private void generateFlag(MapLayer layer) {
		MapObject obj = layer.getObjects().get("flag");
		float x = (Float) obj.getProperties().get("x") * World.scale;
		float y = (Float) obj.getProperties().get("y") * World.scale;
		float width = Float.valueOf((String) obj.getProperties().get("width"));
		float height = Float.valueOf((String) obj.getProperties().get("height"));
		
		// The object in the map named 'flag_end' determines the position Mario walks to after the flag
		MapObject flag_end = layer.getObjects().get("flag_end");
		float flag_end_x = (Float) flag_end.getProperties().get("x") * World.scale;	
		float flag_end_y = (Float) flag_end.getProperties().get("y") * World.scale;	
		
		flag = new Flag(x, y, width, height, flag_end_x, flag_end_y);
		stage.addActor(flag);
	}
	
	/**
	 * Turn all bricks into actors.
	 * @param layer
	 */
	private void generateBricks(TiledMapTileLayer layer) {
		for (int x = 1; x < layer.getWidth(); x++) {
			for (int y = 1; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					TiledMapTile oldTile = cell.getTile();
					if(oldTile.getProperties().containsKey("actor")) {
						String type = (String) oldTile.getProperties().get("actor");
						StaticActor actor = null;
						if(type.equals("Brick") || type.equals("Bonus")) {
							//TODO add other colored bricks
							String color = (String) oldTile.getProperties().get("color");
							boolean destructable = false;
							if(oldTile.getProperties().containsKey("destructable")) {
								
								String destr = (String) oldTile.getProperties().get("destructable");
								destructable = destr.equals("true") ? true : false;
							}
							
							actor = new Brick(this, x, y, color, type.equals("Bonus"), destructable);
							itemsInBrick((Brick) actor, x, y);
						}
						layer.setCell(x, y, null);
						stage.addActor(actor);
					}
				}
			}
		}
	}
	
	/**
	 * Check if there are items in a brick, if there are they are added to the brick.
	 * @param brick
	 * @param x
	 * @param y
	 */
	private void itemsInBrick(Brick brick, int x, int y) {
		MapLayer layer = map.getLayers().get("hidden_items");
		MapObjects objects = layer.getObjects();
		for(MapObject obj : objects) {
			
			int obj_x = (int) ((Float) obj.getProperties().get("x") * (1/16f));
			int obj_y = (int) ((Float) obj.getProperties().get("y") * (1/16f));
			if(obj_x == x && obj_y == y) {
				String type = (String) obj.getProperties().get("type");
				Actor item = null;
				if(type.equals("super_mushroom")) {
					item = new Super(this, x, y, 4f);
					mushrooms.add((Mushroom) item);
				}
				stage.addActor(item);				
				brick.addItem(item);
			}
		}
	}
	
	/**
	 * @return All StaticActor classes. Bricks for example.
	 */
	public Array<StaticActor> getStaticActors() {
		Array<StaticActor> staticActors = new Array<StaticActor>();
		for(Actor a : stage.getActors()) {
			if(a instanceof StaticActor) {
				staticActors.add((StaticActor) a);
			}
		}
		return staticActors;
	}
	
	/**
	 * Make the tiles containing 'animation' key animated.
	 * @param layer
	 */
	private void animateTiles(TiledMapTileLayer layer) {
		for (int x = 1; x < layer.getWidth(); x++) {
			for (int y = 1; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					TiledMapTile oldTile = cell.getTile();
					if(oldTile.getProperties().containsKey("animation")) {
						String animation = (String) oldTile.getProperties().get("animation");
						float speed = 0.15f;
						if(oldTile.getProperties().containsKey("speed")) {
							speed = Float.parseFloat((String) oldTile.getProperties().get("speed"));
						}
						AnimatedTiledMapTile newTile = new AnimatedTiledMapTile(speed, 
								Tiles.getAnimatedTile(animation));
						newTile.getProperties().putAll(oldTile.getProperties());
						cell.setTile(newTile);
					}
				}
			}
		}
	}
	
	
	private void endLevel() {
		if(!level_ended && player.rect.overlaps(flag.rect())) {
			player.captureFlag(flag, flag.getEndX(), flag.getEndY());
			level_ended = true;
		}
		else if(reset_flag) {
			if(!Audio.currentSong.equals("finish")) Audio.stopSong();
			if(!playing_finish_song && !Audio.getSong().isPlaying()) {
				Audio.playSong("finish", false);
				playing_finish_song = true;
			}
			// If song stops playing, reset the level
			if(!Audio.getSong().isPlaying()) this.reset();
		}
	}
	
	public WorldRenderer getRenderer() {
		return wr;
	}
	
	public Mario getPlayer() {
		return player;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public Array<Goomba> getEnemies() {
		Array<Actor> actors = stage.getActors();
		Array<Goomba> enemies = new Array<Goomba>();
		for(Actor a : actors) {
			if(a instanceof Goomba) {
				enemies.add((Goomba) a);
			}
		}
		return enemies;
	}
	
	public Array<Mushroom> getMushrooms() {
		Array<Actor> actors = stage.getActors();
		Array<Mushroom> mushrooms = new Array<Mushroom>();
		for(Actor a : actors) {
			if(a instanceof Mushroom) {
				mushrooms.add((Mushroom) a);
			}
		}
		return mushrooms;
	}
	
	/**
	 * Tiles that have a 'texture' property will be using an optimized tileset. This is to avoid screen tearing.
	 * @param layer
	 */
	private void initTileset(TiledMapTileLayer layer) {
		ArrayMap<String, TextureRegion> textureArr = new ArrayMap<String, TextureRegion>();
		for(int x = 0; x < layer.getWidth(); x++) {
			for(int y = 0; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					TiledMapTile oldTile = cell.getTile();
					
					if(oldTile.getProperties().containsKey("texture")) {
						D.o("Initializing textures");
						String texture = (String) oldTile.getProperties().get("texture");
						if(textureArr.containsKey(texture)) {
							oldTile.getTextureRegion().setRegion(textureArr.get(texture));
						}
						else {
							TextureRegion t = Tiles.getTile(texture);
							textureArr.put(texture, t);
							oldTile.getTextureRegion().setRegion(t);
						}						
					}
				}
			}
		}
	}
	
	
	
	public Array<Rectangle> getTiles(int startX, int startY, int endX, int endY)
	{
		Array<Rectangle> tiles = new Array<Rectangle>();
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++)
		{
			for (int x = startX; x <= endX; x++)
			{
				Cell cell = layer.getCell(x, y);
				if (cell != null)
				{
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
		return tiles;
	}
	
	
	
	public void dispose() {
		map.dispose();
		tiles.dispose();
		player.dispose();
		for(Goomba g : getEnemies()) {
			g.dispose();
		}
		for(Mushroom m : getMushrooms()) {
			m.dispose();
		}
		wr.dispose();
		Audio.dispose();
		
	}



	public Stage getStage() {
		return stage;
	}

}
