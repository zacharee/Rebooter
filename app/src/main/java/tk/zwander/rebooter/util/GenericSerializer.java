package tk.zwander.rebooter.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Taken from: https://stackoverflow.com/a/40084718/5496177.
 *
 * Properly handles serializing and deserializing base/sub classes.
 */
public class GenericSerializer implements JsonSerializer<Object>, JsonDeserializer<Object> {
        private static final String CLASS_PROPERTY_NAME = "class";
        private final Gson gson;

        public GenericSerializer() {
            gson = new Gson();
        }

        public GenericSerializer(Gson gson) {
            this.gson = gson;
        }

        @Override
        public Object deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
            Class<?> actualClass;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                String className = jsonObject.get(CLASS_PROPERTY_NAME).getAsString();
                try {
                    actualClass = Class.forName(className);
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new JsonParseException(e.getMessage());
                }
            }
            else {
                actualClass = typeOfT.getClass();
            }

            return gson.fromJson(json, actualClass);
        }

        @Override
        public JsonElement serialize(Object src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonElement retValue = gson.toJsonTree(src);
            if (retValue.isJsonObject()) {
                retValue.getAsJsonObject().addProperty(CLASS_PROPERTY_NAME, src.getClass().getName());
            }
            return retValue;
        }

    }