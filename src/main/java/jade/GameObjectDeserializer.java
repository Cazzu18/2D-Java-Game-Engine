package jade;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        JsonArray components = jsonObject.getAsJsonArray("components");
        Transform transform = jsonDeserializationContext.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = jsonDeserializationContext.deserialize(jsonObject.get("zIndex"), int.class);

        GameObject gameObject = new GameObject(name, transform, zIndex);
        for (JsonElement e : components) {
            Component c = jsonDeserializationContext.deserialize(e, Component.class);//using our abstract Compnent class so the deserializer deserializes to the correct subclass
            gameObject.addComponent(c);
        }

        return gameObject;
    }
}
