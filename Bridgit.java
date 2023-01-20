import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a tile on the game board
interface Tile {

  // EFFECT: Link this Tile to the Tile above
  public void linkUp(Tile t);

  // EFFECT: Link this Tile to the Tile below
  public void linkDown(Tile t);

  // EFFECT: Link this Tile to the Tile to the left
  public void linkLeft(Tile t);

  // EFFECT: Link this Tile to the Tile to the right
  public void linkRight(Tile t);

  // EFFECT: add the links of this Tile into this links
  public ArrayList<Tile> addLinks();

  // EFFECT: set this Tile as the Target to win the game
  public void makeTarget();

  // is this Tile the target tile?
  public boolean isTarget();

  // is this Tile traversable from this given player's turn?
  public boolean path(int turn);

}

//Represents an empty piece that is outside the board
class EmptyCell implements Tile {
  // nothing to link in the EmptyCell
  public void linkUp(Tile t) {
    // do nothing
  }

  // nothing to link in the EmptyCell
  public void linkDown(Tile t) {
    // do nothing
  }

  // nothing to link in the EmptyCell
  public void linkLeft(Tile t) {
    // do nothing
  }

  // nothing to link in the EmptyCell
  public void linkRight(Tile t) {
    // do nothing
  }

  // nothing to link in the EmptyCell
  public ArrayList<Tile> addLinks() {
    throw new RuntimeException("EmptyCell has no links");
  }

  // nothing to do in the EmptyCell
  public void makeTarget() {
    // do nothing
  }

  // this is not the target
  public boolean isTarget() {
    return false;
  }

  // this is not a valid path
  public boolean path(int turn) {
    return false;
  }
}

//represents a Cell on the game board
abstract class Cell implements Tile {
  Tile up;
  Tile down;
  Tile left;
  Tile right;
  Boolean target;

  Cell() {
    this.up = null;
    this.down = null;
    this.left = null;
    this.right = null;
    this.target = false;
  }

  // EFFECT: Link this Cell to the Cell above
  public void linkUp(Tile t) {
    this.up = t;
  }

  // EFFECT: Link this Cell to the Cell below
  public void linkDown(Tile t) {
    this.down = t;
  }

  // EFFECT: Link this Cell to the Cell to the left
  public void linkLeft(Tile t) {
    this.left = t;
  }

  // EFFECT: Link this Cell to the Cell to the right
  public void linkRight(Tile t) {
    this.right = t;
  }

  // Draws this Cell onto the background
  public abstract WorldImage drawAt(int col, int row, int length, WorldImage background);

  // Draws this Cell onto the background with its color
  public WorldImage drawAtHelper(int col, int row, int length, WorldImage background, Color color) {
    Double size = background.getWidth();

    WorldImage draw = new RectangleImage(size.intValue() / length, size.intValue() / length,
        "solid", color);

    return new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, draw, -col * draw.getWidth(),
        -row * draw.getHeight(), background);
  }

  // return the turn of the turn of the current player
  public int click(int turn, int row, int column, ArrayList<ArrayList<Cell>> arr) {
    return turn;
  }

  // EFFECT: add the links of this Cell into this links
  public ArrayList<Tile> addLinks() {
    ArrayList<Tile> arr = new ArrayList<Tile>();

    arr.add(this.up);
    arr.add(this.down);
    arr.add(this.left);
    arr.add(this.right);

    return arr;
  }

  // EFFECT: set this Cell as the target
  public void makeTarget() {
    this.target = true;
  }

  // this is not the target
  public boolean isTarget() {
    return this.target;
  }

  // is this Cell a valid path?
  public abstract boolean path(int turn);

}

//Represents an WCell
class WCell extends Cell {
  Color col = Color.white;

  WCell() {
    super();
  }

  // Draws this WCell onto the background
  public WorldImage drawAt(int col, int row, int length, WorldImage background) {
    return this.drawAtHelper(col, row, length, background, this.col);
  }

