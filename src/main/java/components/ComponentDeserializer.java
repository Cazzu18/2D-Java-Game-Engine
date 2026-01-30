package components;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {



    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        //result.add(property, value) -> in this case result.add("type", the actual type)
        result.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("properties", jsonSerializationContext.serialize(component, component.getClass()));//letting Gson serialize the context for us
        return result;
    }

    @Override
    public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String objectType = jsonObject.get("type").getAsString();
        JsonElement properties = jsonObject.get("properties");
        try {
            return jsonDeserializationContext.deserialize(properties, Class.forName(objectType));
        } catch(ClassNotFoundException e){
            throw new JsonParseException("Unknown element type: " + objectType, e);
        }

    }
}
