import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;

import javalib.worldimages.*;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

//Represents an individual tile
class Tile {
  // The number on the tile. Use 0 to represent the hole
  int value;

  Tile(int value) {
    this.value = value;
  }

  // Draws this tile onto the background at the specified logical coordinates
  WorldImage drawAt(int col, int row, WorldImage background) {
    Double size = background.getWidth();

    WorldImage draw = new OverlayImage(
        new TextImage(this.value + "", size.intValue() / 15, Color.black),
        new RectangleImage(size.intValue() / 4, size.intValue() / 4, "outline", Color.black));

    return new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, draw, -col * draw.getWidth(),
        -row * draw.getHeight(), background);
  }

  // determines if the value of this tile and the given tiles are the same
  public boolean isEqual(Tile t) {
    return this.value == t.value;
  }

}

class FifteenGame extends World {
  // represents the rows of tiles
  ArrayList<ArrayList<Tile>> tiles;
  ArrayList<String> keyInputs;

  // original constructor
  FifteenGame(ArrayList<ArrayList<Tile>> tiles) {
    this.tiles = tiles;
    this.keyInputs = new ArrayList<String>();
  }

  // for generating random tiles
  FifteenGame() {
    this(new ArrayList<ArrayList<Tile>>());

    this.makeTile();

  }

  // for testing
  FifteenGame(ArrayList<ArrayList<Tile>> tiles, ArrayList<String> keyInputs) {
    this.tiles = tiles;
    this.keyInputs = keyInputs;
  }

  // draws the game
  public WorldScene makeScene() {
    WorldScene scene = this.getEmptyScene();

    WorldImage board = new RectangleImage(scene.height, scene.width, "solid", Color.white);

    for (int i = 0; i < this.tiles.size(); i++) {
      for (int j = 0; j < this.tiles.get(0).size(); j++) {
        board = this.tiles.get(i).get(j).drawAt(j, i, board);
      }
    }

    scene.placeImageXY(board, scene.width / 2, scene.height / 2);

    return scene;
  }

  // make the 2d ArrayList of Tiles from 0 to 15
  public void makeTile() {
    Random r = new Random();

    ArrayList<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < 16; i++) {
      Tile tile = new Tile(i);
      tiles.add(tile);
    }