  // EFFECT: remove the Cell in the given row and column and replace it with
  // a RCell or a BCell depending on the player's turn
  // return the turn of the next player
  public int click(int turn, int row, int column, ArrayList<ArrayList<Cell>> cells) {
    Cell currTile = cells.get(row).remove(column);

    if (turn == 1) {
      Cell newTile = new RCell();

      newTile.linkUp(currTile.up);
      currTile.up.linkDown(newTile);
      newTile.linkDown(currTile.down);
      currTile.down.linkUp(newTile);
      newTile.linkLeft(currTile.left);
      currTile.left.linkRight(newTile);
      newTile.linkRight(currTile.right);
      currTile.right.linkLeft(newTile);

      cells.get(row).add(column, newTile);
      return 2;
    }
    else {
      Cell newTile = new BCell();

      newTile.linkUp(currTile.up);
      currTile.up.linkDown(newTile);
      newTile.linkDown(currTile.down);
      currTile.down.linkUp(newTile);
      newTile.linkLeft(currTile.left);
      currTile.left.linkRight(newTile);
      newTile.linkRight(currTile.right);
      currTile.right.linkLeft(newTile);

      cells.get(row).add(column, newTile);

      return 1;
    }
  }

  // a WCell is not a valid path
  public boolean path(int turn) {
    return false;
  }
}

//Represents a BCell
class BCell extends Cell {
  Color col = Color.blue;

  BCell() {
    super();
  }

  // Draws this WCell onto the background
  public WorldImage drawAt(int col, int row, int length, WorldImage background) {
    return this.drawAtHelper(col, row, length, background, this.col);
  }

  // is it player's 2 turn?
  public boolean path(int turn) {
    return turn == 2;
  }
}

//Represents a RCell
class RCell extends Cell {
  Color col = Color.red;

  RCell() {
    super();
  }

  // Draws this WCell onto the background
  public WorldImage drawAt(int col, int row, int length, WorldImage background) {
    return this.drawAtHelper(col, row, length, background, this.col);
  }

  // is it player's 2 turn?
  public boolean path(int turn) {
    return turn == 1;
  }
}

//Represents the Bridgit Game
class Bridgit extends World {

  int grid;
  ArrayList<ArrayList<Cell>> cells;
  int turn;

  // original constructor
  Bridgit(int grid) {
    this.grid = grid;
    this.cells = new ArrayList<ArrayList<Cell>>();
    this.turn = 1;

    if (this.grid % 2 == 0 || this.grid < 3) {
      throw new IllegalArgumentException("the number needs to be odd and greater than 3");
    }

    this.makeGame(this.grid);

  }

  // for testing
  Bridgit(int grid, ArrayList<ArrayList<Cell>> cells) {
    this.grid = grid;
    this.cells = cells;
  }

  // EFFECT: make the Cells and link them together and the Cells to that
  // needs to be linked to EmptyCell
  public void makeGame(int grid) {
    this.cells = new ArrayList<ArrayList<Cell>>();
    Tile empty = new EmptyCell();

    for (int i = 0; i < grid; i++) {
      this.cells.add(new ArrayList<Cell>());

      for (int j = 0; j < grid; j++) {
        // add Cells to the even row
        if (i % 2 == 0) {
          // add WCells on every even column
          if (j % 2 == 0) {
            this.cells.get(i).add(new WCell());
          }

          // add RCells on every odd column
          else {
            this.cells.get(i).add(new RCell());
          }
        }

        // add Cells to the odd rows
        else {
          // add RCells on every even column
          if (j % 2 == 0) {
            this.cells.get(i).add(new BCell());
          }

          // add WCells on every odd column
          else {
            this.cells.get(i).add(new WCell());
          }
        }

        Tile currentTile = this.cells.get(i).get(j);

        // link the Cells on the top row to the Empty Cell above
        if (i == 0) {
          currentTile.linkUp(empty);
        }
        // link the cells up and down
        else {
          Tile up = this.cells.get(i - 1).get(j);
          currentTile.linkUp(up);
          up.linkDown(currentTile);
        }

        // link the Cells on the last row to the Empty Cell below
        if (i == this.grid - 1) {
          currentTile.linkDown(empty);

          // make every odd cell, i.e RCell, a target
          if (j % 2 == 1) {
            currentTile.makeTarget();
          }
        }

        // link the Cells on the first column to the Empty Cell on the left
        if (j == 0) {
          currentTile.linkLeft(empty);
        }
        // link the Cells left and right
        else {
          Tile left = this.cells.get(i).get(j - 1);
          currentTile.linkLeft(left);
          left.linkRight(currentTile);
        }

        // link the Cells on the last column to the Empty Cell on the right
        if (j == this.grid - 1) {
          currentTile.linkRight(empty);

          // make every odd cell, i.e BCell, a target
          if (i % 2 == 1) {
            currentTile.makeTarget();
          }
        }
      }
    }
  }

