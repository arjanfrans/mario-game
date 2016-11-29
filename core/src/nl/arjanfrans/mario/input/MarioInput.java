package nl.arjanfrans.mario.input;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class MarioInput extends InputListener {

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		return super.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		super.touchUp(event, x, y, pointer, button);
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		return super.keyDown(event, keycode);
	}

	@Override
	public boolean keyUp(InputEvent event, int keycode) {
		return super.keyUp(event, keycode);
	}

}
