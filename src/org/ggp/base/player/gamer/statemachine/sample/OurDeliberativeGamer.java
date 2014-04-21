package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.util.statemachine.MachineState;
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
public final class OurDeliberativeGamer extends SampleGamer {

	private int moveNo = 1;

	/**
	 * This function is called at the start of each round
	 * You are required to return the Move your player will play
	 * before the timeout.	 */
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException	{
		// We get the current start time
		long start = System.currentTimeMillis();

		ArrayList<Move> movesToDo = new ArrayList<Move>();

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		System.out.print("\nStarting to find new move: "+ moveNo);
		Move selection = bestMove(movesToDo, moves, getCurrentState());
		System.out.print("\nCompleted Move number: "+ moveNo +"\n\n");
		moveNo++;

		// We get the end time ... It is mandatory that stop<timeout
		long stop = System.currentTimeMillis();

		// leave this
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
	}

	// get the next possible states
	// return the move to the state that'll give the greatest possible points
	private Move bestMove(ArrayList<Move> movesToDo, List<Move> moves, MachineState state) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		Move move = moves.get(0);
		int score = 0;
		for (int i = 0; i < moves.size(); i++) {

			List<Move> mvs = new ArrayList<Move>();
			mvs.add(moves.get(i));
			MachineState nextState = getStateMachine().getNextState(state, mvs);

			int result = maxScore(nextState, movesToDo);
			System.out.print("maxScore = "+ result);

			if (result == 100)	return move;
			if (result > score)	{
				score = result;
				move = moves.get(i);
			}
		}
		return move;
	}

	private int maxScore(MachineState state, ArrayList<Move> movesToDo) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		int highestscore = 0;

		// BASE CASE
		if (getStateMachine().isTerminal(state) ||  getStateMachine().getGoal(state, getRole()) == 100) {
			//System.out.print("---Hit the base case!!---\n");
			return getStateMachine().getGoal(state, getRole());
		}

		// RECURSIVE CALL
		List<MachineState> nextStates = getStateMachine().getNextStates(state);
		for (int i = 0; i < nextStates.size(); i++) {
			//System.out.print("i = " + i + "   score = " + highestscore + "\n");
			int result = maxScore(nextStates.get(i));
			if (result == 100)	return result;
			if (result > highestscore)	highestscore = result;
		}
		System.out.print("\n");
		return highestscore;
	}
}