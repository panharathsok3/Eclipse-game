import tester.*;                
import javalib.worldimages.*;   
import javalib.funworld.*;      
import java.awt.Color;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

//an interface to represent a game piece in the game
interface IGamePiece {
  //produce a new game piece that is shifted by x an y from this IGamePiece
  IGamePiece move(String key);
  
  //draws this IGamePiece onto the canvas
  WorldImage draw();
  
  //returns the x coordinate of this IGamPiece
  int getX();
  
  //returns the y coordinate of this IGamPiece
  int getY();
  
  //returns the size of this IGamPiece
  int getSize();
  
  //returns the color of this IGamePiece
  Color getColor();
  
  //returns the speed of this IGamePiece
  int getSpeed();
  
  //loops the player to the other side of the screen when they move over the
  //width of the canvas
  int loopX(int x);
  
  //loops the player to the other side of the screen when they move over the
  //height of the canvas
  int loopY(int y);
  
  //compare the size of the given IGamePiece to this IGamePiece
  int compareSize(IGamePiece fish);
  
  //check if the given IGamePiece is collided with this IGamePiece
  boolean collide(IGamePiece fish);
  
  //compute the distance between the given IGamePiece and this IGamePiece
  double distance(IGamePiece fish);
  
  //produce a new FrenzyWorld with the increased size for the player fish
  //and moving the location of the fishes to its next positions
  FrenzyWorld eat(IList<IGamePiece> fishes, IGamePiece fish);
  
  //determines if this IGamePiece is the same as the given IGamePiece
  boolean sameFish(IGamePiece fish);
  
  //determines if this IGamePiece is a fish or a Snack
  boolean fishOrSnack();
}

//abstract class for a game piece
abstract class AGamePiece implements IGamePiece {
  int x;
  int y;
  Color color;
  int size;
  int speed;
  int height = 400;
  int width = 600;
  
  //original constructor
  AGamePiece(int x, int y, Color col, int size, int speed) {
    this.x = x;
    this.y = y;
    this.color = col;
    this.size = size;
    this.speed = speed;
  }
  
  //for in game use
  AGamePiece(Color col, int size, int speed) {
    Random r = new Random();
    this.x = r.nextInt(this.height);
    this.y = r.nextInt(this.width);
    this.color = col;
    this.size = size;
    if (r.nextInt(2) == 0) {
      this.speed = speed;
    }
    else {
      this.speed = -1 * speed;
    }
    
    
  }
  
  //for testing
  AGamePiece(Color col, int size, int speed, Random r) {
    this.x = r.nextInt(this.height);
    this.y = r.nextInt(this.width);
    this.color = col;
    this.size = size;
    this.speed = speed;
  }
  
  //for player's fish
  AGamePiece() {
    this.x = this.width / 2;
    this.y = this.height / 2;
    this.color = Color.green;
    this.size = 20;
    this.speed = 20;
  }
  
  /* fields:
   *  this.x ...   int
   *  this.y ...   int
   *  this.color ...   Color
   *  this.size ...   int
   *  this.speed ...   int
   * methods:
   *  this.move(String) ...   IGamePiece
   *  this.draw() ...   WorldImage
   *  this.getX() ...   int
   *  this.getY() ...   int
   *  this.getSize() ...   int
   *  this.getColor() ...   Color
   *  this.getSpeed() ...   int
   *  this.move() ...   IGamePiece
   *  this.loopX(int) ...   int
   *  this.loopY(int) ...   int
   *  this.compareSize(IGamePiece) ...   int
   *  this.collide(IGamePiece) ...   boolean
   *  this.distance(IGamePiece) ...   double
   *  this.eat(IList<IGamePiece>, IGamePiece) ...   FrenzyWorld
   *  this.sameFish(IGamePiece fish) ...   boolean
   *  this.fishOrSnack() ...   boolean
   */
  
  //returns the x coordinate of this AGamePiece
  public int getX() {
    return this.x;
  }
  
  //returns the y coordinate of this AGamePiece
  public int getY() {
    return this.y;
  }
  
  //returns the size of this AGamePiece
  public int getSize() {
    return this.size;
  }
  
  //returns the color of this AGamePiece
  public Color getColor() {
    return this.color;
  }
  
  //returns the speed of this AGamePiece
  public int getSpeed() {
    return this.speed;
  }
  
  //abstract method for draw()
  public abstract WorldImage draw();
  
