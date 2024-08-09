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

package me.wolfyscript.customcrafting.configs.recipebook;

import com.google.common.base.Preconditions;

public enum AlignItems {

    /**
     * Aligns the items in the center of the inventory,<br>
     * keeping them together in the center as much as possible.<br>
     * If the amount of categories is a power of two, then there may
     * be an empty slot in the middle.
     */
    CENTER_CLUSTER(
            new int[]{4},
            new int[]{3, 5},
            new int[]{3, 4, 5},
            new int[]{2, 3, 5, 6},
            new int[]{2, 3, 4, 5, 6},
            new int[]{1, 2, 3, 5, 6, 7},
            new int[]{1, 2, 3, 4, 5, 6, 7},
            new int[]{0, 1, 2, 3, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    ),
    /**
     * Aligns the items in the center of the inventory,<br>
     * keeping an equal amount of spacing between items<br>
     * while keeping them as much in the center as possible.
     */
    CENTER_SPREAD(
            new int[]{4},
            new int[]{3, 5},
            new int[]{2, 4, 6},
            new int[]{1, 3, 5, 7},
            new int[]{0, 2, 4, 6, 8},
            new int[]{0, 2, 3, 5, 6, 8},
            new int[]{0, 1, 3, 4, 5, 7, 8},
            new int[]{0, 1, 2, 3, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    ),
    /**
     * Keeps the same space between the categories as far as possible.<br>
     * When equal spacing cannot be achieved between each item, then
     * it makes smaller clusters and spaces those equally instead.
     */
    SPACE_BETWEEN(
            new int[]{4},
            new int[]{2, 6},
            new int[]{1, 4, 7},
            new int[]{1, 3, 5, 7},
            new int[]{0, 2, 4, 6, 8},
            new int[]{0, 1, 3, 5, 7, 8},
            new int[]{0, 1, 3, 4, 5, 7, 8},
            new int[]{0, 1, 2, 3, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    ),
    /**
     * Aligns the items to the left of the inventory.
     */
    LEFT(
            new int[]{0},
            new int[]{0, 1},
            new int[]{0, 1, 2},
            new int[]{0, 1, 2, 3},
            new int[]{0, 1, 2, 3, 4},
            new int[]{0, 1, 2, 3, 4, 5},
            new int[]{0, 1, 2, 3, 4, 5, 6},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}),
    /**
     * Aligns the items to right of the inventory.
     */
    RIGHT(
            new int[]{8},
            new int[]{7, 8},
            new int[]{6, 7, 8},
            new int[]{5, 6, 7, 8},
            new int[]{4, 5, 6, 7, 8},
            new int[]{3, 4, 5, 6, 7, 8},
            new int[]{2, 3, 4, 5, 6, 7, 8},
            new int[]{1, 2, 3, 4, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    ),
    /**
     * Aligns the items using the specified priority.
     * It first tries to put the items on the left and right
     * of the inventory.
     * When there is a remaining item it is put into the middle.
     */
    LEFT_RIGHT_CENTER(
            new int[]{4},
            new int[]{0, 8},
            new int[]{0, 4, 8},
            new int[]{0, 1, 7, 8},
            new int[]{0, 1, 4, 7, 8},
            new int[]{0, 1, 2, 6, 7, 8},
            new int[]{0, 1, 2, 4, 6, 7, 8},
            new int[]{0, 1, 2, 3, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    ),
    /**
     * Aligns the items using the specified priority.
     * It first tries to put the items on the left and right
     * of the inventory.
     * When there is a remaining item it is moved to the left.
     */
    LEFT_RIGHT_LEFT(
            new int[]{0},
            new int[]{0, 8},
            new int[]{0, 1, 8},
            new int[]{0, 1, 7, 8},
            new int[]{0, 1, 2, 7, 8},
            new int[]{0, 1, 2, 6, 7, 8},
            new int[]{0, 1, 2, 3, 6, 7, 8},
            new int[]{0, 1, 2, 3, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    ),
    /**
     * Aligns the items using the specified priority.
     * It first tries to put the items on the left and right
     * of the inventory.
     * When there is a remaining item it is moved to the right.
     */
    LEFT_RIGHT_RIGHT(
            new int[]{0},
            new int[]{0, 8},
            new int[]{0, 7, 8},
            new int[]{0, 1, 7, 8},
            new int[]{0, 1, 6, 7, 8},
            new int[]{0, 1, 2, 6, 7, 8},
            new int[]{0, 1, 2, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 5, 6, 7, 8},
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}
    );

    private final int[][] arrangements;

    AlignItems(int[]... arrangements) {
        Preconditions.checkArgument(arrangements != null && arrangements.length > 0 && arrangements.length <= 9);
        this.arrangements = arrangements;
    }

    public int[] getSlotsForAmount(int amountOfItems) {
        if (amountOfItems <= 0) return new int[0];
        return arrangements[amountOfItems - 1];
    }


}
