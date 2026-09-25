package physics2d;

import jade.GameObject;
import jade.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;


//see https://box2d.org/documentation/ for docs

public class Physics2D {

    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f; //16ms

    private int velocityIterations = 8; //the more you have the better the physics at the cost of performance
    private int positionIterations = 3;

    //adding GameObjects with rigidBody
    public void add(GameObject go){
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        //getRawBody returns whether the body has been added to the physics engine. If so, we don't want to add it twice
        if(rb != null && rb.getRawBody() == null){
            Transform transform = go.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);

            //setting from user provided values

            //physics engine settings used to slow down and stabilize moving or rotating objects over time
            bodyDef.angularDamping = rb.getAngularDamping(); // Slows down the rotation or spinning of an object along its axis by applying a torque in the opposing current angular velocity. Sorta like rotational friction or air resistance acting on a spinning top
            bodyDef.linearDamping = rb.getLinearDamping(); //  Slows down the straight-line (translational) movement of an object in any direction by applying a force opposing the current velocity. Sorta like simulating air resistance, drag, or friction
            bodyDef.fixedRotation = rb.isFixedRotation();

            //bullet ghosting(motion blur, sprite trails, or after-images) is a visual technique used to convey high speed, emphasize fast-moving objects, or create astylized special effect
            //The system records a history of the object’s past coordinates (X, Y) and rotations over the last few frames.
            //In each frame, the engine renders the current sprite normally, then renders the past positions behind it with decreasing opacity (alpha fading) or color tinting
            // keep performance high, developers limit the trail to a small number of ghosts (e.g., 3 to 5 frames) and recycle the visual assets.

            //Technical Ghosting
            //Physics Ghosting (Tunneling): If a fast-moving bullet(object) skips past a thin wall collision between frames, developers sometimes use "ghost" hitboxes or continuous collision detection
            // (CCD) to ensure the game calculates the trajectory accurately without letting the bullet teleport through obstacles.
            bodyDef.bullet = rb.isContinuousCollision();

            switch(rb.getBodyType()){
                case Kinematic:
                    bodyDef.type = org.jbox2d.dynamics.BodyType.KINEMATIC;
                    break;
                case Static:
                    bodyDef.type = org.jbox2d.dynamics.BodyType.STATIC;
                    break;
                case Dynamic:
                    bodyDef.type = org.jbox2d.dynamics.BodyType.DYNAMIC;
                    break;
            }

            //currently supporting circle or boxes
            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2DCollider boxCollider;

            if((circleCollider = go.getComponent(CircleCollider.class)) != null){
                shape.setRadius(circleCollider.getRadius());

            } else if((boxCollider = go.getComponent(Box2DCollider.class)) != null){
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = new Vector2f(boxCollider.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            //added to Box2D physics engine
            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass()); //create polygon shape and attach to rigid body
        }
    }

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