  //abstract method for move
  public abstract IGamePiece move(String key);
  
  //loops the player to the other side of the screen when they move over the
  //width of the canvas
  public int loopX(int x) {
    if (x - this.width > 0) {
      return x - this.width;
    }
    else if (x < 0) {
      return x + this.width;
    }
    else {
      return x;
    }
  }
  
  //loops the player to the other side of the screen when they move over the
  //height of the canvas
  public int loopY(int y) {
    if (y - this.height > 0) {
      return y - this.height;
    }
    else if (y < 0) {
      return y + this.height;
    }
    else {
      return y;
    }
  }
  
  //returns:
  //<0 if this fish is lesser than the size of the given fish
  //0 if this fish is the same size as the given fish
  //>0 if this fish is bigger than than the size of the given fish
  public int compareSize(IGamePiece fish) {
    return this.size - fish.getSize();
  }
  
  //check if the given IGamePiece collides to this AGamePiece
  public boolean collide(IGamePiece fish) {
    return this.distance(fish) <= this.size + fish.getSize();
  }
  
  //computes the distance between the given IGamePiece and this AGamePiece 
  public double distance(IGamePiece fish) {
    return Math.sqrt(Math.pow((this.x - fish.getX()), 2) 
        + Math.pow((this.y - fish.getY()), 2));
  }
  
  //abstract method for eat that produces the FrenzyWorld
  public abstract FrenzyWorld eat(IList<IGamePiece> fishes, IGamePiece fish);
  
  //check if the given AGamePiece is the same to this AGamePiece
  public boolean sameFish(IGamePiece fish) {
    return this.x == fish.getX()
        && this.y == fish.getY()
        && this.color.equals(fish.getColor())
        && this.size == fish.getSize()
        && this.speed == fish.getSpeed();
  }
  
  //determines if this AGamePiece is a fish or a snack
  public boolean fishOrSnack() {
    return true;
  }
}

//a class to represent the player's fish in the game
class Player extends AGamePiece {
  
  //original constructor
  Player(int x, int y, Color col, int size, int speed) {
    super(x, y, col, size, speed);
  }
  
  //Convenient constructor
  Player() {}

  /* fields:
   *  this.x ...   int
   *  this.y ...   int
   *  this.color ...   Color
   *  this.size ...   int
   *  this.speed ...   int
   * methods:
   *  this.move(String) ...   IGamePiece
   *  this.draw() ...   WorldImage
   *  this.getX() ...   int
   *  this.getY() ...   int
   *  this.getSize() ...   int
   *  this.getColor() ...   Color
   *  this.getSpeed() ...   int
   *  this.move() ...   IGamePiece
   *  this.loopX(int) ...   int
   *  this.loopY(int) ...   int
   *  this.compareSize(IGamePiece) ...   int
   *  this.collide(IGamePiece) ...   boolean
   *  this.distance(IGamePiece) ...   double
   *  this.eat(IList<IGamePiece>, IGamePiece, IGamePiece) ...   FrenzyWorld
   *  this.sameFish(IGamePiece fish) ...   boolean
   *  this.fishOrSnack() ...   boolean
   */
  
  //produce a player fish that is shifted by x and y from this player fish
  public IGamePiece move(String key) {
    if (key.equals("up")) {
      return new Player(this.x, this.loopY(this.y - this.speed), this.color, this.size, this.speed);
    }
    else if (key.equals("down")) {
      return new Player(this.x, this.loopY(this.y + this.speed), this.color, this.size, this.speed);
    }
    else if (key.equals("right")) {
      return new Player(this.loopX(this.x + this.speed), this.y, this.color, this.size, this.speed);
    }
    else if (key.equals("left")) {
      return new Player(this.loopX(this.x - this.speed), this.y, this.color, this.size, this.speed);
    }
    else {
      return this;
    }
  }
  
  //produce a new FrenzyWorld with the increased size for the player fish
  //and moving the location of the fishes to its next positions
  public FrenzyWorld eat(IList<IGamePiece> fishes, IGamePiece fish) {
    if (fish.fishOrSnack()) {
      return new FrenzyWorld(new Player(this.x, this.y, 
          this.color, this.size + fish.getSize() / 4, this.speed), 
          fishes.filter(new RemoveFish(fish)).map(new MoveFishes()));
    }
    else {
      return new FrenzyWorld(new Player(this.x, this.y, 
          this.color, this.size + this.size / 3, this.speed), 
          fishes.filter(new RemoveFish(fish)).map(new MoveFishes()));
    }
    
  }


