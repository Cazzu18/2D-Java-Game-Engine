package components;

public class FontRenderer extends Component {

    @Override
    public void start(){
        if(gameObject.getComponent(SpriteRenderer.class) != null){//.class provides a reference to the hava.lang.Class object representing the specified class
            System.out.println("Found Font Renderer!");
        }
    }

    @Override
    public void update(float dt) {

    }
}
