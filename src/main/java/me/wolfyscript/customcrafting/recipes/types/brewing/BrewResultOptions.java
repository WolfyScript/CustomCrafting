package me.wolfyscript.customcrafting.recipes.types.brewing;

import com.google.common.collect.Streams;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonParser;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.DeserializationContext;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonSerialize(using = BrewResultOptions.Serializer.class)
@JsonDeserialize(using = BrewResultOptions.Deserializer.class)
public class BrewResultOptions {

    private final CustomItem customItem;

    //These options are for general changes made to the potions, if advanced features are not required or you want to edit all effects before editing them further in detail.
    private int durationChange; //added to the Duration. if <0 it will be subtracted
    private int amplifierChange; //added to the Amplifier. if <0 it will be subtracted
    private boolean resetEffects; //If true resets all the effects
    private Color effectColor; //Alternative to colorChange

    //These options are more precise and you can specify the exact effect you want to edit.
    private List<PotionEffectType> effectRemovals; //These effects will be removed from the potions
    private Map<PotionEffect, Boolean> effectAdditions; //These effects will be added with an option if they should be replaced if they are already present
    private Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades; //These effects will be added to the existing potion effects. Meaning that the the values of these PotionEffects will added to the existing effects and boolean values will be replaced.

    //Instead of all these options you can use a set result.
    private List<CustomItem> result;

    //Conditions for the Potions inside the 3 slots at the bottom
    private Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects; //The effects that are required with the current Duration and amplitude. Integer values == 0 will be ignored and any value will be allowed.

    protected BrewResultOptions(CustomItem customItem) {
        this.customItem = customItem;
        this.durationChange = 0;
        this.amplifierChange = 0;
        this.resetEffects = false;
        this.effectColor = null;
        this.effectRemovals = new ArrayList<>();
        this.effectAdditions = new HashMap<>();
        this.effectUpgrades = new HashMap<>();
        this.result = new ArrayList<>();
        this.requiredEffects = new HashMap<>();
    }

    public BrewResultOptions() {
        this(null);
    }

    public CustomItem getCustomItem() {
        return customItem;
    }

    public int getDurationChange() {
        return durationChange;
    }

    public void setDurationChange(int durationChange) {
        this.durationChange = durationChange;
    }

    public int getAmplifierChange() {
        return amplifierChange;
    }

    public void setAmplifierChange(int amplifierChange) {
        this.amplifierChange = amplifierChange;
    }

    public boolean isResetEffects() {
        return resetEffects;
    }

