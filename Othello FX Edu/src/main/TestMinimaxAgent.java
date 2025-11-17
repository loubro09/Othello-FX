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

    private int maxDepth = 5;          // maximum depth for cutoff
    private long maxTimeMillis = 5000; // max 5 seconds per move

    public TestMinimaxAgent(String name) {
        super(name, PlayerTurn.PLAYER_ONE);
    }

    public TestMinimaxAgent(String name, PlayerTurn playerTurn) {
        super(name, playerTurn);
    }

    @Override
    public AgentMove getMove(GameBoardState gameState) {
        long startTime = System.currentTimeMillis();
        resetCounters();

        MoveWrapper bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        List<ObjectiveWrapper> moves = AgentController.getAvailableMoves(gameState, playerTurn);

        // Sort moves by path length (largest first)
        moves.sort((m1, m2) -> Integer.compare(m2.getPath().size(), m1.getPath().size()));

        for (ObjectiveWrapper move : moves) {
            if (move == null) continue; // safety check
            GameBoardState childState = AgentController.getNewState(gameState, move);
            double value = alphaBeta(childState, maxDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, startTime);
            if (value > bestValue) {
                bestValue = value;
                bestMove = new MoveWrapper(move); // wrap ObjectiveWrapper in MoveWrapper
            }
        }

        System.out.println("Search Depth: " + maxDepth);
        System.out.println("Nodes Examined: " + nodesExamined);
        System.out.println("Board Evaluation: " + bestValue);

        return bestMove;
    }


    private double alphaBeta(GameBoardState state, int depth, double alpha, double beta, boolean maximizingPlayer, long startTime) {
        nodesExamined++;

        // Cutoff if depth is 0, time exceeded, or terminal node
        if (depth == 0 || AgentController.isTerminal(state, playerTurn) || (System.currentTimeMillis() - startTime) > maxTimeMillis) {
            return AgentController.heuristicEvaluation(state, HeuristicType.DYNAMIC, playerTurn);
        }

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
