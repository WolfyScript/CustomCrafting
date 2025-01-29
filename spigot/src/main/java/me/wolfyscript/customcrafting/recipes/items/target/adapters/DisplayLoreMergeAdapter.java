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

package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.MiniMessage;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.Tag;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DisplayLoreMergeAdapter extends MergeAdapter {

    @JsonIgnore
    private CustomCrafting customCrafting;
    @JsonIgnore
    private MiniMessage miniMessage;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean replaceLore = false;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ValueProvider<Integer> insertAtIndex = null;
    private List<ElementOptionComponentBukkit> lines;
    private List<? extends ValueProvider<String>> extra;
    private boolean addExtraFirst = false;

    public DisplayLoreMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "display_lore"));
        this.replaceLore = false;
        this.extra = new ArrayList<>();
        this.customCrafting = CustomCrafting.inst(); // TODO: inject instead! (v5)
        this.miniMessage = customCrafting.getApi().getChat().getMiniMessage();
    }

    public DisplayLoreMergeAdapter(DisplayLoreMergeAdapter adapter) {
        super(adapter);
        this.replaceLore = adapter.replaceLore;
        this.extra = adapter.extra;
    }

    public void setLines(List<ElementOptionComponentBukkit> lines) {
        this.lines = lines;
    }

    public List<ElementOptionComponentBukkit> getLines() {
        return lines;
    }

    public void setReplaceLore(boolean replaceLore) {
        this.replaceLore = replaceLore;
    }

    public boolean isReplaceLore() {
        return replaceLore;
    }

    @JsonGetter
    public List<? extends ValueProvider<String>> getExtra() {
        return extra;
    }

    public void setExtra(List<? extends ValueProvider<String>> extra) {
        this.extra = extra;
    }

    public void setInsertAtIndex(ValueProvider<Integer> insertAtIndex) {
        this.insertAtIndex = insertAtIndex;
    }

    public boolean isAddExtraFirst() {
        return addExtraFirst;
    }

    public void setAddExtraFirst(boolean addExtraFirst) {
        this.addExtraFirst = addExtraFirst;
    }

    @JsonGetter
    ValueProvider<Integer> getInsertAtIndex() {
        return insertAtIndex;
    }

    public Optional<ValueProvider<Integer>> insertAtIndex() {
        return Optional.ofNullable(insertAtIndex);
    }

    public List<String> extra(EvalContext context, TagResolver... resolvers) {
        return getExtra().stream().map(valueProvider -> BukkitComponentSerializer.legacy().serialize(miniMessage.deserialize(valueProvider.getValue(context), resolvers))).toList();
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        var resultMeta = result.getItemMeta();
        if (resultMeta == null) return result;
        final TagResolver papiResolver = TagResolverUtil.papi(player);
        final TagResolver langResolver = TagResolver.resolver("translate", (args, context) -> {
            String text = args.popOr("The <translate> tag requires exactly one argument! The path to the language entry!").value();
            return Tag.selfClosingInserting(customCrafting.getApi().getChat().translated(text, papiResolver));
        });
        var evalContext = player == null ? new EvalContext() : new EvalContextPlayer(player);
        List<String> finalLore = new ArrayList<>();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            var meta = data.itemStack().getItemMeta();
            if (meta == null) continue;
            if (meta.hasLore()) {
                List<String> targetedLore = meta.getLore();
                assert targetedLore != null;
                for (ElementOptionComponentBukkit line : lines) {
                    finalLore.addAll(line.readFromSource(targetedLore, evalContext, miniMessage, papiResolver, langResolver));
                }
            }
        }
        List<String> resultLore = resultMeta.hasLore() ? resultMeta.getLore() : new ArrayList<>();
        assert resultLore != null;
        if (replaceLore) {
            resultLore = addExtraFirst ? extra(evalContext, papiResolver, langResolver) : new ArrayList<>();
            resultLore.addAll(finalLore);
        } else {
            if (addExtraFirst) {
                resultLore.addAll(extra(evalContext, papiResolver, langResolver));
            }
            int index = insertAtIndex().map(integerValueProvider -> integerValueProvider.getValue(evalContext)).orElse(resultLore.size());
            if (index < 0) {
                index = resultLore.size() + (index % (resultLore.size()+1)); //Convert the negative index to a positive reverted index, that starts from the end.
            }
            index = index % (resultLore.size() + 1); //Prevent out of bounds
            if (index <= resultLore.size()) { // Shouldn't be false! Index out of bounds!
                resultLore.addAll(index, finalLore);
            }
        }
        if (!addExtraFirst) {
            resultLore.addAll(extra(evalContext, papiResolver, langResolver));
        }
        resultMeta.setLore(resultLore);
        result.setItemMeta(resultMeta);
        return result;
    }

    @Override
    public DisplayLoreMergeAdapter clone() {
        return new DisplayLoreMergeAdapter(this);
    }

}
