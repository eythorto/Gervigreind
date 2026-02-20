public class State {
    public enum Square {
        EMPTY,
        BLOCKED,
        BLACK,
        WHITE
    }
    public enum Role {
        BLACK,
        WHITE
    }
    public Square[][] board;
    public Role role;
    public State(Square[][] board, Role role) { 
        this.board = board;
        this.role = role;
    }
}
