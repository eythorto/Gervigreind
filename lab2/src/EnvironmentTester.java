import java.util.*;
import java.util.stream.Collectors;

public class EnvironmentTester {

    // Helper class to return multiple values from bfsTraverse
    public static class BfsResult {
        public List<State> states;
        public double branchingFactor;

        public BfsResult(List<State> states, double branchingFactor) {
            this.states = states;
            this.branchingFactor = branchingFactor;
        }
    }

    /**
     * Returns a list of all reachable states in the environment and the average branching factor.
     */
    public static BfsResult bfsTraverse(Environment env, long expectedNbStates) {
        State initialState = env.getInitialState();
        
        // Java's HashSet/HashMap relies on .hashCode() and .equals()
        Set<State> visitedSet = new HashSet<>();
        visitedSet.add(initialState);
        
        // To maintain insertion order for list output (similar to Python's dict keys sometimes), 
        // we can track the list separately or just convert the set later. 
        // BFS requires a queue (FIFO), Python used a list as a stack (LIFO) via pop(), 
        // but typically BFS uses a Queue. The Python code `pop()` implies DFS behavior 
        // unless open_list was treated as a queue elsewhere. 
        // *Correction*: Python `list.pop()` removes the *last* item (stack/DFS). 
        // `list.pop(0)` is queue/BFS. The function name is bfs_traverse, but `pop()` makes it DFS.
        // I will implement standard BFS using a Queue or DFS using a Stack based on exact Python syntax translation.
        // Python: open_list.pop() -> Last In First Out (Stack/DFS).
        
        Stack<State> openList = new Stack<>();
        openList.push(initialState);

        double branchingFactor = 0;
        int n = 0;
        int nVisited = 0;

        while (!openList.isEmpty()) {
            if (nVisited > expectedNbStates) {
                System.out.println("ERROR: generating more states than expected!");
                System.out.println("Either identical states are not detected or the expected number is wrong.");
                System.out.println("Stopping state generation before all states are found!\n");
                break;
            }

            if (nVisited > 0 && nVisited % 10000 == 0) {
                System.out.printf("generated %d states (%.0f%% of %d expected) and going ...%n", 
                    nVisited, 100.0 * nVisited / expectedNbStates, expectedNbStates);
            }

            State state = openList.pop();

            if (!env.isGoalState(state)) {
                List<Action> actions = env.getLegalActions(state);
                n++;
                
                // Iterative average formula
                branchingFactor += 1.0 / n * (actions.size() - branchingFactor);

                for (Action a : actions) {
                    State nextState = env.getNextState(state, a);
                    if (!visitedSet.contains(nextState)) {
                        visitedSet.add(nextState);
                        nVisited++;
                        openList.push(nextState);
                        // System.out.println("" + nVisited + ": " + nextState);
                    }
                }
            }
        }
        return new BfsResult(new ArrayList<>(visitedSet), branchingFactor);
    }

    public static void runSimulation(Environment env, int nbSteps) {
        State state = env.getInitialState();
        List<Action> actions = env.getLegalActions(state);

        if (actions.isEmpty()) {
            System.out.println("ERROR: initial state " + state + " has no legal actions");
            return;
        }

        boolean terminal = false;
        int step = 0;
        double pathCost = 0;
        Random random = new Random();

        while (!terminal && step < nbSteps) {
            System.out.println("Step " + step);
            System.out.println("  state: " + state);
            System.out.println("  state has " + actions.size() + " legal actions: " + actions);

            Action action = actions.get(random.nextInt(actions.size()));
            System.out.println("  chosen action: " + action);

            double cost = env.getCost(state, action);
            pathCost += cost;
            System.out.println("  cost: " + cost);

            state = env.getNextState(state, action);
            System.out.println("  next state: " + state);

            if (env.isGoalState(state)) {
                System.out.println("Goal state found! Path cost: " + pathCost);
                terminal = true;
            } else {
                actions = env.getLegalActions(state);
                if (actions.isEmpty()) {
                    System.out.println("Dead end found! (state with no legal actions)");
                    terminal = true;
                }
            }
            System.out.println("");
            step++;
        }
    }

