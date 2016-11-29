package nl.arjanfrans.mario.view;

import java.util.Iterator;

import nl.arjanfrans.mario.debug.D;
import nl.arjanfrans.mario.model.World;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;

/**
 * a parallax background
 * 
 * http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=1795
 * 
 * @author bitowl
 *
 */
public class ParallaxBackground {

	/**habe ich mir auch mal so zwei Klassen ParallaxLayer und ParallaxBackground geschrieben.
	 * the layers of this background
	 */
	private ParallaxLayer[] layers;
	/**
	 * the camera 
	 */
	private Camera camera;
	/**
	 * sprite batch
	 */
	private SpriteBatch batch;
	private World world;

	/**
	 * create a background
	 * @param pLayers
	 * @param pCamera your camera, so you can define whatever you want :P 
	 * @param pBatch your batch, so we do not have to use more than necessary
	 */
	public ParallaxBackground(World world, ParallaxLayer[] pLayers, Camera pCamera,
			SpriteBatch pBatch) {
		this.world = world;
		layers = pLayers;
		camera = pCamera;
		batch = pBatch;
	}


	
	/**
	 * render the parallax background
	 */
	public void render() {
		batch.begin();
		for (ParallaxLayer layer : layers) {
			drawLayer(layer, batch);
		}
		batch.end();
	}
	
	private void drawLayer(ParallaxLayer layer, SpriteBatch batch) {
		Iterator<MapObject> it = layer.getLayerObjects().iterator();
		while(it.hasNext()) {
			MapObject obj = it.next();
			float x = (float) ((Float) obj.getProperties().get("x") * 1/16f);
			float y = (float) ((Float) obj.getProperties().get("y") * 1/16f);
			
			String file = "data/backgrounds/" + (String) obj.getProperties().get("src");
			Texture texture = layer.getLayerTextures().get(file);
			batch.draw(layer.getLayerTextures().get(file), x + layer.positionX,
					y + layer.positionY, texture.getWidth() * 1/16f, texture.getHeight() * 1/16f);
		}
	}
	

	/**
	 * move the parallax background on the x-axis
	 * @param pDelta
	 */
	public void moveX(float pDelta) {
		for (ParallaxLayer layer : layers) {
			layer.moveX(pDelta);
		}
	}

	/**
	 * move the parallax background on the y-axis
	 * @param pDelta
	 */
	public void moveY(float pDelta) {
		for (ParallaxLayer layer : layers) {
			layer.moveY(pDelta);
		}
	}



	public void dispose() {
		for(int i = 0; i < layers.length; i++) {
			layers[i].dispose();
		}
	}
}