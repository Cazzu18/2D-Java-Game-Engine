package jade;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name) {
        init(name, new Transform(), new ArrayList<>());
    }

    public GameObject(String name, Transform transform) {
        init(name, transform, new ArrayList<>());
    }

    public GameObject(String name, List<Component> components) {
        init(name, new Transform(), components);
    }

    public GameObject(String name, Transform transform, List<Component> components) {
        init(name, transform, components);
    }

    public void init(String name, Transform transform, List<Component> components) {
        this.name = name;
        this.transform = transform;
        this.components = components;
    }

    //we have a class T that extends Component so the return type will be a subclass of component
    //we take in the class that represents that same type
    public<T extends Component> T getComponent(Class<T> componentClass){
        for(Component c : components){
            //Determines if the class or interface represented by this Class object is either the same as, or is a superclass or superinterface of, the class or interface represented by the specified Class parameter.
            if(componentClass.isAssignableFrom(c.getClass())){
                try {
                    return componentClass.cast(c); //cast the component c to type componentClass
                } catch(ClassCastException e) {
                    e.printStackTrace();
                    assert false: "Error: Casting component.";
                }
            }
        }

        return null;//if we dont find it, null
    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        for(int i=0; i < components.size(); i++){
            Component c = components.get(i);
            if(componentClass.isAssignableFrom(c.getClass())){
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c){
        this.components.add(c);
        c.gameObject = this; //we send a reference of this to the Component class
    }

    public void update(float dt){
        for(int i=0; i < components.size(); i++){
            components.get(i).update(dt); //the update function of the GameObject class calls the update function of every individual component in the List
        }
    }

    public void start(){
        for(int i =0; i < components.size(); i++){
            components.get(i).start();//the start function of the GameObject class calls the start function of every individual component in the List
        }
    }



}
