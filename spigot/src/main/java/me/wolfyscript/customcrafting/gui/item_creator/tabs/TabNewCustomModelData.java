package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.ChatTabComplete;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabNewCustomModelData extends ItemCreatorTabVanilla {
    public static final String KEY = "custom_model_data_new";

    public TabNewCustomModelData() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.LAPIS_LAZULI, this));
        // STRING
        {
            creator.registerButton(new DummyButton<>(KEY + ".info_string", Material.NAME_TAG, ((hashMap, ccCache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                CustomItem item = guiHandler.getCustomCache().getItems().getItem();
                String text_model_data = !item.hasItemMeta() ? "&7&l/"
                        : !item.getItemMeta().hasCustomModelData() ? "&7&l/"
                        : item.getItemMeta().getCustomModelDataComponent().getStrings().isEmpty() ? "&7&l/"
                        : String.join(", ", item.getItemMeta().getCustomModelDataComponent().getStrings());
                hashMap.put("%VAR%", text_model_data);
                return itemStack;
            })));
            creator.registerButton(new ChatInputButton<>(KEY + ".set_string", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> itemStack, ((guiHandler, player, s, strings) -> {
                BukkitStackIdentifier identifier = guiHandler.getCustomCache().getItems().asBukkitIdentifier().orElse(null);
                if (identifier != null) {
                    guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                        ItemMeta meta = stack.getItemMeta();
                        CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
                        List<String> stringData = new ArrayList<>(modelDataComponent.getStrings());
                        if (!stringData.contains(s) && !s.isEmpty()) {
                            stringData.add(s);
                        }
                        modelDataComponent.setStrings(stringData);
                        meta.setCustomModelDataComponent(modelDataComponent);
                        stack.setItemMeta(meta);
                    });
                    creator.sendMessage(player, KEY + ".success_string_add", new Pair<>("%VALUE%", s));
                }
                return false;
            })));
            creator.registerButton(new ChatInputButton<>(KEY + ".remove_string", Material.RED_CONCRETE, ((hashMap, ccCache, guiHandler, player, guiInventory, itemStack, i, b) -> itemStack), (guiHandler, player, s, strings) -> {
                guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                    ItemMeta meta = stack.getItemMeta();
                    CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
                    List<String> stringData = new ArrayList<>(modelDataComponent.getStrings());
                    stringData.remove(s);
                    modelDataComponent.setStrings(stringData);
                    meta.setCustomModelDataComponent(modelDataComponent);
                    stack.setItemMeta(meta);
                });
                creator.sendMessage(player, KEY + ".success_string_remove", new Pair<>("%VALUE%", s));
                return false;
            }));
        }

        // FLOAT
        {
            creator.registerButton(new DummyButton<>(KEY + ".info_float", Material.NAME_TAG, ((hashMap, ccCache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                CustomItem item = guiHandler.getCustomCache().getItems().getItem();
                String text_model_data = !item.hasItemMeta() ? "&7&l/"
                        : !item.getItemMeta().hasCustomModelData() ? "&7&l/"
                        : item.getItemMeta().getCustomModelDataComponent().getStrings().isEmpty() ? "&7&l/"
                        : String.join(", ", item.getItemMeta().getCustomModelDataComponent().getFloats().stream().map(String::valueOf).toList());
                hashMap.put("%VAR%", text_model_data);
                return itemStack;
            })));
            creator.registerButton(new ChatInputButton<>(KEY + ".set_float", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> itemStack, ((guiHandler, player, s, strings) -> {
                BukkitStackIdentifier identifier = guiHandler.getCustomCache().getItems().asBukkitIdentifier().orElse(null);
                if (identifier != null) {
                    try {
                        float value = Float.parseFloat(s);
                        guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                            ItemMeta meta = stack.getItemMeta();
                            CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
                            List<Float> floatData = new ArrayList<>(modelDataComponent.getFloats());
                            if (!floatData.contains(value)) {
                                floatData.add(value);
                            }
                            modelDataComponent.setFloats(floatData);
                            meta.setCustomModelDataComponent(modelDataComponent);
                            stack.setItemMeta(meta);
                        });
                        creator.sendMessage(player, KEY + ".success_float_add", new Pair<>("%VALUE%", String.valueOf(value)));
                    } catch (Exception e) {
                        creator.sendMessage(player, KEY + ".error_invalid_value", new Pair<>("%VALUE%", s), new Pair<>("%DATA_TYPE%", "Float"));
                        return true;
                    }
                }
                return false;
            })));
            creator.registerButton(new ChatInputButton<>(KEY + ".remove_float", Material.RED_CONCRETE, ((hashMap, ccCache, guiHandler, player, guiInventory, itemStack, i, b) -> itemStack), (guiHandler, player, s, strings) -> {
                try {
                    float value = Float.parseFloat(s);
                    guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                        ItemMeta meta = stack.getItemMeta();
                        CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
                        List<Float> floatData = new ArrayList<>(modelDataComponent.getFloats());
                        floatData.remove(value);
                        modelDataComponent.setFloats(floatData);
                        meta.setCustomModelDataComponent(modelDataComponent);
                        stack.setItemMeta(meta);
                    });
                    creator.sendMessage(player, KEY + ".success_float_remove", new Pair<>("%VALUE%", s));
                } catch (Exception e) {
                    creator.sendMessage(player, KEY + ".error_invalid_value", new Pair<>("%VALUE%", s), new Pair<>("%DATA_TYPE%", "Float"));
                    return true;
                }
                return false;
            }));
        }

        // COLOR
        {
            creator.registerButton(new DummyButton<>(KEY + ".info_color", Material.NAME_TAG, ((hashMap, ccCache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                CustomItem item = guiHandler.getCustomCache().getItems().getItem();
                String text_model_data = !item.hasItemMeta() ? "&7&l/"
                        : !item.getItemMeta().hasCustomModelData() ? "&7&l/"
                        : item.getItemMeta().getCustomModelDataComponent().getStrings().isEmpty() ? "&7&l/"
                        : String.join(", ", item.getItemMeta().getCustomModelDataComponent().getColors().stream().map(color -> "A:" + color.getAlpha() + " | R:" + color.getRed() + " | G:" + color.getGreen() + " | B:" + color.getBlue()).toList());
                hashMap.put("%VAR%", text_model_data);
                return itemStack;
            })));
            ChatTabComplete<CCCache> tabComplete = (guiHandler, player, args) -> {
                if (args.length == 0) {
                    List<String> list = new ArrayList<>(Arrays.stream(Color.class.getFields()).map(Field::getName).toList());
                    list.add("ARGB");
                    list.add("RGB");
                    return list;
                }
                if (args.length == 1) {
                    List<String> list = new ArrayList<>(Arrays.stream(Color.class.getFields()).map(Field::getName).toList());
                    list.add("ARGB");
                    list.add("RGB");
                    return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
                }
                return null;
            };
            new ChatInputButton.Builder<>(creator, KEY + ".set_color")
                    .state(state -> state.icon(Material.GREEN_CONCRETE))
                    .tabComplete(tabComplete)
                    .inputAction((guiHandler, player, s, args) -> {
                        if (args.length == 0) {
                            creator.sendMessage(player, KEY + ".color_error");
                            return false;
                        }
                        Color c = getColor(creator, player, s, args);
                        if (c == null) {
                            return true;
                        }
                        guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                            ItemMeta meta = stack.getItemMeta();
                            CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
                            List<Color> colorData = new ArrayList<>(modelDataComponent.getColors());
                            if (!colorData.contains(c)) {
                                colorData.add(c);
                            }
                            modelDataComponent.setColors(colorData);
                            meta.setCustomModelDataComponent(modelDataComponent);
                            stack.setItemMeta(meta);
                        });
                        creator.sendMessage(player, KEY + ".success_color_add", new Pair<>("%VALUE%", s));
                        return false;
                    })
                    .register();
            new ChatInputButton.Builder<>(creator, KEY + ".remove_color")
                    .state(state -> state.icon(Material.RED_CONCRETE))
                    .tabComplete(tabComplete)
                    .inputAction((guiHandler, player, s, args) -> {
                        if (args.length == 0) {
                            creator.sendMessage(player, KEY + ".color_error");
                            return false;
                        }
                        Color c = getColor(creator, player, s, args);
                        if (c == null) {
                            return true;
                        }
                        guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> {
                            ItemMeta meta = stack.getItemMeta();
                            CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
                            List<Color> colorData = new ArrayList<>(modelDataComponent.getColors());
                            colorData.remove(c);
                            modelDataComponent.setColors(colorData);
                            meta.setCustomModelDataComponent(modelDataComponent);
                            stack.setItemMeta(meta);
                        });
                        creator.sendMessage(player, KEY + ".success_color_remove", new Pair<>("%VALUE%", s));
                        return false;
                    })
                    .register();
        }
    }


    private Color getColor(MenuItemCreator creator, Player player, String s, String[] args) {
        String input = args[0];
        Field f = Arrays.stream(Color.class.getFields()).filter(field -> field.getName().equalsIgnoreCase(input)).findAny().orElse(null);
        if (f != null) {
            try {
                return (Color) f.get(null);
            } catch (IllegalAccessException e) {
            }
        }
        if (input.equalsIgnoreCase("ARGB")) {
            if (args.length != 5) {
                creator.sendMessage(player, KEY + ".error_not_enough_args_argb");
                return null;
            }
            try {
                int a = Integer.parseInt(args[1]);
                int r = Integer.parseInt(args[2]);
                int g = Integer.parseInt(args[3]);
                int b = Integer.parseInt(args[4]);
                return Color.fromARGB(a, r, g, b);
            } catch (Exception e) {
                creator.sendMessage(player, KEY + ".error_argb", new Pair<>("%ERROR%", e.getMessage()));
                return null;
            }
        } else if (input.equalsIgnoreCase("RGB")) {
            if (args.length != 4) {
                creator.sendMessage(player, KEY + ".error_not_enough_args_rgb");
                return null;
            }
            try {
                int r = Integer.parseInt(args[1]);
                int g = Integer.parseInt(args[2]);
                int b = Integer.parseInt(args[3]);
                return Color.fromRGB(r, g, b);
            } catch (Exception e) {
                creator.sendMessage(player, KEY + ".error_rgb", new Pair<>("%ERROR%", e.getMessage()));
                return null;
            }
        }
        creator.sendMessage(player, KEY + ".color_error");
        return null;
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(20, KEY + ".info_string");
        update.setButton(29, KEY + ".set_string");
        update.setButton(38, KEY + ".remove_string");

        update.setButton(22, KEY + ".info_float");
        update.setButton(31, KEY + ".set_float");
        update.setButton(40, KEY + ".remove_float");

        update.setButton(24, KEY + ".info_color");
        update.setButton(33, KEY + ".set_color");
        update.setButton(42, KEY + ".remove_color");
    }
}

