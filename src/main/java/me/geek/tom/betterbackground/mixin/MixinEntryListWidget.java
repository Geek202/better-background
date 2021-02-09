package me.geek.tom.betterbackground.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.geek.tom.betterbackground.BetterBackground;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(EntryListWidget.class)
public abstract class MixinEntryListWidget {

    @Shadow private boolean field_26846;

    @Shadow @Final protected MinecraftClient client;

    @Shadow protected int left;

    @Shadow protected int right;

    @Shadow protected int bottom;

    @Shadow protected int top;

    @Shadow public abstract double getScrollAmount();

    @Shadow protected int width;

    @Shadow protected int height;

    @Shadow private boolean field_26847;

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;field_26846:Z"))
    public boolean redirect_render_field26846(EntryListWidget<?> widget) {
        if (this.field_26846) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            Optional<BetterBackground.BackgroundData> background = BetterBackground.getBackground();

            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            if (background.isPresent()) {
                float imageWidth = background.get().image.width;
                float imageHeight = background.get().image.height;
                boolean isImageLandscape = imageWidth > imageHeight;

                float imageScale;

                int screenWidth = this.width;
                int screenHeight = this.height;

                float relativeLeft = (float) this.left / screenWidth;
                float relativeRight = (float) this.right / screenWidth;
                float relativeTop = (float) (this.top + this.getScrollAmount()) / screenHeight;
                float relativeBottom = (float) (this.bottom + this.getScrollAmount()) / screenHeight;

                float u2;
                float v2;
                if (isImageLandscape) {
                    imageScale = screenWidth / imageWidth;
                    u2 = 1.0f;
                    v2 = screenHeight / (imageHeight * imageScale);
                } else {
                    imageScale = screenHeight / imageHeight;
                    u2 = screenWidth / (imageWidth * imageScale);
                    v2 = 1.0f;
                }

                bufferBuilder.vertex(this.left,  this.bottom, 0.0D).texture(relativeLeft  * u2, relativeBottom * v2).color(32, 32, 32, 255).next();
                bufferBuilder.vertex(this.right, this.bottom, 0.0D).texture(relativeRight * u2, relativeBottom * v2).color(32, 32, 32, 255).next();
                bufferBuilder.vertex(this.right, this.top,    0.0D).texture(relativeRight * u2, relativeTop    * v2).color(32, 32, 32, 255).next();
                bufferBuilder.vertex(this.left,  this.top,    0.0D).texture(relativeLeft  * u2, relativeTop    * v2).color(32, 32, 32, 255).next();
            } else {
                bufferBuilder.vertex(this.left,  this.bottom, 0.0D).texture(this.left  / 32f, (float) (this.bottom + this.getScrollAmount()) / 32f).color(32, 32, 32, 255).next();
                bufferBuilder.vertex(this.right, this.bottom, 0.0D).texture(this.right / 32f, (float) (this.bottom + this.getScrollAmount()) / 32f).color(32, 32, 32, 255).next();
                bufferBuilder.vertex(this.right, this.top,    0.0D).texture(this.right / 32f, (float) (this.top    + this.getScrollAmount()) / 32f).color(32, 32, 32, 255).next();
                bufferBuilder.vertex(this.left,  this.top,    0.0D).texture(this.left  / 32f, (float) (this.top    + this.getScrollAmount()) / 32f).color(32, 32, 32, 255).next();
            }

            tessellator.draw();
        }

        return false;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;field_26847:Z"))
    private boolean redirect_render_field26847(EntryListWidget<?> widget) {
        if (this.field_26847) {
            Optional<BetterBackground.BackgroundData> background = BetterBackground.getBackground();

            float u2;
            float v2;
            float topV;
            float bottomV;

            if (background.isPresent()) {
                float imageWidth = background.get().image.width;
                float imageHeight = background.get().image.height;
                boolean isImageLandscape = imageWidth > imageHeight;

                float imageScale;


                float relativeTop = (float) this.top / this.height;
                float relativeBottom = (float) this.bottom / this.height;

                if (isImageLandscape) {
                    imageScale = this.width / imageWidth;
                    u2 = 1.0f;
                    v2 = this.height / (imageHeight * imageScale);
                } else {
                    imageScale = this.height / imageHeight;
                    u2 = this.width / (imageWidth * imageScale);
                    v2 = 1.0f;
                }

                topV = relativeTop * v2;
                bottomV = relativeBottom * v2;
            } else {
                u2 = this.width / 32f;
                v2 = this.height / 32f;
                topV = this.top / 32f;
                bottomV = this.bottom / 32f;
            }

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.left,              this.top,    -100.0D).texture(0.0F, topV)   .color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, this.top,    -100.0D).texture(u2,   topV)   .color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, 0.0D,        -100.0D).texture(u2,   0.0F)   .color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left,              0.0D,        -100.0D).texture(0.0F, 0.0F)   .color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left,              this.height, -100.0D).texture(0.0F, v2)     .color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, this.height, -100.0D).texture(u2,   v2)     .color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, this.bottom, -100.0D).texture(u2,   bottomV).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left,              this.bottom, -100.0D).texture(0.0F, bottomV).color(64, 64, 64, 255).next();
            tessellator.draw();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableTexture();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.left,  this.top + 4,    0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.right, this.top + 4,    0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.right, this.top,        0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.left,  this.top,        0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.left,  this.bottom,     0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.right, this.bottom,     0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.right, this.bottom - 4, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.left,  this.bottom - 4, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 0).next();
            tessellator.draw();
        }
        return false;
    }
}
