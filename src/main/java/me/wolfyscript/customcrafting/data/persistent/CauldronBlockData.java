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

package me.wolfyscript.customcrafting.data.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import java.util.Optional;
import java.util.Random;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronCookEvent;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class CauldronBlockData extends CustomBlockData {

    public static final NamespacedKey ID = new NamespacedKey("customcrafting", "cauldron");
    private static final Random random = new Random();

    private final CustomCrafting customCrafting;
    private final Vector pos;
    private final ChunkStorage chunkStorage;

    private CustomRecipeCauldron recipe;
    private int cookingTime;
    private int passedTicks;
    private BukkitTask ticker;
    private boolean dropItems;
    private CustomItem result;

    @JsonCreator
    public CauldronBlockData(@JacksonInject Vector pos, @JacksonInject ChunkStorage chunkStorage) {
        super(ID);
        this.pos = pos;
        this.chunkStorage = chunkStorage;
        this.customCrafting = CustomCrafting.inst(); //TODO: Somehow use Guice to inject this?! Together with Jackson serialization...
        reset();
        resetResult();
    }

    public void initNewRecipe(CauldronPreCookEvent event) {
        this.recipe = event.getRecipe();
        this.result = recipe.getResult().getItem(event.getCauldron()).orElse(new CustomItem(Material.AIR));
        this.recipe.getResult().removeCachedItem(event.getCauldron());
        this.dropItems = event.dropItems();
        this.cookingTime = event.getCookingTime();
        this.passedTicks = 0;
        this.ticker = Bukkit.getScheduler().runTaskTimer(customCrafting, this::tick, 1, 1);
    }

    public int getPassedTicks() {
        return passedTicks;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @JsonGetter("recipe")
    private String getRecipeKey() {
        return getRecipe().map(recipe -> recipe.getNamespacedKey().toString()).orElse("");
    }

    @JsonSetter("recipe")
    private void setRecipeByKey(String key) {
        if (key == null || key.isBlank()) {
            this.recipe = null;
        } else {
            CustomRecipe<?> customRecipe = customCrafting.getRegistries().getRecipes().get(NamespacedKey.of(key));
            if (customRecipe instanceof CustomRecipeCauldron recipeCauldron) {
                this.recipe = recipeCauldron;
            } else {
                this.recipe = null;
                resetResult();
            }
        }
    }

    @JsonIgnore
    public Optional<CustomRecipeCauldron> getRecipe() {
        return Optional.ofNullable(recipe);
    }

    @JsonIgnore
    public Optional<CustomItem> getResult() {
        return Optional.ofNullable(result);
    }

    @JsonGetter("result")
    private APIReference getResultReference() {
        return getResult().map(CustomItem::getApiReference).orElse(null);
    }

    @JsonSetter("result")
    private void setResultReference(APIReference reference) {
        this.result = ItemLoader.load(reference);
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void tick() {
        if (recipe == null) return;
        if (chunkStorage.getChunk().isEmpty()) return;
        final Block block = chunkStorage.getChunk().get().getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        final Location loc = block.getLocation();

        if (passedTicks >= cookingTime) {
            // TODO: Output Result & reset data!
            var event = new CauldronCookEvent(this);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                passedTicks = 0;
            } else {
                if (event.getRecipe().getWaterLevel() > 0 && block.getBlockData() instanceof Levelled levelled) {
                    int newLevel = levelled.getLevel() - event.getRecipe().getWaterLevel();
                    if (newLevel <= 0) {
                        block.setType(Material.CAULDRON);
                    } else {
                        levelled.setLevel(newLevel);
                        block.setBlockData(levelled);
                    }
                }
                recipe.getResult().executeExtensions(loc.clone(), true, null);
                if (event.dropItems()) {
                    var dropLocation = loc.clone().add(0.0, 0.5, 0.0);
                    loc.getWorld().dropItemNaturally(dropLocation, event.getResult().create());
                    if (recipe.getXp() > 0) {
                        ExperienceOrb orb = (ExperienceOrb) dropLocation.getWorld().spawnEntity(dropLocation, EntityType.EXPERIENCE_ORB);
                        orb.setExperience(recipe.getXp());
                    }
                }
                reset();
            }
            return;
        }

        final Block blockBelow = block.getLocation().subtract(0, 1, 0).getBlock();
        final boolean hasCampfire = blockBelow.getType().equals(Material.CAMPFIRE);
        final boolean hasSoulCampfire = !hasCampfire && blockBelow.getType().equals(Material.SOUL_CAMPFIRE);
        boolean isLit = false;
        boolean isSignalFire = false;
        if (hasCampfire || hasSoulCampfire) {
            Campfire campfire = (Campfire) blockBelow.getState();
            isLit = campfire.isLit();
            isSignalFire = campfire.isSignalFire();
        }

        final int level = Cauldrons.getLevel(block);
        final World world = block.getWorld();

        if (level >= recipe.getWaterLevel() && (block.getType().equals(Material.CAULDRON) || recipe.needsWater()) && (!recipe.needsFire() || isLit)) {
            spawnBubbles(world, loc, level);
            world.spawnParticle(Particle.REDSTONE, loc.add(particleLevel(level)), 1, 0.17, 0.2, 0.17, 4.0, new Particle.DustOptions(Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255)), random.nextInt(2)));
            passedTicks++;
        } else {
            //The cauldron doesn't fulfill the requirements of the recipe. Perhaps water level changed or the campfire was extinguished.
            passedTicks -= 2;
            if (passedTicks <= 0) {
                for (CustomItem customItem : recipe.getIngredient().getChoices()) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), customItem.getItemStack()));
                }
                reset();
                resetResult();
            }
        }

    }

    private Vector particleLevel(int level) {
        return new Vector(0.5, 0.35 + level * 0.2, 0.5);
    }

    private void spawnBubbles(World world, Location location, int level) {
        world.spawnParticle(Particle.BUBBLE_POP, location.clone().add(particleLevel(level)), 1, 0.15, 0.1, 0.15, 0.0000000001);
    }

    private void reset() {
        this.recipe = null;
        this.cookingTime = 0;
        this.passedTicks = 0;
        this.dropItems = false;
        if (ticker != null) {
            this.ticker.cancel();
        }
    }

    public void resetResult() {
        this.result = new CustomItem(Material.AIR);
    }

    @Override
    public void onLoad() {
        if (ticker == null || ticker.isCancelled()) {
            ticker = Bukkit.getScheduler().runTaskTimer(customCrafting, this::tick, 1, 1);
        }
    }

    @Override
    public void onUnload() {
        if (ticker != null) {
            this.ticker.cancel();
        }
    }

    @Override
    public CustomBlockData copy() {
        return null;
    }

    @Override
    public CustomBlockData copyTo(BlockStorage blockStorage) {
        return null;
    }
}
