package BBM104_PA3;// TO DO:

// Implement Player Class.
// It should inherit from GameObject.
// You should define attributes if needed.
// It must also override the update() method and call the animate() method inside it because it has an animation.

public class Player extends GameObject{
    public Player(double x, double y){
        super("assets/Player(22x26x11).png", 22, 26);
        setTranslateX(x);
        setTranslateY(y);
    }
    @Override
    public void update() {
        animate(11, 5);
    }
}

