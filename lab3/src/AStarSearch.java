import java.util.List;
import java.util.PriorityQueue;

public class AStarSearch implements SearchAlgorithm {

	private int nbNodeExpansions;
	private int maxFrontierSize;

	private Heuristics heuristics;

	private Node goalNode;
	private long runtime;
	
	private static final long TIMEOUT_MILLIS = 900000; // 15 minutes

	public AStarSearch(Heuristics h) {
		this.heuristics = h;
	}

	@Override
	public void doSearch(Environment env) {
		heuristics.init(env);
		nbNodeExpansions = 0;
		maxFrontierSize = 0;
		goalNode = null;
		runtime = 0;
		long startTime = System.currentTimeMillis();

		// Priority queue for the frontier (min-heap based on node evaluation)
		PriorityQueue<Node> frontier = new PriorityQueue<>();
		
		// Create initial node
		State initialState = env.getCurrentState();
		int initialHeuristic = heuristics.eval(initialState);
		Node initialNode = new Node(initialState, initialHeuristic);
		
		frontier.add(initialNode);
		
		while (!frontier.isEmpty()) {
			// Check for timeout
			long elapsedTime = System.currentTimeMillis() - startTime;
			if (elapsedTime > TIMEOUT_MILLIS) {
				runtime = elapsedTime;
				System.out.println("Search timed out after " + (elapsedTime / 1000.0) + " seconds (15 minutes limit)");
				return;
			}
			
			// Track max frontier size
			if (frontier.size() > maxFrontierSize) {
				maxFrontierSize = frontier.size();
			}
			
			// Pop node with lowest f(n) = g(n) + h(n)
			Node currentNode = frontier.poll();
			nbNodeExpansions++;
			
			// Check if goal state
			if (env.isGoalState(currentNode.state)) {
				goalNode = currentNode;
				runtime = System.currentTimeMillis() - startTime;
				return;
			}
			
			// Expand node - generate successors
			List<Action> legalActions = env.legalMoves(currentNode.state);
			for (Action action : legalActions) {
				State nextState = env.getNextState(currentNode.state, action);
				int costToNext = env.getCost(currentNode.state, action);
				int gValue = currentNode.evaluation - heuristics.eval(currentNode.state) + costToNext;
				int hValue = heuristics.eval(nextState);
				int fValue = gValue + hValue;
				
				Node childNode = new Node(currentNode, nextState, action, fValue);
				frontier.add(childNode);
			}
		}
		
		runtime = System.currentTimeMillis() - startTime;
	}

	@Override
	public List<Action> getPlan() {
		if (goalNode == null) return null;
		else return goalNode.getPlan();
	}

	@Override
	public int getNbNodeExpansions() {
		return nbNodeExpansions;
	}

	@Override
	public int getMaxFrontierSize() {
		return maxFrontierSize;
	}

	@Override
	public int getPlanCost() {
		if (goalNode != null) return goalNode.evaluation;
		else return 0;
	}
	
	public long getRuntime() {
		return runtime;
	}

}
