// File: src/main/java/com/drastic193/aquaclient/screen/DeltaMainMenu.java
package com.drastic193.aquaclient.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class DeltaMainMenu extends Screen {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float animationTick = 0.0f;
    private float logoScale = 1.0f;
    private float particleTime = 0.0f;
    private List<Particle> particles = new ArrayList<>();

    // Modern Delta colors
    private static final Color BG_MAIN = new Color(12, 12, 16, 255);
    private static final Color BG_SECONDARY = new Color(18, 18, 24, 200);
    private static final Color BG_TERTIARY = new Color(24, 24, 32, 180);

    // Gradient colors
    private static final Color GRADIENT_PRIMARY = new Color(138, 43, 226, 120);
    private static final Color GRADIENT_SECONDARY = new Color(30, 144, 255, 120);
    private static final Color GRADIENT_ACCENT = new Color(255, 20, 147, 80);

    // Text colors
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(170, 170, 180, 255);
    private static final Color TEXT_ACCENT = new Color(138, 43, 226, 255);

    // Button colors
    private static final Color BUTTON_BG = new Color(30, 30, 40, 180);
    private static final Color BUTTON_HOVER = new Color(45, 45, 60, 220);
    private static final Color BUTTON_ACCENT = new Color(138, 43, 226, 100);

    // Custom buttons list
    private List<DeltaButton> deltaButtons = new ArrayList<>();

    public DeltaMainMenu() {
        super(Text.literal("Delta Main Menu"));
        initParticles();
    }

    private void initParticles() {
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle());
        }
    }

    @Override
    protected void init() {
        super.init();
        deltaButtons.clear();

        int buttonWidth = 260;
        int buttonHeight = 45;
        int centerX = this.width / 2;
        int startY = this.height / 2 - 50;
        int spacing = 55;

        // Main buttons
        deltaButtons.add(new DeltaButton(
                "Singleplayer",
                "▶ Start your adventure",
                centerX - buttonWidth / 2,
                startY,
                buttonWidth,
                buttonHeight,
                GRADIENT_PRIMARY,
                () -> client.setScreen(new SelectWorldScreen(this))
        ));

        deltaButtons.add(new DeltaButton(
                "Multiplayer",
                "🌐 Join servers",
                centerX - buttonWidth / 2,
                startY + spacing,
                buttonWidth,
                buttonHeight,
                GRADIENT_SECONDARY,
                () -> client.setScreen(new MultiplayerScreen(this))
        ));

        deltaButtons.add(new DeltaButton(
                "Account Manager",
                "👤 Manage accounts",
                centerX - buttonWidth / 2,
                startY + spacing * 2,
                buttonWidth,
                buttonHeight,
                GRADIENT_ACCENT,
                () -> client.setScreen(new DeltaAccountManager(this))
        ));

        deltaButtons.add(new DeltaButton(
                "Options",
                "⚙ Game settings",
                centerX - buttonWidth / 2,
                startY + spacing * 3,
                buttonWidth,
                buttonHeight,
                new Color(255, 165, 0, 120),
                () -> client.setScreen(new OptionsScreen(this, client.options))
        ));

        deltaButtons.add(new DeltaButton(
                "Quit Game",
                "❌ Exit Minecraft",
                centerX - buttonWidth / 2,
                startY + spacing * 4,
                buttonWidth,
                buttonHeight,
                new Color(255, 69, 0, 120),
                () -> client.scheduleStop()
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTick += delta * 0.02f;
        particleTime += delta * 0.01f;
        logoScale = 1.0f + (float) Math.sin(animationTick * 3.0f) * 0.03f;

        // Animated background
        renderDeltaBackground(context);

        // Particles
        renderParticles(context);

        // Logo and title
        renderLogo(context);

        // Version and info
        renderInfo(context);

        // Delta buttons
        renderDeltaButtons(context, mouseX, mouseY);

        // Floating elements
        renderFloatingElements(context);
    }

    private void renderDeltaBackground(DrawContext context) {
        // Dark base background
        context.fill(0, 0, this.width, this.height, BG_MAIN.getRGB());

        // Animated gradient waves
        for (int i = 0; i < this.height; i += 3) {
            float wave1 = (float) Math.sin((i + animationTick * 100) * 0.008f) * 0.4f + 0.3f;
            float wave2 = (float) Math.cos((i + animationTick * 70) * 0.006f) * 0.3f + 0.2f;
            float wave3 = (float) Math.sin((i + animationTick * 50) * 0.004f) * 0.2f + 0.1f;

            int alpha1 = Math.max(0, Math.min(120, (int) (wave1 * 60)));
            int alpha2 = Math.max(0, Math.min(120, (int) (wave2 * 40)));
            int alpha3 = Math.max(0, Math.min(80, (int) (wave3 * 30)));

            // Purple wave
            context.fill(0, i, this.width, i + 3,
                    new Color(138, 43, 226, alpha1).getRGB());
            // Blue wave
            context.fill(this.width / 3, i, this.width, i + 3,
                    new Color(30, 144, 255, alpha2).getRGB());
            // Pink accent
            context.fill(this.width * 2 / 3, i, this.width, i + 3,
                    new Color(255, 20, 147, alpha3).getRGB());
        }

        // Diagonal gradient overlay
        for (int x = 0; x < this.width; x += 4) {
            for (int y = 0; y < this.height; y += 4) {
                float distance = (float) Math.sqrt((x - this.width * 0.7f) * (x - this.width * 0.7f) +
                        (y - this.height * 0.3f) * (y - this.height * 0.3f));
                float normalizedDistance = Math.min(1.0f, distance / Math.max(this.width, this.height));

                float alpha = (1.0f - normalizedDistance) * 0.1f * (float) Math.sin(animationTick * 2.0f + distance * 0.01f);
                if (alpha > 0) {
                    int alphaInt = Math.max(0, Math.min(40, (int) (alpha * 255)));
                    context.fill(x, y, x + 4, y + 4,
                            new Color(138, 43, 226, alphaInt).getRGB());
                }
            }
        }
    }

    private void renderParticles(Context context) {
        for (Particle particle : particles) {
            particle.update(particleTime);

            float alpha = particle.getAlpha();
            if (alpha > 0) {
                int size = particle.getSize();
                int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 100)));

                Color particleColor = particle.getColor();
                context.fill((int) particle.x, (int) particle.y,
                        (int) particle.x + size, (int) particle.y + size,
                        new Color(particleColor.getRed(), particleColor.getGreen(),
                                particleColor.getBlue(), alphaInt).getRGB());
            }
        }
    }

    private void renderLogo(DrawContext context) {
        int centerX = this.width / 2;
        int logoY = this.height / 4 - 30;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, logoY, 0);
        context.getMatrices().scale(logoScale, logoScale, 1.0f);

        // Main title with glow effect
        String title = "AQUACLIENT";
        int titleWidth = client.textRenderer.getWidth(title);

        // Glow layers
        for (int i = 3; i >= 0; i--) {
            int glowAlpha = Math.max(0, Math.min(100, 30 - i * 5));
            Color glowColor = new Color(138, 43, 226, glowAlpha);

            context.drawText(client.textRenderer, title,
                    -titleWidth / 2 + i, i, glowColor.getRGB(), false);
        }

        // Main text with rainbow effect
        float hue = (animationTick * 0.5f) % 1.0f;
        Color rainbowColor = Color.getHSBColor(hue, 0.8f, 1.0f);
        context.drawText(client.textRenderer, title, -titleWidth / 2, 0,
                rainbowColor.getRGB(), false);

        // Subtitle with typewriter effect
        String subtitle = "DELTA EDITION";
        int subtitleWidth = client.textRenderer.getWidth(subtitle);

        // Animated dots
        int dots = (int) (animationTick * 3) % 4;
        String animatedSubtitle = subtitle + ".".repeat(dots);

        context.drawText(client.textRenderer, animatedSubtitle,
                -subtitleWidth / 2, 22, TEXT_ACCENT.getRGB(), false);

        context.getMatrices().pop();
    }

    private void renderInfo(DrawContext context) {
        // Version info (bottom left)
        String version = "AquaClient v2.1 Delta";
        context.drawText(client.textRenderer, version, 15, this.height - 35,
                TEXT_SECONDARY.getRGB(), false);

        String mcVersion = "Minecraft 1.21.1";
        context.drawText(client.textRenderer, mcVersion, 15, this.height - 20,
                TEXT_SECONDARY.getRGB(), false);

        // Copyright (bottom right)
        String copyright = "© 2024 drastic193";
        int copyrightWidth = client.textRenderer.getWidth(copyright);
        context.drawText(client.textRenderer, copyright,
                this.width - copyrightWidth - 15, this.height - 35,
                TEXT_SECONDARY.getRGB(), false);

        String build = "Build: " + System.currentTimeMillis() % 10000;
        int buildWidth = client.textRenderer.getWidth(build);
        context.drawText(client.textRenderer, build,
                this.width - buildWidth - 15, this.height - 20,
                TEXT_SECONDARY.getRGB(), false);

        // Player name (top right)
        if (client.getSession() != null) {
            String playerName = "Welcome, " + client.getSession().getUsername();
            int nameWidth = client.textRenderer.getWidth(playerName);

            // Background for player name
            drawRoundedRect(context, this.width - nameWidth - 25, 10,
                    nameWidth + 20, 20, 4,
                    new Color(30, 30, 40, 150));

            context.drawText(client.textRenderer, playerName,
                    this.width - nameWidth - 15, 16,
                    TEXT_PRIMARY.getRGB(), false);
        }
    }

    private void renderDeltaButtons(DrawContext context, int mouseX, int mouseY) {
        for (int i = 0; i < deltaButtons.size(); i++) {
            DeltaButton button = deltaButtons.get(i);
            boolean isHovered = mouseX >= button.x && mouseX <= button.x + button.width &&
                    mouseY >= button.y && mouseY <= button.y + button.height;

            renderDeltaButton(context, button, isHovered, i * 0.1f);
        }
    }

    private void renderDeltaButton(DrawContext context, DeltaButton button, boolean hovered, float delay) {
        // Button animation
        float scale = hovered ? 1.05f : 1.0f;
        float currentScale = lerp(button.currentScale, scale, 0.15f);
        button.currentScale = currentScale;

        context.getMatrices().push();
        context.getMatrices().translate(button.x + button.width / 2f, button.y + button.height / 2f, 0);
        context.getMatrices().scale(currentScale, currentScale, 1.0f);
        context.getMatrices().translate(-button.width / 2f, -button.height / 2f, 0);

        // Button background with gradient
        drawRoundedRect(context, 0, 0, button.width, button.height, 8, BUTTON_BG);

        // Gradient overlay
        Color gradientColor = button.accentColor;
        int gradientAlpha = hovered ? 150 : 100;
        drawRoundedRect(context, 0, 0, button.width, button.height, 8,
                new Color(gradientColor.getRed(), gradientColor.getGreen(),
                        gradientColor.getBlue(), gradientAlpha));

        // Glow effect when hovered
        if (hovered) {
            float glowIntensity = (float) Math.sin(animationTick * 10) * 0.3f + 0.7f;
            for (int i = 1; i <= 3; i++) {
                int glowAlpha = (int) (glowIntensity * 40 / i);
                drawRoundedRect(context, -i, -i, button.width + i * 2, button.height + i * 2,
                        8 + i, new Color(gradientColor.getRed(), gradientColor.getGreen(),
                                gradientColor.getBlue(), glowAlpha));
            }
        }

        // Border
        drawRoundedBorder(context, 0, 0, button.width, button.height, 8,
                new Color(255, 255, 255, hovered ? 80 : 40));

        // Button text
        int titleWidth = client.textRenderer.getWidth(button.title);
        int titleX = (button.width - titleWidth) / 2;
        int titleY = button.height / 2 - 12;

        // Title shadow
        context.drawText(client.textRenderer, button.title, titleX + 1, titleY + 1,
                new Color(0, 0, 0, 150).getRGB(), false);

        // Main title
        context.drawText(client.textRenderer, button.title, titleX, titleY,
                TEXT_PRIMARY.getRGB(), false);

        // Subtitle
        int subtitleWidth = client.textRenderer.getWidth(button.subtitle);
        int subtitleX = (button.width - subtitleWidth) / 2;
        int subtitleY = titleY + 15;

        context.drawText(client.textRenderer, button.subtitle, subtitleX, subtitleY,
                TEXT_SECONDARY.getRGB(), false);

        context.getMatrices().pop();
    }

    private void renderFloatingElements(DrawContext context) {
        // Floating geometric shapes
        for (int i = 0; i < 8; i++) {
            float angle = animationTick + i * 0.785f; // 45 degrees in radians
            float radius = 100 + (float) Math.sin(animationTick + i) * 20;

            float x = this.width * 0.15f + (float) Math.cos(angle) * radius;
            float y = this.height * 0.15f + (float) Math.sin(angle) * radius;

            int size = 4 + (int) (Math.sin(animationTick * 2 + i) * 2);
            float alpha = (float) Math.abs(Math.sin(animationTick + i)) * 0.6f;

            if (alpha > 0 && x >= 0 && x < this.width && y >= 0 && y < this.height) {
                int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 120)));
                Color shapeColor = Color.getHSBColor((angle + animationTick) % 1.0f, 0.7f, 1.0f);

                context.fill((int) x, (int) y, (int) x + size, (int) y + size,
                        new Color(shapeColor.getRed(), shapeColor.getGreen(),
                                shapeColor.getBlue(), alphaInt).getRGB());
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color) {
        context.fill(x, y, x + width, y + height, color.getRGB());
        // Add corner effects (simplified)
    }

    private void drawRoundedBorder(DrawContext context, int x, int y, int width, int height,
                                   int radius, Color color) {
        // Top and bottom
        context.fill(x + radius, y, x + width - radius, y + 1, color.getRGB());
        context.fill(x + radius, y + height - 1, x + width - radius, y + height, color.getRGB());

        // Left and right  
        context.fill(x, y + radius, x + 1, y + height - radius, color.getRGB());
        context.fill(x + width - 1, y + radius, x + width, y + height - radius, color.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            for (DeltaButton deltaButton : deltaButtons) {
                if (mouseX >= deltaButton.x && mouseX <= deltaButton.x + deltaButton.width &&
                        mouseY >= deltaButton.y && mouseY <= deltaButton.y + deltaButton.height) {
                    deltaButton.action.run();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Inner classes
    private static class DeltaButton {
        public final String title;
        public final String subtitle;
        public final int x, y, width, height;
        public final Color accentColor;
        public final Runnable action;
        public float currentScale = 1.0f;

        public DeltaButton(String title, String subtitle, int x, int y, int width, int height,
                           Color accentColor, Runnable action) {
            this.title = title;
            this.subtitle = subtitle;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.accentColor = accentColor;
            this.action = action;
        }
    }

    private class Particle {
        public float x, y, vx, vy, life, maxLife;
        private Color color;
        private int size;

        public Particle() {
            reset();
        }

        public void reset() {
            x = (float) (Math.random() * DeltaMainMenu.this.width);
            y = (float) (Math.random() * DeltaMainMenu.this.height);
            vx = (float) (Math.random() - 0.5) * 0.5f;
            vy = (float) (Math.random() - 0.5) * 0.5f;
            maxLife = life = (float) Math.random() * 200 + 100;
            size = (int) (Math.random() * 3) + 1;

            float hue = (float) Math.random();
            color = Color.getHSBColor(hue, 0.7f, 1.0f);
        }

        public void update(float time) {
            x += vx;
            y += vy;
            life -= 1;

            if (life <= 0 || x < 0 || x > DeltaMainMenu.this.width || y < 0 || y > DeltaMainMenu.this.height) {
                reset();
            }
        }

        public float getAlpha() {
            return life / maxLife;
        }

        public Color getColor() {
            return color;
        }

        public int getSize() {
            return size;
        }
    }
}