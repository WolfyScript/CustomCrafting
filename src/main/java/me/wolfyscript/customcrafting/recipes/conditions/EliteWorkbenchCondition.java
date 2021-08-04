package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAlias;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class EliteWorkbenchCondition extends Condition {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "elite_crafting_table");

    @JsonAlias({"elite_crafting_tables", "elite_workbenches"})
    @JsonProperty
    private final List<NamespacedKey> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (RecipeType.ELITE_WORKBENCH.isInstance(recipe)) {
            if (data.getBlock() != null) {
                CustomItem customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                if (customItem != null && customItem.getApiReference() instanceof WolfyUtilitiesRef wolfyUtilsRef) {
                    return eliteWorkbenches.contains(wolfyUtilsRef.getNamespacedKey()) && ((EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isEnabled();
                }
            }
            return false;
        }
        return true;
    }

    public void addEliteWorkbenches(NamespacedKey eliteWorkbenches) {
        if (!this.eliteWorkbenches.contains(eliteWorkbenches)) {
            this.eliteWorkbenches.add(eliteWorkbenches);
        }
    }

    @JsonIgnore
    public List<NamespacedKey> getEliteWorkbenches() {
        return eliteWorkbenches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (NamespacedKey eliteWorkbench : eliteWorkbenches) {
            stringBuilder.append(eliteWorkbench.toString()).append(",");
        }
        return stringBuilder.toString();
    }

    public class GUIComponent extends Condition.AbstractGUIComponent {

        private static final String PARENT_LANG = "conditions.elite_crafting_table";
        private static final String ADD = PARENT_LANG + ".add";
        private static final String LIST = PARENT_LANG + ".list";
        private static final String REMOVE = PARENT_LANG + ".remove";

        protected GUIComponent() {
            super(Material.CRAFTING_TABLE, "Elite Crafting Table", List.of(""));
        }

        @Override
        public void init(ConditionsMenu menu, WolfyUtilities api) {
            menu.registerButton(new ChatInputButton<>(ADD, Material.GREEN_CONCRETE, (guiHandler, player, s, args) -> {
                if (args.length > 1) {
                    var customItem = Registry.CUSTOM_ITEMS.get(new NamespacedKey(args[0], args[1]));
                    if (customItem == null) {
                        menu.sendMessage(player, "error");
                        return true;
                    }
                    var namespacedKey = customItem.getNamespacedKey();
                    EliteWorkbenchData data = (EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE);
                    if (!data.isEnabled()) {
                        menu.sendMessage(player, "not_elite_workbench");
                        return true;
                    }
                    EliteWorkbenchCondition condition = guiHandler.getCustomCache().getRecipe().getConditions().getByType(EliteWorkbenchCondition.class);
                    if (condition.getEliteWorkbenches().contains(namespacedKey)) {
                        menu.sendMessage(player, "already_existing");
                        return true;
                    }
                    condition.addEliteWorkbenches(namespacedKey);
                    return false;
                }
                menu.sendMessage(player, "no_name");
                return true;
            }));
            menu.registerButton(new DummyButton<>(LIST, Material.BOOK, (hashMap, cache, guiHandler, player, guiInventory, itemStack, slot, b) -> {
                var condition = guiHandler.getCustomCache().getRecipe().getConditions().getEliteCraftingTableCondition();
                hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.inst().getApi()));
                for (int i = 0; i < 4; i++) {
                    if (i < condition.getEliteWorkbenches().size()) {
                        hashMap.put("%var" + i + "%", condition.getEliteWorkbenches().get(i));
                    } else {
                        hashMap.put("%var" + i + "%", "...");
                    }
                }
                return itemStack;
            }));
            menu.registerButton(new ActionButton<>(REMOVE, Material.RED_CONCRETE, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                var condition = guiHandler.getCustomCache().getRecipe().getConditions().getByType(EliteWorkbenchCondition.class);
                if (!condition.getEliteWorkbenches().isEmpty()) {
                    condition.getEliteWorkbenches().remove(condition.getEliteWorkbenches().size() - 1);
                }
                return true;
            }));
        }

        @Override
        public void renderMenu(GuiUpdate<CCCache> update, CCCache cache, ICustomRecipe<?> recipe) {
            update.setButton(29, ADD);
            update.setButton(31, LIST);
            update.setButton(33, REMOVE);
        }
    }
}
