// File: src/main/java/com/drastic193/aquaclient/util/DeltaRenderUtils.java
package com.drastic193.aquaclient.util;

import net.minecraft.client.gui.DrawContext;
import java.awt.Color;

public class DeltaRenderUtils {

    /**
     * Draws a rounded rectangle with proper corner radius
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                       int radius, Color color) {
        if (radius <= 0) {
            context.fill(x, y, x + width, y + height, color.getRGB());
            return;
        }

        // Main rectangle
        context.fill(x + radius, y, x + width - radius, y + height, color.getRGB());
        context.fill(x, y + radius, x + radius, y + height - radius, color.getRGB());
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color.getRGB());

        // Corners (approximated with smaller rectangles for smooth appearance)
        drawCorner(context, x, y, radius, color, Corner.TOP_LEFT);
        drawCorner(context, x + width - radius, y, radius, color, Corner.TOP_RIGHT);
        drawCorner(context, x, y + height - radius, radius, color, Corner.BOTTOM_LEFT);
        drawCorner(context, x + width - radius, y + height - radius, radius, color, Corner.BOTTOM_RIGHT);
    }

    /**
     * Draws a rounded border
     */
    public static void drawRoundedBorder(DrawContext context, int x, int y, int width, int height,
                                         int radius, int borderWidth, Color color) {
        for (int i = 0; i < borderWidth; i++) {
            drawRoundedRect(context, x - i, y - i, width + i * 2, height + i * 2, radius + i,
                    new Color(color.getRed(), color.getGreen(), color.getBlue(),
                            Math.max(10, color.getAlpha() / (i + 1))));
        }
    }

    /**
     * Draws a gradient rectangle
     */
    public static void drawGradient(DrawContext context, int x, int y, int width, int height,
                                    Color startColor, Color endColor, GradientDirection direction) {
        switch (direction) {
            case VERTICAL -> drawVerticalGradient(context, x, y, width, height, startColor, endColor);
            case HORIZONTAL -> drawHorizontalGradient(context, x, y, width, height, startColor, endColor);
            case DIAGONAL_DOWN -> drawDiagonalGradient(context, x, y, width, height, startColor, endColor, true);
            case DIAGONAL_UP -> drawDiagonalGradient(context, x, y, width, height, startColor, endColor, false);
        }
    }

    /**
     * Draws a rounded gradient rectangle
     */
    public static void drawRoundedGradient(DrawContext context, int x, int y, int width, int height,
                                           int radius, Color startColor, Color endColor,
                                           GradientDirection direction) {
        // Create gradient mask
        switch (direction) {
            case VERTICAL -> {
                for (int i = 0; i < height; i++) {
                    float progress = (float) i / height;
                    Color currentColor = interpolateColor(startColor, endColor, progress);
                    drawRoundedRect(context, x, y + i, width, 1, i < radius ? radius - i : 0, currentColor);
                }
            }
            case HORIZONTAL -> {
                for (int i = 0; i < width; i++) {
                    float progress = (float) i / width;
                    Color currentColor = interpolateColor(startColor, endColor, progress);
                    drawRoundedRect(context, x + i, y, 1, height, i < radius ? radius - i : 0, currentColor);
                }
            }
        }
    }

    /**
     * Draws a glow effect around a rectangle
     */
    public static void drawGlow(DrawContext context, int x, int y, int width, int height,
                                int radius, Color glowColor, int glowSize, float intensity) {
        for (int i = glowSize; i > 0; i--) {
            float alpha = (intensity * (glowSize - i + 1) / glowSize) * (glowColor.getAlpha() / 255.0f);
            int glowAlpha = Math.max(0, Math.min(255, (int) (alpha * 255)));

            Color currentGlowColor = new Color(glowColor.getRed(), glowColor.getGreen(),
                    glowColor.getBlue(), glowAlpha);

            drawRoundedRect(context, x - i, y - i, width + i * 2, height + i * 2,
                    radius + i, currentGlowColor);
        }
    }

