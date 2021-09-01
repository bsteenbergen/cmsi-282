package main.t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) {
        int[] bestMove = abpruning(state, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        T3Action bestAction = new T3Action(bestMove[1], bestMove[2], bestMove[3]);
        return bestAction;
    }
    
    /**
     * @param currentState The state from which to begin pruning
     * @param maximizingPlayer The player who's turn it is
     * @param alpha The max value to compare
     * @param beta The min value to compare
     * @return An int array containing the utility score and col, row, move of the 
     *         optimal turn to take
     */
    private int[] abpruning (T3State currentState, Boolean maximizingPlayer, int alpha, int beta) {
        int[] solution = {-1, -1, -1, -1};
        if (maximizingPlayer && currentState.isWin()) {
    	    return solution;
        }
        if (!maximizingPlayer && currentState.isWin()) {
            solution[0] = 1;
    	    return solution;
        }
        if (currentState.isTie()) {
            solution[0] = 0;
    	    return solution;
        }
    	
        if (maximizingPlayer) {
    	    int v = Integer.MIN_VALUE;
    	    T3Action bestAction = null;
    	    for (Map.Entry<T3Action, T3State> transition : currentState.getTransitions().entrySet()) {
    		    int childScore = abpruning(transition.getValue(), false, alpha, beta)[0];
    		    T3Action childAction = transition.getKey();
    			
    		    if (currentState.getNextState(childAction).isWin()) {
    		        int[] bestSolution = {1, childAction.col, childAction.row, childAction.move};
    			    return bestSolution;
    		    }
    	        if (childScore > v) {
    			    v = childScore;
    			    bestAction = transition.getKey();
    		    }
    		    alpha = Math.max(alpha, v);
    		    if (beta <= alpha) {
    		        break;
    		    }
    	    }
		    int[] bestSolution = {v, bestAction.col, bestAction.row, bestAction.move};
		    return bestSolution;
        }
        else {
            int v = Integer.MAX_VALUE;
    	    T3Action bestAction = null;
    	    for (Map.Entry<T3Action, T3State> transition : currentState.getTransitions().entrySet()) { 
    		    int childScore = abpruning(transition.getValue(), false, alpha, beta)[0];
    		    T3Action childAction = transition.getKey();
    		
    		    if (currentState.getNextState(childAction).isWin()) {
    			    int[] bestSolution = {1, childAction.col, childAction.row, childAction.move};
    			    return bestSolution;
    		    }
    		    if (childScore < v) {
    			    v = childScore;
    			    bestAction = transition.getKey();
    		    }    			
    		    beta = Math.min(beta, v);
    		    if (beta <= alpha) {
    		        break;
    		    }
    	    }
		    int[] bestSolution = {v, bestAction.col, bestAction.row, bestAction.move};
		    return bestSolution;
        }
    }
}

