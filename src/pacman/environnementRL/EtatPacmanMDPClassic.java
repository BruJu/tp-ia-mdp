package pacman.environnementRL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pacman.elements.ActionPacman;
import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;
/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire

 */
public class EtatPacmanMDPClassic implements Etat , Cloneable{
	private boolean[] positionsMurs;
	private List<Integer> xGhosts;
	private List<Integer> yGhosts;
	private int positionNearestDot;



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + positionNearestDot;
		result = prime * result + Arrays.hashCode(positionsMurs);
		result = prime * result + ((xGhosts == null) ? 0 : xGhosts.hashCode());
		result = prime * result + ((yGhosts == null) ? 0 : yGhosts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EtatPacmanMDPClassic other = (EtatPacmanMDPClassic) obj;
		if (positionNearestDot != other.positionNearestDot)
			return false;
		if (!Arrays.equals(positionsMurs, other.positionsMurs))
			return false;
		if (xGhosts == null) {
			if (other.xGhosts != null)
				return false;
		} else if (!xGhosts.equals(other.xGhosts))
			return false;
		if (yGhosts == null) {
			if (other.yGhosts != null)
				return false;
		} else if (!yGhosts.equals(other.yGhosts))
			return false;
		return true;
	}

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman){
		int xPacman = _stategamepacman.getPacmanState(0).getX();
		int yPacman = _stategamepacman.getPacmanState(0).getY();

		positionsMurs = identifyWalls(_stategamepacman);

		xGhosts = new ArrayList<>();
		yGhosts = new ArrayList<>();

		for (int i = 0 ; i != _stategamepacman.getNumberOfGhosts() ; i++) {
			StateAgentPacman ghost = _stategamepacman.getGhostState(i);

			int x = ghost.getX() - xPacman;
			int y = ghost.getY() - yPacman;


			if (x + y >= 5)
				continue;

			if (xGhosts.size() >= 5) {
				// Do not store more than 5 ghosts to have a limited number of states for QL Approx
				break;
			}

			xGhosts.add(x);
			yGhosts.add(y);
		}

		positionNearestDot = -1;

		int distanceNear = _stategamepacman.getClosestDot(_stategamepacman.getPacmanState(0));

		if (distanceNear == 1) {
			for (int i = 0 ; i != 4 ; i++) {

				int[] dest = _stategamepacman.getNextPosition(new ActionPacman(i), _stategamepacman.getPacmanState(0));

				if (_stategamepacman.getMaze().isFood(dest[0], dest[1])) {
					positionNearestDot = i;
					break;
				}
			}
		} else {
			positionNearestDot = -1;
		}
	}

	private boolean[] identifyWalls(StateGamePacman _stategamepacman) {
		StateAgentPacman pacman = _stategamepacman.getPacmanState(0);

		boolean[] walls = new boolean[4];

		for (int i = 0 ; i != 4 ; i++) {
			int[] dest = _stategamepacman.getNextPosition(new ActionPacman(i), pacman);

			walls[i] = !(dest[0] == pacman.getX() && dest[1] == pacman.getY());
		}

		return walls;
	}

	public int getDimensions() {
		// We build an upper bound of the dimensions because we didn't calculate the precise numbers of possible
		// ghost positions
		int positionsMursNbOfStates = 2*2*2*2;
		// (5 x coordinates * 5 y coordinates + eventually not here) * maximum 5 ghosts in state
		int numberOfGhostsPositions = (5 * 5 + 1) * 5;
		int numberOfDirections = 5;

		return positionsMursNbOfStates * numberOfGhostsPositions * numberOfDirections;
	}

	@Override
	public String toString() {

		return "";
	}


	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la
			// methode super.clone()
			clone = (EtatPacmanMDPClassic)super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}



		// on renvoie le clone
		return clone;
	}
}