  //draw this Player as a circle
  public WorldImage draw() {
    return new CircleImage(this.size, "solid", this.color);
  }
}

//a class to represent an enemy Fish in the game
class Fish extends AGamePiece {
  
  //original constructor
  Fish(int x, int y, Color color, int size, int speed) {
    super(x, y, color, size, speed);
  }
  
  //for in game use
  Fish(Color color, int size, int speed) {
    super(color, size, speed);
  }
  
  //for testing
  Fish(Color col, int size, int speed, Random r) {
    super(col, size, speed,  r);
  }
  
  /* fields:
   *  this.x ...   int
   *  this.y ...   int
   *  this.color ...   Color
   *  this.size ...   int
   *  this.speed ...   int
   * methods:
   *  this.move(String) ...   IGamePiece
   *  this.draw() ...   WorldImage
   *  this.getX() ...   int
   *  this.getY() ...   int
   *  this.getSize() ...   int
   *  this.getColor() ...   Color
   *  this.getSpeed() ...   int
   *  this.move() ...   IGamePiece
   *  this.loopX(int) ...   int
   *  this.loopY(int) ...   int
   *  this.compareSize(IGamePiece) ...   int
   *  this.collide(IGamePiece) ...   boolean
   *  this.distance(IGamePiece) ...   double
   *  this.eat(IList<IGamePiece>, IGamePiece, IGamePiece) ...   FrenzyWorld
   *  this.sameFish(IGamePiece fish) ...   boolean
   *  this.fishOrSnack() ...   boolean
   */
  
  //produce a Fish that is shifted by x and y from this Fish
  public IGamePiece move(String key) {
    return new Fish(this.loopX(this.x + this.speed), this.y, 
        this.color, this.size, this.speed);  
  }
 
  //draw this Fish as a circle
  public WorldImage draw() {
    return new CircleImage(size, "solid", this.color);
  }

  //This should not be called so throw an error
  public FrenzyWorld eat(IList<IGamePiece> fishes, IGamePiece fish) {
    throw new RuntimeException("Error: this method should not be called");
  }

}

class Snack extends AGamePiece {
  
  //original constructor
  Snack(int x, int y, Color color, int size, int speed) {
    super(x, y, color, size, speed);
  }
  
  //for in game use
  Snack(Color color, int size, int speed) {
    super(color, size, speed);
  }
  
  /* fields:
   *  this.x ...   int
   *  this.y ...   int
   *  this.color ...   Color
   *  this.size ...   int
   *  this.speed ...   int
   * methods:
   *  this.move(String) ...   IGamePiece
   *  this.draw() ...   WorldImage
   *  this.getX() ...   int
   *  this.getY() ...   int
   *  this.getSize() ...   int
   *  this.getColor() ...   Color
   *  this.getSpeed() ...   int
   *  this.move() ...   IGamePiece
   *  this.loopX(int) ...   int
   *  this.loopY(int) ...   int
   *  this.compareSize(IGamePiece) ...   int
   *  this.collide(IGamePiece) ...   boolean
   *  this.distance(IGamePiece) ...   double
   *  this.eat(IList<IGamePiece>, IGamePiece, IGamePiece) ...   FrenzyWorld
   *  this.sameFish(IGamePiece fish) ...   boolean
   */
  
  //draw this SizeSnack as a square
  public WorldImage draw() {
    return new CircleImage(20, "solid", Color.red);
  }

  //This should not be called so throw an error
  public IGamePiece move(String key) {
    return new Fish(this.loopX(this.x + this.speed), this.y, 
        this.color, this.size, this.speed);  
  }

  //This should not be called so throw an error
  public FrenzyWorld eat(IList<IGamePiece> fishes, IGamePiece fish) {
    throw new RuntimeException("Error: this method should not be called");
  }
  
  public boolean fishOrSnack() {
    return false;
  }
  
}

//represents a list of T
interface IList<T> {

  // filter this list using the given predicate
  IList<T> filter(Predicate<T> pred);

  // map a function onto every member of this list
  <U> IList<U> map(Function<T, U> converter);

  // combine the items in this list from right to left
  <U> U fold(BiFunction<T, U, U> converter, U initial);
  
  //produce true if all the items in the list passes the predicate
  boolean andmap(Predicate<T> pred);
  