    public void setResetEffects(boolean resetEffects) {
        this.resetEffects = resetEffects;
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    public List<PotionEffectType> getEffectRemovals() {
        return effectRemovals;
    }

    public void setEffectRemovals(List<PotionEffectType> effectRemovals) {
        this.effectRemovals = effectRemovals;
    }

    public Map<PotionEffect, Boolean> getEffectAdditions() {
        return effectAdditions;
    }

    public void setEffectAdditions(Map<PotionEffect, Boolean> effectAdditions) {
        this.effectAdditions = effectAdditions;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getEffectUpgrades() {
        return effectUpgrades;
    }

    public void setEffectUpgrades(Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades) {
        this.effectUpgrades = effectUpgrades;
    }

    public List<CustomItem> getResult() {
        return result;
    }

    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getRequiredEffects() {
        return requiredEffects;
    }

    public void setRequiredEffects(Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects) {
        this.requiredEffects = requiredEffects;
    }

    class Serializer extends StdSerializer<BrewResultOptions> {

        public Serializer() {
            super(BrewResultOptions.class);
        }

        protected Serializer(Class<BrewResultOptions> vc) {
            super(vc);
        }

        @Override
        public void serialize(BrewResultOptions brewResultOptions, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartObject();
            //Load simple global options
            gen.writeNumberField("duration_change", brewResultOptions.durationChange);
            gen.writeNumberField("amplifier_change", brewResultOptions.amplifierChange);
            gen.writeBooleanField("reset_effects", brewResultOptions.resetEffects);
            gen.writeObjectField("color", brewResultOptions.effectColor);

            //Load advanced options
            gen.writeArrayFieldStart("effect_removals");
            for (PotionEffectType effectRemoval : brewResultOptions.effectRemovals) {
                gen.writeObject(effectRemoval);
            }
            gen.writeEndArray();
            gen.writeArrayFieldStart("effect_additions");
            for (Map.Entry<PotionEffect, Boolean> entry : brewResultOptions.effectAdditions.entrySet()) {
                if (entry.getKey() != null) {
                    gen.writeStartObject();
                    gen.writeObjectField("effect", entry.getKey());
                    gen.writeBooleanField("replace", entry.getValue());
                    gen.writeEndObject();
                }
            }
            gen.writeEndArray();
            gen.writeArrayFieldStart("effect_upgrades");
            for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : brewResultOptions.effectUpgrades.entrySet()) {
                if (entry.getKey() != null) {
                    gen.writeStartObject();
                    gen.writeObjectField("effect_type", entry.getKey());
                    gen.writeNumberField("amplifier", entry.getValue().getKey());
                    gen.writeNumberField("duration", entry.getValue().getValue());
                    gen.writeEndObject();
                }
            }
            gen.writeEndArray();

            //Load input condition options
            gen.writeArrayFieldStart("required_effects");
            for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : brewResultOptions.requiredEffects.entrySet()) {
                if (entry.getKey() != null) {
                    gen.writeStartObject();
                    gen.writeObjectField("effect_type", entry.getKey());
                    gen.writeNumberField("amplifier", entry.getValue().getKey());
                    gen.writeNumberField("duration", entry.getValue().getValue());
                    gen.writeEndObject();
                }
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    class Deserializer extends StdDeserializer<BrewResultOptions> {

        public Deserializer() {
            super(BrewResultOptions.class);
        }

        protected Deserializer(Class<BrewResultOptions> vc) {
            super(vc);
        }

        @Override
        public BrewResultOptions deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = JacksonUtil.getObjectMapper();
            JsonNode node = jsonParser.readValueAsTree();

            BrewResultOptions brewResultOptions = node.has("customItem") ? new BrewResultOptions(mapper.convertValue(node.path("customItem"), CustomItem.class)) : new BrewResultOptions();
            brewResultOptions.setDurationChange(node.path("duration_change").asInt());
            brewResultOptions.setAmplifierChange(node.path("amplifier_change").asInt());
            brewResultOptions.setResetEffects(node.path("reset_effects").asBoolean(false));
            brewResultOptions.setEffectColor(node.has("color") ? mapper.convertValue(node.path("color"), Color.class) : null);

            brewResultOptions.setEffectRemovals(Streams.stream(node.path("effect_removals").elements()).map(n -> mapper.convertValue(n, PotionEffectType.class)).collect(Collectors.toList()));
            Map<PotionEffect, Boolean> effectAdditions = new HashMap<>();
            node.path("effect_additions").elements().forEachRemaining(n -> {
                PotionEffect potionEffect = mapper.convertValue(n.path("effect"), PotionEffect.class);
                if (potionEffect != null) {
                    effectAdditions.put(potionEffect, n.path("replace").asBoolean());
                }
            });
            brewResultOptions.setEffectAdditions(effectAdditions);

            Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades = new HashMap<>();
            node.path("effect_upgrades").elements().forEachRemaining(n -> {
                PotionEffectType potionEffect = mapper.convertValue(n.path("effect_type"), PotionEffectType.class);
                if (potionEffect != null) {
                    effectUpgrades.put(potionEffect, new Pair<>(n.get("amplifier").asInt(), n.path("duration").asInt()));
                }
            });
            brewResultOptions.setEffectUpgrades(effectUpgrades);

            brewResultOptions.setResult(Streams.stream(node.path("results").elements()).map(n -> mapper.convertValue(n, CustomItem.class)).collect(Collectors.toList()));

            Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects = new HashMap<>();
            node.path("required_effects").elements().forEachRemaining(n -> {
                PotionEffectType potionEffect = mapper.convertValue(n.path("type"), PotionEffectType.class);
                if (potionEffect != null) {
                    requiredEffects.put(potionEffect, new Pair<>(n.get("amplifier").asInt(), n.path("duration").asInt()));
                }
            });
            brewResultOptions.setRequiredEffects(requiredEffects);
            return brewResultOptions;
        }


    }
}
