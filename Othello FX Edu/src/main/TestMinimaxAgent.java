package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.AgentController.HeuristicType;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.utilities.GameTreeUtility;
import java.util.List;

public class TestMinimaxAgent extends Agent {
    private int maxDepth = 5; //maximum depth for search before cutoff
    private long maxTimeMillis = 5000; //maximum time for AI to make move

    public TestMinimaxAgent(String name) {
        super(name, PlayerTurn.PLAYER_ONE);
    }

    public TestMinimaxAgent(String name, PlayerTurn playerTurn) {
        super(name, playerTurn);
    }

    @Override
    public AgentMove getMove(GameBoardState state) {
        long startTime = System.currentTimeMillis(); //start counter for max time
        resetCounters(); //reset tree values

        ObjectiveWrapper bestMove = null; //keeps best move so far
        double bestValue = Double.NEGATIVE_INFINITY; //initial value for best value is the lowest possible

        List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(state, playerTurn); //gets all possible moves for current player

        if(moves.isEmpty()){
            return null;
        }

        for (ObjectiveWrapper move : moves) { //loops through all moves
            if (move == null) continue;

            GameBoardState childState = AgentController.getNewState(state, move); //creates new board based on the current move

            //uses alpha-beta pruning to evaluate move
            double value = alphaBeta(
                    childState, //new board
                    maxDepth - 1, //decreases depth with 1 for next move
                    Double.NEGATIVE_INFINITY, //alpha start-value
                    Double.POSITIVE_INFINITY, //beta start-value
                    false, //opponent (minimizing) player has next move
                    startTime //timer to check if max time has been reached
            );

            //checks if the value from the alpha-beta pruning is better than the saved value
            if (value > bestValue) {
                bestValue = value; //updates the best value if the new value is greater
                bestMove = move; //updates the best move with the current move
            }
        }

        //prints the status of the board after the move has been made
        if(bestMove != null) {
            GameBoardState newState = AgentController.getNewState(state, bestMove);
            System.out.println("Board after the best move:");
            System.out.println(newState);
        }

        System.out.println("Search Depth: " + maxDepth); //prints the depth of the search
        System.out.println("Nodes Examined: " + nodesExamined); //prints number of nodes examined

        return new MoveWrapper(bestMove); //returns best move (performs it on the board)
    }

    //TODO: add ++ to counters:
    //		searchDepth
    //		reachedLeafNodes
    //		nodesExamined
    //		prunedCounter
    //	}
    /**
     *
     * @param state The current board configuration.
     * @param depth How many moves ahead to explore.
     * @param alpha The best value found so far for the maximizer (upper bound for the min).
     * @param beta The best value found so far for the minimizer (lower bound for the max).
     * @param maximizingPlayer If it's your agent's turn, or the opponent.
     * @param startTime Used to stop the search if it exceeds the time limit.
     * @return
     */


    private double alphaBeta(GameBoardState state, int depth, double alpha, double beta, boolean maximizingPlayer, long startTime) {
        nodesExamined++; //Counts how many nodes we have evaluated

        // Cutoff if depth is 0, time exceeded, or the game is over
        if (depth == 0 || AgentController.isTerminal(state, playerTurn) || (System.currentTimeMillis() - startTime) > maxTimeMillis) {
            return AgentController.heuristicEvaluation(state, HeuristicType.DYNAMIC, playerTurn);
        }

        //Generate all legal moves for the current player
        List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(
                state,
                maximizingPlayer ? playerTurn : GameTreeUtility.getCounterPlayer(playerTurn)
        );

        if (maximizingPlayer) {
            double value = Double.NEGATIVE_INFINITY;
            for (ObjectiveWrapper move : moves) {
                if (move == null) continue;
                GameBoardState child = AgentController.getNewState(state, move);
                value = Math.max(value, alphaBeta(child, depth - 1, alpha, beta, false, startTime));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // beta cutoff
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (ObjectiveWrapper move : moves) {
                if (move == null) continue;
                GameBoardState child = AgentController.getNewState(state, move);
                value = Math.min(value, alphaBeta(child, depth - 1, alpha, beta, true, startTime));
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // alpha cutoff
            }
            return value;
        }
    }
}
