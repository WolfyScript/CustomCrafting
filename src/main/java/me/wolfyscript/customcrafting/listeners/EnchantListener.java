package me.wolfyscript.customcrafting.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class EnchantListener implements Listener {

    @EventHandler
    public void prepareEnchant(PrepareItemEnchantEvent event) {
        System.out.println("Try to enchant: " + event.getItem());
        System.out.println("with: [" + event.getOffers().length + "]");

        EnchantmentOffer[] offers = event.getOffers();

        for (int i = 0; i < offers.length; i++) {
            EnchantmentOffer offer = offers[i];
            if (offer != null) {
                System.out.println("    " + offer.getEnchantment());
            } else {
                offers[i] = new EnchantmentOffer(Enchantment.PROTECTION_ENVIRONMENTAL, 2, 3);
            }
        }
        System.out.println(event.isCancelled());
        event.getEnchanter().updateInventory();
    }

    public void enchantItem(EnchantItemEvent event) {

    }

}
