import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class SearchAgent implements Agent
{
	Random rand = new Random();
	private Random random = new Random();

	private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
	private int width, height; // dimensions of the board
	private long startTime; // Track when move calculation started

    private Environment env;
	
	/*
		init(String role, int playclock) is called once before you have to select the first action. Use it to initialize the agent. role is either "white" or "black" and playclock is the number of seconds after which nextAction must return.
	*/
    public void init(String role, int width, int height, int playclock, int[][] white_positions, int[][] black_positions) {
		System.out.println("Playing " + role + " on a " + width + "x" + height + " board with " + playclock + "s per move");
		System.out.println("White starting positions: " + Arrays.deepToString(white_positions));
		System.out.println("Black starting positions: " + Arrays.deepToString(black_positions));
		this.role = role;
		this.playclock = playclock;
		myTurn = !role.equals("white");
		this.width = width;
		this.height = height;
		// TODO: add your own initialization code here
		// Game always starts with white to move, regardless of which agent this is
        env = new Environment(State.Role.WHITE,width, height, white_positions, black_positions);
    }

	// lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    public String nextAction(int[] lastMove) {
		// TODO: time limit
		// TODO: return "NOOP" if not your players turn
    	if (lastMove != null) {
    		int x1 = lastMove[0], y1 = lastMove[1], x2 = lastMove[2], y2 = lastMove[3];
    		String roleOfLastPlayer;
    		if (myTurn && role.equals("white") || !myTurn && role.equals("black")) {
    			roleOfLastPlayer = "white";
    		} else {
    			roleOfLastPlayer = "black";
    		}
   			System.out.println(roleOfLastPlayer + " moved from " + x1 + "," + y1 + " to " + x2 + "," + y2);
    		// TODO: 1. update your internal world model according to the action that was just executed
			env.doMove(new Environment.Move(x1-1, y1-1, x2-1, y2-1));
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
		myTurn = !myTurn;
		if (myTurn) {
			// TODO: 2. run alpha-beta search to determine the best move

			// Here we just construct a random move (that will most likely not even be possible),
			// this needs to be replaced with the actual best move.

			// List<Environment.Move> moves = env.legalMoves(env.current_state);
			// System.out.println("Color to move: " + env.current_state.role);
			// for (Environment.Move m : moves) {
			// 	System.out.println("Legal move: " + (m.fromX + 1) + "," + (m.fromY + 1) + " to " + (m.toX + 1) + "," + (m.toY + 1));
			// }
						
		// Use minimax search to find the best move
		startTime = System.currentTimeMillis(); // Start timing
		
		State.Square[][] copy = new State.Square[this.width][this.height];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				copy[i][j] = env.current_state.board[i][j];
			}
		}
		State.Role current_player = env.current_state.role == State.Role.WHITE ? State.Role.WHITE : State.Role.BLACK;
		State state_copy = new State(copy, current_player); // make a copy of the current state to run the search on, so we don't mess with our actual state
		int depth = 1;
		Environment.Move bestMove = null;
		try{
			while (depth <= (this.width * this.height)) {  // iterative deepening
				bestMove = minimaxRoot(state_copy, depth, -100, 100);
				depth++;
			}
		}
		catch (Exception e) {}
		return "(play " + (bestMove.fromX + 1) + " " + (bestMove.fromY + 1) + " " + (bestMove.toX + 1) + " " + (bestMove.toY + 1) + ")";
		} else {
			return "noop";
		}
	}

	public Environment.Move minimaxRoot(State state, int depth, int alpha, int beta) throws Exception {
		// the search should start in the root - its possible to prune from the root node
		// if timeIsUp throw Exception() -> don't return anything! Stop the time a few seconds earlier than we need, so we don't run out of time
		long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // Convert to seconds
		if (elapsedTime >= playclock - 2) // Leave 2 second buffer
			throw new Exception("Time is up!"); 
		int best_value = -101;  // Start with worst possible value
		Environment.Move best_move = null;
		List<Environment.Move> moves = env.legalMoves(state);
		for (Environment.Move m : moves) {
			env.doMoveForState(state, m);
			int value = -minimax(state, depth - 1, -beta, -alpha);
			//System.out.println("Depth: " + depth + " Move: " + m + " Value: " + value + " Alpha: " + alpha + " Beta: " + beta); // for debugging, take this out later when we have larger spaces 
			if (value > best_value){
				best_value = value;
				best_move = m;
				// for pruning
				if (best_value > alpha) {
					alpha = best_value;
					if (alpha >= beta) {
						env.undoMoveForState(state, m);
						break;
					}
				}
			}
			env.undoMoveForState(state, m);
		}
		return best_move;
	}

	public int minimax(State state, int depth, int alpha, int beta) throws Exception {
		long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // Convert to seconds
		if (elapsedTime >= playclock - 2) // Leave 2 second buffer
			throw new Exception("Time is up!"); // for time limit, catch this in the minimaxRoot and return the best move found so far
		if (env.isTerminal(state) || depth <= 0){ //depth limit == 0, reached end of depth
			return env.evaluationFunction(state);
		}
		else{
			int best_value = -101;
			List<Environment.Move> moves = env.legalMoves(state);
			for (Environment.Move m : moves) {
				env.doMoveForState(state, m);
				int value = -minimax(state, depth - 1, -beta, -alpha);
				//System.out.println("Depth: " + depth + " Move: " + m + " Value: " + value + " Alpha: " + alpha + " Beta: " + beta); // for debugging, take this out later when we have larger spaces 
				if (value > best_value){
					best_value = value;
					// for pruning
					// if alpha >= beta break the loop - 
					if (best_value > alpha) {
						alpha = best_value;
						if (alpha >= beta) {
							env.undoMoveForState(state, m);
							break;
						}
					}
				}
				env.undoMoveForState(state, m);
			}
			return best_value;
		}
	}


	// TODO: Add pruning and changable depth + timestamp stuff
	// for now try implementing this without pruning and without iterative depth - to test
	// if search is suspiciously fast - check if pruning is right, draw it out on paper and compare
	// try to minimize running the legal_moves function, since that takes the longest time



	// ##########################################################3

	// is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
		// TODO: cleanup so that the agent is ready for the next match
		// garbage collector in java - check it out
		// env = null; - clear environment
		// table.clear(); when we have transposition tables
	}

}
