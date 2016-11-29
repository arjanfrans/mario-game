package nl.arjanfrans.mario;

import com.badlogic.gdx.Game;
import nl.arjanfrans.mario.model.World;

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
