package pacman.environnementRL;

import javafx.util.Pair;
import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MazeSituation {
	public int[] adjacentSlots; // Indication of what is in the adjacent slot
	public int[] numberOfNearDots; // Number of nears dots in a direction. 0 = none, 1 = between 1 and 5, 2 = more
	public int directionNearestGhost;
	public boolean[] monstersInDirection; // Same as numberOfNearDots but 0 = none, 1 = there are
	public boolean[] numberOfFarAwayDots; // 0 = none, 1 = there are



	public MazeSituation(StateGamePacman _stategamepacman, StateAgentPacman pacmanState) {

		if (pacmanState == null)
			pacmanState = _stategamepacman.getPacmanState(0);

		int x = pacmanState.getX();
		int y = pacmanState.getY();

		MazePacman maze = _stategamepacman.getMaze();

		Set<Pair<Integer, Integer>> positionsMonsters = getPositionsMonsters(_stategamepacman);

		buildAdjacentSlots(x, y, maze, positionsMonsters);
		buildNumberOfNear(x, y, maze, positionsMonsters);
	}

	private void buildNumberOfNear(int pacX, int pacY, MazePacman maze, Set<Pair<Integer,Integer>> positionsMonsters) {
		numberOfNearDots = new int[4];
		directionNearestGhost = 0;
		int maxDistance = Integer.MAX_VALUE;
		monstersInDirection = new boolean[4];
		numberOfFarAwayDots = new boolean[4];

		for (int x = 0 ; x != maze.getSizeX() ; x++) {
			for (int y = 0 ; y != maze.getSizeY() ; y++) {
				int type = convertPositionIntoObject(maze, positionsMonsters, x, y);
				if (type == 0)
					continue;

				int distance = computeDistance(pacX, pacY, x, y);
				int direction = getDirection(pacX, pacY, x, y);

				if (distance <= 1) {
					continue;
				} else if (distance <= 5) {
					if (type == GHOST) {
						if (maxDistance > distance) {
							maxDistance = distance;
							directionNearestGhost = direction;
						}

						monstersInDirection[direction] = true;
					} else if (type == DOT) {
						numberOfNearDots[direction]++;
					}
				} else {
					if (type == DOT) {
						numberOfFarAwayDots[direction] = true;
					}
				}
			}
		}
	}

	private int computeDistance(int pacX, int pacY, int x, int y) {
		return Math.abs(x - pacX) + Math.abs(y - pacY);
	}

	private int getDirection(int pacX, int pacY, int x, int y) {
		return getDirection(x - pacX, y - pacY);
	}

	private int getDirection(int x, int y) {
		if (Math.abs(x) < Math.abs(y)) {
			if (y < 0) {
				return 0;
			} else {
				return 2;
			}
		} else {
			if (x < 0) {
				return 1;
			} else {
				return 3;
			}
		}
	}


	private void buildAdjacentSlots(int x, int y, MazePacman maze, Set<Pair<Integer, Integer>> positionsMonsters) {
		adjacentSlots = new int[4];
		adjacentSlots[0] = convertPositionIntoObject(maze, positionsMonsters, x, y - 1);
		adjacentSlots[1] = convertPositionIntoObject(maze, positionsMonsters, x, y + 1);
		adjacentSlots[2] = convertPositionIntoObject(maze, positionsMonsters, x - 1, y);
		adjacentSlots[3] = convertPositionIntoObject(maze, positionsMonsters, x + 1, y);
	}

	public static Set<Pair<Integer, Integer>> getPositionsMonsters(StateGamePacman _stategamepacman) {
		Set<Pair<Integer, Integer>> positions = new HashSet<>();
		
		for (int i = 0 ; i != _stategamepacman.getNumberOfGhosts() ; i++) {
			StateAgentPacman ghostState = _stategamepacman.getGhostState(i);
			positions.add(new Pair<>(ghostState.getX(), ghostState.getY()));
		}

		return positions;
	}


	public static final int GHOST = 1;
	public static final int DOT = 2;
	public static final int WALL = 3;

	public static int convertPositionIntoObject(MazePacman maze, Set<Pair<Integer, Integer>> monsters, int x, int y) {
		if (maze.isFood(x, y) || maze.isCapsule(x, y)) {
			return DOT;
		} else if (maze.isWall(x, y)) {
			return WALL;
		} else if (monsters.contains(new Pair<>(x, y))) {
			return GHOST;
		} else {
			return 0;
		}
	}



}
