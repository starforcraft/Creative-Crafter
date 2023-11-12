package com.ultramega.creativecrafter.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TooltipBuilder {
    List<Component> tooltip = new ArrayList<>();

    public TooltipBuilder addTip(String text, ChatFormatting formatting) {
        tooltip.add(Component.literal(formatting + text));
        return this;
    }

    public List<Component> build() {
        return tooltip.isEmpty() ? null : tooltip;
    }
}