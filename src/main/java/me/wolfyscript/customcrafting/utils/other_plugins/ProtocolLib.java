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

package me.wolfyscript.customcrafting.utils.other_plugins;

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
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProtocolLib {

    private final CustomCrafting plugin;
    private final ProtocolManager protocolManager;

    public ProtocolLib(CustomCrafting plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        init();
    }

    private void init() {
        registerServerSide();
    }

    private void registerServerSide() {
        Function<MinecraftKey, Boolean> recipeFilter = minecraftKey -> {
            if (minecraftKey.getPrefix().equals(NamespacedKeyUtils.NAMESPACE)) {
                CustomRecipe<?> recipe = plugin.getRegistries().getRecipes().get(NamespacedKeyUtils.toInternal(NamespacedKey.of(minecraftKey.getFullKey())));
                if (recipe instanceof ICustomVanillaRecipe<?> vanillaRecipe && vanillaRecipe.isVisibleVanillaBook()) {
                    return !recipe.isHidden() && !recipe.isDisabled();
                }
                return false;
            }
            return true;
        };
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.RECIPES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier<List<MinecraftKey>> lists = packet.getLists(MinecraftKey.getConverter());
                lists.modify(0, input -> { //Modify the recipes
                    return input.stream().filter(recipeFilter::apply).collect(Collectors.toList());
                });
                lists.modify(1, input -> { //Modify Highlighted recipes
                    return input.stream().filter(recipeFilter::apply).collect(Collectors.toList());
                });
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.RECIPE_UPDATE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                StructureModifier<List<RecipeWrapper>> lists = packet.getLists(getRecipeKeyConverter());
                lists.modify(0, input -> input.stream().filter(recipeWrapper -> recipeFilter.apply(recipeWrapper.getKey())).collect(Collectors.toList()));
            }
        });
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
