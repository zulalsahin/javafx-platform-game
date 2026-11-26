package BBM104_PA3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;
import java.util.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class GameEngine extends Application {
    private final Pane gameRoot = new Pane();
    private final StackPane mainRoot = new StackPane();
    private static final int GAME_WIDTH = 960, GAME_HEIGHT = 480;
    private static final int VIEWPORT_WIDTH = 480, VIEWPORT_HEIGHT = 480;
    private Label scoreLabel;
    private Label spikesLabel;
    private Label applesLabel;
    private ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<Terrain> terrains = new ArrayList<>();
    private ArrayList<Spike>  spikes = new ArrayList<>();
    private ArrayList<Apple> apples = new ArrayList<>();
    private Player player;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean skillPressed = false;
    private double velocityY = 0;
    private boolean onGround = false;
    private int appleCount = 0;
    private long lastSpawnTime = 0;
    private Random random = new Random();
    private int score = 0;
    private int collectedApples = 0;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Uses a StackPane to overlay the UI components atop the game world.
        mainRoot.getChildren().add(gameRoot);
        reset();

        // Initializes the background image for the game environment.
        /*gameRoot.setBackground(new Background(new BackgroundImage(
                new Image(new File("assets/Background(960x480x1).png").toURI().toString()), null, null, null, null)));*/

        Image bgImage = new Image(
                new File("assets/Background(960x480x1).png").toURI().toString()
        );

        BackgroundSize bgSize = new BackgroundSize(
                GAME_WIDTH,
                GAME_HEIGHT,
                false,
                false,
                false,
                false
        );

        BackgroundImage bg = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                bgSize
        );

        gameRoot.setBackground(new Background(bg));

        Scene scene = new Scene(mainRoot, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);



        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                leftPressed = true;
            }
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                rightPressed = true;
            }
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                jumpPressed = true;
            }
            if(e.getCode() == KeyCode.SPACE) {
                skillPressed = true;
            }

        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                leftPressed = false;
            }
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                rightPressed = false;
            }
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                jumpPressed = false;
            }
            if (e.getCode() == KeyCode.SPACE) {
                skillPressed = false;
            }

        });

        // Configures stage properties and displays the primary scene.
        stage.setTitle("BBM104 Assignment 3 Platform Game");
        stage.setScene(scene);
        stage.show();
        gameRoot.requestFocus();

        // Initializes and starts the primary game loop.
        new GameLoop().start();

    }

    private void reset() {
        // Resets the game world state and reinitializes UI labels.
        gameRoot.getChildren().clear();

        ImageView bg = new ImageView(
                new Image(new File("assets/Background(960x480x1).png").toURI().toString())
        );

        bg.setFitWidth(GAME_WIDTH);
        bg.setFitHeight(GAME_HEIGHT);

        gameRoot.getChildren().add(bg);
        bg.toBack();

        gameRoot.setTranslateX(0);
        gameRoot.setTranslateY(0);

        if (scoreLabel == null) {
            scoreLabel = new Label("Score: 0");
            scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(scoreLabel, Pos.BOTTOM_LEFT);
            StackPane.setMargin(scoreLabel, new Insets(0, 0, 5, 10));
            mainRoot.getChildren().add(scoreLabel);

            spikesLabel = new Label("Spikes: 0");
            spikesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(spikesLabel, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(spikesLabel, new Insets(0, 10, 5, 0));
            mainRoot.getChildren().add(spikesLabel);

            applesLabel = new Label("Apples: 0");
            applesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(applesLabel, Pos.BOTTOM_CENTER);
            StackPane.setMargin(applesLabel, new Insets(0, 0, 5, 0));
            mainRoot.getChildren().add(applesLabel);
        } else {
            score = 0;
            appleCount = 0;
            collectedApples = 0;
            scoreLabel.setText("Score: 0");
            spikesLabel.setText("Spikes: 0");
            applesLabel.setText("Apples: 0");
            lastSpawnTime = System.nanoTime();
        }

        scoreLabel.toFront();
        spikesLabel.toFront();
        applesLabel.toFront();

        blocks.clear();
        spikes.clear();
        terrains.clear();
        apples.clear();
        player = null;

        try {
            Scanner sc = new Scanner(new File("level.txt"));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()){
                    continue;
                }

                String[] p = line.split(",");

                if (p.length < 3){
                    continue;
                }
                String type = p[0];
                double x = Double.parseDouble(p[1]);
                double y = Double.parseDouble(p[2]);

                if (type.equals("Block")) {
                    Block obj = new Block(x, y);
                    blocks.add(obj);
                    gameRoot.getChildren().add(obj);

                } else if (type.equals("Terrain")) {
                    Terrain obj = new Terrain(x, y);
                    terrains.add(obj);
                    gameRoot.getChildren().add(obj);

                }else if (type.equals("Spike")) {
                    Spike obj = new Spike(x, y);
                    spikes.add(obj);
                    gameRoot.getChildren().add(obj);

                }else if (type.equals("Apple")) {
                    Apple obj = new Apple(x, y);
                    apples.add(obj);
                    gameRoot.getChildren().add(obj);

                }else if (type.equals("Player")) {
                    player = new Player(x, y);
                    gameRoot.getChildren().add(player);

                }
            }
            sc.close();

        } catch (Exception e){
            e.printStackTrace();
        }



        // TO DO:

        // First, you should reset all the game attributes to their initial values.
        // Don't forget to clear all the GameObject lists.

        // Read the level.txt file line by line and parse the object's information.
        // There are 3 elements for each line (object) which are separated by spaces.
        // The first element is the type of the object, a String.
        // The second element is the x coordinate of the object, a double.
        // The third element is the y coordinate of the object, a double.

        // Use these elements to create the corresponding game objects and add them to
        // the game root. You can use the following code snippet:
        // obj.setLayoutX(Double.parseDouble(p[1]));
        // obj.setLayoutY(Double.parseDouble(p[2]));
        // gameRoot.getChildren().add(obj);

        // You should also add the created objects to the appropriate GameObject lists.

    }

    private boolean isInvalid(GameObject obj) {

        if (obj.getTranslateX() < 0 || obj.getTranslateY()< 0 || obj.getTranslateX() + obj.getObjectWidth() > GAME_WIDTH || obj.getTranslateY() + obj.getObjectHeight() > GAME_HEIGHT) {
           return true;
        }

        for( Block block : blocks){
            if (obj != block && obj.getBoundsInParent().intersects(block.getBoundsInParent())){
                return true;
            }
        }

        for (Terrain terrain : terrains){
            if(obj != terrain && obj.getBoundsInParent().intersects(terrain.getBoundsInParent())) {
                return true;
            }
        }



        // TO DO:

        // You should check if the object is out of game bounds (using GAME_WIDTH and
        // GAME_HEIGHT) or if it intersects with any game object.
        // If it is out of bounds or intersects with any game object, return true.

        // Note: You may think about (temporarily) adding or subtracting a small
        // value (like 0.1) from the coordinates (x and y) to avoid returning "true"
        // when the object is not actually intersecting but just touching the border
        // of the other object.

        // Hint: You can use
        // object1.getBoundsInParent().intersects(object2.getBoundsInParent()) to check
        // collisions.

        // Additionally, if needed, you can use:
        // object.getLayoutX() or object.getLayoutY() to get the coordinates.
        // object.getMinX(), object.getMaxX(), object.getMinY(), object.getMaxY() to
        // get the minimum and maximum x and y coordinates of the object.
        // The width and height of the object can be calculated by
        // object.getObjectWidth() and object.getObjectHeight() respectively.

        return false; // or true;
    }

    private void spawn() {

        for (int i =0; i<2;i++){
            Apple apple;

            do {
                double x = random.nextDouble() * (GAME_WIDTH - 16);
                double y = random.nextDouble() * (GAME_HEIGHT - 18);
                apple = new Apple(x, y);
            }while (isInvalid(apple));

            apples.add(apple);
            gameRoot.getChildren().add(apple);

        }
        appleCount += 2;
        applesLabel.setText("Apples: " +apples.size());

        if (!terrains.isEmpty() || !blocks.isEmpty()) {
            GameObject platform;

            do {
                if (random.nextBoolean() && !terrains.isEmpty()) {
                    platform = terrains.get(random.nextInt(terrains.size()));
                } else if (!blocks.isEmpty()) {
                    platform = blocks.get(random.nextInt(blocks.size()));
                } else {
                    platform = terrains.get(random.nextInt(terrains.size()));
                }
            }while (platform.getTranslateY() < 80);

            Spike spike = new Spike(0, 0);
            double x = platform.getTranslateX() + random.nextDouble() * (platform.getObjectWidth() - 14);
            double y = platform.getTranslateY() - spike.getObjectHeight();



                /*boolean blockedAbove = false;

                for (Block block : blocks) {
                    if (block.getTranslateX() < x + 14 && block.getTranslateX() + block.getObjectWidth() > x && block.getTranslateY() < platform.getTranslateY() && platform.getTranslateY() - block.getTranslateY() < 40) {
                        blockedAbove = true;
                    }
                }

                for (Terrain terrain : terrains) {
                    if (terrain.getTranslateX() < x + 14 && terrain.getTranslateX() + terrain.getObjectWidth() > x && terrain.getTranslateY() < platform.getTranslateY() && platform.getTranslateY() - terrain.getTranslateY() < 40) {
                        blockedAbove = true;
                    }
                }
                if (blockedAbove) {
                    continue;
                }*/


                spike.setTranslateX(x);
                spike.setTranslateY(y);


                spikes.add(spike);
                gameRoot.getChildren().add(spike);
                spike.setViewOrder(-100);
                spike.toFront();

            spikesLabel.setText("Spikes: " + spikes.size());
        }










        // TO DO:

        // You need to place a spike and 2 apples on the game screen.

        // Randomly select a platform (a terrain or a block) from the game screen
        // (You can use your GameObject lists). Check if the top of the selected
        // platform is available for spike spawning (using isInvalid() method).
        // If so, determine the x and y coordinates of newly spawned spike (You should
        // also place the spike above the platform in a random position but not outside
        // the platform horizontally) and add it to the screen and appropriate
        // GameObject list. You can use gameRoot.getChildren().add(object) to add
        // objects to the screen.

        // Randomly select 2 empty coordinates to place 2 apples. You can check if a
        // coordinate is empty by checking if it intersects with any existing object.
        // If the space is empty, place the apple there. They can fly in the air.
        // You should add them to the screen and appropriate GameObject list as well.

        // Note: Apples and Spikes can overlap each other. It means that if the game is
        // open for hours, the game must continue spawning apples and spikes. It is
        // enough only checking the collision cases for player, blocks and terrains.

    }

    // --------------------------------------------------------------------
    // This update method is called automatically in every animation frame.
    // --------------------------------------------------------------------
    private void update() {

        for (Node node : gameRoot.getChildren()){
            if (node instanceof GameObject){
                ((GameObject) node).update();
            }
        }

        // Updates the state of all active game entities.
        for (javafx.scene.Node node : gameRoot.getChildren()) {
            if (node instanceof GameObject) {
                ((GameObject) node).update();
            }
        }

        if (player != null){
            if (leftPressed){
                player.setTranslateX(player.getTranslateX() - 1);
                player.setScaleX(-1);

                if (isInvalid(player)){
                    player.setTranslateX(player.getTranslateX() + 1);
                }
            }

            if (rightPressed){
                player.setTranslateX(player.getTranslateX() + 1);
                player.setScaleX(+1);

                if (isInvalid(player)){
                    player.setTranslateX(player.getTranslateX() - 1);
                }
            }


        }

        if (player != null){
            if (jumpPressed && onGround){
                velocityY = -3.4;
                onGround = false;

            }

            velocityY += 0.1;

            player.setTranslateY(player.getTranslateY() + velocityY);

            if (isInvalid(player)) {
                player.setTranslateY(player.getTranslateY() - velocityY);

                if (velocityY > 0){
                    onGround = true;
                }
                velocityY = 0;

            }else {
                onGround = false;
            }


        }

        Iterator<Apple> appleIterator = apples.iterator();
        while (appleIterator.hasNext()){

            Apple apple = appleIterator.next();

            if (player.getBoundsInParent().intersects(apple.getBoundsInParent())){
                gameRoot.getChildren().remove(apple);
                appleIterator.remove();
                collectedApples++;
                applesLabel.setText("Apples: " + apples.size());

                if (collectedApples % 2 == 0){
                    score++;
                    scoreLabel.setText("Score: " + score);

                    if (!spikes.isEmpty()){
                        int index = random.nextInt(spikes.size());
                        Spike removedSpike = spikes.remove(index);
                        gameRoot.getChildren().remove(removedSpike);
                        spikesLabel.setText("Spikes: " + spikes.size());
                    }
                }
            }
        }

        for (Spike spike : spikes){
            if (player.getBoundsInParent().intersects(spike.getBoundsInParent())){
                reset();
                return;
            }
        }

        if (skillPressed && score >= 5 && !spikes.isEmpty()){
            Spike nearestSpike = null;
            double minDistance = Double.MAX_VALUE;

            for (Spike spike : spikes){
                double dx = player.getTranslateX() - spike.getTranslateX();
                double dy  = player.getTranslateY() - spike.getTranslateY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance< minDistance){
                    minDistance = distance;
                    nearestSpike = spike;
                }
            }

            if (nearestSpike != null){
                spikes.remove(nearestSpike);
                gameRoot.getChildren().remove(nearestSpike);

                score -= 5;
                scoreLabel.setText("Score: " + score);
                spikesLabel.setText("Spikes: " + spikes.size());
            }
        }

        long now = System.nanoTime();
        if (now - lastSpawnTime >= 3_000_000_000L){
            spawn();
            lastSpawnTime = now;
        }

        if (player != null) {
            double playerCenterX = player.getTranslateX() + player.getObjectWidth() / 2;

            double offsetX = VIEWPORT_WIDTH / 2.0 - playerCenterX;

            if (offsetX > 0) {
                offsetX = 0;
            }
            if (offsetX < VIEWPORT_WIDTH - GAME_WIDTH) {
                offsetX = VIEWPORT_WIDTH - GAME_WIDTH;
            }

            gameRoot.setTranslateX(offsetX);
        }


        scoreLabel.toFront();
        applesLabel.toFront();
        spikesLabel.toFront();

    }

    private class GameLoop extends AnimationTimer {
        @Override
        public void handle(long now) {
            update();
        }
    }
}
