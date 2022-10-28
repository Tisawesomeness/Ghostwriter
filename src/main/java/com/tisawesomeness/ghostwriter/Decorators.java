package com.tisawesomeness.ghostwriter;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.CompletableFuture;

public class Decorators {

    private static final String NASTY_TEXT = "(something nasty)";

    private static final Decorator NONE = (serverPlayer, originalText) -> Text.literal(originalText);

    private static final Decorator RAINBOW = (serverPlayer, string) -> {
        int n = string.length();
        float f = Math.nextDown(1.0f) * (float) n;
        MutableText mutableComponent = Text.literal(String.valueOf(string.charAt(0)))
                .setStyle(Style.EMPTY.withColor(MathHelper.hsvToRgb(Math.nextDown(1.0f), 1.0f, 1.0f)));
        for (int i = 1; i < n; i++) {
            mutableComponent.append(Text.literal(String.valueOf(string.charAt(i)))
                    .setStyle(Style.EMPTY.withColor(MathHelper.hsvToRgb((float) i / f, 1.0f, 1.0f))));
        }
        return mutableComponent;
    };

    private static final Decorator SPEED = (serverPlayer, originalText) -> {
        String modified = originalText.equals("lol") ? NASTY_TEXT : originalText;
        return Text.literal(modified);
    };

    private static final Decorator SLEIGHT = (serverPlayer, originalText) -> {
        String modified = modifyLongString(originalText);
        return Text.literal(modified);
    };

    private static final Decorator SHADOW = (serverPlayer, originalText) -> {
        String modified = modifyLongString(originalText);
        return Text.literal(modified).setStyle(Style.EMPTY.withColor(0x000000));
    };

    private static final Decorator SNEAK = (serverPlayer, originalText) -> {
        HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(NASTY_TEXT));
        Style style = Style.EMPTY.withHoverEvent(e);
        return Text.literal(originalText).setStyle(style);
    };

    private static final Decorator SUPPRESS = (serverPlayer, originalText) -> {
        Style style = Style.EMPTY.withInsertion(NASTY_TEXT);
        return Text.literal(originalText).setStyle(style);
    };

    private static final Decorator SHOWCASE = (serverPlayer, originalText) -> {
        if (originalText.contains("[item]")) {

            ItemStack held = serverPlayer.getMainHandStack();
            if (held != null && !held.isOf(Items.AIR)) {
                ItemStack modified = held.copy();
                modified.setCustomName(Text.literal(NASTY_TEXT));

                HoverEvent e = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(modified));
                Style style = Style.EMPTY.withColor(0x55ffff).withHoverEvent(e);

                int itemTagStartIdx = originalText.indexOf("[item]");
                int itemTagEndIdx = itemTagStartIdx + "[item]".length();
                return Text.literal(originalText.substring(0, itemTagStartIdx))
                        .append(Text.literal("[item]").setStyle(style))
                        .append(originalText.substring(itemTagEndIdx));
            }

        }
        return Text.literal(originalText);
    };

    private static final Decorator SUS = (serverPlayer, originalText) -> Text.literal("among us");

    private static String modifyLongString(String originalText) {
        if (originalText.length() <= 150) {
            return originalText;
        }
        return NASTY_TEXT + originalText.substring(NASTY_TEXT.length() + 1);
    }

    @FunctionalInterface
    private interface Decorator {
        Text decorate(ServerPlayerEntity serverPlayer, String originalText);
    }

    private static Decorator currentDecorator = NONE;

    public static final MessageDecorator DECORATOR = (serverPlayer, component) -> {
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
            // inserts completely invisible text, must shift click to see
            case "!suppress" -> currentDecorator = SUPPRESS;
            // showcases a held item but changes the display name
            case "!showcase" -> currentDecorator = SHOWCASE;
            // among us
            case "!sus" -> currentDecorator = SUS;
            // disables modifying the preview (except for commands)
            case "!none" -> currentDecorator = NONE;
            default -> {
                Text output = currentDecorator.decorate(serverPlayer, originalText);
                return CompletableFuture.completedFuture(output);
            }
        }
        return CompletableFuture.completedFuture(Text.literal("Decorator changed!")
                .setStyle(Style.EMPTY.withColor(0x00ff00)));
    };

}
