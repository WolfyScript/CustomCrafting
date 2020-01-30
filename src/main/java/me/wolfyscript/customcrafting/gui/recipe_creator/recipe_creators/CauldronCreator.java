package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CauldronContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronConfig;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CauldronCreator extends ExtendedGuiWindow {

    public CauldronCreator(InventoryAPI inventoryAPI) {
        super("cauldron", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("recipe_creator", "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            if (validToSave(cache)) {
                openChat("recipe_creator", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    TestCache cache1 = ((TestCache) guiHandler1.getCustomCache());
                    CauldronConfig config = cache1.getCauldronConfig();
                    if (args.length > 1) {
                        if (!config.saveConfig(args[0], args[1], player1)) {
                            return true;
                        }
                        try {
                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                CustomCrafting.getRecipeHandler().injectRecipe(new CauldronRecipe(config));
                                api.sendPlayerMessage(player, "recipe_creator", "loading.success");
                            }, 1);
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "recipe_creator", "error_loading", new String[]{"%REC%", config.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.openCluster("none"), 1);
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "recipe_creator", "save.empty");
            }
            return false;
        })));

        registerButton(new ToggleButton("hidden", new ButtonState("recipe_creator", "hidden.enabled", WolfyUtilities.getSkullViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setHidden(false);
            return true;
        }), new ButtonState("recipe_creator", "hidden.disabled", WolfyUtilities.getSkullViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setHidden(true);
            return true;
        })));

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("recipe_creator", "priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setPriority(RecipePriority.getByOrder(order));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getPriority();
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));

        registerButton(new DummyButton("cauldron", new ButtonState("cauldron", Material.CAULDRON)));

        registerButton(new CauldronContainerButton(0));
        registerButton(new CauldronContainerButton(1));
        registerButton(new ItemInputButton("handItem_container", new ButtonState("handItem_container", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                            TestCache cache = (TestCache) guiHandler.getCustomCache();
                            cache.getItems().setItem(true, CustomItem.getByItemStack(inventory.getItem(slot)));
                            cache.setApplyItem((items, cache1, customItem) -> cache1.getCauldronConfig().setHandItem(items.getItem()));
                            guiHandler.changeToInv("none", "item_editor");
                        }
                    });
                    return true;
                }
                Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setHandItem(inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR)));
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                CustomItem customItem = ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getHandItem();
                if (customItem != null) {
                    return customItem.getItemStack();
                }
                return itemStack;
            }
        })));


        registerButton(new ToggleButton("dropItems", new ButtonState("dropItems.enabled", Material.DROPPER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setDropItems(false);
            return true;
        }), new ButtonState("dropItems.disabled", Material.CHEST, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setDropItems(true);
            return true;
        })));
        registerButton(new ToggleButton("fire", new ButtonState("fire.enabled", Material.FLINT_AND_STEEL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setFire(false);
            return true;
        }), new ButtonState("fire.disabled", Material.FLINT, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setFire(true);
            return true;
        })));
        registerButton(new ToggleButton("water", new ButtonState("water.enabled", Material.WATER_BUCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setWater(false);
            return true;
        }), new ButtonState("water.disabled", Material.BUCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setWater(true);
            return true;
        })));
        registerButton(new ChatInputButton("xp", new ButtonState("xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%xp%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getXP());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setXP(xp);
            return false;
        }));
        registerButton(new ChatInputButton("cookingTime", new ButtonState("cookingTime", Material.CLOCK, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%time%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getCookingTime());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setCookingTime(time);
            return false;
        }));
        registerButton(new ChatInputButton("waterLevel", new ButtonState("waterLevel", Material.GLASS_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%level%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getWaterLevel());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int waterLvl;
            try {
                waterLvl = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            if (waterLvl > 3) {
                waterLvl = 3;
            }
            ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setWaterLevel(waterLvl);
            return false;
        }));

        if (WolfyUtilities.hasMythicMobs()) {
            registerButton(new ActionButton("mythicMob", new ButtonState("mythicMob", Material.WITHER_SKELETON_SKULL, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent event) {
                    if (event.getClick().isLeftClick()) {
                        openChat("mythicMob", guiHandler, (guiHandler1, player1, s, args) -> {
                            if (args.length > 1) {
                                CauldronConfig config = ((TestCache) guiHandler.getCustomCache()).getCauldronConfig();
                                MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(args[0]);
                                if (mythicMob != null) {
                                    int level = 1;
                                    try {
                                        level = Integer.parseInt(args[1]);
                                    } catch (NumberFormatException e) {
                                        api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                                        return true;
                                    }
                                    double modX = config.getMythicMobMod().getX();
                                    double modY = config.getMythicMobMod().getY();
                                    double modZ = config.getMythicMobMod().getZ();
                                    if (args.length >= 5) {
                                        try {
                                            modX = Double.parseDouble(args[2]);
                                            modY = Double.parseDouble(args[3]);
                                            modZ = Double.parseDouble(args[4]);
                                        } catch (NumberFormatException e) {
                                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                                            return true;
                                        }
                                    }
                                    config.setMythicMob(args[0], level, modX, modY, modZ);
                                    guiHandler.openCluster();
                                    return false;
                                }
                            }
                            guiHandler.openCluster();
                            return true;
                        });
                    } else {
                        ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().setMythicMob("<none>", 0, 0, 0.5, 0);
                    }
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                    hashMap.put("%mob.name%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getMythicMobName());
                    hashMap.put("%mob.level%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getMythicMobLevel());
                    hashMap.put("%mob.modX%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getMythicMobMod().getX());
                    hashMap.put("%mob.modY%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getMythicMobMod().getY());
                    hashMap.put("%mob.modZ%", ((TestCache) guiHandler.getCustomCache()).getCauldronConfig().getMythicMobMod().getZ());
                    return itemStack;
                }
            })));
        }
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            CauldronConfig cauldronConfig = cache.getCauldronConfig();
            ((ToggleButton) event.getGuiWindow().getButton("fire")).setState(event.getGuiHandler(), cauldronConfig.needsFire());
            ((ToggleButton) event.getGuiWindow().getButton("water")).setState(event.getGuiHandler(), cauldronConfig.needsWater());
            ((ToggleButton) event.getGuiWindow().getButton("dropItems")).setState(event.getGuiHandler(), cauldronConfig.dropItems());
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), cauldronConfig.isHidden());

            event.setButton(1, "hidden");
            event.setButton(3, "recipe_creator", "conditions");
            event.setButton(5, "priority");
            event.setButton(7, "exact_meta");
            event.setButton(11, "cauldron.container_0");
            event.setButton(13, "cookingTime");

            event.setButton(19, "water");
            event.setButton(20, "cauldron");
            event.setButton(21, "waterLevel");

            event.setButton(23, "xp");
            event.setButton(25, "cauldron.container_1");

            event.setButton(29, "fire");
            event.setButton(34, "dropItems");

            if (!cache.getCauldronConfig().dropItems()) {
                event.setButton(33, "handItem_container");
            }
            if (WolfyUtilities.hasMythicMobs()) {
                event.setButton(13, "mythicMob");
            }

            event.setButton(44, "save");
        }
    }

    private boolean validToSave(TestCache cache) {
        CauldronConfig config = cache.getCauldronConfig();
        return config.getIngredients() != null && !config.getIngredients().isEmpty() && ((config.getResult() != null && !config.getResult().isEmpty()) || (WolfyUtilities.hasMythicMobs() && !config.getMythicMobName().equals("<none>")));
    }
}
