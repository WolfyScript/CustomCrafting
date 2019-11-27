package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.AnvilContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
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

    public AnvilCreator(InventoryAPI inventoryAPI) {
        super("anvil", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("recipe_creator", "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            if (validToSave(cache)) {
                openChat("recipe_creator", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    PlayerCache cache1 = CustomCrafting.getPlayerCache(player1);
                    AnvilConfig anvilConfig = cache1.getAnvilConfig();
                    if (args.length > 1) {
                        if (!anvilConfig.saveConfig(args[0], args[1], player1)) {
                            return true;
                        }
                        try {
                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                CustomCrafting.getRecipeHandler().injectRecipe(new CustomAnvilRecipe(anvilConfig));
                                api.sendPlayerMessage(player, "recipe_creator", "loading.success");
                            }, 1);
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "recipe_creator", "error_loading", new String[]{"%REC%", anvilConfig.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "recipe_creator", "save.empty");
            }
            return false;
        })));

        registerButton(new AnvilContainerButton(0));
        registerButton(new AnvilContainerButton(1));
        registerButton(new AnvilContainerButton(2));

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getAnvilConfig().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                CustomCrafting.getPlayerCache(player).getAnvilConfig().setPriority(RecipePriority.getByOrder(order));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getAnvilConfig().getPriority();
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));
        registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().getConditions().getByID("permission").setOption(Conditions.Option.EXACT);
            return true;
        }), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().getConditions().getByID("permission").setOption(Conditions.Option.IGNORE);
            return true;
        })));

        registerButton(new ActionButton("anvil.mode", new ButtonState("anvil.mode", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                CustomAnvilRecipe.Mode mode = CustomCrafting.getPlayerCache(player).getAnvilConfig().getMode();
                int id = mode.getId();
                if (id < 2) {
                    id++;
                } else {
                    id = 0;
                }
                CustomCrafting.getPlayerCache(player).getAnvilConfig().setMode(CustomAnvilRecipe.Mode.getById(id));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%MODE%", CustomCrafting.getPlayerCache(player).getAnvilConfig().getMode().name());
                return itemStack;
            }
        })));
        registerButton(new ActionButton("anvil.repair_mode", new ButtonState("anvil.repair_mode", Material.GLOWSTONE_DUST, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(CustomCrafting.getPlayerCache(player).getAnvilConfig().getRepairCostMode()) + 1;
                CustomCrafting.getPlayerCache(player).getAnvilConfig().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(player).getAnvilConfig().getRepairCostMode().name());
                return itemStack;
            }
        })));
        registerButton(new ToggleButton("anvil.repair_apply", new ButtonState("anvil.repair_apply.true", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setApplyRepairCost(false);
            return true;
        }), new ButtonState("anvil.repair_apply.false", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton("anvil.block_repair", false, new ButtonState("anvil.block_repair.true", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setBlockEnchant(false);
            return true;
        }), new ButtonState("anvil.block_repair.false", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ToggleButton("anvil.block_rename", false, new ButtonState("anvil.block_rename.true", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setBlockRename(false);
            return true;
        }), new ButtonState("anvil.block_rename.false", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton("anvil.block_enchant", false, new ButtonState("anvil.block_enchant.true", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setBlockEnchant(false);
            return true;
        }), new ButtonState("anvil.block_enchant.false", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton("anvil.repair_cost", new ButtonState("anvil.repair_cost", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", CustomCrafting.getPlayerCache(player).getAnvilConfig().getRepairCost());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton("anvil.durability", new ButtonState("anvil.durability", Material.IRON_SWORD), (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getAnvilConfig().setDurability(durability);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            event.setButton(0, "back");
            AnvilConfig anvilRecipe = cache.getAnvilConfig();
            ((ToggleButton) event.getGuiWindow().getButton("permission")).setState(event.getGuiHandler(), !anvilRecipe.getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT));
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), anvilRecipe.isExactMeta());
            event.setButton(2, "permission");
            event.setButton(4, "priority");
            event.setButton(6, "exact_meta");
            event.setButton(19, "anvil.container_0");
            event.setButton(21, "anvil.container_1");
            if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                event.setButton(25, "anvil.container_2");
            } else if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                event.setButton(25, "anvil.durability");
            } else {
                event.setItem(25, new ItemStack(Material.BARRIER));
            }
            event.setButton(23, "anvil.mode");
            event.setButton(36, "anvil.block_enchant");
            event.setButton(37, "anvil.block_rename");
            event.setButton(38, "anvil.block_repair");
            event.setButton(40, "anvil.repair_apply");
            event.setButton(41, "anvil.repair_cost");
            event.setButton(42, "anvil.repair_mode");
            event.setButton(53, "save");
        }
    }

    private boolean validToSave(PlayerCache cache) {
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
