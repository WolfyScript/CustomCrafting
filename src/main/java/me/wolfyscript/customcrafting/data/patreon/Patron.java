package me.wolfyscript.customcrafting.data.patreon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;

public class Patron {

    private final String name;
    private ItemStack head;

    public Patron(String name, String uuid, @NotNull ItemStack head, Patreon.Tier tier) {
        this.head = head;
        //https://sessionserver.mojang.com/session/minecraft/profile/db61eab07fb148db986f125e73787976?unsigned=false
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrafting.getInst(), () -> {
            try {
                if (!uuid.isEmpty()) {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "") + "?unsigned=false");
                    InputStreamReader reader = new InputStreamReader(url.openStream());
                    JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
                    String minecraftName = object.get("name").getAsString();
                    JsonObject textureProperty = object.get("properties").getAsJsonArray().get(0).getAsJsonObject();
                    this.head = PlayerHeadUtils.getViaValue(textureProperty.get("value").getAsString());
                    ItemMeta meta = this.head.getItemMeta();
                    if (!minecraftName.isEmpty()) {
                        meta.setLore(Collections.singletonList("§7aka. " + minecraftName));
                    }
                    this.head.setItemMeta(meta);
                }
            } catch (IOException e) {
                Bukkit.getLogger().info("Could not get skin data from session servers!");
            }
            ItemMeta meta = this.head.getItemMeta();
            meta.setDisplayName(tier.getColor() + "§l" + name);
            this.head.setItemMeta(meta);
        });
        this.name = name;
    }

    public Patron(String name, String uuid, Patreon.Tier tier) {
        this(name, uuid, new ItemStack(Material.CREEPER_HEAD), tier);
    }

    public Patron(String name, Patreon.Tier tier) {
        this(name, "", new ItemStack(Material.CREEPER_HEAD), tier);
    }

    public Patron(String name) {
        this(name, "", new ItemStack(Material.CREEPER_HEAD), Patreon.Tier.WOLFRAM);
    }

    public ItemStack getHead() {
        return head;
    }

    public String getName() {
        return name;
    }
}
