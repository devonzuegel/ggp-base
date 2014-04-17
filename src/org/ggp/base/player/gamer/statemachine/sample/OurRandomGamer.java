package org.ggp.base.player.gamer.statemachine.sample;

import java.util.List;
import java.util.Random;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * SampleLegalGamer is a minimal gamer which always plays the first
 * legal move it identifies, regardless of the state of the game.
 *
 * For your first players, you should extend the class SampleGamer
 * The only function that you are required to override is :
 * public Move stateMachineSelectMove(long timeout)
 *
 */
public final class OurRandomGamer extends SampleGamer {
	/**
	 * This function is called at the start of each round
	 * You are required to return the Move your player will play
	 * before the timeout.
	 *
	 */
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException	{
		// We get the current start time
		long start = System.currentTimeMillis();

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());

		// SampleLegalGamer is very simple : it picks the first legal move
	    Random generator = new Random();
	    int randomIndex = generator.nextInt(moves.size());
		Move selection = moves.get(randomIndex);

		// We get the end time
		// It is mandatory that stop<timeout
		long stop = System.currentTimeMillis();


		// leave this
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
	}
}