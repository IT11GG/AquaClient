// File: src/main/java/com/drastic193/aquaclient/gui/AquaClickGuiScreen.java
package com.drastic193.aquaclient.gui;

import com.drastic193.aquaclient.module.Module;
import com.drastic193.aquaclient.module.ModuleManager;
import com.drastic193.aquaclient.util.AquaRenderUtils;
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
    private int hoveredSetting = -1;
    private float categoryAnimation = 0.0f;
    private Map<Integer, Float> moduleAnimations = new HashMap<>();
    private Map<Integer, Float> settingAnimations = new HashMap<>();

    // Layout constants
    private final int CATEGORY_WIDTH = 120;
    private final int MODULE_WIDTH = 200;
    private final int SETTINGS_WIDTH = 280;
    private final int PADDING = 10;
    private final int MODULE_HEIGHT = 28;
    private final int CATEGORY_HEIGHT = 35;
    private final int CORNER_RADIUS = 8;

    // Modern Aqua-style colors
    private static final Color BG_MAIN = new Color(16, 16, 20, 255);
    private static final Color BG_SECONDARY = new Color(22, 22, 28, 255);
    private static final Color BG_TERTIARY = new Color(28, 28, 35, 255);
    private static final Color BG_HOVER = new Color(35, 35, 45, 255);

    // Accent colors for categories
    private static final Color ACCENT_COMBAT = new Color(255, 85, 85, 255);
    private static final Color ACCENT_VISUALS = new Color(85, 170, 255, 255);
    private static final Color ACCENT_MOVEMENT = new Color(85, 255, 170, 255);
    private static final Color ACCENT_MISC = new Color(255, 170, 85, 255);

    // Text colors
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(170, 170, 170, 255);
    private static final Color TEXT_DISABLED = new Color(100, 100, 100, 255);

    // Gradient colors
    private static final Color GRADIENT_START = new Color(120, 119, 198, 100);
    private static final Color GRADIENT_END = new Color(255, 152, 203, 100);

    // Category colors mapping
    private static final Map<Module.Category, Color> CATEGORY_COLORS = Map.of(
            Module.Category.COMBAT, ACCENT_COMBAT,
            Module.Category.VISUALS, ACCENT_VISUALS,
            Module.Category.MOVEMENT, ACCENT_MOVEMENT,
            Module.Category.MISC, ACCENT_MISC
    );

    public AquaClickGuiScreen() {
        super(Text.literal("Aqua GUI"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime += delta * 0.05f;
        updateAnimations(delta);

        // Background with animated gradient
        renderAnimatedBackground(context);

        // Main GUI panels with aqua-style design
        renderCategoryPanel(context, mouseX, mouseY);
        renderModulePanel(context, mouseX, mouseY);
        renderSettingsPanel(context, mouseX, mouseY);

        // Render watermark
        renderWatermark(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void updateAnimations(float delta) {
        categoryAnimation = lerp(categoryAnimation, selectedCategory, delta * 12.0f);

        // Update module animations
        List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
        for (int i = 0; i < modules.size(); i++) {
            float target = (hoveredModule == i) ? 1.0f : 0.0f;
            moduleAnimations.put(i, lerp(moduleAnimations.getOrDefault(i, 0.0f), target, delta * 15.0f));
        }

        // Update settings animations
        if (hoveredModule >= 0 && hoveredModule < modules.size()) {
            for (int i = 0; i < 5; i++) { // Max 5 settings per module
                float target = (hoveredSetting == i) ? 1.0f : 0.0f;
                settingAnimations.put(i, lerp(settingAnimations.getOrDefault(i, 0.0f), target, delta * 15.0f));
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(t, 1.0f);
    }

    private void renderModuleSettings(DrawContext context, Module module, int x, int y, int width, int height,
                                      int mouseX, int mouseY) {
        Color categoryColor = CATEGORY_COLORS.get(module.getCategory());

        // Module header
        context.drawText(mc.textRenderer, module.getName(), x + 10, y + 8,
                categoryColor.getRGB(), false);

        // Category badge
        String categoryName = getCategoryName(module.getCategory());
        int badgeWidth = mc.textRenderer.getWidth(categoryName) + 12;
        drawRoundedRect(context, x + 10, y + 25, badgeWidth, 16, 2,
                new Color(categoryColor.getRed(), categoryColor.getGreen(),
                        categoryColor.getBlue(), 100));
        context.drawText(mc.textRenderer, categoryName, x + 16, y + 29,
                categoryColor.getRGB(), false);

        // Status
        String status = module.isEnabled() ? "ENABLED" : "DISABLED";
        Color statusColor = module.isEnabled() ? ACCENT_VISUALS : ACCENT_COMBAT;
        context.drawText(mc.textRenderer, status, x + 10, y + 50,
                statusColor.getRGB(), false);

        // Settings (mock settings for demo)
        hoveredSetting = -1;
        String[] settingNames = {"Range", "Speed", "Priority", "Bypass", "Visual"};
        boolean[] settingValues = {true, false, true, false, true};

        for (int i = 0; i < settingNames.length; i++) {
            int settingY = y + 80 + i * 25;

            boolean isHovered = mouseX >= x + 10 && mouseX <= x + width - 10 &&
                    mouseY >= settingY && mouseY <= settingY + 20;

            if (isHovered) hoveredSetting = i;

            float settingAnim = settingAnimations.getOrDefault(i, 0.0f);

            // Setting background
            if (settingAnim > 0) {
                int hoverAlpha = (int) (settingAnim * 40);
                drawRoundedRect(context, x + 6, settingY - 2, width - 12, 20, 3,
                        new Color(255, 255, 255, hoverAlpha));
            }

            // Setting name
            context.drawText(mc.textRenderer, settingNames[i], x + 15, settingY + 2,
                    TEXT_PRIMARY.getRGB(), false);

            // Setting toggle
            boolean value = settingValues[i];
            Color toggleColor = value ? categoryColor : TEXT_DISABLED;
            String toggleText = value ? "ON" : "OFF";
            int toggleWidth = mc.textRenderer.getWidth(toggleText);

            drawRoundedRect(context, x + width - toggleWidth - 20, settingY,
                    toggleWidth + 8, 14, 2,
                    new Color(toggleColor.getRed(), toggleColor.getGreen(),
                            toggleColor.getBlue(), 60));

            context.drawText(mc.textRenderer, toggleText, x + width - toggleWidth - 16, settingY + 3,
                    toggleColor.getRGB(), false);
        }

        // Keybind section
        context.drawText(mc.textRenderer, "Keybind:", x + 10, y + height - 40,
                TEXT_SECONDARY.getRGB(), false);

        String keybind = getModuleKeybind(module);
        drawRoundedRect(context, x + 60, y + height - 43, 40, 16, 2, BG_TERTIARY);
        context.drawText(mc.textRenderer, keybind, x + 70, y + height - 40,
                TEXT_PRIMARY.getRGB(), false);
    }

    private void renderWatermark(DrawContext context) {
        String watermark = "AquaClient Modern";
        int watermarkWidth = mc.textRenderer.getWidth(watermark);

        // Watermark background with holographic effect
        AquaRenderUtils.drawHolographic(context, this.width - watermarkWidth - 20, 10,
                watermarkWidth + 12, 16, animationTime);

        AquaRenderUtils.drawRoundedRect(context, this.width - watermarkWidth - 20, 10,
                watermarkWidth + 12, 16, 3,
                new Color(0, 0, 0, 100));

        // Rainbow watermark text with glow
        AquaRenderUtils.drawRainbowText(context, mc.textRenderer, watermark,
                this.width - watermarkWidth - 14, 14,
                animationTime, 2.0f);

        // Add subtle glow effect
        AquaRenderUtils.drawGlow(context, this.width - watermarkWidth - 20, 10,
                watermarkWidth + 12, 16, 3,
                new Color(138, 43, 226, 150), 2, 0.4f);
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color) {
        // Simplified rounded rectangle (just fill for now)
        context.fill(x, y, x + width, y + height, color.getRGB());

        // Add corner rounding effect with additional fills
        context.fill(x, y, x + radius, y + radius, color.getRGB());
        context.fill(x + width - radius, y, x + width, y + radius, color.getRGB());
        context.fill(x, y + height - radius, x + radius, y + height, color.getRGB());
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color.getRGB());
    }

    private void drawRoundedBorder(DrawContext context, int x, int y, int width, int height,
                                   int radius, Color color) {
        // Top and bottom borders
        context.fill(x + radius, y, x + width - radius, y + 1, color.getRGB());
        context.fill(x + radius, y + height - 1, x + width - radius, y + height, color.getRGB());

        // Left and right borders
        context.fill(x, y + radius, x + 1, y + height - radius, color.getRGB());
        context.fill(x + width - 1, y + radius, x + width, y + height - radius, color.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Category selection
            int categoryPanelX = PADDING;
            int categoryPanelY = PADDING + 25;

            if (mouseX >= categoryPanelX && mouseX <= categoryPanelX + CATEGORY_WIDTH) {
                Module.Category[] categories = Module.Category.values();
                for (int i = 0; i < categories.length; i++) {
                    int catY = categoryPanelY + i * CATEGORY_HEIGHT;
                    if (mouseY >= catY && mouseY <= catY + CATEGORY_HEIGHT - 2) {
                        selectedCategory = i;
                        return true;
                    }
                }
            }

            // Module toggle
            if (hoveredModule >= 0) {
                List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
                if (hoveredModule < modules.size()) {
                    modules.get(hoveredModule).toggle();
                    return true;
                }
            }

            // Setting toggle
            if (hoveredSetting >= 0) {
                // Toggle mock setting (in real implementation, you'd handle actual settings)
                System.out.println("Toggled setting " + hoveredSetting);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC key
            mc.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Helper methods
    private List<Module> getModulesForCategory(Module.Category category) {
        return ModuleManager.getModulesByCategory(category);
    }

    private String getCategoryName(Module.Category category) {
        return switch (category) {
            case COMBAT -> "Combat";
            case VISUALS -> "Visuals";
            case MOVEMENT -> "Movement";
            case MISC -> "Misc";
        };
    }

    private String getCategoryIcon(Module.Category category) {
        return switch (category) {
            case COMBAT -> "⚔";
            case VISUALS -> "👁";
            case MOVEMENT -> "↗";
            case MISC -> "⚙";
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

    private void renderModuleSettings(DrawContext context, Module module, int x, int y, int width, int height,
                                      int mouseX, int mouseY) {
        Color categoryColor = CATEGORY_COLORS.get(module.getCategory());

        // Module header
        context.drawText(mc.textRenderer, module.getName(), x + 10, y + 8,
                categoryColor.getRGB(), false);

        // Category badge
        String categoryName = getCategoryName(module.getCategory());
        int badgeWidth = mc.textRenderer.getWidth(categoryName) + 12;
        drawRoundedRect(context, x + 10, y + 25, badgeWidth, 16, 2,
                new Color(categoryColor.getRed(), categoryColor.getGreen(),
                        categoryColor.getBlue(), 100));
        context.drawText(mc.textRenderer, categoryName, x + 16, y + 29,
                categoryColor.getRGB(), false);

        // Status
        String status = module.isEnabled() ? "ENABLED" : "DISABLED";
        Color statusColor = module.isEnabled() ? ACCENT_VISUALS : ACCENT_COMBAT;
        context.drawText(mc.textRenderer, status, x + 10, y + 50,
                statusColor.getRGB(), false);

        // Settings (mock settings for demo)
        hoveredSetting = -1;
        String[] settingNames = {"Range", "Speed", "Priority", "Bypass", "Visual"};
        boolean[] settingValues = {true, false, true, false, true};

        for (int i = 0; i < settingNames.length; i++) {
            int settingY = y + 80 + i * 25;

            boolean isHovered = mouseX >= x + 10 && mouseX <= x + width - 10 &&
                    mouseY >= settingY && mouseY <= settingY + 20;

            if (isHovered) hoveredSetting = i;

            float settingAnim = settingAnimations.getOrDefault(i, 0.0f);

            // Setting background
            if (settingAnim > 0) {
                int hoverAlpha = (int) (settingAnim * 40);
                drawRoundedRect(context, x + 6, settingY - 2, width - 12, 20, 3,
                        new Color(255, 255, 255, hoverAlpha));
            }

            // Setting name
            context.drawText(mc.textRenderer, settingNames[i], x + 15, settingY + 2,
                    TEXT_PRIMARY.getRGB(), false);

            // Setting toggle
            boolean value = settingValues[i];
            Color toggleColor = value ? categoryColor : TEXT_DISABLED;
            String toggleText = value ? "ON" : "OFF";
            int toggleWidth = mc.textRenderer.getWidth(toggleText);

            drawRoundedRect(context, x + width - toggleWidth - 20, settingY,
                    toggleWidth + 8, 14, 2,
                    new Color(toggleColor.getRed(), toggleColor.getGreen(),
                            toggleColor.getBlue(), 60));

            context.drawText(mc.textRenderer, toggleText, x + width - toggleWidth - 16, settingY + 3,
                    toggleColor.getRGB(), false);
        }

        // Keybind section
        context.drawText(mc.textRenderer, "Keybind:", x + 10, y + height - 40,
                TEXT_SECONDARY.getRGB(), false);

        String keybind = getModuleKeybind(module);
        drawRoundedRect(context, x + 60, y + height - 43, 40, 16, 2, BG_TERTIARY);
        context.drawText(mc.textRenderer, keybind, x + 70, y + height - 40,
                TEXT_PRIMARY.getRGB(), false);
    }

    private void renderWatermark(DrawContext context) {
        String watermark = "AquaClient Modern";
        int watermarkWidth = mc.textRenderer.getWidth(watermark);

        // Watermark background with holographic effect
        AquaRenderUtils.drawHolographic(context, this.width - watermarkWidth - 20, 10,
                watermarkWidth + 12, 16, animationTime);

        AquaRenderUtils.drawRoundedRect(context, this.width - watermarkWidth - 20, 10,
                watermarkWidth + 12, 16, 3,
                new Color(0, 0, 0, 100));

        // Rainbow watermark text with glow
        AquaRenderUtils.drawRainbowText(context, mc.textRenderer, watermark,
                this.width - watermarkWidth - 14, 14,
                animationTime, 2.0f);

        // Add subtle glow effect
        AquaRenderUtils.drawGlow(context, this.width - watermarkWidth - 20, 10,
                watermarkWidth + 12, 16, 3,
                new Color(138, 43, 226, 150), 2, 0.4f);
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color) {
        // Simplified rounded rectangle (just fill for now)
        context.fill(x, y, x + width, y + height, color.getRGB());

        // Add corner rounding effect with additional fills
        context.fill(x, y, x + radius, y + radius, color.getRGB());
        context.fill(x + width - radius, y, x + width, y + radius, color.getRGB());
        context.fill(x, y + height - radius, x + radius, y + height, color.getRGB());
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color.getRGB());
    }

    private void drawRoundedBorder(DrawContext context, int x, int y, int width, int height,
                                   int radius, Color color) {
        // Top and bottom borders
        context.fill(x + radius, y, x + width - radius, y + 1, color.getRGB());
        context.fill(x + radius, y + height - 1, x + width - radius, y + height, color.getRGB());

        // Left and right borders
        context.fill(x, y + radius, x + 1, y + height - radius, color.getRGB());
        context.fill(x + width - 1, y + radius, x + width, y + height - radius, color.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Category selection
            int categoryPanelX = PADDING;
            int categoryPanelY = PADDING + 25;

            if (mouseX >= categoryPanelX && mouseX <= categoryPanelX + CATEGORY_WIDTH) {
                Module.Category[] categories = Module.Category.values();
                for (int i = 0; i < categories.length; i++) {
                    int catY = categoryPanelY + i * CATEGORY_HEIGHT;
                    if (mouseY >= catY && mouseY <= catY + CATEGORY_HEIGHT - 2) {
                        selectedCategory = i;
                        return true;
                    }
                }
            }

            // Module toggle
            if (hoveredModule >= 0) {
                List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
                if (hoveredModule < modules.size()) {
                    modules.get(hoveredModule).toggle();
                    return true;
                }
            }

            // Setting toggle
            if (hoveredSetting >= 0) {
                // Toggle mock setting (in real implementation, you'd handle actual settings)
                System.out.println("Toggled setting " + hoveredSetting);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC key
            mc.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Helper methods
    private List<Module> getModulesForCategory(Module.Category category) {
        return ModuleManager.getModulesByCategory(category);
    }

    private String getCategoryName(Module.Category category) {
        return switch (category) {
            case COMBAT -> "Combat";
            case VISUALS -> "Visuals";
            case MOVEMENT -> "Movement";
            case MISC -> "Misc";
        };
    }

    private String getCategoryIcon(Module.Category category) {
        return switch (category) {
            case COMBAT -> "⚔";
            case VISUALS -> "👁";
            case MOVEMENT -> "↗";
            case MISC -> "⚙";
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
}AnimatedBackground(DrawContext context) {
    // Dark background
    context.fill(0, 0, this.width, this.height, BG_MAIN.getRGB());

    // Animated gradient waves
    for (int i = 0; i < this.height; i += 2) {
        float wave1 = (float) Math.sin((i + animationTime * 50) * 0.01f) * 0.3f + 0.3f;
        float wave2 = (float) Math.cos((i + animationTime * 30) * 0.008f) * 0.2f + 0.2f;

        int alpha1 = Math.max(0, Math.min(100, (int) (wave1 * 50)));
        int alpha2 = Math.max(0, Math.min(100, (int) (wave2 * 30)));

        context.fill(0, i, this.width, i + 2,
                new Color(120, 119, 198, alpha1).getRGB());
        context.fill(this.width / 2, i, this.width, i + 2,
                new Color(255, 152, 203, alpha2).getRGB());
    }
}

private void renderCategoryPanel(DrawContext context, int mouseX, int mouseY) {
    int panelX = PADDING;
    int panelY = PADDING;
    int panelHeight = this.height - PADDING * 2;

    // Panel background with rounded corners
    drawRoundedRect(context, panelX, panelY, CATEGORY_WIDTH, panelHeight,
            CORNER_RADIUS, BG_SECONDARY);

    // Panel border with glow effect
    drawRoundedBorder(context, panelX, panelY, CATEGORY_WIDTH, panelHeight,
            CORNER_RADIUS, new Color(255, 255, 255, 30));

    // Title
    context.drawText(mc.textRenderer, "Categories", panelX + 10, panelY + 8,
            TEXT_PRIMARY.getRGB(), false);

    Module.Category[] categories = Module.Category.values();
    int startY = panelY + 25;

    for (int i = 0; i < categories.length; i++) {
        Module.Category category = categories[i];
        int catY = startY + i * CATEGORY_HEIGHT;

        boolean isSelected = selectedCategory == i;
        boolean isHovered = mouseX >= panelX && mouseX <= panelX + CATEGORY_WIDTH &&
                mouseY >= catY && mouseY <= catY + CATEGORY_HEIGHT - 2;

        Color categoryColor = CATEGORY_COLORS.get(category);

        // Category background with animation
        if (isSelected) {
            float progress = 1.0f - Math.abs(categoryAnimation - i);
            if (progress > 0) {
                int bgAlpha = (int) (progress * 120);
                drawRoundedRect(context, panelX + 4, catY, CATEGORY_WIDTH - 8,
                        CATEGORY_HEIGHT - 2, 4,
                        new Color(categoryColor.getRed(), categoryColor.getGreen(),
                                categoryColor.getBlue(), bgAlpha));

                // Selection indicator line
                context.fill(panelX + 2, catY + 4, panelX + 4, catY + CATEGORY_HEIGHT - 6,
                        categoryColor.getRGB());
            }
        } else if (isHovered) {
            drawRoundedRect(context, panelX + 4, catY, CATEGORY_WIDTH - 8,
                    CATEGORY_HEIGHT - 2, 4, BG_HOVER);
        }

        // Category icon
        String icon = getCategoryIcon(category);
        context.drawText(mc.textRenderer, icon, panelX + 10, catY + 8,
                categoryColor.getRGB(), false);

        // Category name
        String name = getCategoryName(category);
        Color textColor = isSelected ? categoryColor : TEXT_SECONDARY;
        context.drawText(mc.textRenderer, name, panelX + 25, catY + 8,
                textColor.getRGB(), false);

        // Module count
        int moduleCount = getModulesForCategory(category).size();
        String countText = String.valueOf(moduleCount);
        int countWidth = mc.textRenderer.getWidth(countText);
        context.drawText(mc.textRenderer, countText,
                panelX + CATEGORY_WIDTH - countWidth - 8, catY + 8,
                TEXT_DISABLED.getRGB(), false);
    }
}

private void renderModulePanel(DrawContext context, int mouseX, int mouseY) {
    int panelX = PADDING + CATEGORY_WIDTH + PADDING;
    int panelY = PADDING;
    int panelHeight = this.height - PADDING * 2;

    // Panel background
    drawRoundedRect(context, panelX, panelY, MODULE_WIDTH, panelHeight,
            CORNER_RADIUS, BG_SECONDARY);
    drawRoundedBorder(context, panelX, panelY, MODULE_WIDTH, panelHeight,
            CORNER_RADIUS, new Color(255, 255, 255, 30));

    Module.Category currentCategory = Module.Category.values()[selectedCategory];
    Color categoryColor = CATEGORY_COLORS.get(currentCategory);

    // Title with category color
    String title = getCategoryName(currentCategory);
    context.drawText(mc.textRenderer, title, panelX + 10, panelY + 8,
            categoryColor.getRGB(), false);

    List<Module> modules = getModulesForCategory(currentCategory);
    int startY = panelY + 25;
    hoveredModule = -1;

    for (int i = 0; i < modules.size() && i < 20; i++) { // Limit to 20 modules
        Module module = modules.get(i);
        int modY = startY + i * MODULE_HEIGHT;

        if (modY + MODULE_HEIGHT > panelY + panelHeight - 10) break;

        boolean isHovered = mouseX >= panelX && mouseX <= panelX + MODULE_WIDTH &&
                mouseY >= modY && mouseY <= modY + MODULE_HEIGHT - 2;

        if (isHovered) hoveredModule = i;

        float hoverAnim = moduleAnimations.getOrDefault(i, 0.0f);
        boolean isEnabled = module.isEnabled();

        // Module background
        if (isEnabled) {
            drawRoundedRect(context, panelX + 4, modY, MODULE_WIDTH - 8,
                    MODULE_HEIGHT - 2, 3,
                    new Color(categoryColor.getRed(), categoryColor.getGreen(),
                            categoryColor.getBlue(), 80));
        }

        // Hover effect
        if (hoverAnim > 0) {
            int hoverAlpha = (int) (hoverAnim * 60);
            drawRoundedRect(context, panelX + 4, modY, MODULE_WIDTH - 8,
                    MODULE_HEIGHT - 2, 3,
                    new Color(255, 255, 255, hoverAlpha));
        }

        // Enable indicator
        if (isEnabled) {
            context.fill(panelX + 6, modY + 6, panelX + 8, modY + MODULE_HEIGHT - 8,
                    categoryColor.getRGB());
        }

        // Module name
        Color textColor = isEnabled ? TEXT_PRIMARY : TEXT_SECONDARY;
        context.drawText(mc.textRenderer, module.getName(), panelX + 12, modY + 8,
                textColor.getRGB(), false);

        // Toggle indicator
        String toggleText = isEnabled ? "ON" : "OFF";
        Color toggleColor = isEnabled ? categoryColor : TEXT_DISABLED;
        int toggleWidth = mc.textRenderer.getWidth(toggleText);

        // Toggle background
        int toggleBgWidth = toggleWidth + 8;
        drawRoundedRect(context, panelX + MODULE_WIDTH - toggleBgWidth - 8, modY + 4,
                toggleBgWidth, 16, 2,
                new Color(toggleColor.getRed(), toggleColor.getGreen(),
                        toggleColor.getBlue(), 40));

        context.drawText(mc.textRenderer, toggleText,
                panelX + MODULE_WIDTH - toggleWidth - 12, modY + 8,
                toggleColor.getRGB(), false);
    }
}

private void renderSettingsPanel(DrawContext context, int mouseX, int mouseY) {
    int panelX = PADDING + CATEGORY_WIDTH + PADDING + MODULE_WIDTH + PADDING;
    int panelY = PADDING;
    int panelHeight = this.height - PADDING * 2;

    // Panel background
    drawRoundedRect(context, panelX, panelY, SETTINGS_WIDTH, panelHeight,
            CORNER_RADIUS, BG_SECONDARY);
    drawRoundedBorder(context, panelX, panelY, SETTINGS_WIDTH, panelHeight,
            CORNER_RADIUS, new Color(255, 255, 255, 30));

    if (hoveredModule >= 0) {
        List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
        if (hoveredModule < modules.size()) {
            Module module = modules.get(hoveredModule);
            renderModuleSettings(context, module, panelX, panelY, SETTINGS_WIDTH, panelHeight,
                    mouseX, mouseY);
        }
    } else {
        // Default settings panel
        context.drawText(mc.textRenderer, "Settings", panelX + 10, panelY + 8,
                TEXT_PRIMARY.getRGB(), false);

        context.drawText(mc.textRenderer, "Hover over a module", panelX + 10, panelY + 30,
                TEXT_SECONDARY.getRGB(), false);
        context.drawText(mc.textRenderer, "to see its settings", panelX + 10, panelY + 45,
                TEXT_SECONDARY.getRGB(), false);

        // AquaClient info
        context.drawText(mc.textRenderer, "AquaClient", panelX + 10, panelY + 80,
                GRADIENT_START.getRGB(), false);
        context.drawText(mc.textRenderer, "Modern Style GUI", panelX + 10, panelY + 95,
                TEXT_DISABLED.getRGB(), false);

        // Statistics
        int totalModules = ModuleManager.modules.size();
        int enabledModules = (int) ModuleManager.modules.stream()
                .mapToLong(m -> m.isEnabled() ? 1 : 0).sum();

        context.drawText(mc.textRenderer, "Statistics:", panelX + 10, panelY + 120,
                TEXT_SECONDARY.getRGB(), false);
        context.drawText(mc.textRenderer, "Total: " + totalModules, panelX + 10, panelY + 135,
                TEXT_DISABLED.getRGB(), false);
        context.drawText(mc.textRenderer, "Enabled: " + enabledModules, panelX + 10, panelY + 150,
                ACCENT_VISUALS.getRGB(), false);
    }
}

private void render