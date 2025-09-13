// File: src/main/java/com/drastic193/aquaclient/screen/AquaMainMenu.java
package com.drastic193.aquaclient.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;

public class AquaMainMenu extends Screen {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float animationTick = 0.0f;
    private float logoScale = 1.0f;
    private float buttonAlpha = 0.0f;

    // Color scheme - modern dark theme
    private static final Color PRIMARY_BG = new Color(13, 17, 23, 255);
    private static final Color SECONDARY_BG = new Color(21, 28, 36, 180);
    private static final Color ACCENT_BLUE = new Color(58, 134, 255);
    private static final Color ACCENT_CYAN = new Color(79, 172, 254);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(160, 174, 192);
    private static final Color BUTTON_BG = new Color(30, 41, 59, 200);
    private static final Color BUTTON_HOVER = new Color(51, 65, 85, 220);

    public AquaMainMenu() {
        super(Text.literal("AquaClient"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 300;
        int buttonHeight = 45;
        int centerX = this.width / 2;
        int startY = this.height / 2 - 20;
        int spacing = 55;

        // Singleplayer
        this.addDrawableChild(createStyledButton(
                Text.literal("Одиночна гра"),
                centerX - buttonWidth / 2,
                startY,
                buttonWidth,
                buttonHeight,
                btn -> client.setScreen(new SelectWorldScreen(this))
        ));

        // Multiplayer
        this.addDrawableChild(createStyledButton(
                Text.literal("Мережева гра"),
                centerX - buttonWidth / 2,
                startY + spacing,
                buttonWidth,
                buttonHeight,
                btn -> client.setScreen(new MultiplayerScreen(this))
        ));

        // Options
        this.addDrawableChild(createStyledButton(
                Text.literal("Налаштування"),
                centerX - buttonWidth / 2,
                startY + spacing * 2,
                buttonWidth,
                buttonHeight,
                btn -> client.setScreen(new OptionsScreen(this, client.options))
        ));

        // Client Settings
        this.addDrawableChild(createStyledButton(
                Text.literal("Налаштування клієнта"),
                centerX - buttonWidth / 2,
                startY + spacing * 3,
                buttonWidth,
                buttonHeight,
                btn -> client.setScreen(new AquaSettingsScreen(this))
        ));

        // Quit
        this.addDrawableChild(createStyledButton(
                Text.literal("Вихід"),
                centerX - buttonWidth / 2,
                startY + spacing * 4,
                buttonWidth,
                buttonHeight,
                btn -> client.scheduleStop()
        ));
    }

    private ButtonWidget createStyledButton(Text text, int x, int y, int width, int height, ButtonWidget.PressAction action) {
        return new ButtonWidget.Builder(text, action)
                .dimensions(x, y, width, height)
                .build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTick += delta;

        // Animate logo scale
        logoScale = 1.0f + (float) Math.sin(animationTick * 0.05f) * 0.02f;

        // Background gradient
        drawGradientBackground(context);

        // Animated particles
        drawParticleEffect(context);

        // Main logo and title
        drawLogo(context);

        // Version info
        drawVersionInfo(context);

        // Custom button rendering with glassmorphism effect
        renderCustomButtons(context, mouseX, mouseY);

        // Render actual buttons (invisible, just for functionality)
        context.getMatrices().push();
        context.getMatrices().scale(1.0f, 1.0f, 1.0f);
        // Make buttons transparent for custom rendering
        super.render(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    private void drawGradientBackground(DrawContext context) {
        // Multi-layer gradient background
        int width = this.width;
        int height = this.height;

        // Base dark background
        context.fill(0, 0, width, height, PRIMARY_BG.getRGB());

        // Animated gradient overlay
        float wave1 = (float) Math.sin(animationTick * 0.02f) * 0.3f + 0.7f;
        float wave2 = (float) Math.cos(animationTick * 0.025f) * 0.3f + 0.7f;

        // Top gradient
        for (int i = 0; i < height / 3; i++) {
            float alpha = (1.0f - (float) i / (height / 3)) * 0.2f * wave1;
            int color = new Color(58, 134, 255, (int) (alpha * 255)).getRGB();
            context.fill(0, i, width, i + 1, color);
        }

        // Bottom gradient
        for (int i = height * 2 / 3; i < height; i++) {
            float alpha = ((float) (i - height * 2 / 3) / (height / 3)) * 0.15f * wave2;
            int color = new Color(79, 172, 254, (int) (alpha * 255)).getRGB();
            context.fill(0, i, width, i + 1, color);
        }
    }

    private void drawParticleEffect(DrawContext context) {
        // Simple particle effect
        for (int i = 0; i < 50; i++) {
            float time = animationTick * 0.01f + i;
            float x = (float) (Math.sin(time * 0.7f + i) * width * 0.4f + width * 0.5f);
            float y = (float) (Math.cos(time * 0.5f + i) * height * 0.3f + height * 0.5f);
            float alpha = (float) (Math.sin(time * 2.0f) * 0.3f + 0.1f);

            if (alpha > 0) {
                int size = 1 + (i % 3);
                int color = new Color(255, 255, 255, (int) (alpha * 255)).getRGB();
                context.fill((int) x, (int) y, (int) x + size, (int) y + size, color);
            }
        }
    }

    private void drawLogo(DrawContext context) {
        int centerX = this.width / 2;
        int logoY = this.height / 4 - 20;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, logoY, 0);
        context.getMatrices().scale(logoScale, logoScale, 1.0f);

        // Main title with shadow
        String title = "AQUACLIENT";
        int titleWidth = client.textRenderer.getWidth(title);

        // Shadow
        context.drawText(client.textRenderer, title, -titleWidth / 2 + 2, 2,
                new Color(0, 0, 0, 100).getRGB(), false);

        // Main text with gradient effect
        context.drawText(client.textRenderer, title, -titleWidth / 2, 0,
                ACCENT_BLUE.getRGB(), false);

        // Subtitle
        String subtitle = "Premium Minecraft Client";
        int subtitleWidth = client.textRenderer.getWidth(subtitle);
        context.drawText(client.textRenderer, subtitle, -subtitleWidth / 2, 20,
                TEXT_SECONDARY.getRGB(), false);

        context.getMatrices().pop();
    }

    private void drawVersionInfo(DrawContext context) {
        String version = "v2.1 | 1.21.1";
        context.drawText(client.textRenderer, version, 10, this.height - 20,
                TEXT_SECONDARY.getRGB(), false);

        String copyright = "© 2024 drastic193";
        int copyrightWidth = client.textRenderer.getWidth(copyright);
        context.drawText(client.textRenderer, copyright,
                this.width - copyrightWidth - 10, this.height - 20,
                TEXT_SECONDARY.getRGB(), false);
    }

    private void renderCustomButtons(DrawContext context, int mouseX, int mouseY) {
        for (int i = 0; i < this.children().size(); i++) {
            if (this.children().get(i) instanceof ButtonWidget button) {
                renderGlassButton(context, button, mouseX, mouseY, i * 0.1f);
            }
        }
    }

    private void renderGlassButton(DrawContext context, ButtonWidget button, int mouseX, int mouseY, float delay) {
        int x = button.getX();
        int y = button.getY();
        int width = button.getWidth();
        int height = button.getHeight();

        float currentAlpha = Math.max(0, buttonAlpha - delay);
        if (currentAlpha <= 0) return;

        boolean isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        // Glassmorphism background
        int bgAlpha = (int) (currentAlpha * (isHovered ? 100 : 70));
        int borderAlpha = (int) (currentAlpha * 120);

        // Background with blur effect simulation
        context.fill(x, y, x + width, y + height,
                new Color(BUTTON_BG.getRed(), BUTTON_BG.getGreen(), BUTTON_BG.getBlue(), bgAlpha).getRGB());

        if (isHovered) {
            context.fill(x, y, x + width, y + height,
                    new Color(BUTTON_HOVER.getRed(), BUTTON_HOVER.getGreen(), BUTTON_HOVER.getBlue(), 50).getRGB());
        }

        // Gradient border
        context.fill(x, y, x + width, y + 1,
                new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), borderAlpha).getRGB());
        context.fill(x, y + height - 1, x + width, y + height,
                new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), borderAlpha).getRGB());
        context.fill(x, y, x + 1, y + height,
                new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), borderAlpha / 2).getRGB());
        context.fill(x + width - 1, y, x + width, y + height,
                new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), borderAlpha / 2).getRGB());

        // Button text with shadow
        String text = button.getMessage().getString();
        int textWidth = client.textRenderer.getWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;

        int textAlpha = (int) (currentAlpha * 255);

        // Text shadow
        context.drawText(client.textRenderer, text, textX + 1, textY + 1,
                new Color(0, 0, 0, textAlpha / 3).getRGB(), false);

        // Main text
        context.drawText(client.textRenderer, text, textX, textY,
                new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), textAlpha).getRGB(), false);

        // Hover glow effect
        if (isHovered) {
            float glowIntensity = (float) (Math.sin(animationTick * 0.1f) * 0.3f + 0.7f);
            int glowAlpha = (int) (currentAlpha * glowIntensity * 30);

            // Outer glow
            for (int i = 1; i <= 3; i++) {
                context.fill(x - i, y - i, x + width + i, y + height + i,
                        new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), glowAlpha / i).getRGB());
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}