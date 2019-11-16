package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.StonecutterContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipeUtils;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.StonecutterConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.Locale;

public class StonecutterCreator extends ExtendedGuiWindow {

    public StonecutterCreator(InventoryAPI inventoryAPI) {
        super("stonecutter", inventoryAPI, 45);
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
                    StonecutterConfig stonecutterConfig = cache1.getStonecutterConfig();
                    if (args.length > 1) {
                        String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                        String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                        if (!stonecutterConfig.saveConfig(namespace, key, player1)) {
                            return true;
                        }
                        if (CustomCrafting.hasDataBaseHandler()) {
                            CustomCrafting.getDataBaseHandler().updateRecipe(stonecutterConfig);
                        } else {
                            stonecutterConfig.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                        }
                        try {
                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                CustomCrafting.getRecipeHandler().injectRecipe(new CustomStonecutterRecipe(stonecutterConfig));
                                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.loading.success$");
                            }, 1);

                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.error_loading$", new String[]{"%REC%", stonecutterConfig.getId()});
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
        registerButton(new StonecutterContainerButton(0));
        registerButton(new StonecutterContainerButton(1));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            event.setButton(20, "stonecutter.container_0");
            event.setButton(24, "stonecutter.container_1");
            event.setButton(44, "save");
        }
    }

    private boolean validToSave(PlayerCache cache) {
        StonecutterConfig stonecutter = cache.getStonecutterConfig();
        return !stonecutter.getResult().get(0).getType().equals(Material.AIR) && !stonecutter.getSource().isEmpty();
    }
}
