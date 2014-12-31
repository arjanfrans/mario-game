package nl.arjanfrans.mario;

import nl.arjanfrans.mario.model.World;

import com.badlogic.gdx.Game;

/**
 * Super Mario Brothers like very basic platformer, using a tile map build via
 * <a href="http://www.mapeditor.org/">Tiled</a> and a tileset and sprites by <a
 * href="http://www.vickiwenderlich.com/">Vicky Wenderlich</a></p>
 * 
 * Shows simple platformer collision detection as well as on-the-fly map
 * modifications through destructable blocks!
 * 
 * @author mzechner
 * 
 */
public class MarioGame extends Game {
	private World world;
	public static final String VERSION = "0.01";
	public static final boolean DEBUG = true;
	public static final int FPS = 60;

	@Override
	public void create()
	{		
		world = new World();
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
		world.getRenderer().resize(width, height);
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		//super.render();

		world.update();
	}
}
