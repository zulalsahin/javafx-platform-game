package BBM104_PA3;// TO DO

// Implement Apple Class.
// It should inherit from GameObject.
// You should define attributes if needed.
// It must also override the update() method and call the animate() method inside it because it has an animation.

public class Apple extends GameObject{
    public Apple(double x, double y){
        super("assets/Apple(16x18x17).png", 16, 18);
        setTranslateX(x);
        setTranslateY(y);

    }

    @Override
    public void update() {
        animate(17, 5);
    }
}