  //produce true if any of the items in the list passes the predicate
  boolean ormap(Predicate<T> pred);
  
  //search for the items that pass the predicate
  T search(Predicate<T> pred);
  
}

class MtList<T> implements IList<T> {

  /* methods:
   *  this.filter(Predicate<T>) ...   IList<T>
   *  this.map(Function<T, U>) ...   IList<T>
   *  this.fold(BiFucntion<T, U, U>, U) ...   U
   *  this.andmap(Predicate<T>) ...   boolean
   *  this.ormap(Predicate<T>) ...   boolean
   *  this.search(Predicate<T>) ...   T
   */

  // filter this list using the given predicate
  public IList<T> filter(Predicate<T> pred) {
    return new MtList<T>();
  }

  // map a function onto every member of this list
  public <U> IList<U> map(Function<T, U> converter) {
    return new MtList<U>();
  }

  // combine the items in this list from right to left
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return initial;
  }
  
  //produce true if all the items in the list passes the predicate
  public boolean andmap(Predicate<T> pred) {
    return true;
  }
  
  //produce true if any of the items in the list passes the predicate
  public boolean ormap(Predicate<T> pred) {
    return false;
  }
  
  //throw a runtimeException as there are no items in this empty generic list 
  public T search(Predicate<T> pred) {
    throw new RuntimeException("error");
  }
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /* fields:
   *  this.first ...   T
   *  this.rest ...   IList<T>
   * methods:
   *  this.filter(Predicate<T>) ...   IList<T>
   *  this.map(Function<T, U>) ...   IList<T>
   *  this.fold(BiFucntion<T, U, U>, U) ...   U
   *  this.andmap(Predicate<T>) ...   boolean
   *  this.ormap(Predicate<T>) ...   boolean
   *  this.search(Predicate<T>) ...   T
   */

  // filter this list using the given predicate
  public IList<T> filter(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    } 
    else {
      return this.rest.filter(pred);
    }
  }

  // map a function onto every member of this list
  public <U> IList<U> map(Function<T, U> converter) {
    return new ConsList<U>(converter.apply(this.first), this.rest.map(converter));
  }

  // combine the items in this list from right to left
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return converter.apply(this.first, this.rest.fold(converter, initial));
  }
  
  //produce true if all the items in the list passes the predicate
  public boolean andmap(Predicate<T> pred) {
    return pred.test(this.first) && this.rest.andmap(pred);
  }
  
  //produce true if any of the items in the list passes the predicate
  public boolean ormap(Predicate<T> pred) {
    return pred.test(this.first) || this.rest.ormap(pred);
  }
  
  //search for the items that pass the predicate
  public T search(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return this.first;
    }
    else {
      return this.rest.search(pred);
    }
  }
  
}
  
//Fold function for drawing the fish
class DrawFishes implements BiFunction<IGamePiece, WorldScene, WorldScene> {

  //draws the fishes on the WorldScene
  public WorldScene apply(IGamePiece fish, WorldScene scene) {
    return scene.placeImageXY(fish.draw(), fish.getX(), fish.getY());
  }
}

//Map function for moving the fishes
class MoveFishes implements Function<IGamePiece, IGamePiece> {

  //Moves all the fishes
  public IGamePiece apply(IGamePiece fish) {
    return fish.move("");
  }
}

//Ormap function for checking if the given IGamePiece if bigger than
//this IGamePiece
class Bigger implements Predicate<IGamePiece> {
  
  IGamePiece player;
  
  Bigger(IGamePiece player) {
    this.player = player;
  }
  
  /* fields:
   *  this.player ...   IGamePiece
   * methods:
   *  this.test(IGamePiece) ...   boolean
   * methods for fields:
   *  this.player.compareSize(IGamePiece) ...   boolean
   */
  
  //determines if this IGamePiece is bigger than the given IGamePiece
  public boolean test(IGamePiece fish) {
    return this.player.compareSize(fish) >= 0;
  }
}

//Ormap function for checking if the given IGamePiece collided with
//this IGamePiece
class Collide implements Predicate<IGamePiece> {
  
  IGamePiece player;
  
  Collide(IGamePiece player) {
    this.player = player;
  }
  
  /* fields:
   *  this.player ...   IGamePiece
   * methods:
   *  this.test(IGamePiece) ...   boolean
   * methods for fields:
   *  this.player.collide(IGamePiece) ...   boolean
   */
  
