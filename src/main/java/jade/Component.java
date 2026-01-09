package jade;

import imgui.ImGui;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    public transient GameObject gameObject = null;

    public void start(){//concrete overridable method

    }

    public void update(float dt){

    }

    public void imgui(){
        try {
            Field[] fields = this.getClass().getDeclaredFields(); //getting fields of the subclass running the component
            for (Field field : fields) {

                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if(isTransient){
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());//checking if the field is private
                if(isPrivate){
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);//value contained within that field using reflection
                String name = field.getName();//name of the field

                if(type == int.class){
                    int val = (int)value;
                    int[] imInt = {val};//ImGui expects an array for an integer
                    if(ImGui.dragInt(name + ": ", imInt)){//if value changes
                        field.set(this, imInt[0]);
                    }
                }else if(type == float.class){
                    float val = (float)value;
                    float[] imFloat = {val};
                    if(ImGui.dragFloat(name + ": ", imFloat)){
                        field.set(this, imFloat[0]);
                    }
                } else if(type == boolean.class){
                    boolean val = (boolean)value;
                    if(ImGui.checkbox(name + ": ", val)){
                        field.set(this, !val);
                    }
                } else if(type == Vector3f.class){
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x, val.y, val.z};
                    if(ImGui.dragFloat3(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if(type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                }


                if(isPrivate){
                    field.setAccessible(false);
                }


            }
        } catch(IllegalAccessException e){//try to get a member variable that doesnt exist
            e.printStackTrace();
        }
    }
}
