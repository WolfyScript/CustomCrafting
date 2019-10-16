package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Anvil;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.AnvilContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.RecipeUtils;
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
import java.util.Locale;

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
        registerButton(new ActionButton("save", new ButtonState("save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            if (validToSave(cache)) {
                openChat(guiHandler, "$msg.gui.none.recipe_creator.save.input$", (guiHandler1, player1, s, args) -> {
                    PlayerCache cache1 = CustomCrafting.getPlayerCache(player1);
                    Anvil anvil = cache1.getAnvil();
                    if (args.length > 1) {
                        String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                        String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                        if (!RecipeUtils.testNameSpaceKey(namespace, key)) {
                            api.sendPlayerMessage(player, "&cInvalid Namespace or Key! Namespaces & Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        }
                        AnvilConfig anvilConfig;
                        if (CustomCrafting.hasDataBaseHandler()) {
                            anvilConfig = new AnvilConfig("{}", api.getConfigAPI(), namespace, key);
                        } else {
                            anvilConfig = new AnvilConfig(api.getConfigAPI(), namespace, key);
                        }
                        anvilConfig.setBlockEnchant(anvil.isBlockEnchant());
                        anvilConfig.setBlockRename(anvil.isBlockRename());
                        anvilConfig.setBlockRepairing(anvil.isBlockRepair());
                        anvilConfig.setExactMeta(anvil.isExactMeta());
                        anvilConfig.setRepairCostMode(anvil.getRepairCostMode());
                        anvilConfig.setRepairCost(anvil.getRepairCost());
                        anvilConfig.setPriority(anvil.getPriority());
                        anvilConfig.setMode(anvil.getMode());
                        anvilConfig.setResult(anvil.getResult());
                        anvilConfig.setDurability(anvil.getDurability());
                        anvilConfig.setInputLeft(anvil.getIngredients(0));
                        anvilConfig.setInputRight(anvil.getIngredients(1));
                        anvilConfig.setConditions(anvil.getConditions());
                        if (CustomCrafting.hasDataBaseHandler()) {
                            CustomCrafting.getDataBaseHandler().updateRecipe(anvilConfig);
                        } else {
                            anvilConfig.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                        }
                        cache1.resetAnvil();
                        try {
                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                CustomCrafting.getRecipeHandler().injectRecipe(new CustomAnvilRecipe(anvilConfig));
                                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.loading.success$");
                            }, 1);
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.error_loading$", new String[]{"%REC%", anvilConfig.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.save.empty$");
            }
            return false;
        })));

        registerButton(new AnvilContainerButton(0));
        registerButton(new AnvilContainerButton(1));
        registerButton(new AnvilContainerButton(2));

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getAnvil().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                CustomCrafting.getPlayerCache(player).getAnvil().setPriority(RecipePriority.getByOrder(order));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getAnvil().getPriority();
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));
        registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Anvil anvil = CustomCrafting.getPlayerCache(player).getAnvil();
            anvil.getConditions().getByID("permission").setOption(Conditions.Option.EXACT);
            return true;
        }), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Anvil anvil = CustomCrafting.getPlayerCache(player).getAnvil();
            anvil.getConditions().getByID("permission").setOption(Conditions.Option.IGNORE);
            return true;
        })));

        registerButton(new ActionButton("anvil.mode", new ButtonState("anvil.mode", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                CustomAnvilRecipe.Mode mode = CustomCrafting.getPlayerCache(player).getAnvil().getMode();
                int id = mode.getId();
                if (id < 2) {
                    id++;
                } else {
                    id = 0;
                }
                CustomCrafting.getPlayerCache(player).getAnvil().setMode(CustomAnvilRecipe.Mode.getById(id));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%MODE%", CustomCrafting.getPlayerCache(player).getAnvil().getMode().name());
                return itemStack;
            }
        })));
        registerButton(new ActionButton("anvil.repair_mode", new ButtonState("anvil.repair_mode", Material.GLOWSTONE_DUST, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(CustomCrafting.getPlayerCache(player).getAnvil().getRepairCostMode()) + 1;
                CustomCrafting.getPlayerCache(player).getAnvil().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(player).getAnvil().getRepairCostMode().name());
                return itemStack;
            }
        })));
        registerButton(new ToggleButton("anvil.repair_apply", new ButtonState("anvil.repair_apply.true", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setApplyRepairCost(false);
            return true;
        }), new ButtonState("anvil.repair_apply.false", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton("anvil.block_repair", false, new ButtonState("anvil.block_repair.true", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(false);
            return true;
        }), new ButtonState("anvil.block_repair.false", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ToggleButton("anvil.block_rename", false, new ButtonState("anvil.block_rename.true", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockRename(false);
            return true;
        }), new ButtonState("anvil.block_rename.false", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton("anvil.block_enchant", false, new ButtonState("anvil.block_enchant.true", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(false);
            return true;
        }), new ButtonState("anvil.block_enchant.false", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton("anvil.repair_cost", new ButtonState("anvil.repair_cost", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", CustomCrafting.getPlayerCache(player).getAnvil().getRepairCost());
            return itemStack;
        }), "$msg.gui.none.recipe_creator.anvil.repair_cost$", (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.valid_number$");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getAnvil().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton("anvil.durability", new ButtonState("anvil.durability", Material.IRON_SWORD), "$msg.gui.none.recipe_creator.anvil.durability$", (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.valid_number$");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getAnvil().setDurability(durability);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            event.setButton(0, "back");
            Anvil anvil = cache.getAnvil();
            ((ToggleButton) event.getGuiWindow().getButton("permission")).setState(event.getGuiHandler(), !anvil.getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT));
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), anvil.isExactMeta());
            event.setButton(2, "permission");
            event.setButton(4, "priority");
            event.setButton(6, "exact_meta");
            event.setButton(19, "anvil.container_0");
            event.setButton(21, "anvil.container_1");
            if (anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                event.setButton(25, "anvil.container_2");
            } else if (anvil.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
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
        Anvil anvil = cache.getAnvil();
        if (!anvil.getIngredients(0).isEmpty() || !anvil.getIngredients(1).isEmpty()) {
            if (anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                return anvil.getResult() != null && !anvil.getResult().isEmpty();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
