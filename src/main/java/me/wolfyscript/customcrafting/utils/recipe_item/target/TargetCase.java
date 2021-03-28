package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class TargetCase {

    private final APIReference caseReference;
    private final Result<NoneResultTarget> result;

    @JsonIgnore
    private CustomItem ingredient;

    @JsonCreator
    public TargetCase(@JsonProperty("case") APIReference caseReference, @JsonProperty("result") Result<NoneResultTarget> result) {
        this.caseReference = caseReference;
        this.result = result;
        build();
    }

    @JsonProperty("case")
    public APIReference getCaseReference() {
        return caseReference;
    }

    @JsonProperty("result")
    public Result<NoneResultTarget> getResult() {
        return result;
    }

    public void build() {
        ingredient = CustomItem.of(caseReference);
        result.buildChoices();
    }

    public Optional<Result<NoneResultTarget>> check(ItemStack itemStack) {
        return ingredient.isSimilar(itemStack) ? Optional.of(result) : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetCase that = (TargetCase) o;
        return Objects.equals(caseReference, that.caseReference) && Objects.equals(result, that.result) && Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseReference, result, ingredient);
    }
}
