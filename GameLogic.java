import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLogic implements PlayableLogic {
    private ConcretePiece[][] board;
    private Position[][] positions;
    private Position kingPosition;
    private final ConcretePlayer playerOne = new ConcretePlayer(true); // playerOne is defender
    private final ConcretePlayer playerTwo = new ConcretePlayer(false); // playerTwo is attacker
    private boolean gameFinished;
    private boolean isPlayerTwoTurn=true;
    private List <ConcretePiece> capturedPieces;



    // Constructor

    public GameLogic() {
        this.positions = new Position[11][11];
        this.reset();
    }
    public boolean move(Position a, Position b) {
        if (isLegalMove(a, b)) {
            this.capturedPieces = new ArrayList<>();
            ConcretePiece temp = this.board[a.getX()][a.getY()];
            this.board[b.getX()][b.getY()] = temp;
            temp.setPosition(b);
            if(temp instanceof King) {
                this.kingPosition=temp.getPosition();
            }
            this.board[a.getX()][a.getY()] = null;
            System.out.println("king position : "+kingPosition);
            this.hasCapture(temp);
            this.checkWin(b);
            this.isPlayerTwoTurn = !this.isPlayerTwoTurn;
            return true;

        }
         return false;

    }

    private boolean isLegalMove(Position current, Position destination) {
        boolean flag = false;
        // Check if the current position has a piece
        if (this.board[current.getX()][current.getY()] == null) {
            return flag;
        }
        // check if the player plays in his turn and in the correct position
        if (this.isSecondPlayerTurn() && this.getPieceAtPosition(current).getOwner().isPlayerOne() || !(this.isSecondPlayerTurn()) && !(this.getPieceAtPosition(current).getOwner().isPlayerOne())) {
            return flag;
        }
        // Check if the destination position is occupied
        if (this.board[destination.getX()][destination.getY()] != null) {
            return flag;
        }
        // Check if the move is either horizontal or vertical
        if (current.getX() == destination.getX() || current.getY() == destination.getY()) {
            flag=!(hasPiecesInPath(current, destination));
            // Check if the piece is pawn and if true, checks destination is not corner
            if(this.board[current.getX()][current.getY()] instanceof Pawn && isCorner(destination)){
                flag=false;
            }
            return flag;
        }
        return flag;
    }

    private boolean hasPiecesInPath(Position current, Position destination) {
        boolean flag = false;
        // Moving vertically, check for pieces in the column
        if (current.getX() == destination.getX()) {
            int minY = Math.min(current.getY(), destination.getY());
            int maxY = Math.max(current.getY(), destination.getY());
            for (int i = minY + 1; i < maxY; i++) {
                if (this.board[current.getX()][i] != null) {
                    flag=true;// Piece found in the path
                    return flag;
                }
            }
        }
        // Moving horizontally, check for pieces in the row
        if (current.getY() == destination.getY()) {
            int minX = Math.min(current.getX(), destination.getX());
            int maxX = Math.max(current.getX(), destination.getX());
            for (int i = minX + 1; i < maxX; i++) {
                if (this.board[i][current.getY()] != null) {
                    flag=true;; // Piece found in the path
                    return flag;
                }
            }
        }
        return flag; // No intervening pieces found
    }
    private void hasCapture (ConcretePiece piece) {
        if (piece.getPosition() != null) {
            int x = piece.getPosition().getX();
            int y = piece.getPosition().getY();
            if (this.board[x][y] instanceof Pawn) {
                if (isOpponentPieceAt(x - 1, y, piece.getPosition()) && isMyPieceAt(x - 2, y, piece.getPosition())) {
                    this.capturedPieces.add(this.board[x - 1][y]);
                    this.board[x - 1][y] = null;
                    ((Pawn) this.board[x][y]).kill();
                }
                if (isOpponentPieceAt(x + 1, y, piece.getPosition()) && isMyPieceAt(x + 2, y, piece.getPosition())) {
                    this.capturedPieces.add(this.board[x + 1][y]);
                    this.board[x + 1][y] = null;
                    ((Pawn) this.board[x][y]).kill();
                }
                if (isOpponentPieceAt(x, y - 1, piece.getPosition()) && isMyPieceAt(x, y - 2, piece.getPosition())) {
                    this.capturedPieces.add(this.board[x][y - 1]);
                    this.board[x][y - 1] = null;
                    ((Pawn) this.board[x][y]).kill();
                }
                if (isOpponentPieceAt(x, y + 1, piece.getPosition()) && isMyPieceAt(x, y + 2, piece.getPosition())) {
                    this.capturedPieces.add(this.board[x][y + 1]);
                    this.board[x][y + 1] = null;
                    ((Pawn) this.board[x][y]).kill();
                }
                    //Check capture between pawn and a edge
                    if (isOpponentPieceAt(x - 1, y, piece.getPosition()) && (x - 1 == 0)) {
                        this.capturedPieces.add(this.board[x - 1][y]);
                        this.board[x - 1][y] = null;
                        ((Pawn) this.board[x][y]).kill();
                    }
                    if (isOpponentPieceAt(x + 1, y, piece.getPosition()) && (x + 1 == 10)) {
                        this.capturedPieces.add(this.board[x + 1][y]);
                        this.board[x + 1][y] = null;
                        ((Pawn) this.board[x][y]).kill();
                    }
                    if (isOpponentPieceAt(x, y - 1, piece.getPosition()) && (y - 1 == 0)) {
                        this.capturedPieces.add(this.board[x][y - 1]);
                        this.board[x][y - 1] = null;
                        ((Pawn) this.board[x][y]).kill();
                    }
                    if (isOpponentPieceAt(x, y + 1, piece.getPosition()) && (y + 1 == 10)) {
                        this.capturedPieces.add(this.board[x][y + 1]);
                        this.board[x][y + 1] = null;
                        ((Pawn) this.board[x][y]).kill();
                    }
            }

        }
    }
    private void checkWin(Position position){
        Position kingPosition = this.getKingPosition();
        if(this.getPieceAtPosition(position) instanceof King && isCorner(position)){
           this.gameFinished = true;
           this.playerOne.winGame();
       }
       if (isKingSurrounded(kingPosition)){
           System.out.printf("true");
           this.gameFinished = true;
           this.playerTwo.winGame();
       }
    }

    private boolean isKingSurrounded(Position kingPosition) {
        if(kingPosition!=null){
        int x = kingPosition.getX();
        int y = kingPosition.getY();

        if (x==0 && y>0 && y<10) {
            return isOpponentPieceAt(x+1,y,kingPosition) && isOpponentPieceAt(x,y-1,kingPosition) && isOpponentPieceAt(x,y+1,kingPosition);
        }
        if (x==10 && y>0 && y<10) {
            return isOpponentPieceAt(x-1,y,kingPosition) && isOpponentPieceAt(x,y-1,kingPosition) && isOpponentPieceAt(x,y+1,kingPosition);
        }
        if (y==0 && x>0 && x<10) {
            return isOpponentPieceAt(x-1,y,kingPosition) && isOpponentPieceAt(x+1,y,kingPosition) && isOpponentPieceAt(x,y+1,kingPosition);
        }
        if (y==10 && x>0 && x<10) {
            return isOpponentPieceAt(x-1,y,kingPosition) && isOpponentPieceAt(x+1,y,kingPosition) && isOpponentPieceAt(x,y-1,kingPosition);
        }
        if (x>0 && x<10 && y>0 && y<10) {
            return isOpponentPieceAt(x-1,y,kingPosition) && isOpponentPieceAt(x+1,y,kingPosition) &&
                    isOpponentPieceAt(x,y-1,kingPosition) && isOpponentPieceAt(x,y+1,kingPosition);
        }
        }
        return false;
    }

    private boolean isOpponentPieceAt(int x, int y, Position comparePosition) {
            if (x >=0 && x <= 10 && y >= 0 && y <= 10) {
                if (this.board[x][y] != null) {
                    if (this.board[x][y] instanceof Pawn){
                    Player owner1 = this.board[x][y].getOwner();
                    Player owner2 = this.board[comparePosition.getX()][comparePosition.getY()].getOwner();
                   return (owner1.isPlayerOne() != owner2.isPlayerOne());
                    }
                }
            }
            return false;
    }
    private boolean isMyPieceAt(int x, int y, Position comparePosition) {
        if (x >= 0 && x < 11 && y >= 0 && y < 11) {
            if (this.board[x][y] != null) {
                if (this.board[x][y] instanceof Pawn) {
                    Player owner1 = this.board[x][y].getOwner();
                    Player owner2 = this.board[comparePosition.getX()][comparePosition.getY()].getOwner();
                    return (owner1.isPlayerOne() == owner2.isPlayerOne());
                }
            }
            // check corner
            Position position = new Position(x, y);
            if (isCorner(position)) {
                return true;
            }
      }
        return false;
    }
    private boolean isCorner (Position position) {
            if (position.getX() == 0) {
                if (position.getY() == 0 || position.getY() == 10) {
                    return true;
                }
            }
            if (position.getX() == 10) {
                if (position.getY() == 0 || position.getY() == 10) {
                    return true;
                }
            }
        return false;
    }
    public Position getKingPosition() {
        return this.kingPosition;
    }
    public Piece getPieceAtPosition(Position position) {
        return this.board[position.getX()][position.getY()];
    }
    public Player getFirstPlayer() {
        return this.playerOne;
    }
    public Player getSecondPlayer() {
        return this.playerTwo;
    }
    public boolean isGameFinished() {
        return this.gameFinished;
    }
    public boolean isSecondPlayerTurn() {
        return this.isPlayerTwoTurn;
    }
    public void reset() {
    this.board=new ConcretePiece[11][11];
    this.gameFinished=false;
    this.isPlayerTwoTurn = true;

    //PlayerOne's King and Pawns (defender)
    // create pawn in the six column and forth row and define it to be owns by PlayerOne (defender)
        this.board[5][3]= new Pawn(this.playerOne);
    // create pawns in the fifth row and define them to be owned by PlayerOne (defender)
        for (int i=4; i<=6; i++) {
            this.board[i][4] = new Pawn(this.playerOne);
            }
    // create pawns in the sixth row and define them to be owned by PlayerOne (defender)
        for (int i=3; i<=7; i++) {
            if (i != 5) {
                this.board[i][5] = new Pawn(this.playerOne);
            }
        }
    // create pawns in the seventh row and define them to be owned by PlayerOne (defender)
        for (int i=4; i<=6; i++){
            this.board[i][6]=new Pawn(this.playerOne);
        }
    // create pawn in the six column and eighth row and define it to be owns by PlayerOne (defender)
        this.board[5][7]= new Pawn(this.playerOne);
    // create King in the middle of the board and define it to be owns by PlayerOne (defender)
        King king = new King(this.playerOne);
        this.board[5][5]= king;
        this.kingPosition = this.board[5][5].getPosition();
        king.setPosition(new Position(5, 5)); // Set the initial position for the king
        this.kingPosition = king.getPosition();


    //PlayerTwo's Pawns (attacker):
    // create pawns in the first row and define them to be owned by PlayerTwo (attacker)
        for (int i=3; i<=7; i++){
            this.board[i][0]=new Pawn(this.playerTwo);
        }
    // create pawn in the second row and define it to be owned by PlayerTwo (attacker)
        this.board[5][1]=new Pawn(this.playerTwo);
    // create pawns in the first column and define them to be owned by PlayerTwo (attacker)
        for (int i=3; i<=7;i++){
        this.board[0][i]=new Pawn(this.playerTwo);
        }
    // create pawn in the second column and define it to be owned by PlayerTwo (attacker)
        this.board[1][5]=new Pawn(this.playerTwo);
    // create pawn in the tenth column and define it to be owned by PlayerTwo (attacker)
        this.board[9][5]=new Pawn(this.playerTwo);
    //create pawns in the last column and define them to be owned by PlayerTwo (attacker)
        for (int i=3; i<=7;i++) {
            this.board[10][i] = new Pawn(this.playerTwo);
        }
    // create pawn in the tenth row and define it to be owned by PlayerTwo (attacker)
        this.board[5][9]=new Pawn(this.playerTwo);
    // Create pawns in the first row and define them to be owned by PlayerTwo (attacker)
        for (int i=3; i<=7; i++){
            this.board[i][10]=new Pawn(this.playerTwo);
        }
    // Set the numbers for pieces in the board
        int countPlayerOne=1;
        int countPlayerTwo=1;
        for(int i=0; i<=10; i++)
        {
            for(int j=0; j<=10; j++)
            {
                  if (this.board[i][j]!=null)
                  {
                      ConcretePiece piece = this.board[i][j];
                      if (piece.getOwner().isPlayerOne())
                      {
                          piece.setNumConcretePiece(countPlayerOne);
                          countPlayerOne++;
                      }
                      else
                      {
                          piece.setNumConcretePiece(countPlayerTwo);
                          countPlayerTwo++;
                      }
                  }
            }
        }
    }

    @Override
    public void undoLastMove() {
    }
    @Override
    public int getBoardSize() {
        return 11;
    }
}