package nl.arjanfrans.mario.view;

import nl.arjanfrans.mario.model.Mario;
import nl.arjanfrans.mario.model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class WorldRenderer {
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;

	private World world;
	private Mario player;

	//private Color background_color;
	private Texture background_image;
	private Stage stage;
	private ParallaxBackground parallax_bg;

	private static final int VIRTUAL_WIDTH = 512;
	private static final int VIRTUAL_HEIGHT = 448;
	private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	private Rectangle viewport;

	public WorldRenderer(World world) {
		this.world = world;
		//this.background_color = getBackgroundColor();
		background_image = loadBackground();
	
		player = world.getPlayer();

		renderer = new OrthogonalTiledMapRenderer(world.getMap(), 1 / 16f);

		stage = world.getStage();
		//TODO check if works
	


		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		camera.setToOrtho(false, 16, 12);
		camera.update();
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

		stage.setViewport(new StretchViewport(16, 12, camera));
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		ParallaxLayer l1 = new ParallaxLayer(world, "far_background", 0.8f,0);
		ParallaxLayer l2 = new ParallaxLayer(world, "middle_background", 0.5f,0);
		ParallaxLayer l3 = new ParallaxLayer(world, "front_background", 0.3f,0);
		ParallaxLayer[] layers ={l1, l2, l3};
		parallax_bg= new ParallaxBackground(world, layers, camera, (SpriteBatch) renderer.getSpriteBatch());

	}

	public void resize(int width, int height) {
		// calculate new viewport
		float aspectRatio = (float)width/(float)height;
		float scale = 1f;
		Vector2 crop = new Vector2(0f, 0f);
		if(aspectRatio > ASPECT_RATIO)
		{
			scale = (float)height/(float)VIRTUAL_HEIGHT;
			crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
		}
		else if(aspectRatio < ASPECT_RATIO)
		{
			scale = (float)width/(float)VIRTUAL_WIDTH;
			crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
		}
		else
		{
			scale = (float)width/(float)VIRTUAL_WIDTH;
		}

		float w = (float)VIRTUAL_WIDTH*scale;
		float h = (float)VIRTUAL_HEIGHT*scale;
		viewport = new Rectangle(crop.x, crop.y, w, h);
	}

//	private Color getBackgroundColor() {
//		String bg = (String) world.getMap().getProperties().get("backgroundcolor");
//		bg = bg.substring(1);
//		if(bg.startsWith("0x"))
//			bg = bg.substring(2);
//
//		return colorFromHex(Long.parseLong(bg, 16));       
//	}

//	private Color colorFromHex(long hex) {
//		float a = (hex & 0xFF000000L) >> 24;
//		float r = (hex & 0xFF0000L) >> 16;
//		float g = (hex & 0xFF00L) >> 8;
//		float b = (hex & 0xFFL);
//
//		return new Color(r/255f, g/255f, b/255f, a/255f);
//	}

	private Texture loadBackground() {
		String name = (String) world.getMap().getLayers().get("background").getObjects().get("background_image")
				.getProperties().get("src");

		Texture texture = new Texture("data/backgrounds/" + name + ".png");

		texture.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		return texture;
	}

	private void drawBackground(SpriteBatch batch, float posX, float posY) {
		batch.begin();
		Map map = world.getMap();
		int width = (Integer) map.getProperties().get("width");
		//getTexture("big_mountain").getWidth() * 
		batch.draw(background_image , -16 ,0 , width * 16, 16);
		batch.end();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void render()
	{
		if(!world.getMap().equals(renderer.getMap())) renderer.setMap(world.getMap());

		// clear the screen
		//Gdx.gl.glClearColor(background_color.r, background_color.g, background_color.b, background_color.a);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		

		// let the camera follow the player, x-axis only
		camera.position.x = player.getX();
		camera.update();

		// set viewport
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);


		drawBackground((SpriteBatch) renderer.getSpriteBatch(), camera.position.x, 0);
		//drawFarBackground(renderer.getSpriteBatch(), camera.position.x, 0);
		//parallax_bg.moveX(Gdx.graphics.getDeltaTime(), camera.position.x); // move to the right to show the effect

		parallax_bg.moveX(player.getVelocity().x*Gdx.graphics.getDeltaTime());
		parallax_bg.render();


		// set the tile map rendere view based on what the
		// camera sees and render the map
		renderer.setView(camera);
		renderer.render();

		stage.draw();

		//		renderer.getSpriteBatch().begin();
	//		renderer.getSpriteBatch().draw(player.getAnimation().getKeyFrame(0).getTexture(), camera.position.x - camera.viewportWidth/2, 
		//				camera.position.y-camera.viewportHeight/2,
		//				camera.viewportWidth, camera.viewportHeight);
		//		renderer.getSpriteBatch().end();

	}

	public void dispose() {
		stage.dispose();
		renderer.dispose();
		parallax_bg.dispose();
	}

}
