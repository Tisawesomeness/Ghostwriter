package com.tisawesomeness.ghostwriter;

import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class Decorators {

    private static final String NASTY_TEXT = "(something nasty)";

    private static final Decorator NONE = (serverPlayer, originalText) -> Component.literal(originalText);

    private static final Decorator RAINBOW = (serverPlayer, string) -> {
        int n = string.length();
        float f = Math.nextDown(1.0f) * (float) n;
        MutableComponent mutableComponent = Component.literal(String.valueOf(string.charAt(0)))
                .withStyle(Style.EMPTY.withColor(Mth.hsvToRgb(Math.nextDown(1.0f), 1.0f, 1.0f)));
        for (int i = 1; i < n; i++) {
            mutableComponent.append(Component.literal(String.valueOf(string.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(Mth.hsvToRgb((float) i / f, 1.0f, 1.0f))));
        }
        return mutableComponent;
    };

    private static final Decorator SPEED = (serverPlayer, originalText) -> {
        String modified = originalText.equals("lol") ? NASTY_TEXT : originalText;
        return Component.literal(modified);
    };

    private static final Decorator SLEIGHT = (serverPlayer, originalText) -> {
        String modified = modifyLongString(originalText);
        return Component.literal(modified);
    };

    private static final Decorator SHADOW = (serverPlayer, originalText) -> {
        String modified = modifyLongString(originalText);
        return Component.literal(modified).withStyle(Style.EMPTY.withColor(0x000000));
    };

    private static final Decorator SNEAK = (serverPlayer, originalText) -> {
        HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(NASTY_TEXT));
        Style style = Style.EMPTY.withHoverEvent(e);
        return Component.literal(originalText).withStyle(style);
    };

    private static final Decorator SUPPRESS = (serverPlayer, originalText) -> {
        Style style = Style.EMPTY.withInsertion(NASTY_TEXT);
        return Component.literal(originalText).withStyle(style);
    };

    private static final Decorator SHOWCASE = (serverPlayer, originalText) -> {
        if (originalText.contains("[item]")) {

            ItemStack held = serverPlayer.getMainHandItem();
            if (held != null && !held.is(Items.AIR)) {
                ItemStack modified = held.copy();
                modified.setHoverName(Component.literal(NASTY_TEXT));

                HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(modified));
                Style style = Style.EMPTY.withColor(0x55ffff).withHoverEvent(e);

                int itemTagStartIdx = originalText.indexOf("[item]");
                int itemTagEndIdx = itemTagStartIdx + "[item]".length();
                return Component.literal(originalText.substring(0, itemTagStartIdx))
                        .append(Component.literal("[item]").withStyle(style))
                        .append(originalText.substring(itemTagEndIdx));
            }

        }
        return Component.literal(originalText);
    };

    private static final Decorator SUS = (serverPlayer, originalText) -> Component.literal("among us");

    private static String modifyLongString(String originalText) {
        if (originalText.length() <= 150) {
            return originalText;
        }
        return NASTY_TEXT + originalText.substring(NASTY_TEXT.length() + 1);
    }

    @FunctionalInterface
    private interface Decorator {
        Component decorate(ServerPlayer serverPlayer, String originalText);
    }

    private static Decorator currentDecorator = NONE;

    public static final ChatDecorator DECORATOR = (serverPlayer, component) -> {
        String originalText = component.getString().trim();
        switch (originalText.toLowerCase()) {
            case "!rainbow" -> currentDecorator = RAINBOW;
            // changes "lol" before you can realize
            case "!speed" -> currentDecorator = SPEED;
            // changes the start of a sentence while you're focusing on the end
            case "!sleight" -> currentDecorator = SLEIGHT;
            // sleight but with hard-to-see text
            case "!shadow" -> currentDecorator = SHADOW;
            // sneaks naughty text into a hover event
            case "!sneak" -> currentDecorator = SNEAK;
            // inserts completely invisible text
            case "!suppress" -> currentDecorator = SUPPRESS;
            // showcases a held item but changes the display name
            case "!showcase" -> currentDecorator = SHOWCASE;
            // among us
            case "!sus" -> currentDecorator = SUS;
            // disables modifying the preview (except for commands)
            case "!none" -> currentDecorator = NONE;
            default -> {
                Component output = currentDecorator.decorate(serverPlayer, originalText);
                return CompletableFuture.completedFuture(output);
            }
        }
        return CompletableFuture.completedFuture(Component.literal("Decorator changed!")
                .withStyle(Style.EMPTY.withColor(0x00ff00)));
    };

}
