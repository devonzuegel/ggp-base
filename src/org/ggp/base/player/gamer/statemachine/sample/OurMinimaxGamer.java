package org.ggp.base.player.gamer.statemachine.sample;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
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
public final class OurMinimaxGamer extends SampleGamer {
	/**
	 * This function is called at the start of each round
	 * You are required to return the Move your player will play
	 * before the timeout.	 */
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException	{
		// We get the current start time
		long start = System.currentTimeMillis();
//		System.out.print("sakfjhasklfjlsafjlaskjf;ksd");
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = bestMove(moves, getCurrentState());

		// We get the end time ... It is mandatory that stop<timeout
		long stop = System.currentTimeMillis();

		// leave this
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
	}

	// get the next possible states
	// return the move to the state that'll give the greatest possible points
	private Move bestMove(List<Move> moves, MachineState state) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		Move bestmove = moves.get(0);
//		int score = 0;
//		for (int i = 0; i < moves.size(); i++) {
//			Move move = moves.get(i);
//			int result = minscore(getRole(), move, state);
//			if (result > score)		{
//				score = result;
//				bestmove = move;
//			}
//		}
		return bestmove;
	}

	// find first player that is not us (first opponent)
	private Role get_first_opponent(Role my_role) {
		List<Role> all_roles = getStateMachine().getRoles();
		for (int i = 0; i < all_roles.size(); i++) {
			Role opponent = all_roles.get(i);
			if (!opponent.equals(my_role)) 	{
				System.out.print("opponent = " + opponent + "\n");
				return opponent;
			}
		}
		return all_roles.get(0); // this is just to make Java happy
	}

	private int minscore(Role my_role, Move move, MachineState state) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		// get list, loop thru all non-us roles and call min on those
//		System.out.print("\n" + my_role + "\n");

		Role opponent = get_first_opponent(my_role);
		List<Move> opps_moves = getStateMachine().getLegalMoves(getCurrentState(), opponent);
		int score = 100;
		for (int i = 0; i < opps_moves.size(); i++) {
			List<Move> moves = new ArrayList<Move>();
			if (my_role == getStateMachine().getRoles().get(0)) {
				moves.add(move);
				moves.add(opps_moves.get(i));
			} else {
				moves.add(opps_moves.get(i));
				moves.add(move);
			}
			MachineState newstate = getStateMachine().getNextState(state, moves);
			int result = maxscore(my_role, newstate);
			if (result < score)		score = result;
		}

		return score;
	}

	private int maxscore(Role my_role, MachineState state) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
//		return 100;
		if (getStateMachine().isTerminal(state))		return getStateMachine().getGoal(state, getRole());
		List<Move> my_moves = getStateMachine().getLegalMoves(state, my_role);
		int maxscore = 0;
		for (int i = 0; i < my_moves.size(); i++) {
			int result = minscore(my_role, my_moves.get(i), state);
			if (result > maxscore) 	maxscore = result;
		}
		return maxscore;
	}
}

// WANT:
// - get_opponents_role()
// - get next state from current move
// - is a state my_move + opp_move  >>  do we have to make it move from state to state?
// - ask about A-B