  //determines if this IGamePiece collided with the given IGamePiece
  public boolean test(IGamePiece fish) {
    return fish.collide(this.player);
  }
  
}

//Ormap function for checking if the given IGamePiece collided
//and is bigger than this IGamePiece
class Touch implements Predicate<IGamePiece> {
  
  IGamePiece player;
  
  Touch(IGamePiece player) {
    this.player = player;
  }
  
  /* fields:
   *  this.player ...   IGamePiece
   * methods:
   *  this.test(IGamePiece) ...   boolean
   * methods for fields:
   *  this.player.compareSize(IGamePiece) ...   boolean
   *  this.player.collide(IGamePiece) ...   boolean
   */
  
  //determines if the given IGamePiece collided and is bigger than this IGamePiece
  public boolean test(IGamePiece fish) {
    return this.player.compareSize(fish) >= 0 && this.player.collide(fish);
  }
}

//Filter function for determining the list of fishes to keep
class RemoveFish implements Predicate<IGamePiece> {
  
  IGamePiece fish2;
  
  RemoveFish(IGamePiece fish2) {
    this.fish2 = fish2;
  }
  
  /* fields:
   *  this.player ...   IGamePiece
   * methods:
   *  this.test(IGamePiece) ...   boolean
   * methods for fields:
   *  this.fish2.equals(IGamePiece) ...   boolean
   */
  
  //finds the fish that is same as the collided and remove it from the list
  public boolean test(IGamePiece fish1) {
    return !(this.fish2.sameFish(fish1));
  }
}


//World Class for the Frenzy game
class FrenzyWorld extends World {
  IGamePiece player;
  IList<IGamePiece> fishes;
  
  // Constructor for the test
  FrenzyWorld(IGamePiece player) {
    this.player = player;
  }
  
  //original constructor
  FrenzyWorld(IGamePiece player, IList<IGamePiece> fishes) {
    this.player = player;
    this.fishes = fishes;
  }
  
  /* fields:
   *  this.player ...   IGamePiece
   *  this.fishes ...   ILoFrenzy
   * methods:
   *  this.makeScene() ...   WorldScene
   *  this.onKeyEvemt(String) ...   World
   *  this.onTick() ...   World
   *  this.lastScene(String) ...   WorldScene
   * methods for fields:
   *  this.fishes.andmap(Predicate<T>) ...   boolean
   *  this.player.draw() ...   WorldImage
   *  this.fishes.fold(BiFucntion<T, U, U>, U) ...   U
   *  this.player.getX() ...   int
   *  this.player.getY() ...   int
   *  this.player.move(String) ...   IGamePiece
   *  this.fishes.ormap(Predicate<T>) ...   boolean
   *  this.player.eat( 
   */
  
  //draws the fishes and player onto the background
  public WorldScene makeScene() {
    return this.fishes.fold(new DrawFishes(), new WorldScene(600, 400))
        .placeImageXY(this.player.draw(), this.player.getX(), this.player.getY());
  }
  
  //move the player based on the key they pressed 
  public World onKeyEvent(String key) {
    return new FrenzyWorld(this.player.move(key), this.fishes);
  }
  
  //Moves the fishes on the scene unless:
  //- the player is the biggest fish then show the end screen where the player wins
  //- the player collides with a fish then check:
  // if the player bigger than the fish they collided with:
  //  1. if so, then the player will grow based on the size of the fish
  //     and that fish will be removed, and the fishes will move on the scene
  //  2. if not, the player loses, and show the end screen where the player loses
  public World onTick() {
    if (this.fishes.andmap(new Bigger(this.player))) {
      return this.endOfWorld("You win!");
    }
    else if (this.fishes.ormap(new Collide(this.player))) {
      if (this.fishes.ormap(new Touch(this.player))) {
        return this.player.eat(this.fishes, 
            this.fishes.search(new Touch(this.player)));
      }
      else {
        return this.endOfWorld("You lose ):");
      }
    }
    else {
      return new FrenzyWorld(this.player, this.fishes.map(new MoveFishes()));
    }
  }
  
  //produce the lastScene of the game with a given message
  public WorldScene lastScene(String str) {
    return this.makeScene().placeImageXY(new TextImage(str, Color.blue), 300, 200);
  }
}

