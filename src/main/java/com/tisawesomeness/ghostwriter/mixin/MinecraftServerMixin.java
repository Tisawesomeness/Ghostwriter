package com.tisawesomeness.ghostwriter.mixin;

import com.tisawesomeness.ghostwriter.Decorators;

import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "getChatDecorator", at = @At("HEAD"), cancellable = true)
    private void getChatDecorator(CallbackInfoReturnable<ChatDecorator> cir) {
        cir.setReturnValue(Decorators.DECORATOR);
    }
}