    /**
     * Draws animated rainbow border
     */
    public static void drawRainbowBorder(DrawContext context, int x, int y, int width, int height,
                                         int radius, int borderWidth, float time, float speed) {
        int segments = Math.max(20, width / 10);

        for (int i = 0; i < segments; i++) {
            float hue = ((float) i / segments + time * speed) % 1.0f;
            Color rainbowColor = Color.getHSBColor(hue, 0.8f, 1.0f);

            int segmentWidth = width / segments;
            int segmentX = x + i * segmentWidth;

            // Top border
            context.fill(segmentX, y, segmentX + segmentWidth, y + borderWidth,
                    rainbowColor.getRGB());

            // Bottom border
            context.fill(segmentX, y + height - borderWidth, segmentX + segmentWidth, y + height,
                    rainbowColor.getRGB());
        }

        // Side borders
        for (int i = 0; i < height / 2; i++) {
            float hue1 = (time * speed + (float) i / height) % 1.0f;
            float hue2 = (time * speed + 0.5f + (float) i / height) % 1.0f;

            Color leftColor = Color.getHSBColor(hue1, 0.8f, 1.0f);
            Color rightColor = Color.getHSBColor(hue2, 0.8f, 1.0f);

            // Left border
            context.fill(x, y + i * 2, x + borderWidth, y + i * 2 + 2, leftColor.getRGB());
            // Right border
            context.fill(x + width - borderWidth, y + i * 2, x + width, y + i * 2 + 2,
                    rightColor.getRGB());
        }
    }

