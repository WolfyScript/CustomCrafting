package me.wolfyscript.customcrafting.configs.custom_data;

import me.wolfyscript.utilities.api.inventory.custom_items.custom_data.CustomData;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Objects;

public class KnowledgeBookData extends CustomData implements Cloneable {

    private boolean enabled;

    public KnowledgeBookData() {
        super("knowledge_book");
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public CustomData getDefaultCopy() {
        return new KnowledgeBookData();
    }

    @Override
    public void writeToJson(JsonGenerator gen) throws IOException {
        gen.writeBooleanField("enabled", enabled);
    }

    @Override
    public CustomData readFromJson(JsonNode node) throws IOException {
        KnowledgeBookData knowledgeBookData = new KnowledgeBookData();
        knowledgeBookData.setEnabled(node.get("enabled").asBoolean(false));
        return knowledgeBookData;
    }

    @Override
    public KnowledgeBookData clone() {
        KnowledgeBookData knowledgeBookData = new KnowledgeBookData();
        knowledgeBookData.setEnabled(isEnabled());
        return knowledgeBookData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KnowledgeBookData)) return false;
        if (!super.equals(o)) return false;
        KnowledgeBookData that = (KnowledgeBookData) o;
        return enabled == that.enabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled);
    }
}
