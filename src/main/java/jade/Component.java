package jade;

public abstract class Component {
    public transient GameObject gameObject = null;

    public void start(){//concrete overridable method

    }

    public void update(float dt){

    }

    public void imgui(){

    }
}
