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
        env = new Environment(role, width, height, white_positions, black_positions);
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

			List<Environment.Move> moves = env.legalMoves(env.current_state);
						
			int randint = rand.nextInt(moves.size());
			Environment.Move randomMove = moves.get(randint);
			env.doMove(randomMove);
			return "(play " + (randomMove.fromX + 1) + " " + (randomMove.fromY + 1) + " " + (randomMove.toX + 1) + " " + (randomMove.toY + 1) + ")";
		} else {
			return "noop";
		}
	}

	// is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
		// TODO: cleanup so that the agent is ready for the next match
		// garbage collector in java - check it out
		// env = null; - clear environment
		// table.clear(); when we have transposition tables
	}

}