class ExamplesFrenzy {
  IGamePiece player = new Player();
  IGamePiece smollFish1 = new Fish(Color.cyan, 15, 15);
  IGamePiece smollFish2 = new Fish(Color.cyan, 13, 12);
  IGamePiece mediumFish1 = new Fish(Color.pink, 20, 10);
  IGamePiece mediumFish2 = new Fish(Color.pink, 16, 13);
  IGamePiece mediumFish3 = new Fish(Color.blue, 20, 9);
  IGamePiece bigFish = new Fish(Color.orange, 30, 5);
  IGamePiece snack = new Snack(Color.red, 20, 10);
  
  IList<IGamePiece> mt = new MtList<IGamePiece>();
  IList<IGamePiece> list1 = new ConsList<IGamePiece>(smollFish1, this.mt);
  IList<IGamePiece> list2 = new ConsList<IGamePiece>(smollFish2, this.list1);
  IList<IGamePiece> list3 = new ConsList<IGamePiece>(mediumFish1, this.list2);
  IList<IGamePiece> list4 = new ConsList<IGamePiece>(mediumFish2, this.list3);
  IList<IGamePiece> list5 = new ConsList<IGamePiece>(bigFish, this.list4);
  IList<IGamePiece> list6 = new ConsList<IGamePiece>(mediumFish3, this.list5);
  IList<IGamePiece> list7 = new ConsList<IGamePiece>(snack, this.list6);
  
  //Examples used for testing
  IGamePiece smollFishTest = new Fish(Color.cyan, 15, 15, new Random(1));
  IGamePiece mediumFishTest = new Fish(Color.pink, 20, 10, new Random(2));
  IGamePiece bigFishTest = new Fish(Color.orange, 30, 5, new Random(3));
  
  IList<IGamePiece> list1Test = new ConsList<IGamePiece>(smollFishTest, this.mt);
  IList<IGamePiece> list2Test = new ConsList<IGamePiece>(mediumFishTest, this.list1Test);
  IList<IGamePiece> list3Test = new ConsList<IGamePiece>(bigFishTest, this.list2Test);
  
  
  FrenzyWorld fw1 = new FrenzyWorld(this.player, this.list1Test);
  FrenzyWorld fw2 = new FrenzyWorld(this.player, this.list2Test);
  FrenzyWorld fw3 = new FrenzyWorld(this.player, this.list3Test);
  
  DrawFishes drawFishesTest = new DrawFishes();
  MoveFishes moveFishesTest = new MoveFishes();
  Bigger biggerTest = new Bigger(this.player);
  Collide collideTest = new Collide(this.player);
  Touch touchTest = new Touch(this.player);
  RemoveFish removeFishTest = new RemoveFish(this.smollFishTest);
  
  
  void testBigBang(Tester t) {
    FrenzyWorld world = new FrenzyWorld(this.player, this.list7);
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  } 
  
  //testing getX method
  void testGetX(Tester t) {
    t.checkExpect(this.smollFishTest.getX(), 185);
    t.checkExpect(this.mediumFishTest.getX(), 108);
    t.checkExpect(this.bigFishTest.getX(), 134);
    t.checkExpect(this.player.getX(), 300);
  }
  
  //testing getY method
  void testGetY(Tester t) {
    t.checkExpect(this.smollFishTest.getY(), 388);
    t.checkExpect(this.mediumFishTest.getY(), 372);
    t.checkExpect(this.bigFishTest.getY(), 260);
    t.checkExpect(this.player.getY(), 200);
  }
  
  //testing getSize method
  void testGetSize(Tester t) {
    t.checkExpect(this.smollFishTest.getSize(), 15);
    t.checkExpect(this.mediumFishTest.getSize(), 20);
    t.checkExpect(this.bigFishTest.getSize(), 30);
    t.checkExpect(this.player.getSize(), 20);
  }
  
  //testing getColor method
  void testGetColor(Tester t) {
    t.checkExpect(this.smollFishTest.getColor(), Color.cyan);
    t.checkExpect(this.mediumFishTest.getColor(), Color.pink);
    t.checkExpect(this.bigFishTest.getColor(), Color.orange);
    t.checkExpect(this.player.getColor(), Color.green);
  }
  
  //testing getSpeed method
  void testGetSpeed(Tester t) {
    t.checkExpect(this.smollFishTest.getSpeed(), 15);
    t.checkExpect(this.mediumFishTest.getSpeed(), 10);
    t.checkExpect(this.bigFishTest.getSpeed(), 5);
    t.checkExpect(this.player.getSpeed(), 20);
  }
  
