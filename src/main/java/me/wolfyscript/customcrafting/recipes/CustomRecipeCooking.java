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

package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import java.util.logging.Level;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.nms.item.crafting.FunctionalRecipeBuilderCooking;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public abstract class CustomRecipeCooking<C extends CustomRecipeCooking<C, T>, T extends CookingRecipe<?>> extends CustomRecipe<C> implements ICustomVanillaRecipe<T> {

    private Ingredient source;
    private float exp;
    private int cookingTime;

    protected CustomRecipeCooking(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.exp = node.path("exp").floatValue();
        this.cookingTime = node.path("cooking_time").asInt();
        this.source = ItemLoader.loadIngredient(node.path("source"));
    }

    protected CustomRecipeCooking(NamespacedKey key, CustomCrafting customCrafting) {
        super(key, customCrafting);
        this.source = new Ingredient();
        this.exp = 0;
        this.cookingTime = 80;
    }

    protected CustomRecipeCooking(CustomRecipeCooking<C, T> customRecipeCooking) {
        super(customRecipeCooking);
        this.source = customRecipeCooking.source;
        this.exp = customRecipeCooking.exp;
        this.cookingTime = customRecipeCooking.cookingTime;
    }

    @Override
    public abstract C clone();

    /**
     * @param material The type of the block.
     * @return if the recipe can be used inside that type of block!
     */
    public abstract boolean validType(Material material);

    public Ingredient getSource() {
        return this.source;
    }

    public void setSource(@NotNull Ingredient source) {
        Preconditions.checkArgument(!source.isEmpty(), "Invalid source! Recipe must have non-air source!");
        this.source = source;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return this.source;
    }

    private void setIngredient(int slot, Ingredient ingredient) {
        setSource(ingredient);
    }

    /**
     * @param ingredient The ingredient to set as the source.
     * @deprecated Replaced by {@link #setSource(Ingredient)}
     */
    @Deprecated
    public void setIngredient(Ingredient ingredient) {
        setIngredient(0, ingredient);
    }

    public void setCookingTime(int cookingTime) {
        Preconditions.checkArgument(cookingTime <= Short.MAX_VALUE, "The cooking time cannot be higher than 32767.");
        this.cookingTime = cookingTime;
    }

    public float getExp() {
        return exp;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    protected RecipeChoice getRecipeChoice() {
        return isCheckNBT() ? new RecipeChoice.ExactChoice(getSource().getChoices().stream().map(CustomItem::create).toList()) : new RecipeChoice.MaterialChoice(getSource().getChoices().stream().map(i -> i.create().getType()).toList());
    }

    protected void registerRecipeIntoMinecraft(FunctionalRecipeBuilderCooking builder) {
        builder.setGroup(group);
        builder.setExperience(getExp());
        builder.setCookingTime(getCookingTime());
        builder.setRecipeMatcher((inventory, world) -> {
            Location location = inventory.getLocation();
            if (location != null && !checkConditions(new Conditions.Data(null, location.getBlock(), null))) return false;
            return getSource().test(inventory.getItem(0), isCheckNBT());
        });
        builder.setRecipeAssembler(inventory -> java.util.Optional.ofNullable(getResult().getItemStack()));
        builder.createAndRegister();
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        var player = guiHandler.getPlayer();
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(11))).setVariants(guiHandler, getSource().getChoices(player));
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(24))).setVariants(guiHandler, this.getResult().getChoices().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).toList());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        List<Condition<?>> conditions = getConditions().getValues().stream().filter(condition -> !condition.getNamespacedKey().getKey().equals("permission")).toList();
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition<?> condition : conditions) {
            event.setButton(36 + startSlot + slot, new NamespacedKey(ClusterRecipeBook.KEY, "conditions." + condition.getNamespacedKey().toString("_")));
            slot += 2;
        }
        event.setButton(22, cluster.getButton(ClusterRecipeBook.COOKING_ICON.getKey()));
        event.setButton(20, data.getLightBackground());
        event.setButton(11, ButtonContainerIngredient.key(cluster, 11));
        event.setButton(24, ButtonContainerIngredient.key(cluster, 24));
    }

    @Deprecated
    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeNumberField("cooking_time", cookingTime);
        gen.writeNumberField("exp", exp);
        gen.writeObjectField("result", result);
        gen.writeObjectField("source", source);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);

        byteBuf.writeVarInt(source.size());
        for (CustomItem choice : source.getChoices()) {
            byteBuf.writeItemStack(choice.create());
        }
    }

    @Override
    public boolean isVisibleVanillaBook() {
        return vanillaBook;
    }

    @Override
    public void setVisibleVanillaBook(boolean vanillaBook) {
        this.vanillaBook = vanillaBook;
    }

    @Override
    public boolean isAutoDiscover() {
        return autoDiscover;
    }

    @Override
    public void setAutoDiscover(boolean autoDiscover) {
        this.autoDiscover = autoDiscover;
    }
}
