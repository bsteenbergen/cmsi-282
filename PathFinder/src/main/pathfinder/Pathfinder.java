package main.pathfinder;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements A* graph search for the Muddy Maze
 * Pathfinding Problems with Locked Goals.
 */
public abstract class Pathfinder {
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return A List of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static List<String> solve (MazeProblem problem) {
    	if (problem.getGoal() == null) {
    		return null;
    	}

    	int minCost = Integer.MAX_VALUE;
    	List<String> minPath = null;
    	for (MazeState key : problem.getKeys()) {        	
        	SearchTreeNode firstGoal = getGoal(problem, problem.getInitial(), key);
        	SearchTreeNode secondGoal = getGoal(problem, key, problem.getGoal());
        	
        	if (firstGoal == null || secondGoal == null) {
        		return null;
        	}
        	
        	List<String> newPath = new ArrayList<String>();
    		List<String> initialToKey = pathBuilder(firstGoal);
    		List<String> keyToGoal = pathBuilder(secondGoal);
    		
        	newPath.addAll(initialToKey);
        	newPath.addAll(keyToGoal);
        	
        	int newCost = firstGoal.pathCost + secondGoal.pathCost;

			if (newCost < minCost) {
				minCost = newCost;
				minPath = newPath;
			}
    	}
		return minPath;
    }
    
    /**
     * Given a Maze problem, a starting MazeState, and goal MazeState this method finds the 
     * SearchTreeNode of the goal and returns that node or null if it does not exist or can
     * not be reached.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @param initial The starting MazeState of the node to start at.
     * @param goal The MazeState of the node which is to be found.
     * @return A SearchTreeNode that corresponds to the goal.
     */
    private static SearchTreeNode getGoal (MazeProblem problem, MazeState initial, MazeState goal) {
        Queue<SearchTreeNode> frontier = new PriorityQueue<>();
        Set<MazeState> graveyard = new HashSet<>();
        
        frontier.add(new SearchTreeNode(initial, null, null, 0, 0));
        
        while (!frontier.isEmpty()) {
        	SearchTreeNode top = frontier.poll();
    		if (top.state.equals(goal)) {
    			return top;
    		}
    		graveyard.add(top.state);
    		
    		for (Map.Entry<String, MazeState> subMap : problem.getTransitions(top.state).entrySet()) {
        		int newCost = problem.getCost(subMap.getValue());
        		int totalCost = newCost + top.pathCost + manhattanDist(top.state, problem.getGoal());
        		SearchTreeNode nextNode = new SearchTreeNode(subMap.getValue(), subMap.getKey(), top, totalCost, newCost + top.pathCost);
        		
        		if (!graveyard.contains(nextNode.state)) {
        			frontier.add(nextNode);
        		}
    		}
        }
        return null;
    }
    
    /**
     * A method that builds a List containing the path of moves that were made in 
     * order to reach the goal.
     * 
     * @param current The SearchTreeNode that the path is to be built from.
     * @return A list of strings containing the moves that were made to get
     * 		   to the SearchTreeNode that is specified.
     */
    private static List<String> pathBuilder (SearchTreeNode current) {
        ArrayList<String> path = new ArrayList<>();
        
		while (current.parent != null) {
			path.add(0, current.action);
			current = current.parent;
		}
		return path;
    }
    
    /**
     * A method that calculates the manhattan distance from the desired state to
     * the goal state.
     * 
     * @param current The MazeState to start at.
     * @param goal The MazeState to end at.
     * @return The manhattan distance from the starting state to the ending state.
     */
    private static int manhattanDist (MazeState current, MazeState goal) {
    	return Math.abs(current.col - goal.col) + Math.abs(current.row - goal.row);
    }
    
    /**
     * SearchTreeNode private static nested class that is used in the Search algorithm to 
     * construct the Search tree.
     */
    private static class SearchTreeNode implements Comparable<SearchTreeNode> {
        
        MazeState state;
        String action;
        SearchTreeNode parent;
        int heurCost;
        int pathCost;
        
        /**
         * Constructs a new SearchTreeNode to be used in the Search Tree.
         * 
         * @param state The MazeState (row, col) that this node represents.
         * @param action The action that *led to* this state / node.
         * @param parent Reference to parent SearchTreeNode in the Search Tree.
         */
        SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int heurCost, int pathCost) {
            this.state = state;
            this.action = action;
            this.parent = parent;
            this.heurCost = heurCost;
            this.pathCost = pathCost;
        }
        
        /**
         * Override method that returns a negative number, 0, or a positive number depending
         * on which node has the greater heuristic cost. Used to determine which node has 
         * higher priority in the queue.
         *  
         * @return A negative number, 0, or a positive number.
         */
        @Override
        public int compareTo (SearchTreeNode other) {
        	return this.heurCost - other.heurCost;
        }       
    }   
}

