package nl.arjanfrans.mario.debug;

import nl.arjanfrans.mario.MarioGame;

public class D {

	public static void o(String msg) {
		if(MarioGame.DEBUG) {
			System.out.println(msg);
		}
	}
	
	public static void o(float msg) {
		if(MarioGame.DEBUG) {
			System.out.println(msg);
		}
	}
}
