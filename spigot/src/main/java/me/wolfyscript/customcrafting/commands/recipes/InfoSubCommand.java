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

package me.wolfyscript.customcrafting.commands.recipes;

import com.wolfyscript.utilities.dependency.Dependency;
import com.wolfyscript.utilities.dependency.DependencyResolver;
import com.wolfyscript.utilities.verification.VerificationResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.handlers.ResourceLoader;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.chat.CollectionView;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.event.ClickEvent;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InfoSubCommand extends AbstractSubCommand {

    private ResourceLoader resourceLoader;

    private final String PENDING = "<yellow>⌚ Pending";
    private final String FAILED = "<#e2332e> ❌  Failed";
    private final String REGISTERED = "<#0bb134>✔ Registered";
    private final String INVALID = "<gold>⚠ Invalid";
    private final String NAMESPACE = "<#becfca>🪣</#becfca><white> Namespace";
    private final String DIRECTORY = "<b><#4eb1e3>🗀</#4eb1e3></b><white> Directory";
    private final String NAME = "<#4eb1e3>🪧</#4eb1e3><white> Name";

    public InfoSubCommand(CustomCrafting customCrafting) {
        super("info", new ArrayList<>(), customCrafting);
        this.resourceLoader = customCrafting.getDataHandler().getActiveLoader();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes_delete")) {
            var chat = api.getChat();
            if (args.length == 0) {
                sendList(player, resourceLoader, chat);
                return true;
            }

            try {
                NamespacedKey key = NamespacedKey.of(args[0]);
                if (key != null) {
                    CustomRecipe<?> customRecipe = customCrafting.getRegistries().getRecipes().get(key);

                    chat.sendMessage(player, Component.text("----------------------------"));

                    Optional<VerificationResult<? extends CustomRecipe<?>>> invalidState = resourceLoader.getInvalidRecipes()
                            .stream()
                            .filter(verifierContainer -> verifierContainer.value().map(recipe -> recipe.getNamespacedKey().equals(key)).orElse(false))
                            .findFirst();

                    final String state;
                    if (resourceLoader.getFailedRecipes().contains(key)) {
                        state = FAILED;
                    } else if (resourceLoader.getPendingRecipes().stream().anyMatch(recipe -> recipe.getNamespacedKey().equals(key))) {
                        state = PENDING;
                    } else if (invalidState.isPresent()) {
                        state = INVALID;
                    } else if (customRecipe != null) {
                        state = REGISTERED;
                    } else {
                        state = "Unknown";
                    }

                    chat.sendMessage(player, chat.getMiniMessage().deserialize("<b>" + state + "</b>"));
                    chat.sendMessage(player, Component.empty());
                    chat.sendMessage(player, chat.getMiniMessage().deserialize(NAMESPACE + ": <yellow><namespace>", Placeholder.unparsed("namespace", key.getNamespace())));
                    chat.sendMessage(player, chat.getMiniMessage().deserialize(DIRECTORY + ": <yellow><dir>", Placeholder.unparsed("dir", key.getKeyComponent().getFolder())));
                    chat.sendMessage(player, chat.getMiniMessage().deserialize(NAME + ": <yellow><name>", Placeholder.unparsed("name", key.getKeyComponent().getObject())));
                    chat.sendMessage(player, Component.empty());

                    if (invalidState.isPresent() && customRecipe == null) {
                        customRecipe = invalidState.get().value().orElse(null);
                    }

                    if (customRecipe != null) {
                        // Recipe is either registered or invalid
                        Set<Dependency> dependencies = DependencyResolver.resolveDependenciesFor(customRecipe, customRecipe.getClass());
                        if (!dependencies.isEmpty()) {
                            chat.sendMessage(player, chat.getMiniMessage().deserialize("<dark_aqua>🧪</dark_aqua> <white><u>Dependencies"));
                            chat.sendMessage(player, Component.text("   " + dependencies.stream().map(Dependency::toString).collect(Collectors.joining(" - "))));
                            chat.sendMessage(player, Component.empty());
                        }

                        if (invalidState.isPresent()) {
                            chat.sendMessage(player, chat.getMiniMessage().deserialize("<red>⚑</red> <white><u>Detected Issues"));
                            invalidState.ifPresent(verifierContainer -> {

                                verifierContainer.printToOut(0,  false, "   ", string -> {
                                    string = string.replace("\n", "");
                                    if (string.stripLeading().startsWith("!")) {
                                        chat.sendMessage(player, Component.text(string, NamedTextColor.DARK_RED));
                                    } else {
                                        chat.sendMessage(player, Component.text(string, NamedTextColor.RED));
                                    }
                                });
                            });
                        }
                    }

                }
            } catch (IllegalArgumentException ex) {
                chat.sendMessage(player, chat.translated("commands.recipes.invalid_recipe", Placeholder.unparsed("recipe", args[0])));
            }
        }
        return true;
    }

    private List<Component> getCategoriesFor(Player player, int currentIndex, ResourceLoader resourceLoader, Chat chat) {
        final int lengthLimit = 49;
        return List.of(
                chat.getMiniMessage().deserialize(REGISTERED + ": <b><loaded>", Placeholder.unparsed("loaded", String.valueOf(customCrafting.getRegistries().getRecipes().size())))
                        .clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            sendExpandedList(player,
                                    currentIndex,
                                    0,
                                    resourceLoader,
                                    (player2) -> customCrafting.getRegistries().getRecipes().keySet(),
                                    (player2, element) -> {
                                        String value = element.toString();
                                        if (value.length() > lengthLimit) {
                                            value = value.substring(0, lengthLimit) + "...";
                                        }
                                        return Component.text(value).hoverEvent(Component.text(element.toString())).clickEvent(ClickEvent.runCommand("/recipes info " + element));
                                    },
                                    chat
                            ).send(player);
                        })),
                chat.getMiniMessage().deserialize(PENDING + ": <b><pending>", Placeholder.unparsed("pending", String.valueOf(resourceLoader.getPendingRecipes().size())))
                        .clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            sendExpandedList(player,
                                    currentIndex,
                                    1,
                                    resourceLoader,
                                    (player2) -> resourceLoader.getPendingRecipes(),
                                    (player2, element) -> {
                                        String value = element.getNamespacedKey().toString();
                                        if (value.length() > lengthLimit) {
                                            value = value.substring(0, lengthLimit) + "...";
                                        }
                                        return Component.text(value).hoverEvent(Component.text(element.getNamespacedKey().toString())).clickEvent(ClickEvent.runCommand("/recipes info " + element.getNamespacedKey().toString()));
                                    },
                                    chat
                            ).send(player);
                        })),
                chat.getMiniMessage().deserialize(INVALID + ": <b><invalid>", Placeholder.unparsed("invalid", String.valueOf(resourceLoader.getInvalidRecipes().size())))
                        .clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> sendExpandedList(player,
                                currentIndex,
                                2,
                                resourceLoader,
                                (player2) -> resourceLoader.getInvalidRecipes(),
                                (player2, element) -> {
                                    String value = element.value().map(recipe -> recipe.getNamespacedKey().toString()).orElse("missing key");
                                    String stripped = value;
                                    if (value.length() > lengthLimit) {
                                        stripped = value.substring(0, lengthLimit) + "...";
                                    }
                                    return Component.text(stripped).hoverEvent(Component.text(value)).clickEvent(ClickEvent.runCommand("/recipes info " + value));
                                },
                                chat
                        ).send(player))),
                chat.getMiniMessage().deserialize(FAILED + ": <b><failed>", Placeholder.unparsed("failed", String.valueOf(resourceLoader.getFailedRecipes().size())))
                        .clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> sendExpandedList(player,
                                currentIndex,
                                3,
                                resourceLoader,
                                (player2) -> resourceLoader.getFailedRecipes(),
                                (player2, element) -> {
                                    String value = element.toString();
                                    if (value.length() > lengthLimit) {
                                        value = value.substring(0, lengthLimit) + "...";
                                    }
                                    return Component.text(value).hoverEvent(Component.text(element.toString())).clickEvent(ClickEvent.runCommand("/recipes info " + element));
                                },
                                chat
                        ).send(player)))
        );
    }

    private void sendList(Player player, ResourceLoader resourceLoader, Chat chat) {
        sendExpandedList(player, 0, -1, resourceLoader, null, null, chat).send(player);
    }

    private <T> CollectionView<T> sendExpandedList(Player player, int previousIndex, int clickedIndex, ResourceLoader resourceLoader, CollectionView.SupplyEntryCollection<T> supplyCollection, CollectionView.ParseEntryToComponent<T> parseEntryToComponent, Chat chat) {
        boolean collapse = previousIndex == clickedIndex;

        var components = getCategoriesFor(player, collapse ? -1 : clickedIndex, resourceLoader, chat);
        var list = new CollectionView<>(chat, collapse ? null : supplyCollection, collapse ? null : parseEntryToComponent);
        list.setEntriesPerPage(8);
        list.prefix(true);

        if (clickedIndex >= components.size() || clickedIndex < -1) {
            throw new IllegalArgumentException("expandIndex out of bounds");
        }

        if (collapse || clickedIndex < 0) {
            list.header((chat1, player1) -> {
                chat.sendMessage(player, true, Component.text("------------------------"));
            });
        } else {
            list.header((chat1, player1) -> {
                chat.sendMessage(player, true, Component.text("------------------------"));
                for (int i = 0; i < clickedIndex; i++) {
                    chat1.sendMessage(player, true, Component.text("⏵ ").append(components.get(i)));
                }
                chat1.sendMessage(player, true, Component.text("⏷ ").append(components.get(clickedIndex)));
            });
        }
        final int offset = collapse ? 0 : clickedIndex + 1;
        if (offset < components.size()) {
            list.footer((chat1, player1) -> {
                if (offset != 0) {
                    chat1.sendMessage(player, true, Component.text(" "));
                }
                for (int i = offset; i < components.size(); i++) {
                    chat1.sendMessage(player, true, Component.text("⏵ ").append(components.get(i)));
                }
            });
        }

        return list;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<NamespacedKey> list = new ArrayList<>();
        list.addAll(customCrafting.getRegistries().getRecipes().keySet());
        list.addAll(resourceLoader.getFailedRecipes());
        list.addAll(resourceLoader.getPendingRecipes().stream().map(CustomRecipe::getNamespacedKey).toList());
        list.addAll(resourceLoader.getInvalidRecipes().stream().map(verifierContainer -> verifierContainer.value().map(CustomRecipe::getNamespacedKey).orElse(null)).toList());

        return NamespacedKeyUtils.getPartialMatches(args[args.length - 1], list).stream().map(NamespacedKey::toString).collect(Collectors.toList());
    }
}
