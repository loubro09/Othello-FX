package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;

public class TestMinimaxAgent extends Agent {
    public TestMinimaxAgent(PlayerTurn playerTurn) {
        super(playerTurn);
    }

    public TestMinimaxAgent(String agentName) {
        super(agentName);
    }

    public TestMinimaxAgent(String agentName, PlayerTurn playerTurn) {
        super(agentName, playerTurn);
    }

    @Override
    public AgentMove getMove(GameBoardState gameState) {
        return null;
    }
}