  //testing loopX method
  void testLoopX(Tester t) {
    t.checkExpect(this.smollFishTest.loopX(this.smollFishTest.getX()), 185);
    t.checkExpect(this.mediumFishTest.loopX(this.mediumFishTest.getX()), 108);
    t.checkExpect(this.bigFishTest.loopX(this.bigFishTest.getX()), 134);
    t.checkExpect(this.player.loopX(this.player.getX()), 300);
  }
  
  //testing loopY method
  void testLoopY(Tester t) {
    t.checkExpect(this.smollFishTest.loopY(this.smollFishTest.getY()), 388);
    t.checkExpect(this.mediumFishTest.loopY(this.mediumFishTest.getY()), 372);
    t.checkExpect(this.bigFishTest.loopY(this.bigFishTest.getY()), 260);
    t.checkExpect(this.player.loopY(this.player.getY()), 200);
  }
  
  //testing move method
  void testMove(Tester t) {
    t.checkExpect(this.smollFishTest.move(""), new Fish(200, 388, Color.cyan, 15, 15));
    t.checkExpect(this.mediumFishTest.move(""), new Fish(118, 372, Color.pink, 20, 10));
    t.checkExpect(this.bigFishTest.move(""), new Fish(139, 260, Color.orange, 30, 5));
    t.checkExpect(this.player.move("up"), new Player(300, 180, Color.green, 20, 20));
  }
  
  //testing draw method
  void testDraw(Tester t) {
    t.checkExpect(this.smollFishTest.draw(), new CircleImage(15, "solid", Color.cyan));
    t.checkExpect(this.mediumFish1.draw(), new CircleImage(20, "solid", Color.pink));
    t.checkExpect(this.bigFishTest.draw(), new CircleImage(30, "solid", Color.orange));
    t.checkExpect(this.player.draw(), new CircleImage(20, "solid", Color.green));
  }
  
  //testing compareSize method
  void testCompareSize(Tester t) {
    t.checkExpect(this.player.compareSize(this.smollFishTest), 5);
    t.checkExpect(this.player.compareSize(this.mediumFishTest), 0);
    t.checkExpect(this.player.compareSize(this.bigFishTest), -10);
  }
  
  //testing collide method
  void testCollide(Tester t) {
    t.checkExpect(this.player.collide(this.smollFishTest), false);
    t.checkExpect(this.player.collide(this.mediumFishTest), false);
    t.checkExpect(this.player.collide(this.bigFishTest), false);
    t.checkExpect(this.bigFishTest.collide(this.bigFishTest), true);
  }
  
  //testing distance method
  void testDistance(Tester t) {
    t.checkExpect(this.player.distance(this.smollFishTest), 220.38375620721234);
    t.checkExpect(this.player.distance(this.mediumFishTest), 257.7750957714884);
    t.checkExpect(this.player.distance(this.bigFishTest), 176.5106229097841);
  }
  
  //testing eat method
  void testEat(Tester t) {
    t.checkExpect(this.player.eat(this.list1Test, this.smollFishTest), 
        new FrenzyWorld(new Player(300, 200, Color.green, 23, 20), 
            this.mt));
    t.checkExpect(this.player.eat(this.list2Test, this.mediumFishTest), 
        new FrenzyWorld(new Player(300, 200, Color.green, 25, 20), 
            new ConsList<IGamePiece>(this.smollFishTest.move(""), this.mt)));
    t.checkExpect(this.player.eat(this.list3Test, this.smollFishTest), 
        new FrenzyWorld(new Player(300, 200, Color.green, 23, 20), 
            new ConsList<IGamePiece>(this.bigFishTest.move(""), 
                new ConsList<IGamePiece>(this.mediumFishTest.move(""), this.mt))));
  }
  
  //testing search method
  void testSearch(Tester t) {
    t.checkExpect(this.list1Test.search(new Touch(this.smollFishTest)), this.smollFishTest);
    t.checkException("Test for invalid fish since it should not be called",
        new RuntimeException("error"),
        this.list1Test,
        "search",
        new Touch(this.mediumFishTest));
    t.checkExpect(this.list2Test.search(new Touch(this.mediumFishTest)), this.mediumFishTest);
    t.checkExpect(this.list3Test.search(new Touch(this.bigFishTest)), this.bigFishTest);
  }
  
