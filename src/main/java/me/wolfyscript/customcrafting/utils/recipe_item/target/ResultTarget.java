package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ResultTarget {

    private List<TargetCase> cases;

    protected ResultTarget() {
        this.cases = new ArrayList<>();
    }

    protected ResultTarget(ResultTarget target) {
        this.cases = target.cases;
    }

    public List<TargetCase> getCases() {
        return cases;
    }

    public void setCases(List<TargetCase> cases) {
        this.cases = cases;
    }

    public abstract Optional<Result<NoneResultTarget>> get(@Nullable ItemStack[] ingredients);

    protected Optional<Result<NoneResultTarget>> check(ItemStack itemStack) {
        return cases.stream().map(targetCase -> targetCase.check(itemStack)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    public void clearCachedItems(Player player) {
        cases.parallelStream().forEach(targetCase -> targetCase.getResult().removeCachedItem(player));
    }

}
