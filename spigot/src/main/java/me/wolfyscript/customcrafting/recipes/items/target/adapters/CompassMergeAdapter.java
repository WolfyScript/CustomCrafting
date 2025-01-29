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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import me.wolfyscript.utilities.util.eval.operators.BoolOperatorConst;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CompassMergeAdapter extends MergeAdapter {

    private LocationSettings location = null;
    private BoolOperator overrideLocation = new BoolOperatorConst(false);
    private BoolOperator lodestoneTracked = null;
    private BoolOperator overrideLodestoneTracked = new BoolOperatorConst(false);

    public CompassMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "compass"));
    }

    public CompassMergeAdapter(CompassMergeAdapter adapter) {
        super(adapter);
    }

    public LocationSettings getLocation() {
        return location;
    }

    public void setLocation(LocationSettings location) {
        this.location = location;
    }

    public BoolOperator getOverrideLocation() {
        return overrideLocation;
    }

    public void setOverrideLocation(BoolOperator overrideLocation) {
        this.overrideLocation = overrideLocation;
    }

    public BoolOperator getLodestoneTracked() {
        return lodestoneTracked;
    }

    public void setLodestoneTracked(BoolOperator lodestoneTracked) {
        this.lodestoneTracked = lodestoneTracked;
    }

    public BoolOperator getOverrideLodestoneTracked() {
        return overrideLodestoneTracked;
    }

    public void setOverrideLodestoneTracked(BoolOperator overrideLodestoneTracked) {
        this.overrideLodestoneTracked = overrideLodestoneTracked;
    }

    public Optional<Location> location(EvalContext context) {
        return Optional.ofNullable(location).map(locationSettings -> {
                    World world = Bukkit.getWorld(locationSettings.worldName.getValue(context));
                    if (world == null) return null;
                    return new Location(world,
                            locationSettings.x.getValue(context),
                            locationSettings.y.getValue(context),
                            locationSettings.z.getValue(context)
                    );
                }
        );
    }

    public Optional<Boolean> lodestoneTracked(EvalContext context) {
        return Optional.ofNullable(lodestoneTracked).map(boolOperator -> boolOperator.evaluate(context));
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        var meta = result.getItemMeta();
        var context = player == null ? new EvalContext() : new EvalContextPlayer(player);
        if (meta instanceof CompassMeta compassMeta) {
            for (IngredientData ingredientData : recipeData.getBySlots(slots)) {
                if (ingredientData.itemStack().getItemMeta() instanceof CompassMeta ingredientCompassMeta) {
                    if (overrideLocation.evaluate(context)) {
                        compassMeta.setLodestone(location(context).orElse(null));
                    } else {
                        compassMeta.setLodestone(ingredientCompassMeta.getLodestone());
                    }
                    if (overrideLodestoneTracked.evaluate(context)) {
                        compassMeta.setLodestoneTracked(lodestoneTracked(context).orElse(false));
                    } else {
                        compassMeta.setLodestoneTracked(ingredientCompassMeta.isLodestoneTracked());
                    }
                    break; // Only target the first selected ingredient.
                }
            }
            result.setItemMeta(compassMeta);
        }
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new CompassMergeAdapter(this);
    }

    public record LocationSettings(ValueProvider<String> worldName, ValueProvider<Integer> x, ValueProvider<Integer> y, ValueProvider<Integer> z) {

            @JsonCreator
            public LocationSettings(@JsonProperty("worldName") ValueProvider<String> worldName, @JsonProperty("x") ValueProvider<Integer> x, @JsonProperty("y") ValueProvider<Integer> y, @JsonProperty("z") ValueProvider<Integer> z) {
                this.worldName = worldName;
                this.x = x;
                this.y = y;
                this.z = z;
            }
        }
}