  // draws the game and tells which player's turn it is
  public WorldScene makeScene() {
    WorldScene scene = this.getEmptyScene();
    int edge = scene.height / this.grid;
    int edge2 = edge / 2;
    int edge4 = edge / 4;

    WorldImage board = new RectangleImage(scene.height, scene.width, "solid", Color.white);

    for (int i = 0; i < this.grid; i++) {
      for (int j = 0; j < this.grid; j++) {
        board = this.cells.get(i).get(j).drawAt(j, i, this.grid, board);
      }
    }

    if (this.turn == 1) {
      board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("P1", edge2, Color.red), -edge4, -edge4, board);
    }
    else {
      board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("P2", edge2, Color.blue), -edge4, -edge4, board);
    }

    scene.placeImageXY(board, scene.width / 2, scene.height / 2);

    return scene;
  }

  // draws the game with a given canvas width and height and tell which player's
  // turn it is
  // I'm doing this because makeScene() is dependent on the canvas size that
  // bigbang provides so if we don't initialize bigbang
  public WorldScene makeSceneTest(int width, int height) {
    WorldScene scene = new WorldScene(width, height);
    int edge = scene.height / this.grid;
    int edge2 = edge / 2;
    int edge4 = edge / 4;

    WorldImage board = new RectangleImage(scene.height, scene.width, "solid", Color.white);

    for (int i = 0; i < this.cells.size(); i++) {
      for (int j = 0; j < this.cells.get(0).size(); j++) {
        board = this.cells.get(i).get(j).drawAt(j, i, this.grid, board);
      }
    }

    if (this.turn == 1) {
      board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("P1", edge2, Color.red), -edge4, -edge4, board);
    }
    else {
      board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("P2", edge2, Color.blue), -edge4, -edge4, board);
    }

    scene.placeImageXY(board, scene.width / 2, scene.height / 2);

    return scene;
  }

  // EFFECT: if a valid Cell is clicked, make the WCell become a BCell or
  // RCell depending on the player's turn, update the turn, and end the game if
  // a player has won
  public void onMouseClicked(Posn pos) {
    WorldScene scene = this.getEmptyScene();
    int edge = scene.height / this.grid;
    int edgeRow = pos.y / edge;
    int edgeColumn = pos.x / edge;
    int newTurn = this.turn;

    // checks for valid clicks
    if (edgeColumn > 0 && edgeRow > 0 && edgeColumn < this.grid - 1 && edgeRow < this.grid - 1) {
      Cell clicked = this.cells.get(edgeRow).get(edgeColumn);
      newTurn = clicked.click(this.turn, edgeRow, edgeColumn, this.cells);

      ArrayList<Tile> worklist = new ArrayList<Tile>();
      // player's 1 turn
      if (this.turn == 0) {
        for (int i = 1; i < this.grid; i += 2) {
          Tile addTile = this.cells.get(0).get(i);
          worklist.add(addTile);
        }
      }
      // player's 2 turn
      else {
        for (int i = 1; i < this.grid; i += 2) {
          Tile addTile = this.cells.get(i).get(0);
          worklist.add(addTile);
        }
      }

      if (this.checkWin(worklist, this.turn)) {
        this.endOfWorld("Player " + this.turn + " has won");
      }
    }

    this.turn = newTurn;
  }

  // EFFECT: if a valid Cell is clicked, make the WCell become a BCell or
  // RCell depending on the player's turn, update the turn, and end the game if
  // a player has won
  // This is made for testing since onMouseClicked the height and width of the
  // board is dependent on big bang
  public void onMouseClickedTest(Posn pos, int height, int column) {
    WorldScene scene = new WorldScene(height, column);
    int edge = scene.height / this.grid;
    int edgeRow = pos.y / edge;
    int edgeColumn = pos.x / edge;
    int newTurn = this.turn;

    // checks for valid clicks
    if (edgeColumn > 0 && edgeRow > 0 && edgeColumn < this.grid - 1 && edgeRow < this.grid - 1) {
      Cell clicked = this.cells.get(edgeRow).get(edgeColumn);
      newTurn = clicked.click(this.turn, edgeRow, edgeColumn, this.cells);

      ArrayList<Tile> worklist = new ArrayList<Tile>();
      // player's 1 turn
      if (this.turn == 1) {
        for (int i = 1; i < this.grid; i += 2) {
          Tile addTile = this.cells.get(0).get(i);
          worklist.add(addTile);
        }
      }
      // player's 2 turn
      else {
        for (int i = 1; i < this.grid; i += 2) {
          Tile addTile = this.cells.get(i).get(0);
          worklist.add(addTile);
        }
      }

      if (this.checkWin(worklist, this.turn)) {
        this.endOfWorld("Player " + this.turn + " has won");
      }
    }

    this.turn = newTurn;
  }

  // determines if a player has won by checking if there is a path
  // from any source in the given worklist to the target using
  // depth first search
  public boolean checkWin(ArrayList<Tile> worklist, int turn) {
    ArrayList<Tile> seenList = new ArrayList<Tile>();

    while (worklist.size() > 0) {
      Tile next = worklist.remove(0);
      ArrayList<Tile> nextLinks = next.addLinks();

      if (next.isTarget()) {
        return true;
      }
      else if (seenList.contains(next)) {
        // do nothing
      }
      else {
        for (Tile t : nextLinks) {
          if (t.path(turn)) {
            worklist.add(0, t);
          }
        }
        seenList.add(next);
      }
    }

    return false;
  }

  // EFFECT: reset the cells to its original state and make the turn 1, i.e,
  // resetting the game
  public void onKeyEvent(String s) {
    if (s.equals("r")) {
      this.makeGame(this.grid);
      this.turn = 1;
    }
  }

  // produce the lastScene of the game with a given message
  public WorldScene lastScene(String s) {
    WorldScene scene = this.makeScene();
    scene.placeImageXY(new TextImage(s, 30, Color.BLACK), scene.height / 2, scene.width / 2);

    return scene;
  }

}

