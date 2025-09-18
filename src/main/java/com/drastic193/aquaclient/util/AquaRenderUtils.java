// File: src/main/java/com/drastic193/aquaclient/util/AquaRenderUtils.java
package com.drastic193.aquaclient.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.awt.Color;

public class AquaRenderUtils {

    /**
     * Draw text with rainbow effect
     */
    public static void drawRainbowText(DrawContext context, TextRenderer textRenderer, String text,
                                       int x, int y, float time, float speed) {
        float hueStep = 0.1f / text.length();
        for (int i = 0; i < text.length(); i++) {
            float hue = (time * speed + i * hueStep) % 1.0f;
            Color color = Color.getHSBColor(hue, 0.8f, 1.0f);
            context.drawText(textRenderer, String.valueOf(text.charAt(i)),
                    x + textRenderer.getWidth(text.substring(0, i)), y,
                    color.getRGB(), false);
        }
    }

    /**
     * Draw rounded rectangle with gradient
     */
    public static void drawRoundedGradientRect(DrawContext context, int x, int y, int width, int height,
                                               int radius, Color startColor, Color endColor) {
        // Draw main rectangle
        drawGradientRect(context, x + radius, y, x + width - radius, y + height, startColor, endColor);

        // Draw side rectangles
        drawGradientRect(context, x, y + radius, x + radius, y + height - radius, startColor, endColor);
        drawGradientRect(context, x + width - radius, y + radius, x + width, y + height - radius, startColor, endColor);

        // Draw corners
        drawRoundedCorners(context, x, y, width, height, radius, startColor, endColor);
    }

    /**
     * Draw gradient rectangle
     */
    public static void drawGradientRect(DrawContext context, int x1, int y1, int x2, int y2,
                                        Color startColor, Color endColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        // Top-left
        buffer.vertex(matrix, x1, y1, 0)
                .color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha());