    /**
     * Draws a pulsating effect
     */
    public static void drawPulse(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color, float time, float speed, float intensity) {
        float pulse = (float) Math.sin(time * speed) * 0.5f + 0.5f;
        int pulseAlpha = (int) (intensity * pulse * 255);

        Color pulseColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), pulseAlpha);

        int pulseSize = (int) (pulse * 5);
        drawRoundedRect(context, x - pulseSize, y - pulseSize, width + pulseSize * 2,
                height + pulseSize * 2, radius + pulseSize, pulseColor);
    }

    /**
     * Draws flowing particle effect
     */
    public static void drawFlowingParticles(DrawContext context, int x, int y, int width, int height,
                                            Color particleColor, float time, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            float offsetX = (float) Math.sin(time * 2 + i * 0.5f) * (width * 0.3f);
            float offsetY = (float) Math.cos(time * 1.5f + i * 0.3f) * (height * 0.3f);

            float particleX = x + width * 0.5f + offsetX;
            float particleY = y + height * 0.5f + offsetY;

            float alpha = (float) Math.abs(Math.sin(time * 3 + i)) * 0.6f + 0.2f;
            int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 255)));

            Color finalColor = new Color(particleColor.getRed(), particleColor.getGreen(),
                    particleColor.getBlue(), alphaInt);

            int size = 2 + (int) (alpha * 3);
            context.fill((int) particleX, (int) particleY, (int) particleX + size,
                    (int) particleY + size, finalColor.getRGB());
        }
    }

    /**
     * Draws a glitch effect
     */
    public static void drawGlitchEffect(DrawContext context, int x, int y, int width, int height,
                                        Color baseColor, float time, float intensity) {
        // Random glitch lines
        for (int i = 0; i < 8; i++) {
            if (Math.sin(time * 10 + i) > 0.3f) {
                int glitchY = y + (int) (Math.random() * height);
                int glitchHeight = 1 + (int) (Math.random() * 3);
                int glitchOffset = (int) ((Math.random() - 0.5f) * intensity * 5);

                Color glitchColor = i % 2 == 0 ?
                        new Color(255, 0, 100, 150) : new Color(0, 255, 255, 150);

                context.fill(x + glitchOffset, glitchY, x + width + glitchOffset,
                        glitchY + glitchHeight, glitchColor.getRGB());
            }
        }
    }

    /**
     * Draws a holographic effect
     */
    public static void drawHolographic(DrawContext context, int x, int y, int width, int height,
                                       int radius, float time) {
        // Holographic shimmer lines
        for (int i = 0; i < width + height; i += 8) {
            float shimmer = (float) Math.sin(time * 4 + i * 0.1f) * 0.3f + 0.3f;
            int alpha = (int) (shimmer * 80);

            Color shimmerColor = new Color(100, 200, 255, alpha);

            // Diagonal shimmer line
            int startX = x + i - height;
            int startY = y;
            int endX = x + i;
            int endY = y + height;

            if (startX >= x && startX <= x + width) {
                for (int j = 0; j < Math.min(height, width - (startX - x)); j++) {
                    context.fill(startX + j, startY + j, startX + j + 2, startY + j + 1,
                            shimmerColor.getRGB());
                }
            }
        }
    }

    // Helper methods
    private static void drawCorner(DrawContext context, int x, int y, int radius, Color color, Corner corner) {
        // Simplified corner drawing using filled rectangles
        for (int i = 0; i <= radius; i++) {
            for (int j = 0; j <= radius; j++) {
                double distance = Math.sqrt(i * i + j * j);
                if (distance <= radius) {
                    int pixelX = x, pixelY = y;

                    switch (corner) {
                        case TOP_LEFT -> {
                            pixelX = x + radius - i;
                            pixelY = y + radius - j;
                        }
                        case TOP_RIGHT -> {
                            pixelX = x + i;
                            pixelY = y + radius - j;
                        }
                        case BOTTOM_LEFT -> {
                            pixelX = x + radius - i;
                            pixelY = y + j;
                        }
                        case BOTTOM_RIGHT -> {
                            pixelX = x + i;
                            pixelY = y + j;
                        }
                    }

                    context.fill(pixelX, pixelY, pixelX + 1, pixelY + 1, color.getRGB());
                }
            }
        }
    }

    private static void drawVerticalGradient(DrawContext context, int x, int y, int width, int height,
                                             Color startColor, Color endColor) {
        for (int i = 0; i < height; i++) {
            float progress = (float) i / height;
            Color currentColor = interpolateColor(startColor, endColor, progress);
            context.fill(x, y + i, x + width, y + i + 1, currentColor.getRGB());
        }
    }

    private static void drawHorizontalGradient(DrawContext context, int x, int y, int width, int height,
                                               Color startColor, Color endColor) {
        for (int i = 0; i < width; i++) {
            float progress = (float) i / width;
            Color currentColor = interpolateColor(startColor, endColor, progress);
            context.fill(x + i, y, x + i + 1, y + height, currentColor.getRGB());
        }
    }

    private static void drawDiagonalGradient(DrawContext context, int x, int y, int width, int height,
                                             Color startColor, Color endColor, boolean downward) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float progress = downward ?
                        (float) (i + j) / (width + height) :
                        (float) (i + height - j) / (width + height);

                progress = Math.max(0, Math.min(1, progress));
                Color currentColor = interpolateColor(startColor, endColor, progress);
                context.fill(x + i, y + j, x + i + 1, y + j + 1, currentColor.getRGB());
            }
        }
    }

    private static Color interpolateColor(Color start, Color end, float progress) {
        progress = Math.max(0, Math.min(1, progress));

        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * progress);

        return new Color(
                Math.max(0, Math.min(255, r)),
                Math.max(0, Math.min(255, g)),
                Math.max(0, Math.min(255, b)),
                Math.max(0, Math.min(255, a))
        );
    }

    // Enums for configuration
    public enum Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public enum GradientDirection {
        VERTICAL, HORIZONTAL, DIAGONAL_DOWN, DIAGONAL_UP
    }

    /**
     * Advanced text rendering with effects
     */
    public static void drawTextWithShadow(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                          String text, int x, int y, Color textColor, Color shadowColor,
                                          int shadowOffset) {
        // Draw shadow
        context.drawText(textRenderer, text, x + shadowOffset, y + shadowOffset,
                shadowColor.getRGB(), false);
        // Draw main text
        context.drawText(textRenderer, text, x, y, textColor.getRGB(), false);
    }

    /**
     * Draw text with glow effect
     */
    public static void drawTextWithGlow(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                        String text, int x, int y, Color textColor, Color glowColor,
                                        int glowRadius) {
        // Draw glow layers
        for (int i = glowRadius; i > 0; i--) {
            int alpha = Math.max(10, glowColor.getAlpha() / i);
            Color layerColor = new Color(glowColor.getRed(), glowColor.getGreen(),
                    glowColor.getBlue(), alpha);

            // Draw glow in multiple directions
            context.drawText(textRenderer, text, x - i, y, layerColor.getRGB(), false);
            context.drawText(textRenderer, text, x + i, y, layerColor.getRGB(), false);
            context.drawText(textRenderer, text, x, y - i, layerColor.getRGB(), false);
            context.drawText(textRenderer, text, x, y + i, layerColor.getRGB(), false);

            // Diagonal glow
            context.drawText(textRenderer, text, x - i, y - i, layerColor.getRGB(), false);
            context.drawText(textRenderer, text, x + i, y - i, layerColor.getRGB(), false);
            context.drawText(textRenderer, text, x - i, y + i, layerColor.getRGB(), false);
            context.drawText(textRenderer, text, x + i, y + i, layerColor.getRGB(), false);
        }

        // Draw main text
        context.drawText(textRenderer, text, x, y, textColor.getRGB(), false);
    }

    /**
     * Draw rainbow text
     */
    public static void drawRainbowText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                       String text, int x, int y, float time, float speed) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float hue = (time * speed + (float) i / text.length()) % 1.0f;
            Color color = Color.getHSBColor(hue, 0.8f, 1.0f);

            int charWidth = textRenderer.getWidth(String.valueOf(c));
            context.drawText(textRenderer, String.valueOf(c), x, y, color.getRGB(), false);
            x += charWidth;
        }
    }

    /**
     * Draw animated wave text
     */
    public static void drawWaveText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                    String text, int x, int y, Color color, float time, float amplitude, float frequency) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float waveY = (float) Math.sin(time * frequency + i * 0.5f) * amplitude;

            int charWidth = textRenderer.getWidth(String.valueOf(c));
            context.drawText(textRenderer, String.valueOf(c), x, y + (int) waveY, color.getRGB(), false);
            x += charWidth;
        }
    }

    /**
     * Creates a glowing button effect
     */
    public static void drawGlowingButton(DrawContext context, int x, int y, int width, int height,
                                         Color baseColor, Color glowColor, boolean hovered, float time) {
        if (hovered) {
            float pulse = (float) Math.sin(time * 8) * 0.3f + 0.7f;
            drawGlow(context, x, y, width, height, 8, glowColor, 6, pulse);
        }

        drawRoundedRect(context, x, y, width, height, 8, baseColor);

        if (hovered) {
            Color highlightColor = new Color(255, 255, 255, 30);
            drawRoundedRect(context, x, y, width, height, 8, highlightColor);
        }
    }

    /**
     * Creates a modern progress bar
     */
    public static void drawProgressBar(DrawContext context, int x, int y, int width, int height,
                                       float progress, Color backgroundColor, Color progressColor,
                                       Color glowColor) {
        progress = Math.max(0, Math.min(1, progress));

        // Background
        drawRoundedRect(context, x, y, width, height, height / 2, backgroundColor);

        // Progress
        int progressWidth = (int) (width * progress);
        if (progressWidth > 0) {
            drawRoundedRect(context, x, y, progressWidth, height, height / 2, progressColor);

            // Glow effect
            drawGlow(context, x, y, progressWidth, height, height / 2, glowColor, 3, 0.5f);
        }
    }

    /**
     * Creates a floating panel effect
     */
    public static void drawFloatingPanel(DrawContext context, int x, int y, int width, int height,
                                         Color panelColor, float time, float floatAmount) {
        float floatY = (float) Math.sin(time * 2) * floatAmount;

        // Drop shadow
        Color shadowColor = new Color(0, 0, 0, 50);
        drawRoundedRect(context, x + 2, y + 2 + (int) floatY, width, height, 12, shadowColor);

        // Main panel
        drawRoundedRect(context, x, y + (int) floatY, width, height, 12, panelColor);

        // Subtle highlight
        Color highlightColor = new Color(255, 255, 255, 20);
        drawRoundedRect(context, x, y + (int) floatY, width, 2, 12, highlightColor);
    }

    /**
     * Creates a cyberpunk-style grid background
     */
    public static void drawCyberpunkGrid(DrawContext context, int x, int y, int width, int height,
                                         Color gridColor, float time, int gridSize) {
        for (int i = x; i < x + width; i += gridSize) {
            float alpha = (float) Math.sin(time + i * 0.01f) * 0.3f + 0.5f;
            int alphaInt = Math.max(0, Math.min(255, (int) (alpha * gridColor.getAlpha())));

            Color lineColor = new Color(gridColor.getRed(), gridColor.getGreen(),
                    gridColor.getBlue(), alphaInt);

            context.fill(i, y, i + 1, y + height, lineColor.getRGB());
        }

        for (int j = y; j < y + height; j += gridSize) {
            float alpha = (float) Math.cos(time + j * 0.01f) * 0.3f + 0.5f;
            int alphaInt = Math.max(0, Math.min(255, (int) (alpha * gridColor.getAlpha())));

            Color lineColor = new Color(gridColor.getRed(), gridColor.getGreen(),
                    gridColor.getBlue(), alphaInt);

            context.fill(x, j, x + width, j + 1, lineColor.getRGB());
        }
    }
}