package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.utilities.api.inventory.ChatInputAction;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;

public class ExtendedChatInputButton extends ChatInputButton {

    public ExtendedChatInputButton(String id, ButtonState buttonState, String msg, ChatInputAction action) {
        super(id, buttonState, msg, action);
    }

    public ExtendedChatInputButton(String id, ButtonState buttonState, ChatInputAction action) {
        super(id, buttonState, action);
    }

    public ExtendedChatInputButton(String id, ButtonState buttonState, ClickData clickData, ChatInputAction action) {
        super(id, buttonState, clickData, action);
    }


}
