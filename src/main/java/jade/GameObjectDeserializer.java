package jade;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject gameObject = new GameObject(name);
        for (JsonElement e : components) {
            Component c = jsonDeserializationContext.deserialize(e, Component.class);//using our abstract Compnent class so the deserializer deserializes to the correct subclass
            gameObject.addComponent(c);
        }
        gameObject.transform = gameObject.getComponent(Transform.class);

        return gameObject;
    }
}