    for (int i = 0; i < 4; i++) {
      this.tiles.add(new ArrayList<Tile>());
    }

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        int randomIndex = r.nextInt(tiles.size());
        this.tiles.get(i).add(tiles.remove(randomIndex));
      }
    }
  }

  // draws the game with a given canvas width and height
  // I'm doing this because makeScene() is dependent on the canvas size that
  // bigbang provides so if we don't initialize bigbang
  public WorldScene makeSceneTest(int width, int height) {
    WorldScene scene = new WorldScene(width, height);

    WorldImage board = new RectangleImage(scene.height, scene.width, "solid", Color.white);

    for (int i = 0; i < this.tiles.size(); i++) {
      for (int j = 0; j < this.tiles.get(0).size(); j++) {
        board = this.tiles.get(i).get(j).drawAt(j, i, board);
      }
    }

    scene.placeImageXY(board, scene.width / 2, scene.height / 2);

    return scene;
  }

  // handles keystrokes and checks if the game has ended
  // EFFECT: move the hole (tile 0) to the given input
  // either up, down, left or right. If "u" is pressed undo the last move.
  // Store all the key presses except for invalid moves/inputs and remove key
  // inputs
  // if "u" was clicked
  public void onKeyEvent(String k) {
    ArrayUtils u = new ArrayUtils();
    Predicate<Tile> isZero = new IsZero();
    ArrayList<Integer> zeroLoc = u.holeLoc(this.tiles, isZero);

    if (k.equals("up")) {
      if (zeroLoc.get(0) > 0) {
        u.swap(this.tiles, zeroLoc.get(0), zeroLoc.get(1), zeroLoc.get(0) - 1, zeroLoc.get(1));
        this.keyInputs.add(0, k);
      }
    }
    else if (k.equals("down")) {
      if (zeroLoc.get(0) < 3) {
        u.swap(this.tiles, zeroLoc.get(0), zeroLoc.get(1), zeroLoc.get(0) + 1, zeroLoc.get(1));
        this.keyInputs.add(0, k);
      }
    }
    else if (k.equals("right")) {
      if (zeroLoc.get(1) < 3) {
        u.swap(this.tiles, zeroLoc.get(0), zeroLoc.get(1), zeroLoc.get(0), zeroLoc.get(1) + 1);
        this.keyInputs.add(0, k);
      }
    }
    else if (k.equals("left")) {
      if (zeroLoc.get(1) > 0) {
        u.swap(this.tiles, zeroLoc.get(0), zeroLoc.get(1), zeroLoc.get(0), zeroLoc.get(1) - 1);
        this.keyInputs.add(0, k);
      }
    }
    else if (k.equals("u")) {
      if (this.keyInputs.size() > 0) {
        String lastKey = this.keyInputs.remove(0);

        if (lastKey.equals("up")) {
          this.onKeyEvent("down");
          this.keyInputs.remove(0);
        }
        else if (lastKey.equals("down")) {
          this.onKeyEvent("up");
          this.keyInputs.remove(0);
        }
        else if (lastKey.equals("left")) {
          this.onKeyEvent("right");
          this.keyInputs.remove(0);
        }
        else if (lastKey.equals("right")) {
          this.onKeyEvent("left");
          this.keyInputs.remove(0);
        }
      }
    }
    else {
      // do nothing
    }

    this.winCon();

  }

  // EFFECT: End the game if the player has put the tiles in order
  public void winCon() {
    ArrayUtils u = new ArrayUtils();

    // making the tiles in an ArrayList
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    for (int i = 1; i < 16; i++) {
      Tile tile = new Tile(i);
      tiles.add(tile);
    }

    ArrayList<Tile> tilesTemp = new ArrayList<Tile>();

    for (int i = 0; i < 4; i++) {
      tilesTemp.addAll(this.tiles.get(i));
    }

    if (u.containsSequence(tilesTemp, tiles, new SameTile())) {
      this.endOfWorld("You win!");
    }
  }

  // produce the lastScene of the game with a given message
  public WorldScene lastScene(String str) {
    WorldScene scene = this.getEmptyScene();
    scene.placeImageXY(new TextImage(str, 30, Color.BLACK), scene.height / 2, scene.width / 2);

    return scene;
  }
}

class ArrayUtils {
  // EFFECT: swaps the items in the list at the given indices
  <T> void swap(ArrayList<ArrayList<T>> alist, int row1, int col1, int row2, int col2) {
    alist.get(row2).set(col2, alist.get(row1).set(col1, alist.get(row2).get(col2)));
  }

  // returns the row and column index of the element that passes the predicate
  // EFFECT: finds the row and column index of the the element that passes the
  // predicate
  <T> ArrayList<Integer> holeLoc(ArrayList<ArrayList<T>> alist, Predicate<T> pred) {
    ArrayList<Integer> temp = new ArrayList<Integer>();

    for (int i = 0; i < alist.size(); i++) {
      for (int j = 0; j < alist.get(0).size(); j++) {
        if (pred.test(alist.get(i).get(j))) {
          temp.add(i);
          temp.add(j);
        }
      }
    }

    return temp;
  }

  // determines if the the source contains the sequence
  // just used the code that was made for the exam practice
  <T> boolean containsSequence(ArrayList<T> source, ArrayList<T> sequence, BiPredicate<T, T> pred) {
    for (int h = 0; h < source.size() - sequence.size() + 1; h += 1) {
      boolean found = true;
      for (int n = 0; n < sequence.size(); n += 1) {
        if (!pred.test(source.get(h + n), sequence.get(n))) {
          found = false;
        }
      }
      if (found) {
        return true;
      }
    }
    return false;
  }
}

//Predicate function for checking if a tile is the hole
class IsZero implements Predicate<Tile> {

  // is the given tile a hole?
  public boolean test(Tile t) {
    return t.value == 0;
  }
}

