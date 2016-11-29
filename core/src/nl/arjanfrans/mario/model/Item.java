package nl.arjanfrans.mario.model;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class Item extends Actor {
	protected World world;
	
	public Item(World world, boolean visible) {
		this.world = world;
		this.setVisible(visible);
	}
	
	

}