class ExamplesBridgit {
  Tile emptyCell;

  Cell wCell;
  Cell rCell;
  Cell bCell;

  Cell wCell2;
  Cell wCell3;
  Cell wCell4;
  Cell wCell5;

  Cell rCell2;
  Cell rCell3;
  Cell rCell4;
  Cell rCell5;

  Cell bCell2;
  Cell bCell3;
  Cell bCell4;
  Cell bCell5;

  ArrayList<ArrayList<Cell>> cells;

  Bridgit b1;
  Bridgit b2;

  void init() {
    this.emptyCell = new EmptyCell();

    // for 3 by 3
    this.wCell = new WCell();
    this.rCell = new RCell();
    this.bCell = new BCell();

    this.wCell2 = new WCell();
    this.wCell3 = new WCell();
    this.wCell4 = new WCell();
    this.wCell5 = new WCell();

    this.rCell2 = new RCell();
    this.rCell3 = new RCell();
    this.rCell4 = new RCell();
    this.rCell5 = new RCell();

    this.bCell2 = new BCell();
    this.bCell3 = new BCell();
    this.bCell4 = new BCell();
    this.bCell5 = new BCell();

    ArrayList<Cell> row1 = new ArrayList<Cell>();
    row1.add(this.wCell);
    row1.add(this.rCell);
    row1.add(this.wCell2);

    ArrayList<Cell> row2 = new ArrayList<Cell>();
    row2.add(this.bCell);
    row2.add(this.wCell3);
    row2.add(this.bCell2);

    ArrayList<Cell> row3 = new ArrayList<Cell>();
    row3.add(this.wCell4);
    row3.add(this.rCell2);
    row3.add(this.wCell5);

    this.cells = new ArrayList<ArrayList<Cell>>();
    cells.add(row1);
    cells.add(row2);
    cells.add(row3);

    this.bCell2.makeTarget();
    this.rCell2.makeTarget();

    this.wCell.linkUp(this.emptyCell);
    this.wCell.linkDown(this.bCell);
    this.wCell.linkLeft(this.emptyCell);
    this.wCell.linkRight(this.rCell);

    this.rCell.linkUp(this.emptyCell);
    this.rCell.linkDown(this.wCell3);
    this.rCell.linkLeft(this.wCell);
    this.rCell.linkRight(this.wCell2);

    this.wCell2.linkUp(this.emptyCell);
    this.wCell2.linkDown(this.bCell2);
    this.wCell2.linkLeft(this.rCell);
    this.wCell2.linkRight(this.emptyCell);

    this.bCell.linkUp(this.wCell);
    this.bCell.linkDown(this.wCell4);
    this.bCell.linkLeft(this.emptyCell);
    this.bCell.linkRight(this.wCell3);

    this.wCell3.linkUp(this.rCell);
    this.wCell3.linkDown(this.rCell2);
    this.wCell3.linkLeft(this.bCell);
    this.wCell3.linkRight(this.bCell2);

    this.bCell2.linkUp(this.wCell2);
    this.bCell2.linkDown(this.wCell5);
    this.bCell2.linkLeft(this.wCell3);
    this.bCell2.linkRight(this.emptyCell);

    this.wCell4.linkUp(this.bCell);
    this.wCell4.linkDown(this.emptyCell);
    this.wCell4.linkLeft(this.emptyCell);
    this.wCell4.linkRight(this.rCell2);

    this.rCell2.linkUp(this.wCell3);
    this.rCell2.linkDown(this.emptyCell);
    this.rCell2.linkLeft(this.wCell4);
    this.rCell2.linkRight(this.wCell5);

    this.wCell5.linkUp(this.bCell2);
    this.wCell5.linkDown(this.emptyCell);
    this.wCell5.linkLeft(this.rCell2);
    this.wCell5.linkRight(this.emptyCell);

    this.b1 = new Bridgit(3);
    this.b2 = new Bridgit(11);
  }

