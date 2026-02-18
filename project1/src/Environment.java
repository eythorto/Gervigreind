import java.util.LinkedList;
import java.util.List;

public class Environment {

    public static class Move {
        // action -> move a queen fromX, fromY, toX, toY
        // FIXME: could do it int is x_1 + (y_1 << 8) + (x_2 << 16) + (y_2 << 24) instead, faster
        int fromX, fromY;
        int toX, toY;
        public Move(int fromX, int fromY, int toX, int toY) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    int width;
    int height;
    State current_state;
    
    public Environment(State.Role role, int width, int height, int[][] white_positions, int[][] black_positions) {
        this.width = width;
        this.height = height;
        State.Square[][] board = new State.Square[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = State.Square.EMPTY;
            }
        }
        for (int[] pos : white_positions) {
            board[pos[0]-1][pos[1]-1] = State.Square.WHITE;
        }
        for (int[] pos : black_positions) {
            board[pos[0]-1][pos[1]-1] = State.Square.BLACK;
        }
        current_state = new State(board, role);
    }

    public List<Move> legalMoves(State state) {
        // returns a list of possible actions
        // TODO: Returns 0 based x and y values now, fix to return +1s on both x and y
        State.Square color;
        if (state.role == State.Role.WHITE) {
            color = State.Square.WHITE;
        }
        else {
            color = State.Square.BLACK;
        }
        
		List<Move> moves = new LinkedList<>();
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (state.board[x][y] == color) {
                    int xd = x;
                    int yd = y;
                    //Check North
                    for (int i = yd+1; i < this.height; i++) {
                        if (state.board[x][i] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, x, i));
                        }
                        else {
                            break;
                        }
                    }

                    //Check NE
                    xd++;
                    yd++;
                    while (yd < this.height && xd < this.width) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, xd, yd));
                        }
                        else {
                            break;
                        }
                        xd++;
                        yd++;
                    }
                    xd = x;
                    yd = y;

                    //Check East
                    for (int i = xd+1; i < this.width; i++) {
                        if (state.board[i][y] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, i, y));
                        }
                        else {
                            break;
                        }
                    }

                    //Check SE
                    xd++;
                    yd--;
                    while (yd >= 0 && xd < this.width) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, xd, yd));
                        }
                        else {
                            break;
                        }
                        xd++;
                        yd--;
                    }
                    xd = x;
                    yd = y;
                    
                    //Check South
                    for (int i = yd-1; i >= 0; i--) {
                        if (state.board[x][i] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, x, i));
                        }
                        else {
                            break;
                        }
                    }

                    //Check SW
                    xd--;
                    yd--;
                    while (yd >= 0 && xd >= 0) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, xd, yd));
                        }
                        else {
                            break;
                        }
                        xd--;
                        yd--;
                    }
                    xd = x;
                    yd = y;

                    //Check West
                    for (int i = xd-1; i >= 0; i--) {
                        if (state.board[i][y] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, i, y));
                        }
                        else {
                            break;
                        }
                    }

                    // Check NW
                    xd--;
                    yd++;
                    while (yd < this.height && xd >= 0) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            moves.add(new Move(x, y, xd, yd));
                        }
                        else {
                            break;
                        }
                        xd--;
                        yd++;
                    }
                }
            }
        }
        return moves;
    }

    public void doMove(Move m) {
        current_state = getNextState(current_state, m);
    }

    public void doMoveForState(State s, Move m) {
        State.Square square;
        if (s.role == State.Role.BLACK) {
            s.role = State.Role.WHITE;
            square = State.Square.BLACK;
        }
        else {
            s.role = State.Role.BLACK;
            square = State.Square.WHITE;
        }

        s.board[m.fromX][m.fromY] = State.Square.BLOCKED;
        s.board[m.toX][m.toY] = square;
    }

    public void undoMoveForState(State s, Move m){
        State.Square square;
        if (s.role == State.Role.BLACK) {
            s.role = State.Role.WHITE;
            square = State.Square.WHITE;
        }
        else {
            s.role = State.Role.BLACK;
            square = State.Square.BLACK;
        }
        s.board[m.toX][m.toY] = State.Square.EMPTY;
        s.board[m.fromX][m.fromY] = square;
    }

    public State getNextState(State s, Move m) {
        State.Square[][] copy = new State.Square[this.width][this.height];

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                copy[i][j] = s.board[i][j];
            }
        }
        State.Role role;
        State.Square square;
        if (s.role == State.Role.BLACK) {
            role = State.Role.WHITE;
            square = State.Square.BLACK;
        }
        else {
            role = State.Role.BLACK;
            square = State.Square.WHITE;
        }

        copy[m.fromX][m.fromY] = State.Square.BLOCKED;
        copy[m.toX][m.toY] = square;

        return new State(copy, role);
    }

    private boolean isSquareEmpty(int x, int y, State state) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return false; 
        }
        return state.board[x][y] == State.Square.EMPTY;
    }

    public int evaluationFunction (State state) {
        int moveable_white = 0;
        int moveable_black = 0;
        int empty_squares = 0;
        int[] dx = {-1, -1, -1,  0, 0,  1, 1, 1};
        int[] dy = {-1,  0,  1, -1, 1, -1, 0, 1};
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (state.board[x][y] == State.Square.BLACK) {
                    for (int i = 0; i < 8; i++) {
                        int neighborX = x + dx[i];
                        int neighborY = y + dy[i];

                        if (isSquareEmpty(neighborX, neighborY, state)) {
                            moveable_black++;
                            break;
                        }
                    }
                }
                else if (state.board[x][y] == State.Square.WHITE) {
                    for (int i = 0; i < 8; i++) {
                        int neighborX = x + dx[i];
                        int neighborY = y + dy[i];

                        if (isSquareEmpty(neighborX, neighborY, state)) {
                            moveable_white++;
                            break;
                        }
                    }
                }
                else if (state.board[x][y] == State.Square.EMPTY) {
                    empty_squares++;
                }
            }
        }
        // Return value from current player's perspective: positive = good for current player, negative = bad
        // Current player to move is state.role
        if (state.role == State.Role.BLACK) {
            // It's BLACK's turn
            if (moveable_black == 0 && moveable_white > 0) {
                return -100;
            }
            else if (moveable_white == 0 && moveable_black > 0) {
                return 100;
            }
            else if (moveable_black == 0 && moveable_white == 0) {
                return 0;
            }
            return moveable_black - moveable_white;
        }
        else {
            // It's WHITE's turn
            if (moveable_white == 0 && moveable_black > 0) {
                return -100;
            }
            else if (moveable_black == 0 && moveable_white > 0) {
                return 100;
            }
            else if (moveable_black == 0 && moveable_white == 0) {
                return 0;
            }
            return moveable_white - moveable_black;
        }
        
    }

    public boolean isTerminal(State state){        
        // legal moves for current player
        List<Environment.Move> currentPlayerMoves = legalMoves(state);

        // legal moves for opponent player
        State.Role opponentRole = (state.role == State.Role.BLACK) ? State.Role.WHITE : State.Role.BLACK;
        State opponentState = new State(state.board, opponentRole);
        List<Environment.Move> opponentPlayerMoves = legalMoves(opponentState);

        int emptySquares = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (state.board[x][y] == State.Square.EMPTY)
                    emptySquares++;
            }
        }

        // if either player cannot move
        if (currentPlayerMoves.isEmpty() && opponentPlayerMoves.isEmpty()){
            return true; // terminate the state 
        }
        // 2) If only W empty squares left → game ends
        if (emptySquares <= width)
            return true;
            
        return false; // not terminate
    }

}