//BiPredicate function for determining if 2 tiles have the same value
class SameTile implements BiPredicate<Tile, Tile> {

  // check if two tiles are the same
  public boolean test(Tile t1, Tile t2) {
    return t1.isEqual(t2);
  }
}

class ExamplesFifteenGame {

  Tile tile0;
  Tile tile1;
  Tile tile2;
  Tile tile3;
  Tile tile4;
  Tile tile5;
  Tile tile6;
  Tile tile7;
  Tile tile8;
  Tile tile9;
  Tile tile10;
  Tile tile11;
  Tile tile12;
  Tile tile13;
  Tile tile14;
  Tile tile15;

  ArrayList<Tile> row1;
  ArrayList<Tile> row2;
  ArrayList<Tile> row3;
  ArrayList<Tile> row4;

  ArrayList<Tile> rowA;
  ArrayList<Tile> rowB;
  ArrayList<Tile> rowC;
  ArrayList<Tile> rowD;

  ArrayList<ArrayList<Tile>> lol;
  ArrayList<ArrayList<Tile>> lolA;

  FifteenGame game;
  FifteenGame game1;
  FifteenGame game2;

  ArrayUtils u = new ArrayUtils();
  IsZero iszero = new IsZero();
  SameTile sameTile = new SameTile();

  void init() {
    this.tile0 = new Tile(0);
    this.tile1 = new Tile(1);
    this.tile2 = new Tile(2);
    this.tile3 = new Tile(3);
    this.tile4 = new Tile(4);
    this.tile5 = new Tile(5);
    this.tile6 = new Tile(6);
    this.tile7 = new Tile(7);
    this.tile8 = new Tile(8);
    this.tile9 = new Tile(9);
    this.tile10 = new Tile(10);
    this.tile11 = new Tile(11);
    this.tile12 = new Tile(12);
    this.tile13 = new Tile(13);
    this.tile14 = new Tile(14);
    this.tile15 = new Tile(15);

    this.row1 = new ArrayList<Tile>();
    this.row1.add(tile6);
    this.row1.add(tile13);
    this.row1.add(tile7);
    this.row1.add(tile10);

    this.row2 = new ArrayList<Tile>();
    this.row2.add(tile8);
    this.row2.add(tile9);
    this.row2.add(tile11);
    this.row2.add(tile0);

    this.row3 = new ArrayList<Tile>();
    this.row3.add(tile15);
    this.row3.add(tile2);
    this.row3.add(tile12);
    this.row3.add(tile5);

    this.row4 = new ArrayList<Tile>();
    this.row4.add(tile14);
    this.row4.add(tile3);
    this.row4.add(tile1);
    this.row4.add(tile4);

    this.lol = new ArrayList<ArrayList<Tile>>();
    this.lol.add(row1);
    this.lol.add(row2);
    this.lol.add(row3);
    this.lol.add(row4);

    this.game = new FifteenGame(this.lol);
    this.game1 = new FifteenGame();

    this.rowA = new ArrayList<Tile>();
    this.rowA.add(tile0);
    this.rowA.add(tile1);
    this.rowA.add(tile2);
    this.rowA.add(tile3);

    this.rowB = new ArrayList<Tile>();
    this.rowB.add(tile4);
    this.rowB.add(tile5);
    this.rowB.add(tile6);
    this.rowB.add(tile7);

    this.rowC = new ArrayList<Tile>();
    this.rowC.add(tile8);
    this.rowC.add(tile9);
    this.rowC.add(tile10);
    this.rowC.add(tile11);

    this.rowD = new ArrayList<Tile>();
    this.rowD.add(tile12);
    this.rowD.add(tile13);
    this.rowD.add(tile14);
    this.rowD.add(tile15);

    this.lolA = new ArrayList<ArrayList<Tile>>();
    this.lolA.add(rowA);
    this.lolA.add(rowB);
    this.lolA.add(rowC);
    this.lolA.add(rowD);

    this.game2 = new FifteenGame(this.lolA);
  }

  // the game
  void testGame(Tester t) {
    this.init();
    FifteenGame g = new FifteenGame(this.lolA);
    g.bigBang(700, 700);
  }

