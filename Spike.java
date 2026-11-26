package BBM104_PA3;// TO DO:

// Implement Spike Class.
// It should inherit from GameObject.
// You should define attributes if needed.
// Implementing only constructor is enough because it has no animation.

public class Spike extends GameObject{

    public Spike(double x, double y) {
        super("assets/Spike(14x7x1).png",14,7);
        setTranslateX(x);
        setTranslateY(y);
    }
}