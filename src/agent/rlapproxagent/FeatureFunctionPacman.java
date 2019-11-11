package agent.rlapproxagent;

import javafx.util.Pair;
import pacman.elements.ActionPacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import pacman.environnementRL.EnvironnementPacmanMDPClassic;
import environnement.Action;
import environnement.Etat;
import pacman.environnementRL.MazeSituation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Vecteur de fonctions caracteristiques pour jeu de pacman: 4 fonctions phi_i(s,a)
 *
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionPacman implements FeatureFunction{
	private static final int BIAIS = 0;
	private static final int NB_OF_NEAR_GHOSTS = 1;
	private static final int IS_PAC_DOT_SLOT = 2;
	private static final int DISTANCE_NEAREST_PAC_DOT = 3;
	private static final int DISTANCE_NEAREST_GHOST = 4;
	private static final int FEATURES_END = 5;


	private static int NBACTIONS = 4;//5 avec NONE possible pour pacman, 4 sinon
	//--> doit etre coherent avec EnvironnementPacmanRL::getActionsPossibles


	public FeatureFunctionPacman() {

	}

	@Override
	public int getFeatureNb() {
		return FEATURES_END;
	}

	@Override
	public double[] getFeatures(Etat e, Action a) {
		double[] vfeatures = new double[FEATURES_END];
		StateGamePacman stategamepacman ;
		//EnvironnementPacmanMDPClassic envipacmanmdp = (EnvironnementPacmanMDPClassic) e;

		//calcule pacman resulting position a partir de Etat e
		if (e instanceof StateGamePacman){
			stategamepacman = (StateGamePacman)e;
		}
		else{
			System.out.println("erreur dans FeatureFunctionPacman::getFeatures n'est pas un StateGamePacman");
			return vfeatures;
		}

		StateAgentPacman pacmanstate_next= stategamepacman.movePacmanSimu(0, new ActionPacman(a.ordinal()));

		//*** VOTRE CODE

		vfeatures[BIAIS] = 1;
		vfeatures[NB_OF_NEAR_GHOSTS] = computeNearGhosts(stategamepacman, pacmanstate_next) / 4.0;
		vfeatures[IS_PAC_DOT_SLOT] = stategamepacman.getMaze().isFood(pacmanstate_next.getX(), pacmanstate_next.getY()) ? 1 : 0;

		double gameSize = stategamepacman.getMaze().getSizeX() + stategamepacman.getMaze().getSizeY();
		vfeatures[DISTANCE_NEAREST_PAC_DOT] = stategamepacman.getClosestDot(pacmanstate_next) / gameSize;

		//vfeatures[DISTANCE_NEAREST_GHOST] = distanceNearestGhost(stategamepacman, pacmanstate_next) / gameSize;

		return vfeatures;
	}

	private static class Position {
		int x; int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Position position = (Position) o;
			return x == position.x &&
					y == position.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		public int getDistance() {
			return Math.abs(x) + Math.abs(y);
		}
	}

	private double distanceNearestGhost(StateGamePacman stategamepacman, StateAgentPacman pacmanstate_next) {
		int distance = Integer.MAX_VALUE;

		int x = pacmanstate_next.getX();
		int y = pacmanstate_next.getY();

		for (int i = 0 ; i != stategamepacman.getNumberOfGhosts() ; i++) {
			StateAgentPacman ghostState = stategamepacman.getGhostState(i);
			Position p = new Position(ghostState.getX() - x, ghostState.getY() - y);
			distance = Math.min(distance, p.getDistance());
		}

		return distance;
	}

	private double computeNearGhosts(StateGamePacman stategamepacman, StateAgentPacman pacmanstate_next) {
		Set<Position> positionsToConsider = new HashSet<>();

		int x = pacmanstate_next.getX();
		int y = pacmanstate_next.getY();
		positionsToConsider.add(new Position(x, y));
		positionsToConsider.add(new Position(x+1, y));
		positionsToConsider.add(new Position(x-1, y));
		positionsToConsider.add(new Position(x, y+1));
		positionsToConsider.add(new Position(x, y-1));

		int n = 0;

		for (int i = 0 ; i != stategamepacman.getNumberOfGhosts() ; i++) {
			StateAgentPacman ghostState = stategamepacman.getGhostState(i);

			Position p = new Position(ghostState.getX(), ghostState.getY());

			if (positionsToConsider.contains(p)) {
				n++;
			}
		}

		return n;
	}
}
