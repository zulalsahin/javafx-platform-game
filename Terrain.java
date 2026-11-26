package BBM104_PA3;// TO DO:

// Implement Terrain Class.
// It should inherit from GameObject.
// You should define attributes if needed.
// Implementing only constructor is enough because it has no animation.

public class Terrain extends GameObject {
    public Terrain(double x, double y){
        super("assets/Terrain(48x48x1).png", 48, 48);
        setTranslateX(x);
        setTranslateY(y);
    }

}