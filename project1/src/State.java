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
    public State(Square[][] board, Role role) { // FIXME: Change State.role to be a boolean, integer or an enum (like State.Square). String comparison is much more expensive than integer comparison
        this.board = board;
        this.role = role;
    }
}