    // Helper to estimate object size
    public static long getObjectSize(Object o) {
        return JavaSize.getSize(o);
    }
    
    /**
     * checks how many states have the same hash value and how many will be put in the buckets with other states
     *
     * prints the result on stdout
     */
    public static void checkForCollisions(List<State> states) {
        System.out.println();
        int nbStates = states.size();
        Set<Integer> uniqueHashes = new HashSet<>();
        List<Integer> hashes = new ArrayList<>();

        for (State s : states) {
            int h = s.hashCode();
            hashes.add(h);
            uniqueHashes.add(h);
        }

        System.out.printf("%d unique hashes (%.1f%% hash collisions)%n", 
            uniqueHashes.size(), 100.0 * (1 - (double)uniqueHashes.size() / nbStates));

        // size of the bucket table is first power of two, that is larger than #states*4/3, because
        // that is how resize of java.util.HashMap works
        int tableSize = 1 << (int)Math.ceil(Math.log(nbStates * 4 / 3.0) / Math.log(2));
        
        Set<Integer> uniqueHashIndices = new HashSet<>();
        for (int h : hashes) {
            uniqueHashIndices.add(bucketIndex(h, tableSize));
        }
        
        System.out.printf("%d unique hash indizes (%.1f%% index collision)%n", 
            uniqueHashIndices.size(), 100.0 * (1 - (double)uniqueHashIndices.size() / nbStates));
    }

    /**
     * compute the index of the bucket for a key with the given hash code in a hash table of the given size
     */
    private static int bucketIndex(int hash, int tableSize) {
        // JDK up to 5 and 8 and above use the following:
        return (hash ^ (hash >>> 16)) & (tableSize-1);
        // The following was used in JDK 6 and 7:
        // copied from http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/util/HashMap.java#HashMap.hash%28int%29
        // hash ^= (hash >>> 20) ^ (hash >>> 12);
        // hash ^= (hash >>> 7) ^ (hash >>> 4);
        // return hash & (tableSize-1);
        
    }

