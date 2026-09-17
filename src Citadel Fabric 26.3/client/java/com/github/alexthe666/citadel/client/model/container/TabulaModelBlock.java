package com.github.alexthe666.citadel.client.model.container;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TabulaModelBlock
{
    private static final Logger LOGGER = LogManager.getLogger();
    @VisibleForTesting
    static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(TabulaModelBlock.class, new TabulaModelBlock.Deserializer()).registerTypeAdapter(TabulaItemOverride.class, new TabulaItemOverride.Deserializer()).create();
    private final List<CuboidModelElement> elements;
    private final boolean gui3d;
    public final boolean ambientOcclusion;
    private final ItemTransforms cameraTransforms;
    private final List<TabulaItemOverride> overrides;
    public String name = "";
    @VisibleForTesting
    public final Map<String, String> textures;
    @VisibleForTesting
    public TabulaModelBlock parent;
    @VisibleForTesting
    protected Identifier parentLocation;

    public static TabulaModelBlock deserialize(Reader readerIn)
    {
        return JsonUtils.gsonDeserialize(SERIALIZER, readerIn, TabulaModelBlock.class, false);
    }

    public static TabulaModelBlock deserialize(String jsonString)
    {
        return deserialize(new StringReader(jsonString));
    }

    public TabulaModelBlock(@Nullable Identifier parentLocationIn, List<CuboidModelElement> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemTransforms cameraTransformsIn, List<TabulaItemOverride> overridesIn)
    {
        this.elements = elementsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.textures = texturesIn;
        this.parentLocation = parentLocationIn;
        this.cameraTransforms = cameraTransformsIn;
        this.overrides = overridesIn;
    }

    public List<CuboidModelElement> getElements()
    {
        return this.elements.isEmpty() && this.hasParent() ? this.parent.getElements() : this.elements;
    }

    private boolean hasParent()
    {
        return this.parent != null;
    }

    public boolean isAmbientOcclusion()
    {
        return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
    }

    public boolean isGui3d()
    {
        return this.gui3d;
    }

    public boolean isResolved()
    {
        return this.parentLocation == null || this.parent != null && this.parent.isResolved();
    }

    public void getParentFromMap(Map<Identifier, TabulaModelBlock> p_178299_1_)
    {
        if (this.parentLocation != null)
        {
            this.parent = p_178299_1_.get(this.parentLocation);
        }
    }

    public Collection<Identifier> getOverrideLocations()
    {
        Set<Identifier> set = Sets.newHashSet();

        for (TabulaItemOverride itemoverride : this.overrides)
        {
            set.add(itemoverride.getModel());
        }

        return set;
    }

    public List<TabulaItemOverride> getOverrides()
    {
        return this.overrides;
    }

    public boolean isTexturePresent(String textureName)
    {
        return !"missingno".equals(this.resolveTextureName(textureName));
    }

    public String resolveTextureName(String textureName)
    {
        if (!this.startsWithHash(textureName))
        {
            textureName = '#' + textureName;
        }

        return this.resolveTextureName(textureName, new TabulaModelBlock.Bookkeep(this));
    }

    private String resolveTextureName(String textureName, TabulaModelBlock.Bookkeep p_178302_2_)
    {
        if (this.startsWithHash(textureName))
        {
            if (this == p_178302_2_.modelExt)
            {
                LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
                return "missingno";
            }
            else
            {
                String s = this.textures.get(textureName.substring(1));

                if (s == null && this.hasParent())
                {
                    s = this.parent.resolveTextureName(textureName, p_178302_2_);
                }

                p_178302_2_.modelExt = this;

                if (s != null && this.startsWithHash(s))
                {
                    s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
                }

                return s != null && !this.startsWithHash(s) ? s : "missingno";
            }
        }
        else
        {
            return textureName;
        }
    }

    private boolean startsWithHash(String hash)
    {
        return hash.charAt(0) == '#';
    }

    @Nullable
    public Identifier getParentLocation()
    {
        return this.parentLocation;
    }

    public TabulaModelBlock getRootModel()
    {
        return this.hasParent() ? this.parent.getRootModel() : this;
    }

    public ItemTransforms getAllTransforms()
    {
        ItemTransform itemtransformvec3f = this.getTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        ItemTransform itemtransformvec3f1 = this.getTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        ItemTransform itemtransformvec3f2 = this.getTransform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        ItemTransform itemtransformvec3f3 = this.getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        ItemTransform itemtransformvec3f4 = this.getTransform(ItemDisplayContext.HEAD);
        ItemTransform itemtransformvec3f5 = this.getTransform(ItemDisplayContext.GUI);
        ItemTransform itemtransformvec3f6 = this.getTransform(ItemDisplayContext.GROUND);
        ItemTransform itemtransformvec3f7 = this.getTransform(ItemDisplayContext.FIXED);
        return new ItemTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7, ItemTransform.NO_TRANSFORM);
    }

    private ItemTransform getTransform(ItemDisplayContext type)
    {
        ItemTransform local = this.cameraTransforms.getTransform(type);
        return this.parent != null && ItemTransform.NO_TRANSFORM.equals(local) ? this.parent.getTransform(type) : local;
    }

    public static void checkModelHierarchy(Map<Identifier, TabulaModelBlock> p_178312_0_)
    {
        for (TabulaModelBlock TabulaModelBlock : p_178312_0_.values())
        {
            try
            {
                TabulaModelBlock TabulaModelBlock1 = TabulaModelBlock.parent;

                for (TabulaModelBlock TabulaModelBlock2 = TabulaModelBlock1.parent; TabulaModelBlock1 != TabulaModelBlock2; TabulaModelBlock2 = TabulaModelBlock2.parent.parent)
                {
                    TabulaModelBlock1 = TabulaModelBlock1.parent;
                }

                throw new TabulaModelBlock.LoopException();
            }
            catch (NullPointerException ignored)
            {
            }
        }
    }

    static final class Bookkeep
    {
        public final TabulaModelBlock model;
        public TabulaModelBlock modelExt;

        private Bookkeep(TabulaModelBlock modelIn)
        {
            this.model = modelIn;
        }
    }

    public static class Deserializer implements JsonDeserializer<TabulaModelBlock>
    {
        public TabulaModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            List<CuboidModelElement> list = this.getModelElements(p_deserialize_3_, jsonobject);
            String s = this.getParent(jsonobject);
            Map<String, String> map = this.getTextures(jsonobject);
            boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
            ItemTransforms itemcameratransforms = ItemTransforms.NO_TRANSFORMS;

            if (jsonobject.has("display"))
            {
                JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "display");
                itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemTransforms.class);
            }

            List<TabulaItemOverride> list1 = this.getItemOverrides(p_deserialize_3_, jsonobject);
            Identifier parentId = s.isEmpty() ? null : Identifier.parse(s);
            return new TabulaModelBlock(parentId, list, map, flag, true, itemcameratransforms, list1);
        }

        protected List<TabulaItemOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object)
        {
            List<TabulaItemOverride> list = Lists.newArrayList();

            if (object.has("overrides"))
            {
                for (JsonElement jsonelement : JsonUtils.getJsonArray(object, "overrides"))
                {
                    list.add(deserializationContext.deserialize(jsonelement, TabulaItemOverride.class));
                }
            }

            return list;
        }

        private Map<String, String> getTextures(JsonObject object)
        {
            Map<String, String> map = Maps.newHashMap();

            if (object.has("textures"))
            {
                JsonObject jsonobject = object.getAsJsonObject("textures");

                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
                {
                    map.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            return map;
        }

        private String getParent(JsonObject object)
        {
            return JsonUtils.getString(object, "parent", "");
        }

        protected boolean getAmbientOcclusionEnabled(JsonObject object)
        {
            return JsonUtils.getBoolean(object, "ambientocclusion", true);
        }

        protected List<CuboidModelElement> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object)
        {
            List<CuboidModelElement> list = Lists.newArrayList();

            if (object.has("elements"))
            {
                for (JsonElement jsonelement : JsonUtils.getJsonArray(object, "elements"))
                {
                    list.add(deserializationContext.deserialize(jsonelement, CuboidModelElement.class));
                }
            }

            return list;
        }
    }

    public static class LoopException extends RuntimeException
    {
    }

    /**
     * Item model override entry from block/item model JSON ({@code overrides} array).
     * Vanilla {@code ItemOverride} was removed; this preserves Gson deserialization for Tabula wrapper JSON.
     */
    public static final class TabulaItemOverride {
        private final Identifier model;

        public TabulaItemOverride(Identifier model) {
            this.model = model;
        }

        public Identifier getModel() {
            return this.model;
        }

        public static class Deserializer implements JsonDeserializer<TabulaItemOverride> {
            @Override
            public TabulaItemOverride deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject obj = json.getAsJsonObject();
                return new TabulaItemOverride(Identifier.parse(JsonUtils.getString(obj, "model")));
            }
        }
    }
}
