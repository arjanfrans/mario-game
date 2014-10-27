package nl.arjanfrans.mario.actions;

import nl.arjanfrans.mario.actions.MarioActions.stopImmume;
import nl.arjanfrans.mario.model.Mario;
import nl.arjanfrans.mario.model.World;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ActorActions extends Actions {


	static public class removeActor extends Action {
		public removeActor(Actor actor) {
			this.actor = actor;
		}

		public boolean act(float delta) {
			World.objectsToRemove.add(actor);
			return true;
		}
	}

	
}
