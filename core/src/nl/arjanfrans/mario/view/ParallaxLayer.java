package nl.arjanfrans.mario.view;

import java.util.Iterator;

import nl.arjanfrans.mario.model.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * a layer from a parallax background
 * 
 * http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=1795
 * 
 * @author bitowl
 *
 */
public class ParallaxLayer {

	/**
	 * how much shall this layer (in percent) be moved if the whole background is moved
	 * 0.5f is half as fast as the speed
	 * 2.0f is twice the speed
	 */
	float ratioX, ratioY;

	/**
	 * current position
	 */
	float positionX, positionY;

	private World world;
	private Array<MapObject> layer_objects;
	private ArrayMap<String, Texture> layer_textures;
	private String layer_name;
	

	/**
	 * 
	 * @param pRegion
	 * @param pRatioX
	 * @param pRatioY
	 */
	public ParallaxLayer(World world, String layer_name, float pRatioX, float pRatioY) {
		this.world = world;
		this.layer_name = layer_name;
		layer_objects = new Array<MapObject>();
		layer_textures = new ArrayMap<String, Texture>();
		ratioX = pRatioX;
		ratioY = pRatioY;
		loadObjects();
	}
	
	/**
	 * Load the objects from the tmx file, convert them into textures and put them on the layer.
	 */
	private void loadObjects() {
		//TODO Use a spritesheet for the background objects
		Map map = world.getMap();
		Iterator<MapObject> it = map.getLayers().get(layer_name).getObjects().iterator();
		while(it.hasNext()) {
			MapObject obj = it.next();
			String file = "data/backgrounds/" + (String) obj.getProperties().get("src");
			layer_objects.add(obj);
			if(!layer_textures.containsKey(file)) {
				Texture texture = new Texture(file);
				texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				layer_textures.put(file, texture);
			}
		}
	}
	
	public ArrayMap<String, Texture> getLayerTextures() {
		return layer_textures;
	}
	
	public Array<MapObject> getLayerObjects() {
		return layer_objects;
	}

	/**
	 * move this layer
	 * @param pDelta
	 */
	protected void moveX(float pDelta) {
		positionX += pDelta * ratioX;
	}

	/**
	 * move this layer
	 * @param pDelta
	 */
	protected void moveY(float pDelta) {
		positionY += pDelta * ratioY;
	}
	
	public void dispose() {
		for(int i = 0; i < layer_textures.size; i++) {
			layer_textures.getValueAt(i).dispose();
		}
	}
}