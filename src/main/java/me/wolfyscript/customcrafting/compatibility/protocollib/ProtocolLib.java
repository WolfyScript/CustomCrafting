/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.compatibility.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.wrappers.MinecraftKey;
import java.util.function.UnaryOperator;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProtocolLib {

    private final CustomCrafting customCrafting;
    private final ProtocolManager protocolManager;
    private Function<MinecraftKey, Boolean> recipeFilter;
    private final Map<UUID, Long> playersLastRecipeBookInteract = new HashMap<>();
    private static final int RECIPEBOOK_CLICK_DELAY = 100;

    public ProtocolLib(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        init();
    }

    private void init() {
        registerServerSide();
    }

    private void registerServerSide() {
        recipeFilter = minecraftKey -> {
            if (minecraftKey.getPrefix().equals(NamespacedKeyUtils.NAMESPACE)) {
                CustomRecipe<?> recipe = customCrafting.getRegistries().getRecipes().get(NamespacedKey.of(minecraftKey.getFullKey()));
                if (recipe instanceof ICustomVanillaRecipe<?> vanillaRecipe && vanillaRecipe.isVisibleVanillaBook()) {
                    return !recipe.isHidden() && !recipe.isDisabled();
                }
                return false;
            }
            return true;
        };
        // Recipe packet that sends the discovered recipes to the client.
        protocolManager.addPacketListener(new PacketAdapter(customCrafting, ListenerPriority.HIGH, PacketType.Play.Server.RECIPES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier<List<MinecraftKey>> lists = packet.getLists(MinecraftKey.getConverter());
                //Modify the recipes
                lists.modify(0, input -> filterAndAddMissingRecipes(input));
                //Modify Highlighted recipes
                lists.modify(1, input -> filterAndAddMissingRecipes(input));
            }
        });
        // Recipe packet that sends the recipe data to the client.
        protocolManager.addPacketListener(new PacketAdapter(customCrafting, ListenerPriority.HIGH, PacketType.Play.Server.RECIPE_UPDATE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier<List<RecipeWrapper>> lists = packet.getLists(getRecipeKeyConverter());
                lists.modify(0, input -> input.stream().filter(recipeWrapper -> recipeFilter.apply(recipeWrapper.getKey())).collect(Collectors.toList()));
            }
        });

        //Prevent spam clicking of the recipe book, which might cause lag when players are using auto-clickers
        if (!customCrafting.getApi().getCore().getCompatibilityManager().getPlugins().hasIntegration("ItemsAdder")) {
            //No need to register this listener when ItemsAdder is installed. It has its own listener for this.
            protocolManager.addPacketListener(new PacketAdapter(customCrafting, ListenerPriority.HIGH, PacketType.Play.Client.AUTO_RECIPE) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    long currentMillis = System.currentTimeMillis();
                    UUID uuid = event.getPlayer().getUniqueId();
                    if (playersLastRecipeBookInteract.containsKey(uuid)) {
                        long lastInteract = playersLastRecipeBookInteract.getOrDefault(uuid, 0L);
                        if (currentMillis - lastInteract <= RECIPEBOOK_CLICK_DELAY) {
                            event.setCancelled(true);
                        } else {
                            playersLastRecipeBookInteract.put(uuid, currentMillis);
                        }
                    } else {
                        playersLastRecipeBookInteract.put(uuid, currentMillis);
                    }

                    //Call the PrepareItemCraftEvent one more time, if the recipe is a custom recipe, to update the last ingredient in the grid
                    //Issue #136 – Items disappear when using recipe book on crafting table.
                    NamespacedKey recipeId = NamespacedKey.of(event.getPacket().getMinecraftKeys().read(0).getFullKey());
                    if (customCrafting.getRegistries().getRecipes().has(recipeId)) {
                        Player player = event.getPlayer();
                        if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory craftingInventory) {
                            Runnable callPreEvent = () -> Bukkit.getPluginManager().callEvent(new PrepareItemCraftEvent(craftingInventory, event.getPlayer().getOpenInventory(), false));
                            if (event.isAsync()) {
                                Bukkit.getScheduler().runTask(customCrafting, callPreEvent);
                            } else {
                                callPreEvent.run();
                            }

                        }
                    }
                }
            });
        }
    }

    private List<MinecraftKey> filterAndAddMissingRecipes(List<MinecraftKey> input) {
        return input.stream().filter(recipeFilter::apply).collect(Collectors.toList());
    }

    public EquivalentConverter<RecipeWrapper> getRecipeKeyConverter() {
        return Converters.ignoreNull(new EquivalentConverter<>() {

            public Object getGeneric(RecipeWrapper specific) {
                return specific.getRecipe();
            }

            public RecipeWrapper getSpecific(Object generic) {
                FuzzyReflection reflection = FuzzyReflection.fromClass(generic.getClass(), false);
                MethodAccessor idAccessor = Accessors.getMethodAccessor(reflection.getMethod(FuzzyMethodContract.newBuilder().returnTypeExact(MinecraftReflection.getMinecraftKeyClass()).build()));
                return new RecipeWrapper(MinecraftKey.fromHandle(idAccessor.invoke(generic)), generic);
            }

            public Class<RecipeWrapper> getSpecificType() {
                return RecipeWrapper.class;
            }
        });
    }

    /**
     * This wrapper contains data read from a packet using the {@link EquivalentConverter} from {@link #getRecipeKeyConverter()}.
     * It will cache the handle of the recipe, and will be reapplied to the packet.
     */
    private record RecipeWrapper(MinecraftKey key, Object recipe) {

        public MinecraftKey getKey() {
            return key;
        }

        public Object getRecipe() {
            return recipe;
        }
    }

}
