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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.MiniMessage;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.Tag;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import me.wolfyscript.utilities.util.eval.operators.BoolOperatorConst;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderStringConst;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BookMetaMergeAdapter extends MergeAdapter {

    @JsonIgnore
    private CustomCrafting customCrafting;
    @JsonIgnore
    private MiniMessage miniMessage;

    // Page options
    private BoolOperator copyPages = new BoolOperatorConst(false);
    private BoolOperator replacePages = new BoolOperatorConst(false);
    private ValueProvider<Integer> insertAtPage = null;
    private List<ElementOptionComponentBukkit> pages = new ArrayList<>();
    private List<? extends ValueProvider<String>> extraPages = new ArrayList<>();
    private BoolOperator addExtraPagesFirst = new BoolOperatorConst(false);
    // Title options
    private BoolOperator copyTitle = new BoolOperatorConst(false);
    private BoolOperator replaceTitle = new BoolOperatorConst(false);
    private ValueProvider<String> titlePrefix = new ValueProviderStringConst("");
    private ValueProvider<String> titleSuffix = new ValueProviderStringConst("");
    // Author options
    private BoolOperator copyAuthor = new BoolOperatorConst(false);
    private BoolOperator replaceAuthor = new BoolOperatorConst(false);
    private ValueProvider<String> authorPrefix = new ValueProviderStringConst("");
    private ValueProvider<String> authorSuffix = new ValueProviderStringConst("");
    // Generation options
    private BoolOperator copyGeneration = new BoolOperatorConst(false);
    private BookMeta.Generation changeGenerationTo = null;

    public BookMetaMergeAdapter() {
        super(new NamespacedKey(CustomCrafting.inst(), "book"));
        this.customCrafting = CustomCrafting.inst(); // TODO: inject instead (v5)!
        this.miniMessage = customCrafting.getApi().getChat().getMiniMessage();
    }

    public BookMetaMergeAdapter(MergeAdapter adapter) {
        super(adapter);
        this.customCrafting = CustomCrafting.inst(); // TODO: inject instead (v5)!
        this.miniMessage = customCrafting.getApi().getChat().getMiniMessage();
    }

    public BoolOperator getCopyPages() {
        return copyPages;
    }

    public void setCopyPages(BoolOperator copyPages) {
        this.copyPages = copyPages;
    }

    public BoolOperator getReplacePages() {
        return replacePages;
    }

    public void setReplacePages(BoolOperator replacePages) {
        this.replacePages = replacePages;
    }

    public List<ElementOptionComponentBukkit> getPages() {
        return pages;
    }

    public void setPages(List<ElementOptionComponentBukkit> pages) {
        this.pages = pages;
    }

    public List<? extends ValueProvider<String>> getExtraPages() {
        return extraPages;
    }

    public void setExtraPages(List<? extends ValueProvider<String>> extraPages) {
        this.extraPages = extraPages;
    }

    public BoolOperator getAddExtraPagesFirst() {
        return addExtraPagesFirst;
    }

    public void setAddExtraPagesFirst(BoolOperator addExtraPagesFirst) {
        this.addExtraPagesFirst = addExtraPagesFirst;
    }

    public BoolOperator getCopyTitle() {
        return copyTitle;
    }

    public void setCopyTitle(BoolOperator copyTitle) {
        this.copyTitle = copyTitle;
    }

    public BoolOperator getReplaceTitle() {
        return replaceTitle;
    }

    public void setReplaceTitle(BoolOperator replaceTitle) {
        this.replaceTitle = replaceTitle;
    }

    public BoolOperator getCopyAuthor() {
        return copyAuthor;
    }

    public void setCopyAuthor(BoolOperator copyAuthor) {
        this.copyAuthor = copyAuthor;
    }

    public BoolOperator getReplaceAuthor() {
        return replaceAuthor;
    }

    public void setReplaceAuthor(BoolOperator replaceAuthor) {
        this.replaceAuthor = replaceAuthor;
    }

    public BoolOperator getCopyGeneration() {
        return copyGeneration;
    }

    public void setCopyGeneration(BoolOperator copyGeneration) {
        this.copyGeneration = copyGeneration;
    }

    public BookMeta.Generation getChangeGenerationTo() {
        return changeGenerationTo;
    }

    public void setChangeGenerationTo(BookMeta.Generation changeGenerationTo) {
        this.changeGenerationTo = changeGenerationTo;
    }

    private Optional<ValueProvider<Integer>> insertAtPage() {
        return Optional.ofNullable(insertAtPage);
    }

    @JsonGetter
    ValueProvider<Integer> getInsertAtPage() {
        return insertAtPage;
    }

    public void setInsertAtPage(ValueProvider<Integer> insertAtPage) {
        this.insertAtPage = insertAtPage;
    }

    public ValueProvider<String> getTitlePrefix() {
        return titlePrefix;
    }

    public void setTitlePrefix(ValueProvider<String> titlePrefix) {
        this.titlePrefix = titlePrefix;
    }

    public ValueProvider<String> getTitleSuffix() {
        return titleSuffix;
    }

    public void setTitleSuffix(ValueProvider<String> titleSuffix) {
        this.titleSuffix = titleSuffix;
    }

    public ValueProvider<String> getAuthorPrefix() {
        return authorPrefix;
    }

    public void setAuthorPrefix(ValueProvider<String> authorPrefix) {
        this.authorPrefix = authorPrefix;
    }

    public ValueProvider<String> getAuthorSuffix() {
        return authorSuffix;
    }

    public void setAuthorSuffix(ValueProvider<String> authorSuffix) {
        this.authorSuffix = authorSuffix;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        final var resultMeta = result.getItemMeta();
        if (!(resultMeta instanceof BookMeta resultBookMeta)) return result;
        final TagResolver papiResolver = TagResolverUtil.papi(player);
        final TagResolver langResolver = TagResolver.resolver("translate", (args, context) -> {
            String text = args.popOr("The <translate> tag requires exactly one argum/data get entity @s SelectedItement! The path to the language entry!").value();
            return Tag.selfClosingInserting(customCrafting.getApi().getChat().translated(text, papiResolver));
        });
        final EvalContext evalContext = player == null ? new EvalContext() : new EvalContextPlayer(player);

        // TODO: Paper specific Adventure support (v5)

        List<String> updatedPages = new ArrayList<>();
        StringBuilder title = new StringBuilder();
        title.append(BukkitComponentSerializer.legacy().serialize(miniMessage.deserialize(titlePrefix.getValue(evalContext), papiResolver, langResolver)));
        StringBuilder author = new StringBuilder();
        author.append(BukkitComponentSerializer.legacy().serialize(miniMessage.deserialize(authorPrefix.getValue(evalContext), papiResolver, langResolver)));
        BookMeta.Generation generation = null;

        for (IngredientData data : recipeData.getBySlots(slots)) {
            var meta = data.itemStack().getItemMeta();
            if (!(meta instanceof BookMeta bookMeta)) continue;

            List<String> targetPages = new ArrayList<>(bookMeta.getPages());
            if (bookMeta.hasPages() && copyPages.evaluate(evalContext)) {
                for (ElementOptionComponentBukkit page : pages) {
                    page.readFromSource(targetPages, evalContext, miniMessage, papiResolver, langResolver);
                }
            }
            if (bookMeta.hasTitle()) {
                title.append(bookMeta.getTitle());
            }
            if (bookMeta.hasAuthor()) {
                author.append(bookMeta.getAuthor());
            }
            if (bookMeta.hasGeneration()) {
                generation = bookMeta.getGeneration();
            }
        }
        applyPagesToMeta(resultBookMeta, updatedPages, evalContext, papiResolver, langResolver);
        if (copyTitle.evaluate(evalContext)) {
            title.append(BukkitComponentSerializer.legacy().serialize(miniMessage.deserialize(titleSuffix.getValue(evalContext), papiResolver, langResolver)));
            resultBookMeta.setTitle((replaceTitle.evaluate(evalContext) ? "" : resultBookMeta.getTitle()) + title);
        }
        if (copyAuthor.evaluate(evalContext)) {
            author.append(BukkitComponentSerializer.legacy().serialize(miniMessage.deserialize(authorSuffix.getValue(evalContext), papiResolver, langResolver)));
            resultBookMeta.setTitle((replaceAuthor.evaluate(evalContext) ? "" : resultBookMeta.getAuthor()) + author);
        }
        if (copyGeneration.evaluate(evalContext)) {
            resultBookMeta.setGeneration(generation);
        } else if (changeGenerationTo != null) {
            resultBookMeta.setGeneration(changeGenerationTo);
        }
        result.setItemMeta(resultBookMeta);
        return result;
    }

    protected void applyPagesToMeta(BookMeta resultBookMeta, List<String> updatedPages, EvalContext evalContext, TagResolver... tagResolvers) {
        boolean addExtraPagesFirstBool = addExtraPagesFirst.evaluate(evalContext);
        final var extraPagesList = extraPages.stream().map(valueProvider -> BukkitComponentSerializer.legacy().serialize(miniMessage.deserialize(valueProvider.getValue(evalContext), tagResolvers))).collect(Collectors.toList());
        List<String> finalPages = new ArrayList<>(resultBookMeta.getPages());
        if (replacePages.evaluate(evalContext)) {
            if (addExtraPagesFirstBool) {
                finalPages = extraPagesList;
                finalPages.addAll(updatedPages);
            } else {
                finalPages = updatedPages;
                finalPages.addAll(extraPagesList);
            }
        } else {
            int index = insertAtPage().map(integerValueProvider -> integerValueProvider.getValue(evalContext)).orElse(finalPages.size());
            if (index < 0) {
                index = finalPages.size() + (index % (finalPages.size()+1)); //Convert the negative index to a positive reverted index, that starts from the end.
            }
            index = index % (finalPages.size() + 1); //Prevent out of bounds
            if (index <= finalPages.size()) { // Shouldn't be false! Index out of bounds!
                if (addExtraPagesFirstBool) {
                    finalPages.addAll(index, extraPagesList);
                    finalPages.addAll(index + extraPagesList.size(), updatedPages);
                } else {
                    finalPages.addAll(index, updatedPages);
                    finalPages.addAll(index + updatedPages.size(), extraPagesList);
                }
            }
        }
        resultBookMeta.setPages(finalPages);
    }

    @Override
    public MergeAdapter clone() {
        return new BookMetaMergeAdapter(this);
    }
}
