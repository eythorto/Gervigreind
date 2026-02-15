import java.util.LinkedList;
import java.util.List;

public class Environment {

    public static class Move {
        // action -> move a queen fromX, fromY, toX, toY
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
    
    public Environment(String role, int width, int height, int[][] white_positions, int[][] black_positions) {
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
        if (state.role.equals("white")) {
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

    public State getNextState(State s, Move m) {
        State.Square[][] copy = new State.Square[this.width][this.height];

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                copy[i][j] = s.board[i][j];
            }
        }
        String role;
        State.Square square;
        if (s.role.equals("white")) {
            role = "black";
            square = State.Square.WHITE;
        }
        else {
            role = "white";
            square = State.Square.BLACK;
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
        // IF DRAW, We return zero.
        if (empty_squares <= this.width) {
            return 0;
        }
        // IF ONE WINS, We return 100 / -100, else we return <nb. of moveable your queens> - <nb. of moveable enemy queens>
        if (state.role == "black") {
            if (moveable_black == 0) {
                return 100;
            }
            else if (moveable_white == 0) {
                return -100;
            }
            return moveable_black - moveable_white;
        }
        else {
            if (moveable_black == 0) {
                return -100;
            }
            else if (moveable_white == 0) {
                return 100;
            }
            return moveable_white - moveable_black;
        }
    }
    
    // Opponent player has no legal moves left 
    public boolean isWin(State state){
        return legalMoves(state).isEmpty();
    }

    //if there are only W empty squares left on the board.
    public boolean isDraw(State state) {
        int empty_tiles = 0;
        for (int x = 1; x <= this.width; x++) {
            for (int y = 1; y <= this.height; y++) {
                if (state.board[x][y] == State.Square.EMPTY) {
                    empty_tiles++;
                    if (empty_tiles > this.width) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
