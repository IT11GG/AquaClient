// File: src/main/java/com/drastic193/aquaclient/gui/AquaClickGuiScreen.java
package com.drastic193.aquaclient.gui;

import com.drastic193.aquaclient.module.Module;
import com.drastic193.aquaclient.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AquaClickGuiScreen extends Screen {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Animation and interaction
    private float animationTime = 0.0f;
    private int selectedCategory = 0;
    private int hoveredModule = -1;
    private float categoryAnimation = 0.0f;
    private Map<Integer, Float> moduleAnimations = new HashMap<>();
    private List<Float> particleX = new ArrayList<>();
    private List<Float> particleY = new ArrayList<>();
    private List<Float> particleSpeed = new ArrayList<>();

    // Layout
    private int sidebarWidth = 180;
    private int padding = 20;
    private int moduleHeight = 40;
    private int categoryHeight = 45;

    // Modern color scheme - ВИПРАВЛЕНО: всі alpha значення в діапазоні 0-255
    private static final Color BG_PRIMARY = new Color(11, 15, 25, 240);
    private static final Color BG_SECONDARY = new Color(16, 23, 39, 220);
    private static final Color BG_TERTIARY = new Color(21, 32, 54, 180);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246, 255);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246, 255);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94, 255);
    private static final Color ACCENT_RED = new Color(239, 68, 68, 255);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252, 255);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184, 255);
    private static final Color TEXT_MUTED = new Color(100, 116, 139, 255);
    private static final Color BORDER_LIGHT = new Color(71, 85, 105, 100);

    // Category colors
    private static final Map<Module.Category, Color> CATEGORY_COLORS = Map.of(
            Module.Category.COMBAT, ACCENT_RED,
            Module.Category.VISUALS, ACCENT_PURPLE,
            Module.Category.MOVEMENT, ACCENT_BLUE,
            Module.Category.MISC, ACCENT_GREEN
    );

    public AquaClickGuiScreen() {
        super(Text.literal("AquaClient GUI"));
        initParticles();
    }

    private void initParticles() {
        for (int i = 0; i < 30; i++) {
            particleX.add((float) (Math.random() * 1920));
            particleY.add((float) (Math.random() * 1080));
            particleSpeed.add((float) (Math.random() * 0.5f + 0.2f));
        }
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime += delta;
        updateAnimations(delta);

        // Animated background with blur effect
        renderAnimatedBackground(context);

        // Floating particles
        renderParticles(context, delta);

        // Main GUI container with glassmorphism
        renderMainContainer(context);

        // Category sidebar
        renderCategorySidebar(context, mouseX, mouseY);

        // Module list
        renderModuleList(context, mouseX, mouseY);

        // Module details panel
        renderModuleDetails(context, mouseX, mouseY);

        // Header with logo and controls
        renderHeader(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void updateAnimations(float delta) {
        categoryAnimation = lerp(categoryAnimation, selectedCategory, delta * 8.0f);

        // Update module animations
        List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
        for (int i = 0; i < modules.size(); i++) {
            float target = (hoveredModule == i) ? 1.0f : 0.0f;
            moduleAnimations.put(i, lerp(moduleAnimations.getOrDefault(i, 0.0f), target, delta * 10.0f));
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(t, 1.0f);
    }

    private void renderAnimatedBackground(DrawContext context) {
        int width = this.width;
        int height = this.height;

        // Base background
        context.fill(0, 0, width, height, BG_PRIMARY.getRGB());

        // Animated gradient waves - ВИПРАВЛЕНО: обмежено alpha значення
        for (int y = 0; y < height; y += 2) {
            float wave1 = (float) Math.sin((y + animationTime * 30) * 0.01f) * 0.1f + 0.05f;
            float wave2 = (float) Math.cos((y + animationTime * 20) * 0.008f) * 0.08f + 0.04f;

            int alpha1 = Math.max(0, Math.min(255, (int) (wave1 * 255)));
            int alpha2 = Math.max(0, Math.min(255, (int) (wave2 * 255)));

            context.fill(0, y, width, y + 2, new Color(59, 130, 246, alpha1).getRGB());
            context.fill(0, y, width / 3, y + 2, new Color(139, 92, 246, alpha2).getRGB());
        }
    }

    private void renderParticles(DrawContext context, float delta) {
        for (int i = 0; i < particleX.size(); i++) {
            float x = particleX.get(i);
            float y = particleY.get(i);
            float speed = particleSpeed.get(i);

            // Update particle position
            x += (float) Math.sin(animationTime * 0.02f + i) * speed;
            y += (float) Math.cos(animationTime * 0.015f + i) * speed * 0.5f;

            // Wrap around screen
            if (x > this.width) x = 0;
            if (x < 0) x = this.width;
            if (y > this.height) y = 0;
            if (y < 0) y = this.height;

            particleX.set(i, x);
            particleY.set(i, y);

            // Render particle with fade effect - ВИПРАВЛЕНО: обмежено alpha значення
            float alpha = (float) (Math.sin(animationTime * 0.05f + i) * 0.3f + 0.1f);
            if (alpha > 0) {
                int size = 1 + (i % 2);
                int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 255)));
                context.fill((int) x, (int) y, (int) x + size, (int) y + size,
                        new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), alphaInt).getRGB());
            }
        }
    }

    private void renderMainContainer(DrawContext context) {
        int containerWidth = this.width - padding * 2;
        int containerHeight = this.height - padding * 2;
        int containerX = padding;
        int containerY = padding;

        // Glassmorphism background
        context.fill(containerX, containerY, containerX + containerWidth, containerY + containerHeight,
                BG_SECONDARY.getRGB());

        // Subtle border with gradient
        drawGradientBorder(context, containerX, containerY, containerWidth, containerHeight, 1);

        // Inner shadow effect
        context.fill(containerX + 1, containerY + 1, containerX + containerWidth - 1, containerY + 5,
                new Color(0, 0, 0, 30).getRGB());
    }

    private void renderHeader(DrawContext context) {
        int headerHeight = 50;
        int headerY = padding;

        // Header background with gradient
        drawGradient(context, padding, headerY, this.width - padding * 2, headerHeight,
                BG_TERTIARY, BG_SECONDARY);

        // Logo and title
        context.getMatrices().push();
        context.getMatrices().translate(padding + 20, headerY + 15, 0);

        String title = "AQUACLIENT";
        context.drawText(mc.textRenderer, title, 0, 0, ACCENT_BLUE.getRGB(), false);

        String subtitle = "Premium GUI";
        context.drawText(mc.textRenderer, subtitle, 0, 12, TEXT_SECONDARY.getRGB(), false);

        context.getMatrices().pop();

        // Close button
        int closeSize = 30;
        int closeX = this.width - padding - closeSize - 15;
        int closeY = headerY + 10;

        context.fill(closeX, closeY, closeX + closeSize, closeY + closeSize,
                new Color(ACCENT_RED.getRed(), ACCENT_RED.getGreen(), ACCENT_RED.getBlue(), 50).getRGB());

        // X symbol
        context.drawText(mc.textRenderer, "✕", closeX + 10, closeY + 11, ACCENT_RED.getRGB(), false);
    }

    private void renderCategorySidebar(DrawContext context, int mouseX, int mouseY) {
        int sidebarX = padding + 10;
        int sidebarY = padding + 60;
        int sidebarHeight = this.height - padding * 2 - 70;

        // Sidebar background
        context.fill(sidebarX, sidebarY, sidebarX + sidebarWidth, sidebarY + sidebarHeight,
                BG_TERTIARY.getRGB());
        drawGradientBorder(context, sidebarX, sidebarY, sidebarWidth, sidebarHeight, 1);

        Module.Category[] categories = Module.Category.values();

        for (int i = 0; i < categories.length; i++) {
            Module.Category category = categories[i];
            int catY = sidebarY + 10 + i * categoryHeight;

            boolean isSelected = selectedCategory == i;
            boolean isHovered = mouseX >= sidebarX && mouseX <= sidebarX + sidebarWidth &&
                    mouseY >= catY && mouseY <= catY + categoryHeight - 5;

            // Category background with animation
            float animProgress = Math.abs(categoryAnimation - i) < 1.0f ?
                    1.0f - Math.abs(categoryAnimation - i) : 0.0f;

            if (isSelected || animProgress > 0) {
                Color catColor = CATEGORY_COLORS.get(category);
                int alpha = Math.max(0, Math.min(255, (int) ((isSelected ? 80 : 40) + animProgress * 40)));

                context.fill(sidebarX + 5, catY, sidebarX + sidebarWidth - 5, catY + categoryHeight - 5,
                        new Color(catColor.getRed(), catColor.getGreen(), catColor.getBlue(), alpha).getRGB());

                // Selection indicator
                context.fill(sidebarX + 2, catY + 5, sidebarX + 6, catY + categoryHeight - 10,
                        catColor.getRGB());
            }

            if (isHovered && !isSelected) {
                context.fill(sidebarX + 5, catY, sidebarX + sidebarWidth - 5, catY + categoryHeight - 5,
                        new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), 20).getRGB());
            }

            // Category icon and text
            Color textColor = isSelected ? TEXT_PRIMARY : TEXT_SECONDARY;
            String categoryName = getCategoryDisplayName(category);
            String icon = getCategoryIcon(category);

            context.drawText(mc.textRenderer, icon, sidebarX + 15, catY + 8,
                    CATEGORY_COLORS.get(category).getRGB(), false);
            context.drawText(mc.textRenderer, categoryName, sidebarX + 35, catY + 8,
                    textColor.getRGB(), false);

            // Module count
            int moduleCount = getModulesForCategory(category).size();
            String countText = String.valueOf(moduleCount);
            int countWidth = mc.textRenderer.getWidth(countText);
            context.drawText(mc.textRenderer, countText,
                    sidebarX + sidebarWidth - countWidth - 15, catY + 8,
                    TEXT_MUTED.getRGB(), false);
        }
    }

    private void renderModuleList(DrawContext context, int mouseX, int mouseY) {
        int listX = padding + sidebarWidth + 20;
        int listY = padding + 60;
        int listWidth = (this.width - padding * 3 - sidebarWidth) / 2;
        int listHeight = this.height - padding * 2 - 70;

        // List background
        context.fill(listX, listY, listX + listWidth, listY + listHeight, BG_TERTIARY.getRGB());
        drawGradientBorder(context, listX, listY, listWidth, listHeight, 1);

        // Header
        context.drawText(mc.textRenderer, "МОДУЛІ", listX + 15, listY + 15, TEXT_PRIMARY.getRGB(), false);

        List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
        int startY = listY + 40;

        hoveredModule = -1;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int modY = startY + i * moduleHeight;

            if (modY + moduleHeight > listY + listHeight) break;

            boolean isHovered = mouseX >= listX && mouseX <= listX + listWidth &&
                    mouseY >= modY && mouseY <= modY + moduleHeight - 5;

            if (isHovered) hoveredModule = i;

            float hoverAnim = moduleAnimations.getOrDefault(i, 0.0f);

            // Module background
            if (module.isEnabled()) {
                Color categoryColor = CATEGORY_COLORS.get(module.getCategory());
                context.fill(listX + 5, modY, listX + listWidth - 5, modY + moduleHeight - 5,
                        new Color(categoryColor.getRed(), categoryColor.getGreen(), categoryColor.getBlue(), 60).getRGB());

                // Enabled indicator
                context.fill(listX + 5, modY, listX + 9, modY + moduleHeight - 5, categoryColor.getRGB());
            }

            // Hover effect - ВИПРАВЛЕНО: обмежено alpha значення
            if (hoverAnim > 0) {
                int hoverAlpha = Math.max(0, Math.min(255, (int) (hoverAnim * 40)));
                context.fill(listX + 5, modY, listX + listWidth - 5, modY + moduleHeight - 5,
                        new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), hoverAlpha).getRGB());
            }

            // Module icon and name
            String icon = getModuleIcon(module);
            Color textColor = module.isEnabled() ? TEXT_PRIMARY : TEXT_SECONDARY;

            context.drawText(mc.textRenderer, icon, listX + 20, modY + 8,
                    CATEGORY_COLORS.get(module.getCategory()).getRGB(), false);
            context.drawText(mc.textRenderer, module.getName(), listX + 40, modY + 8,
                    textColor.getRGB(), false);

            // Status indicator
            String status = module.isEnabled() ? "ON" : "OFF";
            Color statusColor = module.isEnabled() ? ACCENT_GREEN : TEXT_MUTED;
            int statusWidth = mc.textRenderer.getWidth(status);

            // Status background
            int statusBgWidth = statusWidth + 12;
            int statusX = listX + listWidth - statusBgWidth - 15;
            context.fill(statusX, modY + 6, statusX + statusBgWidth, modY + 22,
                    new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30).getRGB());

            context.drawText(mc.textRenderer, status, statusX + 6, modY + 10, statusColor.getRGB(), false);

            // Keybind indicator (if exists)
            if (hasKeybind(module)) {
                String keybind = getModuleKeybind(module);
                int keybindWidth = mc.textRenderer.getWidth(keybind);
                context.drawText(mc.textRenderer, keybind,
                        listX + listWidth - keybindWidth - statusBgWidth - 25, modY + 18,
                        TEXT_MUTED.getRGB(), false);
            }
        }
    }

    private void renderModuleDetails(DrawContext context, int mouseX, int mouseY) {
        int detailsX = padding + sidebarWidth + (this.width - padding * 3 - sidebarWidth) / 2 + 30;
        int detailsY = padding + 60;
        int detailsWidth = (this.width - padding * 3 - sidebarWidth) / 2 - 20;
        int detailsHeight = this.height - padding * 2 - 70;

        // Details background
        context.fill(detailsX, detailsY, detailsX + detailsWidth, detailsY + detailsHeight,
                BG_TERTIARY.getRGB());
        drawGradientBorder(context, detailsX, detailsY, detailsWidth, detailsHeight, 1);

        if (hoveredModule >= 0) {
            List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
            if (hoveredModule < modules.size()) {
                Module module = modules.get(hoveredModule);
                renderModuleDetailsContent(context, module, detailsX, detailsY, detailsWidth, detailsHeight);
            }
        } else {
            // Default content
            context.drawText(mc.textRenderer, "ДЕТАЛІ МОДУЛЯ", detailsX + 15, detailsY + 15,
                    TEXT_PRIMARY.getRGB(), false);
            context.drawText(mc.textRenderer, "Оберіть модуль для перегляду", detailsX + 15, detailsY + 40,
                    TEXT_SECONDARY.getRGB(), false);

            // Category statistics
            Module.Category currentCat = Module.Category.values()[selectedCategory];
            List<Module> modules = getModulesForCategory(currentCat);
            int enabledCount = (int) modules.stream().mapToInt(m -> m.isEnabled() ? 1 : 0).sum();

            context.drawText(mc.textRenderer, "Статистика категорії:", detailsX + 15, detailsY + 70,
                    TEXT_SECONDARY.getRGB(), false);
            context.drawText(mc.textRenderer, "Всього модулів: " + modules.size(),
                    detailsX + 15, detailsY + 90, TEXT_MUTED.getRGB(), false);
            context.drawText(mc.textRenderer, "Увімкнено: " + enabledCount,
                    detailsX + 15, detailsY + 105, ACCENT_GREEN.getRGB(), false);
            context.drawText(mc.textRenderer, "Вимкнено: " + (modules.size() - enabledCount),
                    detailsX + 15, detailsY + 120, ACCENT_RED.getRGB(), false);
        }
    }

    private void renderModuleDetailsContent(DrawContext context, Module module, int x, int y, int width, int height) {
        // Module header
        context.drawText(mc.textRenderer, module.getName().toUpperCase(), x + 15, y + 15,
                TEXT_PRIMARY.getRGB(), false);

        // Category badge
        Color catColor = CATEGORY_COLORS.get(module.getCategory());
        String catName = getCategoryDisplayName(module.getCategory());
        int badgeWidth = mc.textRenderer.getWidth(catName) + 12;

        context.fill(x + 15, y + 35, x + 15 + badgeWidth, y + 50,
                new Color(catColor.getRed(), catColor.getGreen(), catColor.getBlue(), 100).getRGB());
        context.drawText(mc.textRenderer, catName, x + 21, y + 39, catColor.getRGB(), false);

        // Status
        String status = module.isEnabled() ? "УВІМКНЕНО" : "ВИМКНЕНО";
        Color statusColor = module.isEnabled() ? ACCENT_GREEN : ACCENT_RED;
        context.drawText(mc.textRenderer, "Статус: ", x + 15, y + 70, TEXT_SECONDARY.getRGB(), false);
        context.drawText(mc.textRenderer, status, x + 70, y + 70, statusColor.getRGB(), false);

        // Description
        String description = getModuleDescription(module);
        context.drawText(mc.textRenderer, "Опис:", x + 15, y + 100, TEXT_SECONDARY.getRGB(), false);
        drawWrappedText(context, description, x + 15, y + 120, width - 30, TEXT_MUTED);

        // Controls section
        context.drawText(mc.textRenderer, "Керування:", x + 15, y + 180, TEXT_SECONDARY.getRGB(), false);

        // Toggle button
        int buttonWidth = 100;
        int buttonHeight = 30;
        int buttonX = x + 15;
        int buttonY = y + 200;

        renderActionButton(context, buttonX, buttonY, buttonWidth, buttonHeight,
                module.isEnabled() ? "ВИМКНУТИ" : "УВІМКНУТИ",
                module.isEnabled() ? ACCENT_RED : ACCENT_GREEN);

        // Settings button (placeholder)
        renderActionButton(context, buttonX + buttonWidth + 10, buttonY, buttonWidth, buttonHeight,
                "НАЛАШТУВАННЯ", ACCENT_BLUE);

        // Keybind section
        if (hasKeybind(module)) {
            context.drawText(mc.textRenderer, "Клавіша:", x + 15, y + 250, TEXT_SECONDARY.getRGB(), false);
            String keybind = getModuleKeybind(module);

            int keybindBgWidth = mc.textRenderer.getWidth(keybind) + 16;
            context.fill(x + 15, y + 270, x + 15 + keybindBgWidth, y + 290,
                    new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 100).getRGB());
            context.drawText(mc.textRenderer, keybind, x + 23, y + 275, ACCENT_BLUE.getRGB(), false);
        }
    }

    private void renderActionButton(DrawContext context, int x, int y, int width, int height,
                                    String text, Color color) {
        // Button background with gradient
        context.fill(x, y, x + width, y + height,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 100).getRGB());

        // Border
        drawGradientBorder(context, x, y, width, height, 1);

        // Text
        int textWidth = mc.textRenderer.getWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;

        context.drawText(mc.textRenderer, text, textX, textY, color.getRGB(), false);
    }

    private void drawGradientBorder(DrawContext context, int x, int y, int width, int height, int borderWidth) {
        // Top and bottom borders
        context.fill(x, y, x + width, y + borderWidth, BORDER_LIGHT.getRGB());
        context.fill(x, y + height - borderWidth, x + width, y + height, BORDER_LIGHT.getRGB());

        // Left and right borders
        context.fill(x, y, x + borderWidth, y + height, BORDER_LIGHT.getRGB());
        context.fill(x + width - borderWidth, y, x + width, y + height, BORDER_LIGHT.getRGB());
    }

    private void drawGradient(DrawContext context, int x, int y, int width, int height,
                              Color startColor, Color endColor) {
        for (int i = 0; i < height; i++) {
            float progress = (float) i / height;
            int r = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
            int g = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
            int b = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);
            int a = (int) (startColor.getAlpha() + (endColor.getAlpha() - startColor.getAlpha()) * progress);

            // ВИПРАВЛЕНО: обмежуємо всі значення кольорів
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));
            a = Math.max(0, Math.min(255, a));

            context.fill(x, y + i, x + width, y + i + 1, new Color(r, g, b, a).getRGB());
        }
    }

    private void drawWrappedText(DrawContext context, String text, int x, int y, int maxWidth, Color color) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;
        int lineHeight = 12;

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

            if (mc.textRenderer.getWidth(testLine) > maxWidth && currentLine.length() > 0) {
                context.drawText(mc.textRenderer, currentLine.toString(), x, currentY, color.getRGB(), false);
                currentLine = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            context.drawText(mc.textRenderer, currentLine.toString(), x, currentY, color.getRGB(), false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Check close button
            int closeSize = 30;
            int closeX = this.width - padding - closeSize - 15;
            int closeY = padding + 10;

            if (mouseX >= closeX && mouseX <= closeX + closeSize &&
                    mouseY >= closeY && mouseY <= closeY + closeSize) {
                mc.setScreen(null);
                return true;
            }

            // Check category selection
            int sidebarX = padding + 10;
            int sidebarY = padding + 60;

            if (mouseX >= sidebarX && mouseX <= sidebarX + sidebarWidth) {
                Module.Category[] categories = Module.Category.values();
                for (int i = 0; i < categories.length; i++) {
                    int catY = sidebarY + 10 + i * categoryHeight;
                    if (mouseY >= catY && mouseY <= catY + categoryHeight - 5) {
                        selectedCategory = i;
                        return true;
                    }
                }
            }

            // Check module toggle
            if (hoveredModule >= 0) {
                List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
                if (hoveredModule < modules.size()) {
                    modules.get(hoveredModule).toggle();
                    return true;
                }
            }

            // Check action buttons in details panel
            if (hoveredModule >= 0) {
                int detailsX = padding + sidebarWidth + (this.width - padding * 3 - sidebarWidth) / 2 + 30;
                int detailsY = padding + 60;
                int buttonWidth = 100;
                int buttonHeight = 30;
                int buttonX = detailsX + 15;
                int buttonY = detailsY + 200;

                // Toggle button
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                        mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                    List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
                    modules.get(hoveredModule).toggle();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            mc.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Helper methods
    private List<Module> getModulesForCategory(Module.Category category) {
        return ModuleManager.getModulesByCategory(category);
    }

    private String getCategoryDisplayName(Module.Category category) {
        return switch (category) {
            case COMBAT -> "Бій";
            case VISUALS -> "Візуал";
            case MOVEMENT -> "Рух";
            case MISC -> "Різне";
        };
    }

    private String getCategoryIcon(Module.Category category) {
        return switch (category) {
            case COMBAT -> "⚔";
            case VISUALS -> "👁";
            case MOVEMENT -> "🏃";
            case MISC -> "⚙";
        };
    }

    private String getModuleIcon(Module module) {
        return switch (module.getCategory()) {
            case COMBAT -> "⚡";
            case VISUALS -> "📡";
            case MOVEMENT -> "💨";
            case MISC -> "🔧";
        };
    }

    private String getModuleDescription(Module module) {
        return switch (module.getName()) {
            case "KillAura" -> "Автоматично атакує ворогів навколо вас в певному радіусі. Корисно для PvP бою.";
            case "AutoClicker" -> "Автоматично клікає при утриманні кнопки атаки. Пришвидшує швидкість атаки.";
            case "Aim" -> "Автоматично наводиться на найближчого ворога. Покращує точність стрільби.";
            case "ESP" -> "Показує контури гравців та мобів через стіни. Допомагає знаходити ворогів.";
            case "XRay" -> "Дозволяє бачити руди та цінні блоки через землю. Корисно для видобутку.";
            case "StorageESP" -> "Підсвічує скрині та інші контейнери через стіни.";
            case "Fly" -> "Дозволяє літати в режимі виживання. Ігнорує гравітацію.";
            case "Speed" -> "Збільшує швидкість пересування персонажа. Дозволяє швидше бігати.";
            case "Sprint" -> "Автоматично вмикає біг при русі вперед. Економить витривалість.";
            case "Spider" -> "Дозволяє лізти по стінах як павук. Корисно для паркуру.";
            case "Noclip" -> "Проходження через блоки. Дозволяє проходити крізь стіни.";
            case "Disabler" -> "Намагається обійти античіти сервера. Експериментальний модуль.";
            default -> "Опис модуля недоступний. Це корисний модуль для покращення ігрового процесу.";
        };
    }

    private boolean hasKeybind(Module module) {
        // В цьому прикладі припускаємо, що деякі модулі мають клавіші
        return switch (module.getName()) {
            case "KillAura", "Fly", "XRay", "ESP" -> true;
            default -> false;
        };
    }

    private String getModuleKeybind(Module module) {
        return switch (module.getName()) {
            case "KillAura" -> "R";
            case "Fly" -> "F";
            case "XRay" -> "X";
            case "ESP" -> "B";
            default -> "None";
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}