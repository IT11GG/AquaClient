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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class AquaClickGuiScreen extends Screen {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Animation and interaction
    private float animationTime = 0.0f;
    private int selectedCategory = 0;
    private int hoveredModule = -1;
    private int hoveredSetting = -1;
    private Module selectedModule = null;
    private float categoryAnimation = 0.0f;
    private Map<Integer, Float> moduleAnimations = new HashMap<>();
    private Map<Integer, Float> settingAnimations = new HashMap<>();
    private Map<String, Float> sliderValues = new HashMap<>();

    // Scroll positions
    private float moduleScrollOffset = 0;
    private float settingsScrollOffset = 0;

    // Theme selection
    private int selectedTheme = 0;
    private final String[] themes = {"Морська", "Меланжевий", "Чернікний", "Необіжний", "Оганьовий",
            "Металланчеський", "Прикільний", "Новогодній"};

    // Layout constants - adjusted for better proportions
    private final int CATEGORY_WIDTH = 145;
    private final int MODULE_WIDTH = 230;
    private final int SETTINGS_WIDTH = 320;
    private final int PADDING = 15;
    private final int MODULE_HEIGHT = 32;
    private final int CATEGORY_HEIGHT = 40;
    private final int CORNER_RADIUS = 10;
    private final int HEADER_HEIGHT = 60;

    // Theme colors map
    private static final Map<Integer, ThemeColors> THEME_MAP = new HashMap<>();
    static {
        // Морська (Sea/Aqua)
        THEME_MAP.put(0, new ThemeColors(
                new Color(8, 10, 15, 255),          // BG_MAIN
                new Color(13, 17, 23, 255),         // BG_SECONDARY
                new Color(22, 27, 34, 255),         // BG_TERTIARY
                new Color(30, 41, 59, 255),         // BG_HOVER
                new Color(0, 196, 204, 255),        // ACCENT_PRIMARY (Aqua)
                new Color(0, 240, 240, 255),        // ACCENT_SECONDARY
                new Color(94, 234, 212, 255)        // ACCENT_TERTIARY
        ));

        // Меланжевий (Melange/Mixed)
        THEME_MAP.put(1, new ThemeColors(
                new Color(20, 14, 25, 255),
                new Color(30, 20, 35, 255),
                new Color(40, 28, 45, 255),
                new Color(50, 35, 55, 255),
                new Color(255, 105, 180, 255),      // Hot Pink
                new Color(147, 112, 219, 255),      // Medium Purple
                new Color(255, 182, 193, 255)       // Light Pink
        ));

        // Чернікний (Blueberry)
        THEME_MAP.put(2, new ThemeColors(
                new Color(10, 10, 20, 255),
                new Color(15, 15, 28, 255),
                new Color(20, 20, 36, 255),
                new Color(30, 30, 50, 255),
                new Color(147, 112, 219, 255),      // Violet
                new Color(138, 43, 226, 255),       // Blue Violet
                new Color(123, 104, 238, 255)       // Medium Slate Blue
        ));

        // Необіжний (Unearthly/Cosmic)
        THEME_MAP.put(3, new ThemeColors(
                new Color(5, 5, 15, 255),
                new Color(10, 10, 25, 255),
                new Color(15, 15, 35, 255),
                new Color(25, 25, 50, 255),
                new Color(100, 255, 218, 255),      // Aquamarine
                new Color(189, 147, 249, 255),      // Light Purple
                new Color(255, 154, 158, 255)       // Light Coral
        ));

        // Оганьовий (Fire)
        THEME_MAP.put(4, new ThemeColors(
                new Color(20, 10, 10, 255),
                new Color(30, 15, 15, 255),
                new Color(40, 20, 20, 255),
                new Color(50, 25, 25, 255),
                new Color(255, 94, 77, 255),        // Orange Red
                new Color(255, 154, 0, 255),        // Orange
                new Color(255, 206, 84, 255)        // Yellow Orange
        ));

        // Default to Морська for other indices
        for (int i = 5; i < 8; i++) {
            THEME_MAP.put(i, THEME_MAP.get(0));
        }
    }

    // Get current theme colors
    private ThemeColors getCurrentTheme() {
        return THEME_MAP.getOrDefault(selectedTheme, THEME_MAP.get(0));
    }

    // Text colors (theme independent)
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(180, 180, 190, 255);
    private static final Color TEXT_DISABLED = new Color(100, 100, 110, 255);

    // Category specific colors
    private static final Map<Module.Category, Color> CATEGORY_COLORS = Map.of(
            Module.Category.COMBAT, new Color(255, 85, 85, 255),
            Module.Category.VISUALS, new Color(85, 170, 255, 255),
            Module.Category.MOVEMENT, new Color(85, 255, 170, 255),
            Module.Category.MISC, new Color(255, 170, 85, 255)
    );

    // Category icons
    private static final Map<Module.Category, String> CATEGORY_ICONS = Map.of(
            Module.Category.COMBAT, "⚔",
            Module.Category.VISUALS, "👁",
            Module.Category.MOVEMENT, "➤",
            Module.Category.MISC, "⚙"
    );

    public AquaClickGuiScreen() {
        super(Text.literal("Aqua GUI"));
        initializeDefaultValues();
    }

    private void initializeDefaultValues() {
        // Initialize slider values
        sliderValues.put("range", 0.6f);
        sliderValues.put("speed", 0.8f);
        sliderValues.put("fov", 0.5f);
        sliderValues.put("delay", 0.3f);
        sliderValues.put("distance", 0.7f);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime += delta * 0.03f;
        updateAnimations(delta);

        ThemeColors theme = getCurrentTheme();

        // Background with animated gradient
        renderModernBackground(context, theme);

        // Calculate panel positions
        int categoryX = PADDING;
        int moduleX = categoryX + CATEGORY_WIDTH + PADDING;
        int settingsX = moduleX + MODULE_WIDTH + PADDING;
        int themeX = settingsX + SETTINGS_WIDTH + PADDING;
        int panelY = PADDING + HEADER_HEIGHT;
        int panelHeight = this.height - PADDING * 2 - HEADER_HEIGHT;

        // Render header
        renderHeader(context, theme);

        // Main GUI panels with modern design
        renderCategoryPanel(context, mouseX, mouseY, categoryX, panelY, panelHeight, theme);
        renderModulePanel(context, mouseX, mouseY, moduleX, panelY, panelHeight, theme);
        renderSettingsPanel(context, mouseX, mouseY, settingsX, panelY, panelHeight, theme);
        renderThemePanel(context, mouseX, mouseY, themeX, panelY, theme);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderHeader(DrawContext context, ThemeColors theme) {
        // Header background
        drawRoundedRect(context, PADDING, PADDING, this.width - PADDING * 2, HEADER_HEIGHT - 10,
                CORNER_RADIUS, theme.BG_SECONDARY);

        // Gradient overlay
        for (int i = 0; i < HEADER_HEIGHT - 10; i++) {
            float alpha = (1.0f - (float)i / (HEADER_HEIGHT - 10)) * 0.3f;
            context.fill(PADDING, PADDING + i, this.width - PADDING, PADDING + i + 1,
                    new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                            theme.ACCENT_PRIMARY.getBlue(), (int)(alpha * 255)).getRGB());
        }

        // Logo text with glow
        String logo = "AQUACLIENT";
        context.getMatrices().push();
        context.getMatrices().translate(PADDING + 20, PADDING + 15, 0);
        context.getMatrices().scale(1.5f, 1.5f, 1.0f);

        // Glow effect
        for (int i = 2; i >= 0; i--) {
            int glowAlpha = 60 - i * 15;
            context.drawText(mc.textRenderer, logo, i, i,
                    new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                            theme.ACCENT_PRIMARY.getBlue(), glowAlpha).getRGB(), false);
        }
        context.drawText(mc.textRenderer, logo, 0, 0, TEXT_PRIMARY.getRGB(), false);
        context.getMatrices().pop();

        // Subtitle
        String subtitle = "Modern Cheat GUI • v2.1";
        context.drawText(mc.textRenderer, subtitle, PADDING + 20, PADDING + 35,
                theme.ACCENT_SECONDARY.getRGB(), false);

        // User info on the right
        String username = mc.getSession().getUsername();
        int userWidth = mc.textRenderer.getWidth(username);

        drawRoundedRect(context, this.width - PADDING - userWidth - 20, PADDING + 10,
                userWidth + 15, 25, 6, theme.BG_TERTIARY);
        context.drawText(mc.textRenderer, username,
                this.width - PADDING - userWidth - 12, PADDING + 18,
                TEXT_PRIMARY.getRGB(), false);
    }

    private void renderModernBackground(DrawContext context, ThemeColors theme) {
        // Dark base
        context.fill(0, 0, this.width, this.height, theme.BG_MAIN.getRGB());

        // Animated gradient mesh
        for (int x = 0; x < this.width; x += 30) {
            for (int y = 0; y < this.height; y += 30) {
                float distance = (float) Math.sqrt(
                        Math.pow(x - this.width * 0.5, 2) +
                                Math.pow(y - this.height * 0.5, 2)
                );
                float wave = (float) Math.sin(distance * 0.005f - animationTime * 2) * 0.5f + 0.5f;
                int alpha = (int) (wave * 20);

                context.fill(x, y, x + 2, y + 2,
                        new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                                theme.ACCENT_PRIMARY.getBlue(), alpha).getRGB());
            }
        }

        // Animated particles
        for (int i = 0; i < 20; i++) {
            float particleX = (float) (Math.sin(animationTime + i * 0.5f) * 0.5f + 0.5f) * this.width;
            float particleY = (float) (Math.cos(animationTime * 0.7f + i * 0.3f) * 0.5f + 0.5f) * this.height;
            float size = (float) Math.sin(animationTime * 3 + i) * 2 + 4;
            float alpha = (float) Math.sin(animationTime * 2 + i) * 0.3f + 0.3f;

            drawCircle(context, (int)particleX, (int)particleY, (int)size,
                    new Color(theme.ACCENT_TERTIARY.getRed(), theme.ACCENT_TERTIARY.getGreen(),
                            theme.ACCENT_TERTIARY.getBlue(), (int)(alpha * 100)));
        }
    }

    private void renderCategoryPanel(DrawContext context, int mouseX, int mouseY,
                                     int x, int y, int height, ThemeColors theme) {
        // Panel background
        drawRoundedRect(context, x, y, CATEGORY_WIDTH, height, CORNER_RADIUS, theme.BG_SECONDARY);
        drawGlowBorder(context, x, y, CATEGORY_WIDTH, height, CORNER_RADIUS, theme.ACCENT_PRIMARY, 0.3f);

        // Title
        context.drawText(mc.textRenderer, "Categories", x + 12, y + 12, TEXT_PRIMARY.getRGB(), false);

        Module.Category[] categories = Module.Category.values();
        int startY = y + 35;

        for (int i = 0; i < categories.length; i++) {
            Module.Category category = categories[i];
            int catY = startY + i * (CATEGORY_HEIGHT + 5);

            boolean isSelected = selectedCategory == i;
            boolean isHovered = mouseX >= x && mouseX <= x + CATEGORY_WIDTH &&
                    mouseY >= catY && mouseY <= catY + CATEGORY_HEIGHT;

            // Category background
            if (isSelected) {
                // Selected background with gradient
                drawRoundedRect(context, x + 5, catY, CATEGORY_WIDTH - 10, CATEGORY_HEIGHT, 8,
                        new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                                theme.ACCENT_PRIMARY.getBlue(), 80));

                // Selection indicator
                context.fill(x + 2, catY + 5, x + 4, catY + CATEGORY_HEIGHT - 5,
                        theme.ACCENT_PRIMARY.getRGB());
            } else if (isHovered) {
                drawRoundedRect(context, x + 5, catY, CATEGORY_WIDTH - 10, CATEGORY_HEIGHT, 8,
                        theme.BG_HOVER);
            }

            // Icon with animation
            String icon = CATEGORY_ICONS.get(category);
            float iconScale = isSelected ? 1.2f : (isHovered ? 1.1f : 1.0f);

            context.getMatrices().push();
            context.getMatrices().translate(x + 20, catY + CATEGORY_HEIGHT / 2, 0);
            context.getMatrices().scale(iconScale, iconScale, 1.0f);
            context.drawText(mc.textRenderer, icon, 0, -4,
                    isSelected ? theme.ACCENT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);
            context.getMatrices().pop();

            // Category name
            String name = getCategoryName(category);
            Color textColor = isSelected ? theme.ACCENT_SECONDARY : TEXT_SECONDARY;
            context.drawText(mc.textRenderer, name, x + 40, catY + 14, textColor.getRGB(), false);

            // Module count badge
            int moduleCount = getModulesForCategory(category).size();
            String countText = String.valueOf(moduleCount);
            int badgeWidth = mc.textRenderer.getWidth(countText) + 8;

            drawRoundedRect(context, x + CATEGORY_WIDTH - badgeWidth - 8, catY + 10,
                    badgeWidth, 18, 9, theme.BG_TERTIARY);
            context.drawText(mc.textRenderer, countText,
                    x + CATEGORY_WIDTH - badgeWidth / 2 - 8 - mc.textRenderer.getWidth(countText) / 2,
                    catY + 14, TEXT_DISABLED.getRGB(), false);
        }
    }

    private void renderModulePanel(DrawContext context, int mouseX, int mouseY,
                                   int x, int y, int height, ThemeColors theme) {
        // Panel background
        drawRoundedRect(context, x, y, MODULE_WIDTH, height, CORNER_RADIUS, theme.BG_SECONDARY);
        drawGlowBorder(context, x, y, MODULE_WIDTH, height, CORNER_RADIUS, theme.ACCENT_PRIMARY, 0.2f);

        Module.Category currentCategory = Module.Category.values()[selectedCategory];

        // Header
        String title = getCategoryName(currentCategory) + " Modules";
        context.drawText(mc.textRenderer, title, x + 12, y + 12, TEXT_PRIMARY.getRGB(), false);

        // Search bar placeholder
        drawRoundedRect(context, x + 10, y + 30, MODULE_WIDTH - 20, 25, 6, theme.BG_TERTIARY);
        context.drawText(mc.textRenderer, "🔍 Search...", x + 15, y + 37, TEXT_DISABLED.getRGB(), false);

        List<Module> modules = getModulesForCategory(currentCategory);
        int startY = y + 65;
        hoveredModule = -1;

        // Scrollable area
        int visibleModules = (height - 80) / (MODULE_HEIGHT + 5);
        int scrollStart = (int) moduleScrollOffset;

        for (int i = scrollStart; i < modules.size() && i < scrollStart + visibleModules; i++) {
            Module module = modules.get(i);
            int modY = startY + (i - scrollStart) * (MODULE_HEIGHT + 5);

            boolean isHovered = mouseX >= x && mouseX <= x + MODULE_WIDTH &&
                    mouseY >= modY && mouseY <= modY + MODULE_HEIGHT;

            if (isHovered) hoveredModule = i;

            boolean isEnabled = module.isEnabled();
            boolean isSelected = module == selectedModule;

            // Module card
            Color cardBg = isSelected ? theme.BG_HOVER :
                    (isEnabled ? new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                            theme.ACCENT_PRIMARY.getBlue(), 40) : theme.BG_TERTIARY);

            drawRoundedRect(context, x + 5, modY, MODULE_WIDTH - 10, MODULE_HEIGHT, 6, cardBg);

            // Hover effect
            if (isHovered) {
                drawGlowBorder(context, x + 5, modY, MODULE_WIDTH - 10, MODULE_HEIGHT, 6,
                        theme.ACCENT_SECONDARY, 0.5f);
            }

            // Enable indicator
            drawCircle(context, x + 18, modY + MODULE_HEIGHT / 2, 4,
                    isEnabled ? theme.ACCENT_PRIMARY : TEXT_DISABLED);

            // Module name
            context.drawText(mc.textRenderer, module.getName(), x + 30, modY + 11,
                    isEnabled ? TEXT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);

            // Toggle switch
            drawToggleSwitch(context, x + MODULE_WIDTH - 45, modY + 8, 35, 16,
                    isEnabled, theme);
        }

        // Scroll indicator
        if (modules.size() > visibleModules) {
            int scrollBarHeight = Math.max(20, (visibleModules * height) / modules.size());
            int scrollBarY = y + 65 + (int)((moduleScrollOffset / modules.size()) * (height - 80));

            context.fill(x + MODULE_WIDTH - 3, y + 65, x + MODULE_WIDTH - 1, y + height - 10,
                    theme.BG_TERTIARY.getRGB());
            context.fill(x + MODULE_WIDTH - 3, scrollBarY, x + MODULE_WIDTH - 1, scrollBarY + scrollBarHeight,
                    theme.ACCENT_PRIMARY.getRGB());
        }
    }

    private void renderSettingsPanel(DrawContext context, int mouseX, int mouseY,
                                     int x, int y, int height, ThemeColors theme) {
        // Panel background
        drawRoundedRect(context, x, y, SETTINGS_WIDTH, height, CORNER_RADIUS, theme.BG_SECONDARY);
        drawGlowBorder(context, x, y, SETTINGS_WIDTH, height, CORNER_RADIUS, theme.ACCENT_PRIMARY, 0.2f);

        if (selectedModule != null) {
            // Module info header
            drawRoundedRect(context, x + 10, y + 10, SETTINGS_WIDTH - 20, 60, 8, theme.BG_TERTIARY);

            // Module name
            context.drawText(mc.textRenderer, selectedModule.getName(), x + 20, y + 20,
                    theme.ACCENT_PRIMARY.getRGB(), false);

            // Category badge
            String categoryName = getCategoryName(selectedModule.getCategory());
            int badgeWidth = mc.textRenderer.getWidth(categoryName) + 16;
            Color categoryColor = CATEGORY_COLORS.get(selectedModule.getCategory());

            drawRoundedRect(context, x + 20, y + 38, badgeWidth, 20, 10,
                    new Color(categoryColor.getRed(), categoryColor.getGreen(),
                            categoryColor.getBlue(), 80));
            context.drawText(mc.textRenderer, categoryName, x + 28, y + 43, TEXT_PRIMARY.getRGB(), false);

            // Status
            String status = selectedModule.isEnabled() ? "ENABLED" : "DISABLED";
            Color statusColor = selectedModule.isEnabled() ? theme.ACCENT_PRIMARY : TEXT_DISABLED;
            context.drawText(mc.textRenderer, status, x + SETTINGS_WIDTH - 70, y + 20,
                    statusColor.getRGB(), false);

            // Settings
            renderModuleSettings(context, x, y + 85, mouseX, mouseY, theme);
        } else {
            // Empty state
            context.drawText(mc.textRenderer, "Module Settings", x + 12, y + 12,
                    TEXT_PRIMARY.getRGB(), false);

            // Instruction card
            drawRoundedRect(context, x + 10, y + 40, SETTINGS_WIDTH - 20, 100, 8, theme.BG_TERTIARY);

            String[] instructions = {
                    "Select a module to configure",
                    "• Click to toggle modules",
                    "• Adjust settings here",
                    "• Use keybinds for quick access"
            };

            for (int i = 0; i < instructions.length; i++) {
                context.drawText(mc.textRenderer, instructions[i], x + 20, y + 55 + i * 15,
                        TEXT_SECONDARY.getRGB(), false);
            }
        }
    }

    private void renderModuleSettings(DrawContext context, int x, int y, int mouseX, int mouseY, ThemeColors theme) {
        // Mock settings for demonstration
        String[] toggleSettings = {"Auto Attack", "Criticals", "Multi Aura", "Through Walls"};
        boolean[] toggleValues = {true, false, true, false};

        // Toggle settings
        context.drawText(mc.textRenderer, "Combat Options", x + 15, y, TEXT_PRIMARY.getRGB(), false);
        for (int i = 0; i < toggleSettings.length; i++) {
            int settingY = y + 20 + i * 35;

            // Setting name
            context.drawText(mc.textRenderer, toggleSettings[i], x + 15, settingY + 5,
                    TEXT_SECONDARY.getRGB(), false);

            // Toggle switch
            drawToggleSwitch(context, x + SETTINGS_WIDTH - 60, settingY, 40, 20,
                    toggleValues[i], theme);
        }

        // Slider settings
        int sliderY = y + 160;
        context.drawText(mc.textRenderer, "Parameters", x + 15, sliderY, TEXT_PRIMARY.getRGB(), false);

        String[] sliderNames = {"Attack Range", "Attack Speed", "FOV"};
        String[] sliderKeys = {"range", "speed", "fov"};
        String[] sliderValues = {"3.5", "12.0", "90°"};

        for (int i = 0; i < sliderNames.length; i++) {
            int sY = sliderY + 25 + i * 45;

            // Slider name and value
            context.drawText(mc.textRenderer, sliderNames[i], x + 15, sY,
                    TEXT_SECONDARY.getRGB(), false);
            context.drawText(mc.textRenderer, sliderValues[i], x + SETTINGS_WIDTH - 50, sY,
                    theme.ACCENT_SECONDARY.getRGB(), false);

            // Slider track
            drawSlider(context, x + 15, sY + 15, SETTINGS_WIDTH - 80,
                    this.sliderValues.get(sliderKeys[i]), theme);
        }

        // Keybind section
        int keybindY = sliderY + 180;
        drawRoundedRect(context, x + 10, keybindY, SETTINGS_WIDTH - 20, 35, 6, theme.BG_TERTIARY);
        context.drawText(mc.textRenderer, "Keybind:", x + 20, keybindY + 12,
                TEXT_SECONDARY.getRGB(), false);

        String keybind = "NONE";
        drawRoundedRect(context, x + 80, keybindY + 7, 60, 20, 4,
                new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                        theme.ACCENT_PRIMARY.getBlue(), 60));
        context.drawText(mc.textRenderer, keybind, x + 100, keybindY + 12,
                TEXT_PRIMARY.getRGB(), false);
    }

    private void renderThemePanel(DrawContext context, int mouseX, int mouseY,
                                  int x, int y, ThemeColors theme) {
        int panelWidth = 150;
        int panelHeight = 400;

        // Panel background
        drawRoundedRect(context, x, y, panelWidth, panelHeight, CORNER_RADIUS, theme.BG_SECONDARY);
        drawGlowBorder(context, x, y, panelWidth, panelHeight, CORNER_RADIUS, theme.ACCENT_PRIMARY, 0.2f);

        // Title
        context.drawText(mc.textRenderer, "Theme", x + 12, y + 12, TEXT_PRIMARY.getRGB(), false);

        // Theme options
        int themeY = y + 35;
        for (int i = 0; i < themes.length; i++) {
            int itemY = themeY + i * 35;
            if (itemY + 30 > y + panelHeight - 10) break;

            boolean isHovered = mouseX >= x + 5 && mouseX <= x + panelWidth - 5 &&
                    mouseY >= itemY && mouseY <= itemY + 30;
            boolean isSelected = i == selectedTheme;

            ThemeColors previewTheme = THEME_MAP.get(i);

            // Theme item background
            if (isSelected) {
                drawRoundedRect(context, x + 5, itemY, panelWidth - 10, 30, 6,
                        new Color(previewTheme.ACCENT_PRIMARY.getRed(),
                                previewTheme.ACCENT_PRIMARY.getGreen(),
                                previewTheme.ACCENT_PRIMARY.getBlue(), 80));
            } else if (isHovered) {
                drawRoundedRect(context, x + 5, itemY, panelWidth - 10, 30, 6, theme.BG_HOVER);
            }

            // Theme preview colors
            int previewX = x + 10;
            for (int c = 0; c < 3; c++) {
                Color previewColor = c == 0 ? previewTheme.ACCENT_PRIMARY :
                        c == 1 ? previewTheme.ACCENT_SECONDARY :
                                previewTheme.ACCENT_TERTIARY;
                drawCircle(context, previewX + c * 12, itemY + 15, 4, previewColor);
            }

            // Theme name
            context.drawText(mc.textRenderer, themes[i], x + 50, itemY + 10,
                    isSelected ? TEXT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);
        }
    }

    private void drawToggleSwitch(DrawContext context, int x, int y, int width, int height,
                                  boolean enabled, ThemeColors theme) {
        // Track
        Color trackColor = enabled ?
                new Color(theme.ACCENT_PRIMARY.getRed(), theme.ACCENT_PRIMARY.getGreen(),
                        theme.ACCENT_PRIMARY.getBlue(), 100) : theme.BG_TERTIARY;
        drawRoundedRect(context, x, y, width, height, height / 2, trackColor);

        // Thumb
        int thumbX = enabled ? x + width - height + 2 : x + 2;
        int thumbSize = height - 4;
        Color thumbColor = enabled ? theme.ACCENT_PRIMARY : TEXT_DISABLED;
        drawCircle(context, thumbX + thumbSize / 2, y + height / 2, thumbSize / 2, thumbColor);
    }

    private void drawSlider(DrawContext context, int x, int y, int width, float value, ThemeColors theme) {
        // Track
        drawRoundedRect(context, x, y, width, 6, 3, theme.BG_TERTIARY);

        // Filled portion
        int filledWidth = (int)(width * value);
        drawRoundedRect(context, x, y, filledWidth, 6, 3, theme.ACCENT_PRIMARY);

        // Thumb
        int thumbX = x + filledWidth;
        drawCircle(context, thumbX, y + 3, 8, theme.ACCENT_SECONDARY);
        drawCircle(context, thumbX, y + 3, 5, TEXT_PRIMARY);
    }

    private void drawCircle(DrawContext context, int centerX, int centerY, int radius, Color color) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color.getRGB());
                }
            }
        }
    }

    private void drawGlowBorder(DrawContext context, int x, int y, int width, int height,
                                int radius, Color color, float intensity) {
        int layers = 3;
        for (int i = layers; i > 0; i--) {
            int alpha = (int)(intensity * 255 * (1.0f - (float)i / layers) / 2);
            Color glowColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            drawRoundedBorder(context, x - i, y - i, width + i * 2, height + i * 2, radius + i, glowColor);
        }
    }

    private void updateAnimations(float delta) {
        categoryAnimation = lerp(categoryAnimation, selectedCategory, delta * 12.0f);

        List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
        for (int i = 0; i < modules.size(); i++) {
            float target = (hoveredModule == i) ? 1.0f : 0.0f;
            moduleAnimations.put(i, lerp(moduleAnimations.getOrDefault(i, 0.0f), target, delta * 15.0f));
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(t, 1.0f);
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color) {
        context.fill(x + radius, y, x + width - radius, y + height, color.getRGB());
        context.fill(x, y + radius, x + width, y + height - radius, color.getRGB());

        // Corners
        for (int cx = 0; cx < radius; cx++) {
            for (int cy = 0; cy < radius; cy++) {
                if (cx * cx + cy * cy <= radius * radius) {
                    // Top-left
                    context.fill(x + cx, y + cy, x + cx + 1, y + cy + 1, color.getRGB());
                    // Top-right
                    context.fill(x + width - radius + cx, y + cy, x + width - radius + cx + 1, y + cy + 1, color.getRGB());
                    // Bottom-left
                    context.fill(x + cx, y + height - radius + cy, x + cx + 1, y + height - radius + cy + 1, color.getRGB());
                    // Bottom-right
                    context.fill(x + width - radius + cx, y + height - radius + cy, x + width - radius + cx + 1, y + height - radius + cy + 1, color.getRGB());
                }
            }
        }
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
            int categoryX = PADDING;
            int moduleX = categoryX + CATEGORY_WIDTH + PADDING;
            int settingsX = moduleX + MODULE_WIDTH + PADDING;
            int themeX = settingsX + SETTINGS_WIDTH + PADDING;
            int panelY = PADDING + HEADER_HEIGHT;
            int panelHeight = this.height - PADDING * 2 - HEADER_HEIGHT;

            // Category selection
            if (mouseX >= categoryX && mouseX <= categoryX + CATEGORY_WIDTH) {
                Module.Category[] categories = Module.Category.values();
                int startY = panelY + 35;
                for (int i = 0; i < categories.length; i++) {
                    int catY = startY + i * (CATEGORY_HEIGHT + 5);
                    if (mouseY >= catY && mouseY <= catY + CATEGORY_HEIGHT) {
                        selectedCategory = i;
                        moduleScrollOffset = 0;
                        return true;
                    }
                }
            }

            // Module selection/toggle
            if (hoveredModule >= 0 && mouseX >= moduleX && mouseX <= moduleX + MODULE_WIDTH) {
                List<Module> modules = getModulesForCategory(Module.Category.values()[selectedCategory]);
                if (hoveredModule < modules.size()) {
                    Module module = modules.get(hoveredModule);

                    // Check if click is on toggle switch
                    int startY = panelY + 65;
                    int scrollStart = (int) moduleScrollOffset;
                    int modY = startY + (hoveredModule - scrollStart) * (MODULE_HEIGHT + 5);

                    if (mouseX >= moduleX + MODULE_WIDTH - 45 && mouseX <= moduleX + MODULE_WIDTH - 10) {
                        module.toggle();
                    } else {
                        selectedModule = module;
                    }
                    return true;
                }
            }

            // Theme selection
            if (mouseX >= themeX && mouseX <= themeX + 150) {
                int themeY = panelY + 35;
                for (int i = 0; i < themes.length; i++) {
                    int itemY = themeY + i * 35;
                    if (mouseY >= itemY && mouseY <= itemY + 30) {
                        selectedTheme = i;
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int moduleX = PADDING + CATEGORY_WIDTH + PADDING;
        int panelY = PADDING + HEADER_HEIGHT;

        // Module panel scrolling
        if (mouseX >= moduleX && mouseX <= moduleX + MODULE_WIDTH) {
            Module.Category currentCategory = Module.Category.values()[selectedCategory];
            List<Module> modules = getModulesForCategory(currentCategory);
            int visibleModules = (this.height - panelY - 80) / (MODULE_HEIGHT + 5);

            if (modules.size() > visibleModules) {
                moduleScrollOffset -= verticalAmount;
                moduleScrollOffset = Math.max(0, Math.min(modules.size() - visibleModules, moduleScrollOffset));
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC key
            mc.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private List<Module> getModulesForCategory(Module.Category category) {
        return ModuleManager.getModulesByCategory(category);
    }

    private String getCategoryName(Module.Category category) {
        return switch (category) {
            case COMBAT -> "Combat";
            case VISUALS -> "Render";
            case MOVEMENT -> "Movement";
            case MISC -> "Misc";
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Theme colors holder class
    private static class ThemeColors {
        public final Color BG_MAIN;
        public final Color BG_SECONDARY;
        public final Color BG_TERTIARY;
        public final Color BG_HOVER;
        public final Color ACCENT_PRIMARY;
        public final Color ACCENT_SECONDARY;
        public final Color ACCENT_TERTIARY;

        public ThemeColors(Color bgMain, Color bgSecondary, Color bgTertiary, Color bgHover,
                           Color accentPrimary, Color accentSecondary, Color accentTertiary) {
            this.BG_MAIN = bgMain;
            this.BG_SECONDARY = bgSecondary;
            this.BG_TERTIARY = bgTertiary;
            this.BG_HOVER = bgHover;
            this.ACCENT_PRIMARY = accentPrimary;
            this.ACCENT_SECONDARY = accentSecondary;
            this.ACCENT_TERTIARY = accentTertiary;
        }
    }
}