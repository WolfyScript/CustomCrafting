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
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.CauldronUtils;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.inventory.ItemStack;
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
    private ItemStack[] result;

    @JsonCreator
    public CauldronBlockData(@JacksonInject Vector pos, @JacksonInject ChunkStorage chunkStorage) {
        super(ID);
        this.pos = pos;
        this.chunkStorage = chunkStorage;
        this.customCrafting = CustomCrafting.inst(); //TODO: Somehow use Guice to inject this?! Together with Jackson serialization...
        this.result = new ItemStack[4];
        reset();
        resetResult();
    }

    public void initNewRecipe(CacheCauldronWorkstation cache) {
        cache.getPreCookEvent().ifPresent(event -> {
            cache.resetInput();
            this.recipe = event.getRecipe();
            this.cookingTime = event.getCookingTime();
            this.passedTicks = 0;
            this.ticker = Bukkit.getScheduler().runTaskTimer(customCrafting, this::tick, 1, 1);
        });
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
    public boolean isResultEmpty() {
        for (ItemStack stack : result) {
            if (!ItemUtils.isAirOrNull(stack)) return false;
        }
        return true;
    }

    @JsonIgnore
    public Optional<CustomRecipeCauldron> getRecipe() {
        return Optional.ofNullable(recipe);
    }

    public ItemStack[] getResult() {
        return result;
    }

    public void tick() {
        if (recipe == null) {
            reset();
            resetResult();
            return;
        }
        if (chunkStorage.getChunk().isEmpty()) return;
        final Block block = chunkStorage.getChunk().get().getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        final Location loc = block.getLocation();

        if (passedTicks >= cookingTime) {
            // Output Result & reset data!
            var event = new CauldronCookEvent(this);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                passedTicks = 0;
            } else {
                if (event.getRecipe().getFluidLevel() > 0 && block.getBlockData() instanceof Levelled levelled) {
                    int newLevel = levelled.getLevel() - event.getRecipe().getFluidLevel();
                    if (newLevel <= 0) {
                        block.setType(Material.CAULDRON);
                    } else {
                        levelled.setLevel(newLevel);
                        block.setBlockData(levelled);
                    }
                }
                CustomItem air = new CustomItem(ItemUtils.AIR);
                Location locCopy = loc.clone();

                Result result = recipe.getResult();
                result.executeExtensions(locCopy, true, null);
                this.result[0] = result.getItem(loc.getBlock()).orElse(air).create();
                // Handle additional results
                for (int i = 0; i < 3; i++) {
                    Result additional = recipe.getAdditionalResults()[i];
                    additional.executeExtensions(locCopy, true, null);
                    this.result[i+1] = additional.getItem(block).orElse(air).create();
                }
                reset();
            }
            return;
        }

        getCauldronStatus(block).ifPresent(status -> {
            final int level = CauldronUtils.getLevel(block);
            final World world = block.getWorld();

            if (recipe.checkRecipeStatus(status)) {
                spawnBubbles(world, loc, level);
                world.spawnParticle(Particle.REDSTONE, loc.add(particleLevel(level)), 1, 0.17, 0.2, 0.17, 4.0, new Particle.DustOptions(Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255)), random.nextInt(2)));
                passedTicks++;
            } else {
                //The cauldron doesn't fulfill the requirements of the recipe. Perhaps water level changed or the campfire was extinguished.
                passedTicks -= 2;
                if (passedTicks <= 0) {
                    reset();
                    resetResult();
                }
            }
        });

    }

    @JsonIgnore
    public Optional<CauldronStatus> getCauldronStatus() {
        if (chunkStorage.getChunk().isEmpty()) return Optional.empty();
        final Block block = chunkStorage.getChunk().get().getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        return getCauldronStatus(block);
    }

    private Optional<CauldronStatus> getCauldronStatus(Block block) {
        return Optional.of(new CauldronStatus(block));
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
        if (ticker != null) {
            this.ticker.cancel();
            this.ticker = null;
        }
    }

    public void resetResult() {
        this.result = new ItemStack[4];
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

    public static class CauldronStatus {

        private final boolean hasCampfire;
        private final boolean hasSoulCampfire;
        private final boolean isLit;
        private final boolean isSignalFire;
        private final int level;
        private final boolean hasWater;
        private final boolean hasLava;
        private final Block block;

        public CauldronStatus(Block block) {
            this.block = block;
            this.hasLava = block.getType().equals(Material.LAVA_CAULDRON);
            this.hasWater = !hasLava && block.getType().equals(Material.WATER_CAULDRON);
            final Block blockBelow = block.getLocation().subtract(0, 1, 0).getBlock();
            this.hasCampfire = blockBelow.getType().equals(Material.CAMPFIRE);
            this.hasSoulCampfire = !hasCampfire && blockBelow.getType().equals(Material.SOUL_CAMPFIRE);
            if (hasCampfire || hasSoulCampfire) {
                Campfire campfire = (Campfire) blockBelow.getBlockData();
                this.isLit = campfire.isLit();
                this.isSignalFire = campfire.isSignalFire();
            } else {
                this.isLit = false;
                this.isSignalFire = false;
            }
            this.level = CauldronUtils.getLevel(block);
        }

        public boolean hasCampfire() {
            return hasCampfire;
        }

        public boolean hasSoulCampfire() {
            return hasSoulCampfire;
        }

        public boolean isLit() {
            return isLit;
        }

        public boolean isSignalFire() {
            return isSignalFire;
        }

        public boolean hasLava() {
            return hasLava;
        }

        public boolean hasWater() {
            return hasWater;
        }

        public int getLevel() {
            return level;
        }

        public Block getBlock() {
            return block;
        }
    }
}
