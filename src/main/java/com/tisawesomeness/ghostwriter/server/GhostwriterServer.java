package com.tisawesomeness.ghostwriter.server;

import com.tisawesomeness.ghostwriter.Decorators;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Environment(EnvType.SERVER)
public class GhostwriterServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("gw")
                        .then(
                                argument("strategy", word())
                                        .executes(GhostwriterServer::ghostwriterCommand)
                        )
        ));
    }
    private static int ghostwriterCommand(CommandContext<ServerCommandSource> ctx) {
        String strategy = getString(ctx, "strategy");
        Decorators.fromStrategy(strategy).ifPresentOrElse(
                decorator -> {
                    Decorators.setDecorator(decorator);
                    ctx.getSource().sendFeedback(Text.of("Strategy set to " + strategy), false);
                },
                () -> ctx.getSource().sendError(Text.of("Invalid strategy: " + strategy))
        );
        return 1;
    }

}
