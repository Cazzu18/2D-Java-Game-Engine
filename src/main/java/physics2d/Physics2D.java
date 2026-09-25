package physics2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


//see https://box2d.org/documentation/ for docs

public class Physics2D {

    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f; //16ms

    private int velocityIterations = 8; //the more you have the better the physics at the cost of performance
    private int positionIterations = 3;

    public void update(float dt){

        //frames can run in 16ms ms 17ms 20ms etc...
        // 0 + 16ms(dt)
        //16ms - 16ms
        //14ms
        // 14-16 = -2 so we don't enter the loop and don't update physics
        //-2 + 16ms = 14 and therefore update physics
        //...

        physicsTime += dt;
        if(physicsTime >= 0.0f){
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }
}