  // testing drawAt method
  void testDrawAt(Tester t) {
    this.init();
    WorldImage board = new RectangleImage(100, 100, "solid", Color.white);

    t.checkExpect(this.tile0.drawAt(0, 1, board),
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new OverlayImage(new TextImage(0 + "", 6, Color.black),
                new RectangleImage(25, 25, "outline", Color.black)),
            0, -25, board));
    t.checkExpect(this.tile1.drawAt(3, 2, board),
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new OverlayImage(new TextImage(1 + "", 6, Color.black),
                new RectangleImage(25, 25, "outline", Color.black)),
            -75, -50, board));
    t.checkExpect(this.tile4.drawAt(2, 1, board),
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new OverlayImage(new TextImage(4 + "", 6, Color.black),
                new RectangleImage(25, 25, "outline", Color.black)),
            -50, -25, board));
  }

  // testing makeSceneTest method
  void testMakeSceneTest(Tester t) {
    this.init();

    ArrayList<Tile> row5 = new ArrayList<Tile>();
    row5.add(this.tile1);
    row5.add(this.tile2);
    row5.add(this.tile3);
    row5.add(this.tile4);

    ArrayList<ArrayList<Tile>> lol = new ArrayList<ArrayList<Tile>>();
    lol.add(row5);

    FifteenGame game1 = new FifteenGame(lol);

    WorldScene scene = new WorldScene(400, 400);
    WorldImage board = new RectangleImage(scene.height, scene.width, "solid", Color.white);

    Double size = board.getWidth();

    WorldImage tile1 = new OverlayImage(new TextImage(1 + "", size.intValue() / 15, Color.black),
        new RectangleImage(size.intValue() / 4, size.intValue() / 4, "outline", Color.black));

    WorldImage tile2 = new OverlayImage(new TextImage(2 + "", size.intValue() / 15, Color.black),
        new RectangleImage(size.intValue() / 4, size.intValue() / 4, "outline", Color.black));

    WorldImage tile3 = new OverlayImage(new TextImage(3 + "", size.intValue() / 15, Color.black),
        new RectangleImage(size.intValue() / 4, size.intValue() / 4, "outline", Color.black));

    WorldImage tile4 = new OverlayImage(new TextImage(4 + "", size.intValue() / 15, Color.black),
        new RectangleImage(size.intValue() / 4, size.intValue() / 4, "outline", Color.black));

    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, tile1, 0 * tile1.getWidth(),
        0 * tile1.getHeight(), board);
    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, tile2, -1 * tile1.getWidth(),
        0 * tile1.getHeight(), board);
    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, tile3, -2 * tile1.getWidth(),
        0 * tile1.getHeight(), board);
    board = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, tile4, -3 * tile1.getWidth(),
        0 * tile1.getHeight(), board);

    scene.placeImageXY(board, 200, 200);

    t.checkExpect(game1.makeSceneTest(400, 400), scene);

  }

  // testing onKeyEvent method
  void testOnKeyEvent(Tester t) {
    this.init();

    // moves the hole to the top right corner
    ArrayList<Tile> row5 = new ArrayList<Tile>();
    row5.add(this.tile6);
    row5.add(this.tile13);
    row5.add(this.tile7);
    row5.add(this.tile0);

    ArrayList<Tile> row6 = new ArrayList<Tile>();
    row6.add(tile8);
    row6.add(tile9);
    row6.add(tile11);
    row6.add(tile10);

    ArrayList<Tile> row7 = new ArrayList<Tile>();
    row7.add(tile15);
    row7.add(tile2);
    row7.add(tile12);
    row7.add(tile5);

    ArrayList<Tile> row8 = new ArrayList<Tile>();
    row8.add(tile14);
    row8.add(tile3);
    row8.add(tile1);
    row8.add(tile4);

    ArrayList<ArrayList<Tile>> lol = new ArrayList<ArrayList<Tile>>();
    lol.add(row5);
    lol.add(row6);
    lol.add(row7);
    lol.add(row8);

    ArrayList<String> keys = new ArrayList<String>(Arrays.asList("up"));

    this.game.onKeyEvent("up");
    t.checkExpect(this.game, new FifteenGame(lol, keys));

    // Since it's at the top right corner moving up doesn't do anything
    this.game.onKeyEvent("up");
    t.checkExpect(this.game, new FifteenGame(lol, keys));

    // Since it's at the top right corner moving right doesn't do anything
    this.game.onKeyEvent("right");
    t.checkExpect(this.game, new FifteenGame(lol, keys));

    // Moving the hole down returns the hole to it's original position
    // but the key inputs is added
    this.game.onKeyEvent("down");
    keys.add(0, "down");
    t.checkExpect(this.game, new FifteenGame(this.lol, keys));

    // moves the hole to down
    ArrayList<Tile> row9 = new ArrayList<Tile>();
    row9.add(this.tile6);
    row9.add(this.tile13);
    row9.add(this.tile7);
    row9.add(this.tile10);

    ArrayList<Tile> row10 = new ArrayList<Tile>();
    row10.add(tile8);
    row10.add(tile9);
    row10.add(tile11);
    row10.add(tile5);

    ArrayList<Tile> row11 = new ArrayList<Tile>();
    row11.add(tile15);
    row11.add(tile2);
    row11.add(tile12);
    row11.add(tile0);

    ArrayList<Tile> row12 = new ArrayList<Tile>();
    row12.add(tile14);
    row12.add(tile3);
    row12.add(tile1);
    row12.add(tile4);

    ArrayList<ArrayList<Tile>> lol2 = new ArrayList<ArrayList<Tile>>();
    lol2.add(row9);
    lol2.add(row10);
    lol2.add(row11);
    lol2.add(row12);

    this.game.onKeyEvent("down");
    keys.add(0, "down");
    t.checkExpect(this.game, new FifteenGame(lol2, keys));

    // moves the hole to down
    ArrayList<Tile> row13 = new ArrayList<Tile>();
    row13.add(this.tile6);
    row13.add(this.tile13);
    row13.add(this.tile7);
    row13.add(this.tile10);

    ArrayList<Tile> row14 = new ArrayList<Tile>();
    row14.add(tile8);
    row14.add(tile9);
    row14.add(tile11);
    row14.add(tile5);

    ArrayList<Tile> row15 = new ArrayList<Tile>();
    row15.add(tile15);
    row15.add(tile2);
    row15.add(tile12);
    row15.add(tile4);

    ArrayList<Tile> row16 = new ArrayList<Tile>();
    row16.add(tile14);
    row16.add(tile3);
    row16.add(tile1);
    row16.add(tile0);

    ArrayList<ArrayList<Tile>> lol3 = new ArrayList<ArrayList<Tile>>();
    lol3.add(row13);
    lol3.add(row14);
    lol3.add(row15);
    lol3.add(row16);

    this.game.onKeyEvent("down");
    keys.add(0, "down");
    t.checkExpect(this.game, new FifteenGame(lol3, keys));

    // since the hole is at the bottom right corner it cannot go down
    this.game.onKeyEvent("down");
    t.checkExpect(this.game, new FifteenGame(lol3, keys));

    // moves the hole to the left
    ArrayList<Tile> row17 = new ArrayList<Tile>();
    row17.add(this.tile6);
    row17.add(this.tile13);
    row17.add(this.tile7);
    row17.add(this.tile10);

    ArrayList<Tile> row18 = new ArrayList<Tile>();
    row18.add(tile8);
    row18.add(tile9);
    row18.add(tile11);
    row18.add(tile5);

    ArrayList<Tile> row19 = new ArrayList<Tile>();
    row19.add(tile15);
    row19.add(tile2);
    row19.add(tile12);
    row19.add(tile4);

    ArrayList<Tile> row20 = new ArrayList<Tile>();
    row20.add(tile14);
    row20.add(tile3);
    row20.add(tile0);
    row20.add(tile1);

    ArrayList<ArrayList<Tile>> lol4 = new ArrayList<ArrayList<Tile>>();
    lol4.add(row17);
    lol4.add(row18);
    lol4.add(row19);
    lol4.add(row20);

    this.game.onKeyEvent("left");
    keys.add(0, "left");
    t.checkExpect(this.game, new FifteenGame(lol4, keys));

    // moves the hole to the right
    this.game.onKeyEvent("right");
    keys.add(0, "right");
    t.checkExpect(this.game, new FifteenGame(lol3, keys));

    // moves the hole to the left
    this.game.onKeyEvent("left");
    keys.add(0, "left");
    t.checkExpect(this.game, new FifteenGame(lol4, keys));

    // moves the hole to the left
    ArrayList<Tile> row21 = new ArrayList<Tile>();
    row21.add(this.tile6);
    row21.add(this.tile13);
    row21.add(this.tile7);
    row21.add(this.tile10);

    ArrayList<Tile> row22 = new ArrayList<Tile>();
    row22.add(tile8);
    row22.add(tile9);
    row22.add(tile11);
    row22.add(tile5);

    ArrayList<Tile> row23 = new ArrayList<Tile>();
    row23.add(tile15);
    row23.add(tile2);
    row23.add(tile12);
    row23.add(tile4);

    ArrayList<Tile> row24 = new ArrayList<Tile>();
    row24.add(tile14);
    row24.add(tile0);
    row24.add(tile3);
    row24.add(tile1);

    ArrayList<ArrayList<Tile>> lol5 = new ArrayList<ArrayList<Tile>>();
    lol5.add(row21);
    lol5.add(row22);
    lol5.add(row23);
    lol5.add(row24);

    this.game.onKeyEvent("left");
    keys.add(0, "left");
    t.checkExpect(this.game, new FifteenGame(lol5, keys));

    // moves the hole to the left
    ArrayList<Tile> row25 = new ArrayList<Tile>();
    row25.add(this.tile6);
    row25.add(this.tile13);
    row25.add(this.tile7);
    row25.add(this.tile10);

    ArrayList<Tile> row26 = new ArrayList<Tile>();
    row26.add(tile8);
    row26.add(tile9);
    row26.add(tile11);
    row26.add(tile5);

    ArrayList<Tile> row27 = new ArrayList<Tile>();
    row27.add(tile15);
    row27.add(tile2);
    row27.add(tile12);
    row27.add(tile4);

    ArrayList<Tile> row28 = new ArrayList<Tile>();
    row28.add(tile0);
    row28.add(tile14);
    row28.add(tile3);
    row28.add(tile1);

    ArrayList<ArrayList<Tile>> lol6 = new ArrayList<ArrayList<Tile>>();
    lol6.add(row25);
    lol6.add(row26);
    lol6.add(row27);
    lol6.add(row28);

    this.game.onKeyEvent("left");
    keys.add(0, "left");
    t.checkExpect(this.game, new FifteenGame(lol6, keys));

    // Since the hole is on the leftmost corner, it cannot go left anymore
    this.game.onKeyEvent("left");
    t.checkExpect(this.game, new FifteenGame(lol6, keys));

    // testing the undo
    // undo 1
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(lol5, keys));

    // undo 2
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(lol4, keys));

    // undo 3
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(lol3, keys));

    // undo 4
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(lol4, keys));

    // undo 5
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(lol3, keys));

    // undo 6
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(lol2, keys));

    // undo 7
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(this.lol, keys));

    // undo 8 - reached the beginning
    this.game.onKeyEvent("u");
    keys.remove(0);
    t.checkExpect(this.game, new FifteenGame(this.lol, keys));

    // nothing happens when you undo in the beginning
    this.game.onKeyEvent("u");
    t.checkExpect(this.game, new FifteenGame(this.lol));

    // nothing happens when you click a random key
    this.game.onKeyEvent("a");
    t.checkExpect(this.game, new FifteenGame(this.lol));
  }

  // testing swap method
  void testSwap(Tester t) {
    this.init();

    // swapping the first and second element
    t.checkExpect(this.lol, this.lol);
    this.u.swap(this.lol, 0, 0, 0, 1);

    ArrayList<Tile> row5 = new ArrayList<Tile>();
    row5.add(this.tile13);
    row5.add(this.tile6);
    row5.add(this.tile7);
    row5.add(this.tile10);

    ArrayList<Tile> row6 = new ArrayList<Tile>();
    row6.add(tile8);
    row6.add(tile9);
    row6.add(tile11);
    row6.add(tile0);

    ArrayList<Tile> row7 = new ArrayList<Tile>();
    row7.add(tile15);
    row7.add(tile2);
    row7.add(tile12);
    row7.add(tile5);

    ArrayList<Tile> row8 = new ArrayList<Tile>();
    row8.add(tile14);
    row8.add(tile3);
    row8.add(tile1);
    row8.add(tile4);

    ArrayList<ArrayList<Tile>> lol = new ArrayList<ArrayList<Tile>>();
    lol.add(row5);
    lol.add(row6);
    lol.add(row7);
    lol.add(row8);

    t.checkExpect(this.lol, lol);

    // swapping the last and first element
    this.init();

    t.checkExpect(this.lol, this.lol);
    this.u.swap(this.lol, 0, 0, 3, 3);

    ArrayList<Tile> row9 = new ArrayList<Tile>();
    row9.add(this.tile4);
    row9.add(this.tile13);
    row9.add(this.tile7);
    row9.add(this.tile10);

    ArrayList<Tile> row10 = new ArrayList<Tile>();
    row10.add(tile8);
    row10.add(tile9);
    row10.add(tile11);
    row10.add(tile0);

    ArrayList<Tile> row11 = new ArrayList<Tile>();
    row11.add(tile15);
    row11.add(tile2);
    row11.add(tile12);
    row11.add(tile5);

    ArrayList<Tile> row12 = new ArrayList<Tile>();
    row12.add(tile14);
    row12.add(tile3);
    row12.add(tile1);
    row12.add(tile6);

    ArrayList<ArrayList<Tile>> lol2 = new ArrayList<ArrayList<Tile>>();
    lol2.add(row9);
    lol2.add(row10);
    lol2.add(row11);
    lol2.add(row12);

    t.checkExpect(this.lol, lol2);
  }

  // testing holeLoc method
  void testHoleLoc(Tester t) {
    this.init();

    t.checkExpect(this.u.holeLoc(this.lol, this.iszero),
        new ArrayList<Integer>(Arrays.asList(1, 3)));

    // switching the position of the hole
    this.u.swap(this.lol, 1, 3, 0, 0);

    t.checkExpect(this.u.holeLoc(this.lol, this.iszero),
        new ArrayList<Integer>(Arrays.asList(0, 0)));
  }

  // testing test method
  void testTest(Tester t) {
    this.init();

    t.checkExpect(this.iszero.test(tile0), true);
    t.checkExpect(this.iszero.test(tile1), false);
    t.checkExpect(this.iszero.test(tile2), false);
  }

  // testing containsSequence method
  void testContainsSequence(Tester t) {
    this.init();

    // making the tiles in an ArrayList
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < 16; i++) {
      Tile tile = new Tile(i);
      tiles.add(tile);
    }

    t.checkExpect(this.u.containsSequence(tiles, tiles, this.sameTile), true);
  }

  // testing sameTile test method
  void testSameTile(Tester t) {
    this.init();

    t.checkExpect(this.sameTile.test(tile1, tile0), false);
    t.checkExpect(this.sameTile.test(tile1, tile2), false);
    t.checkExpect(this.sameTile.test(tile0, tile0), true);
  }

  // testing isEqual method
  void testIsEqual(Tester t) {
    this.init();

    t.checkExpect(this.tile0.isEqual(tile0), true);
    t.checkExpect(this.tile1.isEqual(tile0), false);
    t.checkExpect(this.tile1.isEqual(tile2), false);
  }

  // testing winCon method
  void testWinCon(Tester t) {
    this.init();

    this.game2.onKeyEvent("down");
    this.game2.onKeyEvent("up");
    this.game2.winCon();
    t.checkExpect(this.game2, this.game2);
  }
}
