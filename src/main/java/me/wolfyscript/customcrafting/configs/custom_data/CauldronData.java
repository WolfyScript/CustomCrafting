package me.wolfyscript.customcrafting.configs.custom_data;

import me.wolfyscript.utilities.api.inventory.custom_items.custom_data.CustomData;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Objects;

public class CauldronData extends CustomData implements Cloneable {

    private boolean enabled;

    public CauldronData() {
        super("cauldron");
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public CauldronData getDefaultCopy() {
        return new CauldronData();
    }

    @Override
    public void writeToJson(JsonGenerator gen) throws IOException {
        gen.writeBooleanField("enabled", enabled);
    }

    @Override
    public CustomData readFromJson(JsonNode node) throws IOException {
        CauldronData cauldronData = new CauldronData();
        cauldronData.setEnabled(node.get("enabled").asBoolean(false));
        return cauldronData;
    }

    @Override
    public CauldronData clone() {
        CauldronData cauldronData = new CauldronData();
        cauldronData.setEnabled(isEnabled());
        return cauldronData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CauldronData)) return false;
        if (!super.equals(o)) return false;
        CauldronData that = (CauldronData) o;
        return enabled == that.enabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled);
    }
}
