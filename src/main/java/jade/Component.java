package jade;

public abstract class Component {
    public GameObject gameObject = null;

    public void start(){//concrete overridable method

    }

    public abstract void update(float dt);
}
