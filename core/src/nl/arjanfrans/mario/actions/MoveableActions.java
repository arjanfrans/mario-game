package nl.arjanfrans.mario.actions;

import nl.arjanfrans.mario.model.MovingActor;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class MoveableActions extends Actions {
	
	public static Action DieAction(Actor actor) {
		return new Die(actor);
	}
	
	static public class Die extends Action {
		private Actor actor;
		
		public Die(Actor actor) {
			this.actor = actor;
		}

		public boolean act(float delta) {
			((MovingActor) actor).setDead(true);
			return true;
		}
	}
	
	public static Action startMovingAction(Actor actor) {
		return new startMoving(actor);
	}
	
	static public class startMoving extends Action {
		public startMoving(Actor actor) {
			this.actor = actor;
		}

		public boolean act(float delta) {
			((MovingActor) actor).setMoving(true);
			return true;
		}
	}

}