  // testing constructor exceptions
  void testConstructor(Tester t) {
    t.checkConstructorException(
        new IllegalArgumentException("the number needs to be odd and greater than 3"), "Bridgit",
        1);
    t.checkConstructorException(
        new IllegalArgumentException("the number needs to be odd and greater than 3"), "Bridgit",
        2);
    t.checkConstructorException(
        new IllegalArgumentException("the number needs to be odd and greater than 3"), "Bridgit",
        4);
  }

  void testGame(Tester t) {
    this.init();
    Bridgit g = this.b2;
    g.bigBang(500, 500);
  }

  // testing linkUp method
  void testLinkUp(Tester t) {
    this.init();

    // testing the emptyCell
    this.emptyCell.linkUp(emptyCell);
    t.checkExpect(this.emptyCell, new EmptyCell());

    // testing the wCell
    this.wCell.linkUp(emptyCell);
    t.checkExpect(this.wCell.up, new EmptyCell());

    this.wCell.linkUp(this.rCell);
    t.checkExpect(this.wCell.up, this.rCell);

    // testing the rCell
    this.rCell.linkUp(this.rCell);
    t.checkExpect(this.rCell.up, this.rCell);

    this.rCell.linkUp(emptyCell);
    t.checkExpect(this.rCell.up, emptyCell);

    // testing the bCell
    this.bCell.linkUp(emptyCell);
    t.checkExpect(this.bCell.up, new EmptyCell());

    this.bCell.linkUp(this.bCell);
    t.checkExpect(this.bCell.up, this.bCell);
  }

  // testing linkDown method
  void testLinkDown(Tester t) {
    this.init();

    // testing the emptyCell
    this.emptyCell.linkDown(this.rCell);
    t.checkExpect(this.emptyCell, new EmptyCell());

    // testing the wCell
    this.wCell.linkDown(emptyCell);
    t.checkExpect(this.wCell.down, new EmptyCell());

    this.wCell.linkDown(this.rCell);
    t.checkExpect(this.wCell.down, this.rCell);

    // testing the rCell
    this.rCell.linkDown(this.rCell);
    t.checkExpect(this.rCell.down, this.rCell);

    this.rCell.linkDown(emptyCell);
    t.checkExpect(this.rCell.down, emptyCell);

    // testing the bCell
    this.bCell.linkDown(emptyCell);
    t.checkExpect(this.bCell.down, new EmptyCell());

    this.bCell.linkDown(this.bCell);
    t.checkExpect(this.bCell.down, this.bCell);
  }

