package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.AnvilContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AnvilCreator extends ExtendedGuiWindow {

    public AnvilCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("anvil", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("recipe_creator", "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            if (validToSave(cache)) {
                openChat("recipe_creator", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    TestCache cache1 = (TestCache) guiHandler1.getCustomCache();
                    AnvilConfig anvilConfig = cache1.getAnvilConfig();
                    if (args.length > 1) {
                        if (!anvilConfig.saveConfig(args[0], args[1], player1)) {
                            return true;
                        }
                        try {
                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                CustomCrafting.getRecipeHandler().injectRecipe(new CustomAnvilRecipe(anvilConfig));
                                api.sendPlayerMessage(player, "recipe_creator", "loading.success");
                            }, 1);
                            if (CustomCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) {
                                cache.resetAnvilConfig();
                            }
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "recipe_creator", "loading.error", new String[]{"%REC%", anvilConfig.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.openCluster("none"), 1);
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "recipe_creator", "save.empty");
            }
            return false;
        })));

        registerButton(new ToggleButton("hidden", new ButtonState("recipe_creator", "hidden.enabled", WolfyUtilities.getSkullViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setHidden(false);
            return true;
        }), new ButtonState("recipe_creator", "hidden.disabled", WolfyUtilities.getSkullViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setHidden(true);
            return true;
        })));

        registerButton(new AnvilContainerButton(0));
        registerButton(new AnvilContainerButton(1));
        registerButton(new AnvilContainerButton(2));

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("recipe_creator", "priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setPriority(RecipePriority.getByOrder(order));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getPriority();
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));

        registerButton(new ActionButton("mode", new ButtonState("mode", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                CustomAnvilRecipe.Mode mode = ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getMode();
                int id = mode.getId();
                if (id < 2) {
                    id++;
                } else {
                    id = 0;
                }
                ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setMode(CustomAnvilRecipe.Mode.getById(id));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%MODE%", ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getMode().name());
                return itemStack;
            }
        })));
        registerButton(new ActionButton("repair_mode", new ButtonState("repair_mode", Material.GLOWSTONE_DUST, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getRepairCostMode()) + 1;
                ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getRepairCostMode().name());
                return itemStack;
            }
        })));
        registerButton(new ToggleButton("repair_apply", new ButtonState("repair_apply.true", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setApplyRepairCost(false);
            return true;
        }), new ButtonState("repair_apply.false", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton("block_repair", false, new ButtonState("block_repair.true", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setBlockEnchant(false);
            return true;
        }), new ButtonState("block_repair.false", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ToggleButton("block_rename", false, new ButtonState("block_rename.true", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setBlockRename(false);
            return true;
        }), new ButtonState("block_rename.false", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton("block_enchant", false, new ButtonState("block_enchant.true", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setBlockEnchant(false);
            return true;
        }), new ButtonState("block_enchant.false", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton("repair_cost", new ButtonState("repair_cost", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getRepairCost());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            /*
            if(repairCost <= 0){
                repairCost = 1;
            }
            */
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton("durability", new ButtonState("durability", Material.IRON_SWORD, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().getDurability());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getAnvilConfig().setDurability(durability);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            event.setButton(0, "back");
            AnvilConfig anvilRecipe = cache.getAnvilConfig();
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), anvilRecipe.isExactMeta());
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), anvilRecipe.isHidden());
            event.setButton(1, "hidden");
            event.setButton(3, "recipe_creator", "conditions");
            event.setButton(5, "priority");
            event.setButton(7, "exact_meta");
            event.setButton(19, "container_0");
            event.setButton(21, "container_1");
            if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                event.setButton(25, "container_2");
            } else if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                event.setButton(25, "durability");
            } else {
                event.setItem(25, new ItemStack(Material.BARRIER));
            }
            event.setButton(23, "mode");
            event.setButton(36, "block_enchant");
            event.setButton(37, "block_rename");
            event.setButton(38, "block_repair");
            event.setButton(40, "repair_apply");
            event.setButton(41, "repair_cost");
            event.setButton(42, "repair_mode");
            event.setButton(44, "save");
        }
    }

    private boolean validToSave(TestCache cache) {
        AnvilConfig anvilRecipe = cache.getAnvilConfig();
        if (!anvilRecipe.getInputLeft().isEmpty() || !anvilRecipe.getInputRight().isEmpty()) {
            if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                return anvilRecipe.getResult() != null && !anvilRecipe.getResult().isEmpty();
            }
            return true;
        }
        return false;
    }
}
