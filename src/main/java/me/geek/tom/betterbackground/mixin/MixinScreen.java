package me.geek.tom.betterbackground.mixin;

import me.geek.tom.betterbackground.BetterBackground;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public class MixinScreen {
    @Shadow @Nullable protected MinecraftClient client;

    @Shadow public int height;

    @Shadow public int width;

    /**
     * @author Tom_The_Geek
     * @reason Replace logic
     */
    @Overwrite
    public void renderBackgroundTexture(int vOffset) {
        BetterBackground.renderBackgroundHook((Screen) (Object) this, vOffset);
    }
}