  // testing linkLeft method
  void testLinkLeft(Tester t) {
    this.init();

    // testing the emptyCell
    this.emptyCell.linkUp(this.wCell);
    t.checkExpect(this.emptyCell, new EmptyCell());

    // testing the wCell
    this.wCell.linkLeft(emptyCell);
    t.checkExpect(this.wCell.left, new EmptyCell());

    this.wCell.linkLeft(this.rCell);
    t.checkExpect(this.wCell.left, this.rCell);

    // testing the rCell
    this.rCell.linkLeft(this.rCell);
    t.checkExpect(this.rCell.left, this.rCell);

    this.rCell.linkLeft(emptyCell);
    t.checkExpect(this.rCell.left, emptyCell);

    // testing the bCell
    this.bCell.linkLeft(emptyCell);
    t.checkExpect(this.bCell.left, new EmptyCell());

    this.bCell.linkLeft(this.bCell);
    t.checkExpect(this.bCell.left, this.bCell);
  }

  // testing linkRight method
  void testLinkRight(Tester t) {
    this.init();

    // testing the emptyCell
    this.emptyCell.linkRight(this.bCell);
    t.checkExpect(this.emptyCell, new EmptyCell());

    // testing the wCell
    this.wCell.linkRight(emptyCell);
    t.checkExpect(this.wCell.right, new EmptyCell());

    this.wCell.linkRight(this.rCell);
    t.checkExpect(this.wCell.right, this.rCell);

    // testing the rCell
    this.rCell.linkRight(this.rCell);
    t.checkExpect(this.rCell.right, this.rCell);

    this.rCell.linkRight(emptyCell);
    t.checkExpect(this.rCell.right, emptyCell);

    // testing the bCell
    this.bCell.linkRight(emptyCell);
    t.checkExpect(this.bCell.right, new EmptyCell());

    this.bCell.linkRight(this.bCell);
    t.checkExpect(this.bCell.right, this.bCell);
  }

  // testing drawAt method
  void testDrawAt(Tester t) {
    this.init();
    WorldImage board = new RectangleImage(100, 100, "solid", Color.white);

    t.checkExpect(this.wCell.drawAt(0, 0, 4, board), new OverlayOffsetAlign(AlignModeX.LEFT,
        AlignModeY.TOP, new RectangleImage(25, 25, "solid", Color.white), 0, 0, board));
    t.checkExpect(this.rCell.drawAt(0, 0, 4, board), new OverlayOffsetAlign(AlignModeX.LEFT,
        AlignModeY.TOP, new RectangleImage(25, 25, "solid", Color.red), 0, 0, board));
    t.checkExpect(this.bCell.drawAt(0, 0, 4, board), new OverlayOffsetAlign(AlignModeX.LEFT,
        AlignModeY.TOP, new RectangleImage(25, 25, "solid", Color.blue), 0, 0, board));
  }

