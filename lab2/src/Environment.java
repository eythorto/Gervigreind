import java.util.*;

class Environment {
    // TODO: add other attributes that store necessary information about the environment
    // Information that is independent of the state of the environment should be here.
    private int width;
    private int height;
    
    public Environment(int width, int height, int nbDirts) {
        this.width = width;
        this.height = height;

        // TODO: randomly initialize an environment of the given size
        // That is, the starting position, orientation and position of the dirty cells should be (somewhat) random.
        // for example as shown here:
        
        // Generate all possible positions
        List<Position> allPositions = new ArrayList<>();
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                allPositions.add(new Position(x, y));
            }
        }

        Random rand = new Random();
        
        // Randomly choose a home location
        Position home = allPositions.get(rand.nextInt(allPositions.size()));

        // Randomly choose locations for dirt (sample without replacement)
        Collections.shuffle(allPositions);
        List<Position> dirts = new ArrayList<>();
        for (int i = 0; i < nbDirts && i < allPositions.size(); i++) {
            dirts.add(allPositions.get(i));
        }
    }

    public State getInitialState() {
        // TODO: return the initial state of the environment
        return new State(false);
    }

    public List<Action> getLegalActions(State state) {
        List<Action> actions = new ArrayList<>();
        // TODO: check conditions to avoid useless actions
        if (!state.turnedOn) {
            actions.add(new Action("TURN_ON"));
        } else {
            if (true) { // TODO: should be only possible when agent has returned home
                actions.add(new Action("TURN_OFF"));
            }
            if (true) { // TODO: should be only possible if there is dirt in the current position
                actions.add(new Action("SUCK"));
            }
            if (true) { // TODO: should be only possible when next position is inside the grid (avoid bumping in walls)
                actions.add(new Action("GO"));
            }
            actions.add(new Action("TURN_LEFT"));
            actions.add(new Action("TURN_RIGHT"));
        }
        return actions;
    }

    public State getNextState(State state, Action action) {
        // TODO: add missing actions
        String act = action.toString();
        
        if (act.equals("TURN_ON")) {
            return new State(true);
        } else if (act.equals("TURN_OFF")) {
            return new State(false);
        } else {
            throw new RuntimeException("Unknown or Unimplemented action " + act);
        }
    }

    public double getCost(State state, Action action) {
        // TODO: return correct cost of each action
        return 1.0;
    }

    public boolean isGoalState(State state) {
        // TODO: correctly implement the goal test
        return !state.turnedOn;
    }

    public static long expectedNumberOfStates(int width, int height, int nbDirts) {
        // TODO: return a reasonable upper bound on number of possible states
        return 2;
    }
}
