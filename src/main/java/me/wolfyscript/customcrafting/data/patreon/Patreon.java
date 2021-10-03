package me.wolfyscript.customcrafting.data.patreon;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Patreon {

    final List<Patron> patronList = new ArrayList<>();

    public Patreon() {
    }

    public void initialize() {
        addPatron(new Patron("Nat R", "956faa3f-df9e-402b-bc13-39c03d4b4a5b", Tier.ELITE));
        addPatron(new Patron("柏潭 吳", Tier.ELITE));
        addPatron(new Patron("Junye Zhou", Tier.DIAMOND));
        addPatron(new Patron("Jack Bruton", Tier.DIAMOND));
        addPatron(new Patron("Apprehentice", "db61eab0-7fb1-48db-986f-125e73787976", Tier.WOLFRAM));
        addPatron(new Patron("Alex", "af1ef7e4-acc3-44a1-8323-8f50b92be2c9", Tier.WOLFRAM));
        addPatron(new Patron("gizmonster", "e502d121-de9d-4f5d-b7e5-0da747c4e2e8", Tier.WOLFRAM));
        addPatron(new Patron("Alexander", Tier.WOLFRAM));
        addPatron(new Patron("OTZ", Tier.WOLFRAM));
        addPatron(new Patron("The Hound Brothers", Tier.WOLFRAM));
        addPatron(new Patron("Xwdit", Tier.WOLFRAM));
        addPatron(new Patron("Pixelcraftian", Tier.WOLFRAM));
        addPatron(new Patron("DTNTW", Tier.WOLFRAM));
        addPatron(new Patron("Mr_Mint_", Tier.WOLFRAM));
        addPatron(new Patron("Hugo Hronec", Tier.WOLFRAM));
        addPatron(new Patron("Sundial Shark", Tier.WOLFRAM));
        addPatron(new Patron("Marcin Pootski", Tier.WOLFRAM));
        addPatron(new Patron("Mithran", Tier.WOLFRAM));
        addPatron(new Patron("Stanislav Novotný", Tier.WOLFRAM));
        addPatron(new Patron("王聪聪", Tier.WOLFRAM));
        addPatron(new Patron("安田 安田", Tier.WOLFRAM));
        addPatron(new Patron("Vladyslav Kharchuk", Tier.WOLFRAM));
    }

    private void addPatron(Patron patron) {
        patronList.add(patron);
    }

    public List<Patron> getPatronList() {
        return patronList;
    }

    public enum Tier {
        SUPPORTER(ChatColor.GRAY), WOLFRAM(ChatColor.GOLD), DIAMOND(ChatColor.AQUA), ELITE(ChatColor.LIGHT_PURPLE), LEGEND(ChatColor.RED);

        private final ChatColor chatColor;

        Tier(ChatColor chatColor) {
            this.chatColor = chatColor;
        }

        public ChatColor getColor() {
            return chatColor;
        }
    }

}