  // testing drawAtHelper method
  void testDrawAtHelper(Tester t) {
    this.init();
    WorldImage board = new RectangleImage(100, 100, "solid", Color.white);

    t.checkExpect(this.wCell.drawAtHelper(0, 0, 4, board, Color.white),
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new RectangleImage(25, 25, "solid", Color.white), 0, 0, board));
    t.checkExpect(this.rCell.drawAtHelper(0, 0, 4, board, Color.red),
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new RectangleImage(25, 25, "solid", Color.red), 0, 0, board));
    t.checkExpect(this.bCell.drawAtHelper(0, 0, 4, board, Color.blue),
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new RectangleImage(25, 25, "solid", Color.blue), 0, 0, board));
  }

  // testing makeGame method
  void testMakeGame(Tester t) {
    this.init();

    t.checkExpect(this.b1.cells, cells);
  }

  // testing makeSceneTest method
  void testMakeSceneTest(Tester t) {
    this.init();

    ArrayList<Cell> row1 = new ArrayList<Cell>();
    row1.add(this.wCell);
    row1.add(this.rCell);
    row1.add(this.bCell);

    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
    cells.add(row1);

    Bridgit game1 = new Bridgit(3, cells);

    WorldScene scene = new WorldScene(400, 400);
    WorldImage board = new RectangleImage(scene.height, scene.width, "solid", Color.white);

    int edge = scene.height / game1.grid;
    int edge2 = edge / 2;
    int edge4 = edge / 4;

    WorldImage rCell = new RectangleImage(133, 133, "solid", Color.red);
    WorldImage bCell = new RectangleImage(133, 133, "solid", Color.blue);
    WorldImage wCell = new RectangleImage(133, 133, "solid", Color.white);

    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, wCell, 0 * wCell.getWidth(),
        0 * wCell.getHeight(), board);
    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, rCell, -1 * rCell.getWidth(),
        0 * rCell.getHeight(), board);
    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, bCell, -2 * bCell.getWidth(),
        0 * bCell.getHeight(), board);

    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new TextImage("P2", edge2, Color.blue), -edge4, -edge4, board);

    scene.placeImageXY(board, 200, 200);

    t.checkExpect(game1.makeSceneTest(400, 400), scene);

  }

  // testing onMouseClickTest method
  void testOnMouseClickTest(Tester t) {
    this.init();

    // clicking on the edges doesn't do anything
    this.b1.onMouseClickedTest(new Posn(5, 5), 30, 30);
    this.b1.onMouseClickedTest(new Posn(5, 15), 30, 30);
    this.b1.onMouseClickedTest(new Posn(5, 25), 30, 30);
    this.b1.onMouseClickedTest(new Posn(15, 5), 30, 30);
    this.b1.onMouseClickedTest(new Posn(25, 5), 30, 30);
    this.b1.onMouseClickedTest(new Posn(25, 15), 30, 30);
    this.b1.onMouseClickedTest(new Posn(25, 25), 30, 30);
    this.b1.onMouseClickedTest(new Posn(15, 25), 30, 30);
    t.checkExpect(this.b1.cells, this.cells);

    // the middle tile is a WCell so clicking on it will change it to
    // a RCell since it's player 1's turn
    this.init();
    this.b1.onMouseClickedTest(new Posn(15, 15), 30, 30);

    Cell changeTile = this.cells.get(1).remove(1);
    RCell newRCell = new RCell();

    newRCell.linkDown(changeTile.down);
    changeTile.down.linkUp(newRCell);
    newRCell.linkUp(changeTile.up);
    changeTile.up.linkDown(newRCell);
    newRCell.linkLeft(changeTile.left);
    changeTile.left.linkRight(newRCell);
    newRCell.linkRight(changeTile.right);
    changeTile.right.linkLeft(newRCell);

    this.cells.get(1).add(1, newRCell);

    t.checkExpect(this.b1.cells, this.cells);
  }

  // testing onKeyEvent method
  void testOnKeyEvent(Tester t) {
    this.init();

    // clicking on a random key does nothing
    this.b1.onKeyEvent("a");
    t.checkExpect(this.b1.cells, this.cells);

    // clicking on a random key does nothing
    this.b1.onKeyEvent("b");
    t.checkExpect(this.b1.cells, this.cells);

    // resets the game
    this.b1.cells.get(1).get(1).click(0, 1, 1, this.b1.cells);

    this.b1.onKeyEvent("r");
    t.checkExpect(this.b1.cells, this.cells);
  }

  // testing click method
  void testClick(Tester t) {
    this.init();

    t.checkExpect(this.wCell.click(0, 1, 1, this.cells), 1);
    t.checkExpect(this.rCell.click(0, 1, 1, this.cells), 0);
    t.checkExpect(this.bCell.click(1, 1, 1, this.cells), 1);
  }

  // testing addLinks method
  void testAddLinks(Tester t) {
    this.init();

    t.checkException(new RuntimeException("EmptyCell has no links"), this.emptyCell, "addLinks");

    ArrayList<Tile> arr = new ArrayList<Tile>();

    arr.add(this.wCell.up);
    arr.add(this.wCell.down);
    arr.add(this.wCell.left);
    arr.add(this.wCell.right);

    t.checkExpect(this.wCell.addLinks(), arr);

    ArrayList<Tile> arr2 = new ArrayList<Tile>();

    arr2.add(this.bCell.up);
    arr2.add(this.bCell.down);
    arr2.add(this.bCell.left);
    arr2.add(this.bCell.right);

    t.checkExpect(this.bCell.addLinks(), arr2);

    ArrayList<Tile> arr3 = new ArrayList<Tile>();

    arr3.add(this.rCell.up);
    arr3.add(this.rCell.down);
    arr3.add(this.rCell.left);
    arr3.add(this.rCell.right);

    t.checkExpect(this.rCell.addLinks(), arr3);
  }

  // testing checkWin method
  void testCheckWin(Tester t) {
    this.init();

    ArrayList<Tile> arr = new ArrayList<Tile>();

    arr.add(this.rCell);

    t.checkExpect(this.b1.checkWin(arr, 1), false);

    // player 1 wins
    arr.add(this.rCell);

    Cell changeTile = this.cells.get(1).remove(1);
    RCell newRCell = new RCell();

    newRCell.linkDown(changeTile.down);
    changeTile.down.linkUp(newRCell);
    newRCell.linkUp(changeTile.up);
    changeTile.up.linkDown(newRCell);
    newRCell.linkLeft(changeTile.left);
    changeTile.left.linkRight(newRCell);
    newRCell.linkRight(changeTile.right);
    changeTile.right.linkLeft(newRCell);

    this.cells.get(1).add(1, newRCell);

    this.b1.onMouseClickedTest(new Posn(15, 15), 30, 30);

    t.checkExpect(this.b1.checkWin(arr, 1), true);
    t.checkExpect(this.b1.checkWin(arr, 2), false);

    // player 2 wins
    this.init();

    arr.add(this.bCell);

    Cell changeTile2 = this.cells.get(1).remove(1);
    BCell newBCell = new BCell();

    newBCell.linkDown(changeTile2.down);
    changeTile2.down.linkUp(newBCell);
    newBCell.linkUp(changeTile2.up);
    changeTile2.up.linkDown(newBCell);
    newBCell.linkLeft(changeTile2.left);
    changeTile2.left.linkRight(newBCell);
    newBCell.linkRight(changeTile2.right);
    changeTile2.right.linkLeft(newBCell);

    this.cells.get(1).add(1, newBCell);

    this.b1.onMouseClickedTest(new Posn(15, 15), 30, 30);

    this.b1.turn = 2;

    t.checkExpect(this.b1.checkWin(arr, 2), true);
    t.checkExpect(this.b1.checkWin(arr, 1), false);

  }

  // testing lastScene method
  void testLastScene(Tester t) {
    this.init();

    WorldScene scene = this.b1.makeScene();
    scene.placeImageXY(new TextImage("a", 30, Color.BLACK), scene.height / 2, scene.width / 2);

    t.checkExpect(this.b1.lastScene("a"), scene);
  }

  // testing isTarget method
  void testIsTarget(Tester t) {
    this.init();

    t.checkExpect(this.wCell.isTarget(), false);
    t.checkExpect(this.wCell2.isTarget(), false);
    t.checkExpect(this.bCell.isTarget(), false);
    t.checkExpect(this.rCell.isTarget(), false);
    t.checkExpect(this.bCell2.isTarget(), true);
    t.checkExpect(this.rCell2.isTarget(), true);
  }

  // testing makeTarget method
  void testMakeTarget(Tester t) {
    this.init();

    t.checkExpect(this.wCell.target, false);
    t.checkExpect(this.wCell2.target, false);
    t.checkExpect(this.bCell.target, false);
    t.checkExpect(this.rCell.target, false);
    t.checkExpect(this.bCell2.target, true);
    t.checkExpect(this.rCell2.target, true);
  }

  // testing path method
  void testPath(Tester t) {
    this.init();

    t.checkExpect(this.emptyCell.path(1), false);
    t.checkExpect(this.wCell.path(1), false);
    t.checkExpect(this.rCell.path(1), true);
    t.checkExpect(this.rCell.path(2), false);
    t.checkExpect(this.bCell.path(1), false);
    t.checkExpect(this.bCell.path(2), true);
  }

}