    public static void main(String[] args) {
        // Argument parsing manually since Java doesn't have 'argparse' built-in
        int simulateSteps = -1; // -1 indicates --simulate was not used
        int width = 5;
        int height = 5;
        int nbDirts = 5;

        // A list to hold positional arguments parsed
        List<String> positionalArgs = new ArrayList<>();

        // Process command-line arguments
        int i = 0;
        while (i < args.length) {
            String arg = args[i];

            if (arg.equals("-s") || arg.equals("--simulate")) {
                if (i + 1 < args.length) {
                    try {
                        simulateSteps = Integer.parseInt(args[i + 1]);
                        i += 2; // Consume the flag and its value
                    } catch (NumberFormatException e) {
                        System.err.println("Error: Argument for --simulate must be an integer. Received: " + args[i + 1]);
                        printUsageAndExit(); // Helper method to print usage and exit
                    }
                } else {
                    System.err.println("Error: --simulate requires an integer argument.");
                    printUsageAndExit();
                }
            } else if (arg.startsWith("-")) { // Unrecognized flag
                System.err.println("Error: Unrecognized argument: " + arg);
                printUsageAndExit();
            } else { // Positional argument
                positionalArgs.add(arg);
                i++;
            }
        }
        // Process positional arguments based on their order
        if (positionalArgs.size() > 0) {
            try {
                width = Integer.parseInt(positionalArgs.get(0));
            } catch (NumberFormatException e) {
                System.err.println("Error: Width must be an integer. Received: " + positionalArgs.get(0));
                printUsageAndExit();
            }
        }
        if (positionalArgs.size() > 1) {
            try {
                height = Integer.parseInt(positionalArgs.get(1));
            } catch (NumberFormatException e) {
                System.err.println("Error: Height must be an integer. Received: " + positionalArgs.get(1));
                printUsageAndExit();
            }
        }
        if (positionalArgs.size() > 2) {
            try {
                nbDirts = Integer.parseInt(positionalArgs.get(2));
            } catch (NumberFormatException e) {
                System.err.println("Error: Number of dirts must be an integer. Received: " + positionalArgs.get(2));
                printUsageAndExit();
            }
        }
        if (positionalArgs.size() > 3) {
            System.err.println("Error: Too many positional arguments.");
            printUsageAndExit();
        }

        if (simulateSteps > 0) {
            System.out.printf("Creating environment with %dx%d cells and %d dirt spots.%n", width, height, nbDirts);
            Environment env = new Environment(width, height, nbDirts);
            runSimulation(env, simulateSteps);
            return;
        }

        long expectedNbStates = Environment.expectedNumberOfStates(width, height, nbDirts);

        System.out.printf("Creating environment with %dx%d cells and %d dirt spots. Expecting %d states.%n", 
            width, height, nbDirts, expectedNbStates);

        long startTime = System.nanoTime();
        Environment env = new Environment(width, height, nbDirts);
        BfsResult result = bfsTraverse(env, expectedNbStates);
        List<State> states = result.states;
        double branchingFactor = result.branchingFactor;
        long endTime = System.nanoTime();

        if (expectedNbStates < states.size()) {
            return;
        }

        List<State> goalStates = states.stream()
            .filter(env::isGoalState)
            .collect(Collectors.toList());

        System.out.printf("The environment has %d reachable states of which %d are goal states.%n", 
            states.size(), goalStates.size());

        if (expectedNbStates > states.size()) {
            double factorOff = (double) expectedNbStates / states.size();
            System.out.printf("Your estimate is off by a factor of %.2f (Why?)%n", factorOff);
        } else {
            System.out.println("Your estimate was exactly right!");
        }

        System.out.printf("The average branching factor is %.1f (legal actions per state).%n", branchingFactor);
        
        double durationSeconds = (endTime - startTime) / 1e9;
        System.out.printf("Generating those states took: %.2fs (%.0f states/s)%n", 
            durationSeconds, 1e9 * states.size() / (endTime - startTime));

        // Estimate size of states in memory (Java Approximation)
        long sizeOfAllStates = getObjectSize(states);
        long sizeOfInitialState = getObjectSize(env.getInitialState());
        long sizeOfEnvironment = getObjectSize(env);

        System.out.println();
        System.out.printf("size of environment: %d bytes (Approximation)%n", sizeOfEnvironment);
        System.out.printf("size of initial state: %d bytes (Approximation)%n", sizeOfInitialState);
        System.out.printf("size of all reachable states: %d bytes (%d bytes/state on average)%n", 
            sizeOfAllStates, (sizeOfAllStates / (states.isEmpty() ? 1 : states.size())));

        // Check determinism / equality
        System.out.println();
        BfsResult result2 = bfsTraverse(env, expectedNbStates);
        List<State> states2 = result2.states;
        int nbErrors = 0;

        if (states2.size() != states.size()) {
            nbErrors++;
            System.out.println("ERROR: reachable state set is not deterministic");
        }

        // Check if all states in run 2 exist in run 1
        Set<State> stateSet1 = new HashSet<>(states);
        for (State s : states2) {
            if (!stateSet1.contains(s)) {
                nbErrors++;
                System.out.println("ERROR: state " + s + " can't be found in reachable states set");
            }
        }

        if (nbErrors == 0) {
            System.out.println("Great! State's hash and equals functions seem to do the right thing!");
        }

        checkForCollisions(states);
    }
    
    // Helper method to print usage and exit
    private static void printUsageAndExit() {
        System.err.println("\nUsage: java EnvironmentSimulator [-s N] [width] [height] [nb_dirts]");
        System.err.println("  -s, --simulate N  : run an N step simulation of the environment");
        System.err.println("  width             : width of the grid (default: 5)");
        System.err.println("  height            : height of the grid (default: 5)");
        System.err.println("  nb_dirts          : number of dirt spots (default: 5)");
        System.exit(1);
    }
}
