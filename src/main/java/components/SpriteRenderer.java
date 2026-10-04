package components;
import editor.JImGui;
import imgui.ImGui;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
import util.JMath;

public class SpriteRenderer extends Component {

    //private Vector4f color = new Vector4f(1, 1, 1, 1);
    private int color = 0xFFFFFFFF; //using 32 bit integer to represent rgba
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;//transient is a field modifier that indicates a variable should not be included in the default serialization process
    private transient boolean isDirty = true;

    @Override
    public void start(){
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        Vector4f editedColor = JMath.Int32ToVector4f(this.color);

        if (JImGui.colorPicker4("Color Picker", editedColor)) {
            setColor(JMath.Vector4fToInt32(editedColor));
        }
    }

    public void setDirty(){
        this.isDirty = true;
    }

//    public Vector4f getColor() {
//        return color;
//    }

    public int getColor() {
        return color;
    }

    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

//    public void setColor(Vector4f color){
//        if(!this.color.equals(color)){
//            this.color.set(color);
//            this.isDirty = true;
//        }
//    }

    public void setColor(int color){
        if(this.color != color){
            this.color = color;
            this.isDirty = true;
        }
    }

    public boolean isDirty(){
        return this.isDirty;
    }

    public void setClean(){
        this.isDirty = false;
    }

    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }






}
