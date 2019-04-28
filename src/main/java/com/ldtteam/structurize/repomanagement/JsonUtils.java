package com.ldtteam.structurize.repomanagement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ldtteam.structurize.repomanagement.repostructure.*;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import static com.ldtteam.structurize.api.util.constant.RepositoryConstants.*;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    /**
     * Private constructor to hide implicit public one.
     */
    private JsonUtils() {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Parses given JSON to return a new Style
     * 
     * @param json content of <code>style.json</code>
     */
    protected static Style parseToStyle(final String json, final String id, final ResourceLocation origin,
            final String author) {
        try {
            final JsonObject parsedJson = new JsonParser().parse(json).getAsJsonObject();

            final Style newStyle = new Style(id, parsedJson.get(JSON_NAME).getAsString(),
                    parsedJson.get(JSON_DESCRIPT).getAsString(), getOptionalStringTag(JSON_IMAGE, parsedJson), origin,
                    getOptionalStringTag(JSON_JOKE, parsedJson), author, getOptionalStringTag(JSON_BASE, parsedJson),
                    getOptionalStringTag(JSON_SHORTNAME, parsedJson));

            // loop for heads
            final JsonArray jsonHeadsArray = parsedJson.get(JSON_HEADS).getAsJsonArray();
            for (int i = 0; i < jsonHeadsArray.size(); i++) {
                final String jsonHeadId = jsonHeadsArray.get(i).getAsString();
                final String isAnyHead = parsedJson.get(JSON_ANY) != null ? JSON_ANY : jsonHeadId;
                final JsonObject head = parsedJson.get(isAnyHead).getAsJsonObject();
                final String newHeadName = parsedJson.get(JSON_NAMES) != null
                        ? parsedJson.get(JSON_NAMES).getAsJsonArray().get(i).getAsString()
                        : "";
                final Head newHead = new Head(jsonHeadId, newHeadName);

                // loop for types
                final JsonArray jsonTypesArray = head.get(JSON_TYPES).getAsJsonArray();
                for (int j = 0; j < jsonTypesArray.size(); j++) {
                    final String jsonTypeId = jsonTypesArray.get(j).getAsString().replace("$head ", jsonHeadId);
                    final String isAnyType = head.get(JSON_ANY) != null ? JSON_ANY : jsonTypeId;
                    final JsonObject type = head.get(isAnyType).getAsJsonObject();
                    final String newTypeName = head.get(JSON_NAMES) != null
                            ? head.get(JSON_NAMES).getAsJsonArray().get(j).getAsString()
                            : "";
                    final Type newType = new Type(jsonTypeId, newTypeName, id);

                    // loop for files
                    final JsonArray jsonFilesArray = type.get(JSON_FILES).getAsJsonArray();
                    for (int k = 0; k < jsonFilesArray.size(); k++) {
                        final String jsonFileId = jsonFilesArray.get(k).getAsString().replace("$type ", jsonTypeId);
                        final String newFileName = type.get(JSON_NAMES) != null
                                ? type.get(JSON_NAMES).getAsJsonArray().get(k).getAsString()
                                : "";

                        newType.addFile(jsonFileId, newFileName, id);
                    }

                    newHead.addType(newType);
                }

                newStyle.addHead(newHead);
            }

            // is not Default
            if (parsedJson.get(JSON_DEFAULT) == null || parsedJson.get(JSON_DEFAULT).getAsBoolean() == false) {
                return newStyle;
            }

            // is Default -> build registry and transform
            final DefaultStyle newDefaultStyle = new DefaultStyle(newStyle);
            final List<String> registryHeads = new ArrayList<String>();

            // loop for heads
            final List<Head> newStyleHead = newStyle.getHeads();
            for (int i = 0; i < newStyleHead.size(); i++) {
                final String isAnyHead = parsedJson.get(JSON_ANY) != null ? JSON_ANY
                        : jsonHeadsArray.get(i).getAsString();
                final JsonObject head = parsedJson.get(isAnyHead).getAsJsonObject();
                final DefaultHead newDefaultHead = new DefaultHead(newStyleHead.get(i));
                final List<String> registryTypes = new ArrayList<String>();

                // loop for types
                final JsonArray jsonTypesArray = head.get(JSON_TYPES).getAsJsonArray();
                final List<Type> newStyleType = newDefaultHead.getTypes();
                for (int j = 0; j < newStyleType.size(); j++) {
                    final String jsonTypeId = jsonTypesArray.get(j).getAsString().replace("$head ",
                            jsonHeadsArray.get(i).getAsString());
                    final String isAnyType = head.get(JSON_ANY) != null ? JSON_ANY : jsonTypeId;
                    final JsonObject type = head.get(isAnyType).getAsJsonObject();
                    final DefaultType newDefaultType = new DefaultType(newStyleType.get(j),
                            parsedJson.get(JSON_FILE_EXT).getAsString());
                    final List<String> registryFiles = new ArrayList<String>();

                    // loop for files
                    if (type.get(JSON_REGISTRY) != null && !type.get(JSON_REGISTRY).isJsonArray()
                            && type.get(JSON_REGISTRY).getAsString() == "$files") {
                        final List<Trinuple<String, String, String>> newStyleFile = newDefaultType.getFiles();
                        for (int k = 0; k < newStyleFile.size(); k++) {
                            final Trinuple<String, String, String> newDefaultFile = newStyleFile.get(k);

                            registryFiles.add(newDefaultFile.getFirst());
                        }
                    } else if (type.get(JSON_REGISTRY) != null && type.get(JSON_REGISTRY).isJsonArray()) {
                        registryFiles.addAll(getRegistryFromJsonArray(type.get(JSON_REGISTRY).getAsJsonArray()));
                    }

                    if (head.get(JSON_REGISTRY) != null && !head.get(JSON_REGISTRY).isJsonArray()
                            && head.get(JSON_REGISTRY).getAsString() == "$types") {
                        registryTypes.add(newDefaultType.getId());
                    }
                    newDefaultType.setRegistry(registryFiles);
                    newDefaultHead.setType(j, newDefaultType);
                }

                if (head.get(JSON_REGISTRY) != null && head.get(JSON_REGISTRY).isJsonArray()) {
                    registryTypes.addAll(getRegistryFromJsonArray(head.get(JSON_REGISTRY).getAsJsonArray()));
                }

                if (parsedJson.get(JSON_REGISTRY) != null && !parsedJson.get(JSON_REGISTRY).isJsonArray()
                        && parsedJson.get(JSON_REGISTRY).getAsString() == "$heads") {
                    registryHeads.add(newDefaultHead.getId());
                }
                newDefaultHead.setRegistry(registryTypes);
                newDefaultStyle.setHead(i, newDefaultHead);
            }

            if (parsedJson.get(JSON_REGISTRY) != null && parsedJson.get(JSON_REGISTRY).isJsonArray()) {
                registryHeads.addAll(getRegistryFromJsonArray(parsedJson.get(JSON_REGISTRY).getAsJsonArray()));
            }

            newDefaultStyle.setRegistry(registryHeads);

            return newDefaultStyle;
        } catch (Exception e) {
            e.printStackTrace();
            // Intentionally left empty
        }

        return null;
    }

    private static String getOptionalStringTag(final String attributeName, final JsonObject attributeParent) {
        final JsonElement result = attributeParent.get(attributeName);
        if (result == null) {
            return "";
        }
        return result.getAsString();
    }

    private static List<String> getRegistryFromJsonArray(final JsonArray json) {
        final List<String> result = new ArrayList<String>();

        json.forEach(jsonElement -> {
            result.add(jsonElement.getAsString());
        });

        return result;
    }

    /**
     * Parses given JSON to return a new List of Strings with URL suffixes from
     * given attibute
     * 
     * @param json          content of <code>file.json</code>
     * @param jsonAttribute is at the first layer of given JSON
     * @param extendsT      is type of expected json element
     */
    public static <T> T parseFirstLayerAttribute(final String json, final String jsonAttribute, T extendsT) {
        return new Gson().fromJson(new JsonParser().parse(json).getAsJsonObject().get(jsonAttribute),
                new TypeToken<T>() {
                }.getType());
    }

    /**
     * Parses given JSON to return a new List of Strings with URL suffixes from
     * given attibute
     * 
     * @param json                content of <code>file.json</code>
     * @param jsonFirstAttribute  is at the first layer of given JSON
     * @param jsonSecondAttribute is at the second layer of given JSON
     * @param extendsT            is type of expected json element
     */
    public static <T> T parseSecondLayerAttribute(final String json, final String jsonFirstAttribute,
            final String jsonSecondAttribute, T extendsT) {
        return new Gson().fromJson(new JsonParser().parse(json).getAsJsonObject().get(jsonFirstAttribute)
                .getAsJsonObject().get(jsonSecondAttribute), new TypeToken<T>() {
                }.getType());
    }

    public static String stringifyToJson(final Object data) {
        return new Gson().toJson(data);
    }

    protected static List<Tuple<String, String>> parseFirstLayerObjectArrayToTuple(final String json,
            final String jsonFirstAttribute, final String jsonTupleFirst, final String jsonTupleSecond) {
        final List<Tuple<String, String>> result = new ArrayList<Tuple<String, String>>();
        final JsonArray jsonArray = new JsonParser().parse(json).getAsJsonObject().get(jsonFirstAttribute)
                .getAsJsonArray();

        jsonArray.forEach(jsonElement -> {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            result.add(new Tuple<String, String>(jsonObject.get(jsonTupleFirst).getAsString(),
                    jsonObject.get(jsonTupleSecond).getAsString()));
        });

        return result;
    }
}