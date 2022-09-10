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

import me.wolfyscript.customcrafting.CustomCrafting;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Patreon {

    final List<Patron> patronList = new ArrayList<>();
    private final CustomCrafting customCrafting;

    public Patreon(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public void initialize() {
        addPatron(new Patron("Omarlatif", "f24ef68e-5126-414d-9102-54f15c023954", Tier.LEGEND));
        addPatron(new Patron("Nat R", "29e1d027-d8c2-457f-a1a8-e5387b84de31", Tier.ELITE));
        addPatron(new Patron("Junye Zhou", "02fa85c4-97ea-4d1c-a5a9-f3b5e2f85975", Tier.DIAMOND));
        addPatron(new Patron("Apprehentice", "db61eab0-7fb1-48db-986f-125e73787976", Tier.WOLFRAM));
        addPatron(new Patron("PwassonDoDouce", Tier.WOLFRAM));
        addPatron(new Patron("Mr_Mint_", Tier.WOLFRAM));
        addPatron(new Patron("Mithran", Tier.WOLFRAM));
        addPatron(new Patron("Isabel Maskrey", Tier.WOLFRAM));
        addPatron(new Patron("nocando", "777d6910-a1a8-453e-964e-4de7286e1fcd", Tier.WOLFRAM));
        addPatron(new Patron("MoOrizzle", Tier.WOLFRAM));
    }

    public void printPatreonCredits() {
        var logger = customCrafting.getLogger();
        logger.info("");
        logger.info("Special thanks to my Patrons for supporting this project: ");
        int linePos = 0;
        StringBuilder sB = new StringBuilder();
        for (Patron patron : patronList) {
            String name = patron.getName();
            sB.append(name);
            if (linePos == 5) {
                logger.log(Level.INFO, sB.toString());
                sB = new StringBuilder();
                linePos = 0;
            } else {
                sB.append(", ");
                linePos++;
            }
        }
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