  //testing apply method
  void testApply(Tester t) {
    t.checkExpect(this.drawFishesTest.apply(this.player, new WorldScene(600, 400)), 
        new WorldScene(600, 400).placeImageXY(
            this.player.draw(), 300, 200));
    t.checkExpect(this.drawFishesTest.apply(this.smollFishTest, new WorldScene(600, 400)), 
        new WorldScene(600, 400).placeImageXY(
            this.smollFishTest.draw(), 185, 388));
    t.checkExpect(this.moveFishesTest.apply(this.mediumFishTest), this.mediumFishTest.move(""));
    t.checkExpect(this.moveFishesTest.apply(this.bigFishTest), this.bigFishTest.move(""));
  }
  
  //testing test method
  void testTest(Tester t) {
    t.checkExpect(this.biggerTest.test(this.bigFishTest), false);
    t.checkExpect(this.biggerTest.test(this.smollFishTest), true);
    t.checkExpect(this.collideTest.test(this.bigFishTest), false);
    t.checkExpect(this.collideTest.test(this.player), true);
    t.checkExpect(this.touchTest.test(this.player), true);
    t.checkExpect(this.touchTest.test(this.bigFishTest), false);
    t.checkExpect(this.removeFishTest.test(this.smollFishTest), false);
    t.checkExpect(this.removeFishTest.test(this.bigFishTest), true);
  }
  
  //testing makeScene method
  void testMakeScene(Tester t) {
    t.checkExpect(this.fw1.makeScene(),
        new WorldScene(600, 400).placeImageXY(
                new CircleImage(15, "Solid", Color.cyan), 185, 388).placeImageXY(
                    this.player.draw(), 300, 200));
    t.checkExpect(this.fw3.makeScene(), 
        new WorldScene(600, 400).placeImageXY(
            new CircleImage(15, "solid", Color.cyan), 185, 388).placeImageXY(
                new CircleImage(20, "solid", Color.pink), 108, 372).placeImageXY(
                        new CircleImage(30, "solid", Color.orange), 134, 260).placeImageXY(
                                this.player.draw(), 300, 200));
  }
  
  //testing onKeyEvent method
  void testOnKeyEvent(Tester t) {
    t.checkExpect(this.fw1.onKeyEvent("up"), 
        new FrenzyWorld(new Player(300, 180, Color.green, 20, 20),
            this.list1Test));
    t.checkExpect(this.fw2.onKeyEvent("down"), 
        new FrenzyWorld(new Player(300, 220, Color.green, 20, 20),
            this.list2Test));
    t.checkExpect(this.fw3.onKeyEvent("left"), 
        new FrenzyWorld(new Player(280, 200, Color.green, 20, 20),
            this.list3Test));
    t.checkExpect(this.fw1.onKeyEvent("right"), 
        new FrenzyWorld(new Player(320, 200, Color.green, 20, 20),
            this.list1Test));
  }
  
  //testing onTick method
  void testOnTick(Tester t) {
    t.checkExpect(this.fw1.onTick(), 
        new FrenzyWorld(this.player, this.list1Test));
    t.checkExpect(this.fw2.onTick(), 
        new FrenzyWorld(this.player, this.list2Test));
    t.checkExpect(this.fw3.onTick(), 
        new FrenzyWorld(this.player, this.list3Test.map(new MoveFishes())));
  }
  
  //testing lastScene method
  void testLastScene(Tester t) {
    t.checkExpect(this.fw1.lastScene("win"),
        new WorldScene(600, 400).placeImageXY(
            this.smollFishTest.draw(), 185, 388).placeImageXY(
                this.player.draw(), 300, 200).placeImageXY(
                    new TextImage("win", Color.blue), 300, 200));
    t.checkExpect(this.fw2.lastScene("nice"),
        new WorldScene(600, 400).placeImageXY(
            this.smollFishTest.draw(), 185, 388).placeImageXY(
                this.mediumFishTest.draw(), 108, 372).placeImageXY(
                    this.player.draw(), 300, 200).placeImageXY(
                        new TextImage("nice", Color.blue), 300, 200));
  }
  
  //testing fishOrSnack method
  void testFishOrSnack(Tester t) {
    t.checkExpect(this.smollFishTest.fishOrSnack(), true);
    t.checkExpect(this.player.fishOrSnack(), true);
    t.checkExpect(this.snack.fishOrSnack(), false);
  }
}