        // Top-right
        buffer.vertex(matrix, x2, y1, 0)
                .color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha());

        // Bottom-right
        buffer.vertex(matrix, x2, y2, 0)
                .color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha());

        // Bottom-left
        buffer.vertex(matrix, x1, y2, 0)
                .color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha());

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }

    /**
     * Draw rounded rectangle
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                       int radius, Color color) {
        // Main rectangles
        context.fill(x + radius, y, x + width - radius, y + height, color.getRGB());
        context.fill(x, y + radius, x + width, y + height - radius, color.getRGB());

        // Corners
        drawCircleQuarter(context, x + radius, y + radius, radius, color, 0);
        drawCircleQuarter(context, x + width - radius, y + radius, radius, color, 1);
        drawCircleQuarter(context, x + radius, y + height - radius, radius, color, 2);
        drawCircleQuarter(context, x + width - radius, y + height - radius, radius, color, 3);
    }

    /**
     * Draw circle quarter for rounded corners
     */
    private static void drawCircleQuarter(DrawContext context, int centerX, int centerY, int radius,
                                          Color color, int quarter) {
        for (int x = 0; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    int px = centerX, py = centerY;
                    switch (quarter) {
                        case 0 -> { px -= x; py -= y; } // Top-left
                        case 1 -> { px += x; py -= y; } // Top-right
                        case 2 -> { px -= x; py += y; } // Bottom-left
                        case 3 -> { px += x; py += y; } // Bottom-right
                    }
                    context.fill(px, py, px + 1, py + 1, color.getRGB());
                }
            }
        }
    }

    /**
     * Draw rounded corners with gradient
     */
    private static void drawRoundedCorners(DrawContext context, int x, int y, int width, int height,
                                           int radius, Color startColor, Color endColor) {
        for (int cx = 0; cx < radius; cx++) {
            for (int cy = 0; cy < radius; cy++) {
                if (cx * cx + cy * cy <= radius * radius) {
                    float ratio = (float) cy / height;
                    Color color = blendColors(startColor, endColor, ratio);

                    // Top-left
                    context.fill(x + cx, y + cy, x + cx + 1, y + cy + 1, color.getRGB());
                    // Top-right
                    context.fill(x + width - radius + cx, y + cy,
                            x + width - radius + cx + 1, y + cy + 1, color.getRGB());

                    ratio = (float) (height - radius + cy) / height;
                    color = blendColors(startColor, endColor, ratio);

                    // Bottom-left
                    context.fill(x + cx, y + height - radius + cy,
                            x + cx + 1, y + height - radius + cy + 1, color.getRGB());
                    // Bottom-right
                    context.fill(x + width - radius + cx, y + height - radius + cy,
                            x + width - radius + cx + 1, y + height - radius + cy + 1, color.getRGB());
                }
            }
        }
    }

    /**
     * Draw glow effect around rectangle
     */
    public static void drawGlow(DrawContext context, int x, int y, int width, int height,
                                int radius, Color color, int layers, float intensity) {
        for (int i = layers; i > 0; i--) {
            float alpha = intensity * (1.0f - (float) i / layers);
            Color glowColor = new Color(
                    color.getRed() / 255f,
                    color.getGreen() / 255f,
                    color.getBlue() / 255f,
                    alpha
            );

            drawRoundedRect(context, x - i, y - i, width + i * 2, height + i * 2,
                    radius + i, glowColor);
        }
    }

    /**
     * Draw holographic effect
     */
    public static void drawHolographic(DrawContext context, int x, int y, int width, int height, float time) {
        for (int i = 0; i < height; i += 2) {
            float wave = (float) Math.sin((i + time * 50) * 0.1f) * 0.5f + 0.5f;
            int alpha = (int) (wave * 100);

            Color color1 = new Color(138, 43, 226, alpha);
            Color color2 = new Color(0, 255, 255, alpha / 2);

            context.fill(x, y + i, x + width, y + i + 1, color1.getRGB());
            if (i + 1 < height) {
                context.fill(x, y + i + 1, x + width, y + i + 2, color2.getRGB());
            }
        }
    }

    /**
     * Draw animated border
     */
    public static void drawAnimatedBorder(DrawContext context, int x, int y, int width, int height,
                                          int thickness, Color color, float time) {
        float dashLength = 20;
        float gapLength = 10;
        float totalLength = dashLength + gapLength;
        float offset = (time * 50) % totalLength;

        // Top border
        for (float i = -offset; i < width; i += totalLength) {
            int start = Math.max(x, x + (int) i);
            int end = Math.min(x + width, x + (int) (i + dashLength));
            if (start < end) {
                context.fill(start, y, end, y + thickness, color.getRGB());
            }
        }

        // Bottom border
        for (float i = -offset; i < width; i += totalLength) {
            int start = Math.max(x, x + (int) i);
            int end = Math.min(x + width, x + (int) (i + dashLength));
            if (start < end) {
                context.fill(start, y + height - thickness, end, y + height, color.getRGB());
            }
        }

        // Left border
        for (float i = -offset; i < height; i += totalLength) {
            int start = Math.max(y, y + (int) i);
            int end = Math.min(y + height, y + (int) (i + dashLength));
            if (start < end) {
                context.fill(x, start, x + thickness, end, color.getRGB());
            }
        }

        // Right border
        for (float i = -offset; i < height; i += totalLength) {
            int start = Math.max(y, y + (int) i);
            int end = Math.min(y + height, y + (int) (i + dashLength));
            if (start < end) {
                context.fill(x + width - thickness, start, x + width, end, color.getRGB());
            }
        }
    }

    /**
     * Draw sliding text
     */
    public static void drawSlidingText(DrawContext context, TextRenderer textRenderer, String text,
                                       int x, int y, int maxWidth, float time, Color color) {
        int textWidth = textRenderer.getWidth(text);
        if (textWidth <= maxWidth) {
            context.drawText(textRenderer, text, x, y, color.getRGB(), false);
        } else {
            int offset = (int) ((time * 50) % (textWidth + maxWidth)) - maxWidth;

            RenderSystem.enableScissor(x, y - 2, x + maxWidth, y + textRenderer.fontHeight + 2);
            context.drawText(textRenderer, text, x - offset, y, color.getRGB(), false);
            RenderSystem.disableScissor();
        }
    }

    /**
     * Draw circle
     */
    public static void drawCircle(DrawContext context, int centerX, int centerY, int radius, Color color) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color.getRGB());
                }
            }
        }
    }

    /**
     * Draw progress bar
     */
    public static void drawProgressBar(DrawContext context, int x, int y, int width, int height,
                                       float progress, Color bgColor, Color fillColor, Color borderColor) {
        // Background
        drawRoundedRect(context, x, y, width, height, height / 2, bgColor);

        // Fill
        if (progress > 0) {
            int fillWidth = (int) (width * MathHelper.clamp(progress, 0, 1));
            drawRoundedRect(context, x, y, fillWidth, height, height / 2, fillColor);
        }

        // Border
        drawRoundedBorder(context, x, y, width, height, height / 2, borderColor, 1);
    }

    /**
     * Draw rounded border
     */
    public static void drawRoundedBorder(DrawContext context, int x, int y, int width, int height,
                                         int radius, Color color, int thickness) {
        for (int i = 0; i < thickness; i++) {
            // Top and bottom
            context.fill(x + radius, y + i, x + width - radius, y + i + 1, color.getRGB());
            context.fill(x + radius, y + height - i - 1, x + width - radius, y + height - i, color.getRGB());

            // Left and right
            context.fill(x + i, y + radius, x + i + 1, y + height - radius, color.getRGB());
            context.fill(x + width - i - 1, y + radius, x + width - i, y + height - radius, color.getRGB());

            // Corners
            drawCircleQuarterBorder(context, x + radius, y + radius, radius, color, i, 0);
            drawCircleQuarterBorder(context, x + width - radius, y + radius, radius, color, i, 1);
            drawCircleQuarterBorder(context, x + radius, y + height - radius, radius, color, i, 2);
            drawCircleQuarterBorder(context, x + width - radius, y + height - radius, radius, color, i, 3);
        }
    }

    /**
     * Draw circle quarter border for rounded corners
     */
    private static void drawCircleQuarterBorder(DrawContext context, int centerX, int centerY, int radius,
                                                Color color, int offset, int quarter) {
        int r = radius - offset;
        int r2 = (radius - offset - 1);

        for (int x = 0; x <= r; x++) {
            for (int y = 0; y <= r; y++) {
                if (x * x + y * y <= r * r && x * x + y * y > r2 * r2) {
                    int px = centerX, py = centerY;
                    switch (quarter) {
                        case 0 -> { px -= x; py -= y; }
                        case 1 -> { px += x; py -= y; }
                        case 2 -> { px -= x; py += y; }
                        case 3 -> { px += x; py += y; }
                    }
                    context.fill(px, py, px + 1, py + 1, color.getRGB());
                }
            }
        }
    }

    /**
     * Blend two colors
     */
    private static Color blendColors(Color c1, Color c2, float ratio) {
        ratio = MathHelper.clamp(ratio, 0, 1);
        return new Color(
                (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio),
                (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio),
                (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio),
                (int) (c1.getAlpha() * (1 - ratio) + c2.getAlpha() * ratio)
        );
    }

    /**
     * Draw wave pattern
     */
    public static void drawWavePattern(DrawContext context, int x, int y, int width, int height,
                                       Color color, float time, float frequency, float amplitude) {
        for (int i = 0; i < width; i++) {
            float wave = (float) Math.sin((i * frequency + time * 50) * 0.01f) * amplitude;
            int waveY = (int) (y + height / 2 + wave);

            // Draw vertical line with gradient
            for (int j = Math.max(y, waveY); j < Math.min(y + height, waveY + 3); j++) {
                float alpha = 1.0f - Math.abs(j - waveY) / 3.0f;
                Color waveColor = new Color(
                        color.getRed() / 255f,
                        color.getGreen() / 255f,
                        color.getBlue() / 255f,
                        alpha * (color.getAlpha() / 255f)
                );
                context.fill(x + i, j, x + i + 1, j + 1, waveColor.getRGB());
            }
        }
    }

    /**
     * Draw notification
     */
    public static void drawNotification(DrawContext context, TextRenderer textRenderer,
                                        String title, String message, int x, int y,
                                        int width, int height, Color bgColor, Color accentColor) {
        // Background
        drawRoundedRect(context, x, y, width, height, 8, bgColor);

        // Accent bar
        context.fill(x, y, x + 3, y + height, accentColor.getRGB());

        // Title
        context.drawText(textRenderer, title, x + 10, y + 5, accentColor.getRGB(), false);

        // Message
        context.drawText(textRenderer, message, x + 10, y + 20, Color.WHITE.getRGB(), false);

        // Shadow
        drawGlow(context, x, y, width, height, 8, Color.BLACK, 3, 0.2f);
    }
}