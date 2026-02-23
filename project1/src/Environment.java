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
    public static class StateAnalysis {
        public List<Move> legalMoves;
        public int whiteMobility;
        public int blackMobility;
        public boolean isTerminal;
        public int evaluation;
        
        public StateAnalysis(List<Move> moves, int whiteMob, int blackMob) {
            this.legalMoves = moves;
            this.whiteMobility = whiteMob;
            this.blackMobility = blackMob;
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

    // Does a single pass through the board to complete legalmoves, terminal check and evaluation all at once, to save time in the search
    public StateAnalysis analyzeState(State state) {
        List<Move> moves = new LinkedList<>();
        int white_mobility = 0;
        int black_mobility = 0;
        int empty_squares = 0;
        
        State.Square currentColor = (state.role == State.Role.WHITE) ? State.Square.WHITE : State.Square.BLACK;
        
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (state.board[x][y] == State.Square.BLACK) {
                    int xd = x;
                    int yd = y;
                    
                    //Check North
                    for (int i = yd+1; i < this.height; i++) {
                        if (state.board[x][i] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, x, i));
                            }
                        } else break;
                    }

                    //Check NE
                    xd++;
                    yd++;
                    while (yd < this.height && xd < this.width) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd++;
                        yd++;
                    }
                    xd = x;
                    yd = y;

                    //Check East
                    for (int i = xd+1; i < this.width; i++) {
                        if (state.board[i][y] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, i, y));
                            }
                        } else break;
                    }

                    //Check SE
                    xd++;
                    yd--;
                    while (yd >= 0 && xd < this.width) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd++;
                        yd--;
                    }
                    xd = x;
                    yd = y;
                    
                    //Check South
                    for (int i = yd-1; i >= 0; i--) {
                        if (state.board[x][i] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, x, i));
                            }
                        } else break;
                    }

                    //Check SW
                    xd--;
                    yd--;
                    while (yd >= 0 && xd >= 0) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd--;
                        yd--;
                    }
                    xd = x;
                    yd = y;

                    //Check West
                    for (int i = xd-1; i >= 0; i--) {
                        if (state.board[i][y] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, i, y));
                            }
                        } else break;
                    }

                    //Check NW
                    xd--;
                    yd++;
                    while (yd < this.height && xd >= 0) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            black_mobility++;
                            if (currentColor == State.Square.BLACK) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd--;
                        yd++;
                    }
                }
                else if (state.board[x][y] == State.Square.WHITE) {
                    // Count all moves for white pieces
                    int xd = x;
                    int yd = y;
                    
                    //Check North
                    for (int i = yd+1; i < this.height; i++) {
                        if (state.board[x][i] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, x, i));
                            }
                        } else break;
                    }

                    //Check NE
                    xd++;
                    yd++;
                    while (yd < this.height && xd < this.width) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd++;
                        yd++;
                    }
                    xd = x;
                    yd = y;

                    //Check East
                    for (int i = xd+1; i < this.width; i++) {
                        if (state.board[i][y] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, i, y));
                            }
                        } else break;
                    }

                    //Check SE
                    xd++;
                    yd--;
                    while (yd >= 0 && xd < this.width) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd++;
                        yd--;
                    }
                    xd = x;
                    yd = y;
                    
                    //Check South
                    for (int i = yd-1; i >= 0; i--) {
                        if (state.board[x][i] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, x, i));
                            }
                        } else break;
                    }

                    //Check SW
                    xd--;
                    yd--;
                    while (yd >= 0 && xd >= 0) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd--;
                        yd--;
                    }
                    xd = x;
                    yd = y;

                    //Check West
                    for (int i = xd-1; i >= 0; i--) {
                        if (state.board[i][y] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, i, y));
                            }
                        } else break;
                    }

                    //Check NW
                    xd--;
                    yd++;
                    while (yd < this.height && xd >= 0) {
                        if (state.board[xd][yd] == State.Square.EMPTY) {
                            white_mobility++;
                            if (currentColor == State.Square.WHITE) {
                                moves.add(new Move(x, y, xd, yd));
                            }
                        } else break;
                        xd--;
                        yd++;
                    }
                }
                else if (state.board[x][y] == State.Square.EMPTY) {
                    empty_squares++;
                }
            }
        }
        
        StateAnalysis analysis = new StateAnalysis(moves, white_mobility, black_mobility);
        
        // Terminal check
        analysis.isTerminal = (white_mobility == 0 || black_mobility == 0 || empty_squares <= this.width);
        
        // Compute evaluation from current player's perspective
        if (state.role == State.Role.BLACK) {
            if (black_mobility == 0 && white_mobility > 0) {
                analysis.evaluation = -100;
            } else if (white_mobility == 0 && black_mobility > 0) {
                analysis.evaluation = 100;
            } else if (black_mobility == 0 && white_mobility == 0) {
                analysis.evaluation = 0;
            } else {
                analysis.evaluation = black_mobility - white_mobility;
            }
        } else {
            if (white_mobility == 0 && black_mobility > 0) {
                analysis.evaluation = -100;
            } else if (black_mobility == 0 && white_mobility > 0) {
                analysis.evaluation = 100;
            } else if (white_mobility == 0 && black_mobility == 0) {
                analysis.evaluation = 0;
            } else {
                analysis.evaluation = white_mobility - black_mobility;
            }
        }
        
        return analysis;
    }

}

