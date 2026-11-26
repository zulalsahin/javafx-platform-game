package BBM104_PA3;// TO DO:

// Implement Block Class.
// It should inherit from GameObject.
// You should define attributes if needed.
//Implementing only constructor is enough because it has no animation.

public class Block extends GameObject{
    public Block(double x, double y){
        super("assets/Block(20x20x1).png",20,20);
        setTranslateX(x);
        setTranslateY(y);
    }

}