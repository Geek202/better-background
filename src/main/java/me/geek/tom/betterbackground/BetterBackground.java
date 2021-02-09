package me.geek.tom.betterbackground;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

import static net.minecraft.client.gui.DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;

public class BetterBackground implements SimpleSynchronousResourceReloadListener, ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    public static final String MOD_ID = "better-background";
    public static final String MOD_NAME = "Better Background";

    private static BackgroundData data;

    public static Optional<BackgroundData> getBackground() {
        return Optional.ofNullable(data);
    }

    public static void renderBackgroundHook(Screen screen, int vOffset) {
        Optional<BackgroundData> background = getBackground();
        float u2;
        float v2;

        if (background.isPresent()) {
            float imageWidth = background.get().image.width;
            float imageHeight = background.get().image.height;
            boolean isImageLandscape = imageWidth > imageHeight;

            float imageScale;

            if (isImageLandscape) {
                imageScale = screen.width / imageWidth;
                u2 = 1.0f;
                v2 = screen.height / (imageHeight * imageScale);
            } else {
                imageScale = screen.height / imageHeight;
                u2 = screen.width / (imageWidth * imageScale);
                v2 = 1.0f;
            }
        } else {
            u2 = screen.width / 32f;
            v2 = screen.height / 32f;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        MinecraftClient.getInstance().getTextureManager().bindTexture(OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D,         screen.height, 0.0D).texture(0.0F, v2 + vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(screen.width, screen.height, 0.0D).texture(u2,   v2 + vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(screen.width, 0.0D,          0.0D).texture(u2,   vOffset)     .color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0D,         0.0D,          0.0D).texture(0.0F, vOffset)     .color(64, 64, 64, 255).next();
        tessellator.draw();
    }

    @Override
    public void onInitialize() {
        LOGGER.info("["+MOD_NAME+"] Initializing");
        ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID, "tater_wall"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.NORMAL);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(MOD_ID, "background_info");
    }

    @Override
    public void apply(ResourceManager manager) {
        try {
            Reader reader = new InputStreamReader(manager.getResource(new Identifier("textures/gui/options_background.json")).getInputStream());
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            BackgroundImage image = GSON.fromJson(obj.getAsJsonObject("image"), BackgroundImage.class);
            data = new BackgroundData(image);
        } catch(FileNotFoundException e) { // Default/normal resource packs don't have an options_background.json, so we ignore it silently
            data = null;
        } catch (Exception e) {
            LOGGER.error("Failed to load background image data", e);
            data = null;
        }
    }

    public static class BackgroundData {
        public final BackgroundImage image;

        private BackgroundData(BackgroundImage image) {
            this.image = image;
        }
    }

    public static class BackgroundImage {
        public int width;
        public int height;
    }
}
