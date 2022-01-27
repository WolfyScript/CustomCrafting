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
        addPatron(new Patron("Junye Zhou", Tier.DIAMOND));
        addPatron(new Patron("Apprehentice", "db61eab0-7fb1-48db-986f-125e73787976", Tier.WOLFRAM));
        addPatron(new Patron("Alex", "af1ef7e4-acc3-44a1-8323-8f50b92be2c9", Tier.WOLFRAM));
        addPatron(new Patron("PwassonDoDouce", Tier.WOLFRAM));
        addPatron(new Patron("Alexander", Tier.WOLFRAM));
        addPatron(new Patron("Mr_Mint_", Tier.WOLFRAM));
        addPatron(new Patron("Mithran", Tier.WOLFRAM));
        addPatron(new Patron("Jack Bruton", Tier.WOLFRAM));
        addPatron(new Patron("랜디", Tier.WOLFRAM));
        addPatron(new Patron("AkkLive", Tier.WOLFRAM));
        addPatron(new Patron("Jaden Bautista", Tier.WOLFRAM));
        addPatron(new Patron("Tong Teerasak", Tier.SUPPORTER));
        addPatron(new Patron("Илья Nalurione", Tier.SUPPORTER));
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
