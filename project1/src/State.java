public class State {
    public enum Square {
        EMPTY,
        BLOCKED,
        BLACK,
        WHITE
    }
    public Square[][] board;
    public String role;
    public State(Square[][] board, String role) { // FIXME: Change State.role to be a boolean, integer or an enum (like State.Square). String comparison is much more expensive than integer comparison
        this.board = board;
        this.role = role;
    }
}
