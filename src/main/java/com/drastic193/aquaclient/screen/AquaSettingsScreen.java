// File: src/main/java/com/drastic193/aquaclient/screen/AquaSettingsScreen.java
package com.drastic193.aquaclient.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AquaSettingsScreen extends Screen {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Screen parent;
    private float animationTime = 0.0f;
    private int selectedTab = 0;
    private float tabAnimation = 0.0f;
    private int hoveredSetting = -1;

    // Layout
    private int tabWidth = 150;
    private int padding = 20;
    private int settingHeight = 35;

    // Colors
    private static final Color BG_PRIMARY = new Color(11, 15, 25, 240);
    private static final Color BG_SECONDARY = new Color(16, 23, 39, 220);
    private static final Color BG_TERTIARY = new Color(21, 32, 54, 180);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_LIGHT = new Color(71, 85, 105, 100);

    // Setting categories
    private final String[] tabNames = {"Загальні", "GUI", "Модулі", "Про програму"};
    private final String[] tabIcons = {"⚙", "🎨", "🔧", "ℹ"};

    // Sample settings
    private List<Setting> generalSettings;
    private List<Setting> guiSettings;
    private List<Setting> moduleSettings;

    public AquaSettingsScreen(Screen parent) {
        super(Text.literal("Налаштування AquaClient"));
        this.parent = parent;
        initializeSettings();
    }

    private void initializeSettings() {
        // General settings
        generalSettings = new ArrayList<>();
        generalSettings.add(new Setting("Автозбереження", "Автоматично зберігати налаштування", true, SettingType.TOGGLE));
        generalSettings.add(new Setting("Звуки інтерфейсу", "Звукові ефекти в GUI", true, SettingType.TOGGLE));
        generalSettings.add(new Setting("Мова інтерфейсу", "Українська", 0, SettingType.LIST));
        generalSettings.add(new Setting("Швидкість анімацій", "100", 100, SettingType.SLIDER));

        // GUI settings
        guiSettings = new ArrayList<>();
        guiSettings.add(new Setting("Тема оформлення", "Темна", 0, SettingType.LIST));
        guiSettings.add(new Setting("Прозорість GUI", "85", 85, SettingType.SLIDER));
        guiSettings.add(new Setting("Розмір шрифту", "100", 100, SettingType.SLIDER));
        guiSettings.add(new Setting("Анімації", "Увімкнені плавні переходи", true, SettingType.TOGGLE));
        guiSettings.add(new Setting("Розмиття фону", "Розмивати фон під GUI", true, SettingType.TOGGLE));

        // Module settings
        moduleSettings = new ArrayList<>();
        moduleSettings.add(new Setting("Попередження", "Показувати попередження про ризики", true, SettingType.TOGGLE));
        moduleSettings.add(new Setting("Статистика", "Збирати статистику використання", false, SettingType.TOGGLE));
        moduleSettings.add(new Setting("Автооновлення", "Автоматично оновлювати модулі", true, SettingType.TOGGLE));
    }

    @Override
    protected void init() {
        super.init();

        // Back button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("← Назад"),
                btn -> mc.setScreen(parent)
        ).dimensions(padding, this.height - 50, 120, 30).build());

        // Apply button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Застосувати"),
                btn -> applySettings()
        ).dimensions(this.width - padding - 120, this.height - 50, 120, 30).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime += delta;
        updateAnimations(delta);

        // Animated background
        renderAnimatedBackground(context);

        // Main container
        renderMainContainer(context);

        // Header
        renderHeader(context);

        // Tab sidebar
        renderTabSidebar(context, mouseX, mouseY);

        // Settings content
        renderSettingsContent(context, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void updateAnimations(float delta) {
        tabAnimation = lerp(tabAnimation, selectedTab, delta * 8.0f);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(t, 1.0f);
    }

    private void renderAnimatedBackground(DrawContext context) {
        // Similar to main menu background
        context.fill(0, 0, this.width, this.height, BG_PRIMARY.getRGB());

        // Animated waves
        for (int y = 0; y < this.height; y += 3) {
            float wave = (float) Math.sin((y + animationTime * 25) * 0.008f) * 0.05f + 0.02f;
            int alpha = (int) (wave * 255);
            context.fill(0, y, this.width, y + 3, new Color(59, 130, 246, alpha).getRGB());
        }
    }

    private void renderMainContainer(DrawContext context) {
        int containerWidth = this.width - padding * 2;
        int containerHeight = this.height - padding * 2;
        int containerX = padding;
        int containerY = padding;

        // Background
        context.fill(containerX, containerY, containerX + containerWidth, containerY + containerHeight,
                BG_SECONDARY.getRGB());

        // Border
        drawGradientBorder(context, containerX, containerY, containerWidth, containerHeight, 1);
    }

    private void renderHeader(DrawContext context) {
        int headerHeight = 60;
        int headerY = padding;

        // Header background
        drawGradient(context, padding, headerY, this.width - padding * 2, headerHeight,
                BG_TERTIARY, BG_SECONDARY);

        // Title
        context.getMatrices().push();
        context.getMatrices().translate(padding + 20, headerY + 15, 0);

        String title = "НАЛАШТУВАННЯ";
        context.drawText(mc.textRenderer, title, 0, 0, ACCENT_BLUE.getRGB(), false);

        String subtitle = "AquaClient Configuration";
        context.drawText(mc.textRenderer, subtitle, 0, 15, TEXT_SECONDARY.getRGB(), false);

        context.getMatrices().pop();

        // Settings icon
        context.drawText(mc.textRenderer, "⚙", this.width - padding - 35, headerY + 20,
                ACCENT_PURPLE.getRGB(), false);
    }

    private void renderTabSidebar(DrawContext context, int mouseX, int mouseY) {
        int sidebarX = padding + 10;
        int sidebarY = padding + 70;
        int sidebarHeight = this.height - padding * 2 - 120;

        // Sidebar background
        context.fill(sidebarX, sidebarY, sidebarX + tabWidth, sidebarY + sidebarHeight,
                BG_TERTIARY.getRGB());
        drawGradientBorder(context, sidebarX, sidebarY, tabWidth, sidebarHeight, 1);

        for (int i = 0; i < tabNames.length; i++) {
            int tabY = sidebarY + 10 + i * 50;
            int tabHeight = 40;

            boolean isSelected = selectedTab == i;
            boolean isHovered = mouseX >= sidebarX && mouseX <= sidebarX + tabWidth &&
                    mouseY >= tabY && mouseY <= tabY + tabHeight;

            // Tab background with animation
            if (isSelected) {
                context.fill(sidebarX + 5, tabY, sidebarX + tabWidth - 5, tabY + tabHeight,
                        new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 80).getRGB());

                // Selection indicator
                context.fill(sidebarX + 2, tabY + 8, sidebarX + 6, tabY + tabHeight - 8,
                        ACCENT_BLUE.getRGB());
            } else if (isHovered) {
                context.fill(sidebarX + 5, tabY, sidebarX + tabWidth - 5, tabY + tabHeight,
                        new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), 20).getRGB());
            }

            // Tab icon and text
            Color textColor = isSelected ? TEXT_PRIMARY : TEXT_SECONDARY;

            context.drawText(mc.textRenderer, tabIcons[i], sidebarX + 15, tabY + 12,
                    ACCENT_BLUE.getRGB(), false);
            context.drawText(mc.textRenderer, tabNames[i], sidebarX + 35, tabY + 12,
                    textColor.getRGB(), false);
        }
    }

    private void renderSettingsContent(DrawContext context, int mouseX, int mouseY) {
        int contentX = padding + tabWidth + 20;
        int contentY = padding + 70;
        int contentWidth = this.width - padding * 2 - tabWidth - 30;
        int contentHeight = this.height - padding * 2 - 120;

        // Content background
        context.fill(contentX, contentY, contentX + contentWidth, contentY + contentHeight,
                BG_TERTIARY.getRGB());
        drawGradientBorder(context, contentX, contentY, contentWidth, contentHeight, 1);

        // Tab title
        String tabTitle = tabNames[selectedTab].toUpperCase();
        context.drawText(mc.textRenderer, tabTitle, contentX + 20, contentY + 15,
                TEXT_PRIMARY.getRGB(), false);

        // Settings list based on selected tab
        List<Setting> currentSettings = getCurrentSettings();
        hoveredSetting = -1;

        int settingStartY = contentY + 50;

        for (int i = 0; i < currentSettings.size(); i++) {
            Setting setting = currentSettings.get(i);
            int settingY = settingStartY + i * (settingHeight + 10);

            if (settingY + settingHeight > contentY + contentHeight - 20) break;

            boolean isHovered = mouseX >= contentX + 10 && mouseX <= contentX + contentWidth - 10 &&
                    mouseY >= settingY && mouseY <= settingY + settingHeight;

            if (isHovered) hoveredSetting = i;

            renderSetting(context, setting, contentX + 20, settingY, contentWidth - 40, settingHeight,
                    isHovered, mouseX, mouseY);
        }

        // Special content for "About" tab
        if (selectedTab == 3) {
            renderAboutContent(context, contentX, contentY, contentWidth, contentHeight);
        }
    }

    private void renderSetting(DrawContext context, Setting setting, int x, int y, int width, int height,
                               boolean hovered, int mouseX, int mouseY) {
        // Setting background
        if (hovered) {
            context.fill(x - 10, y, x + width + 10, y + height,
                    new Color(TEXT_PRIMARY.getRed(), TEXT_PRIMARY.getGreen(), TEXT_PRIMARY.getBlue(), 20).getRGB());
        }

        // Setting name
        context.drawText(mc.textRenderer, setting.name, x, y + 5, TEXT_PRIMARY.getRGB(), false);

        // Setting description
        if (setting.description != null && !setting.description.isEmpty()) {
            context.drawText(mc.textRenderer, setting.description, x, y + 18, TEXT_MUTED.getRGB(), false);
        }

        // Setting control based on type
        int controlX = x + width - 200;
        int controlY = y + 5;

        switch (setting.type) {
            case TOGGLE -> renderToggle(context, setting, controlX, controlY, mouseX, mouseY);
            case SLIDER -> renderSlider(context, setting, controlX, controlY, width / 3, mouseX, mouseY);
            case LIST -> renderDropdown(context, setting, controlX, controlY, mouseX, mouseY);
            case BUTTON -> renderButton(context, setting, controlX, controlY, mouseX, mouseY);
        }
    }

    private void renderToggle(DrawContext context, Setting setting, int x, int y, int mouseX, int mouseY) {
        int toggleWidth = 50;
        int toggleHeight = 24;

        boolean isOn = setting.boolValue;
        Color bgColor = isOn ? ACCENT_GREEN : new Color(60, 60, 60);
        Color switchColor = TEXT_PRIMARY;

        // Toggle background
        context.fill(x, y, x + toggleWidth, y + toggleHeight,
                new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 180).getRGB());

        // Toggle switch
        int switchSize = 18;
        int switchX = isOn ? x + toggleWidth - switchSize - 3 : x + 3;
        int switchY = y + 3;

        context.fill(switchX, switchY, switchX + switchSize, switchY + switchSize, switchColor.getRGB());

        // Toggle border
        drawGradientBorder(context, x, y, toggleWidth, toggleHeight, 1);

        // Label
        String label = isOn ? "ON" : "OFF";
        context.drawText(mc.textRenderer, label, x + toggleWidth + 10, y + 8,
                isOn ? ACCENT_GREEN.getRGB() : TEXT_MUTED.getRGB(), false);
    }

    private void renderSlider(DrawContext context, Setting setting, int x, int y, int width, int mouseX, int mouseY) {
        int sliderHeight = 6;
        int sliderY = y + 9;

        // Slider background
        context.fill(x, sliderY, x + width, sliderY + sliderHeight,
                new Color(60, 60, 60, 180).getRGB());

        // Slider progress
        float progress = setting.intValue / 100.0f;
        int progressWidth = (int) (width * progress);

        context.fill(x, sliderY, x + progressWidth, sliderY + sliderHeight, ACCENT_BLUE.getRGB());

        // Slider handle
        int handleSize = 14;
        int handleX = x + progressWidth - handleSize / 2;
        int handleY = sliderY - 4;

        context.fill(handleX, handleY, handleX + handleSize, handleY + handleSize, TEXT_PRIMARY.getRGB());

        // Value display
        String valueText = setting.intValue + "%";
        int valueWidth = mc.textRenderer.getWidth(valueText);
        context.drawText(mc.textRenderer, valueText, x + width - valueWidth, y, TEXT_SECONDARY.getRGB(), false);
    }

    private void renderDropdown(DrawContext context, Setting setting, int x, int y, int mouseX, int mouseY) {
        int dropdownWidth = 120;
        int dropdownHeight = 24;

        // Dropdown background
        context.fill(x, y, x + dropdownWidth, y + dropdownHeight, BG_SECONDARY.getRGB());
        drawGradientBorder(context, x, y, dropdownWidth, dropdownHeight, 1);

        // Current value
        String currentValue = setting.stringValue;
        context.drawText(mc.textRenderer, currentValue, x + 8, y + 8, TEXT_PRIMARY.getRGB(), false);

        // Dropdown arrow
        context.drawText(mc.textRenderer, "▼", x + dropdownWidth - 20, y + 8, TEXT_SECONDARY.getRGB(), false);
    }

    private void renderButton(DrawContext context, Setting setting, int x, int y, int mouseX, int mouseY) {
        int buttonWidth = 100;
        int buttonHeight = 24;

        boolean isHovered = mouseX >= x && mouseX <= x + buttonWidth &&
                mouseY >= y && mouseY <= y + buttonHeight;

        Color buttonColor = isHovered ? ACCENT_BLUE : new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 150);

        context.fill(x, y, x + buttonWidth, y + buttonHeight, buttonColor.getRGB());
        drawGradientBorder(context, x, y, buttonWidth, buttonHeight, 1);

        // Button text
        String buttonText = setting.stringValue;
        int textWidth = mc.textRenderer.getWidth(buttonText);
        int textX = x + (buttonWidth - textWidth) / 2;
        int textY = y + (buttonHeight - 8) / 2;

        context.drawText(mc.textRenderer, buttonText, textX, textY, TEXT_PRIMARY.getRGB(), false);
    }

    private void renderAboutContent(DrawContext context, int x, int y, int width, int height) {
        int contentY = y + 50;

        // Client info
        context.drawText(mc.textRenderer, "AquaClient v2.1", x + 20, contentY, ACCENT_BLUE.getRGB(), false);
        context.drawText(mc.textRenderer, "Розроблено для Minecraft 1.21.1", x + 20, contentY + 20, TEXT_SECONDARY.getRGB(), false);

        // Developer info
        context.drawText(mc.textRenderer, "Розробник:", x + 20, contentY + 50, TEXT_SECONDARY.getRGB(), false);
        context.drawText(mc.textRenderer, "drastic193", x + 20, contentY + 70, TEXT_PRIMARY.getRGB(), false);

        // Version info
        context.drawText(mc.textRenderer, "Інформація про версію:", x + 20, contentY + 100, TEXT_SECONDARY.getRGB(), false);
        context.drawText(mc.textRenderer, "• Fabric Loader: 0.15.11+", x + 20, contentY + 120, TEXT_MUTED.getRGB(), false);
        context.drawText(mc.textRenderer, "• Java Version: 21+", x + 20, contentY + 140, TEXT_MUTED.getRGB(), false);
        context.drawText(mc.textRenderer, "• Модулів: " + getModuleCount(), x + 20, contentY + 160, TEXT_MUTED.getRGB(), false);

        // Features
        context.drawText(mc.textRenderer, "Особливості:", x + 20, contentY + 190, TEXT_SECONDARY.getRGB(), false);
        context.drawText(mc.textRenderer, "✓ Сучасний інтерфейс", x + 20, contentY + 210, ACCENT_GREEN.getRGB(), false);
        context.drawText(mc.textRenderer, "✓ Високопродуктивні модулі", x + 20, contentY + 230, ACCENT_GREEN.getRGB(), false);
        context.drawText(mc.textRenderer, "✓ Безпечні алгоритми", x + 20, contentY + 250, ACCENT_GREEN.getRGB(), false);
        context.drawText(mc.textRenderer, "✓ Регулярні оновлення", x + 20, contentY + 270, ACCENT_GREEN.getRGB(), false);

        // Warning
        context.drawText(mc.textRenderer, "⚠ Увага:", x + 20, contentY + 300, ACCENT_RED.getRGB(), false);
        context.drawText(mc.textRenderer, "Використовуйте відповідально та згідно з правилами серверів.",
                x + 20, contentY + 320, TEXT_MUTED.getRGB(), false);

        // Logo area
        int logoX = x + width - 150;
        int logoY = contentY + 50;
        context.fill(logoX, logoY, logoX + 120, logoY + 120,
                new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 30).getRGB());
        drawGradientBorder(context, logoX, logoY, 120, 120, 2);

        context.drawText(mc.textRenderer, "AQUA", logoX + 35, logoY + 45, ACCENT_BLUE.getRGB(), false);
        context.drawText(mc.textRenderer, "CLIENT", logoX + 25, logoY + 65, TEXT_PRIMARY.getRGB(), false);
    }

    private List<Setting> getCurrentSettings() {
        return switch (selectedTab) {
            case 0 -> generalSettings;
            case 1 -> guiSettings;
            case 2 -> moduleSettings;
            default -> new ArrayList<>();
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Tab selection
            int sidebarX = padding + 10;
            int sidebarY = padding + 70;

            if (mouseX >= sidebarX && mouseX <= sidebarX + tabWidth) {
                for (int i = 0; i < tabNames.length; i++) {
                    int tabY = sidebarY + 10 + i * 50;
                    if (mouseY >= tabY && mouseY <= tabY + 40) {
                        selectedTab = i;
                        return true;
                    }
                }
            }

            // Setting interactions
            if (hoveredSetting >= 0) {
                List<Setting> currentSettings = getCurrentSettings();
                if (hoveredSetting < currentSettings.size()) {
                    Setting setting = currentSettings.get(hoveredSetting);
                    handleSettingClick(setting, mouseX, mouseY);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSettingClick(Setting setting, double mouseX, double mouseY) {
        switch (setting.type) {
            case TOGGLE -> setting.boolValue = !setting.boolValue;
            case LIST -> cycleSetting(setting);
            case BUTTON -> executeSettingAction(setting);
            case SLIDER -> handleSliderClick(setting, mouseX, mouseY);
        }
    }

    private void cycleSetting(Setting setting) {
        // Simple cycling for demo
        if (setting.name.equals("Мова інтерфейсу")) {
            setting.stringValue = setting.stringValue.equals("Українська") ? "English" : "Українська";
        } else if (setting.name.equals("Тема оформлення")) {
            String[] themes = {"Темна", "Світла", "Синя", "Фіолетова"};
            for (int i = 0; i < themes.length; i++) {
                if (themes[i].equals(setting.stringValue)) {
                    setting.stringValue = themes[(i + 1) % themes.length];
                    break;
                }
            }
        }
    }

    private void executeSettingAction(Setting setting) {
        // Placeholder for button actions
        System.out.println("Executed action for: " + setting.name);
    }

    private void handleSliderClick(Setting setting, double mouseX, double mouseY) {
        // Simplified slider interaction
        int contentX = padding + tabWidth + 20;
        int sliderX = contentX + (this.width - padding * 2 - tabWidth - 30) - 200;
        int sliderWidth = (this.width - padding * 2 - tabWidth - 30) / 3;

        if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth) {
            float progress = (float) (mouseX - sliderX) / sliderWidth;
            setting.intValue = Math.max(0, Math.min(100, (int) (progress * 100)));
        }
    }

    private void applySettings() {
        // Apply all settings
        System.out.println("Settings applied!");
        // Here you would save settings to config file
    }

    private void drawGradientBorder(DrawContext context, int x, int y, int width, int height, int borderWidth) {
        context.fill(x, y, x + width, y + borderWidth, BORDER_LIGHT.getRGB());
        context.fill(x, y + height - borderWidth, x + width, y + height, BORDER_LIGHT.getRGB());
        context.fill(x, y, x + borderWidth, y + height, BORDER_LIGHT.getRGB());
        context.fill(x + width - borderWidth, y, x + width, y + height, BORDER_LIGHT.getRGB());
    }

    private void drawGradient(DrawContext context, int x, int y, int width, int height, Color startColor, Color endColor) {
        for (int i = 0; i < height; i++) {
            float progress = (float) i / height;
            int r = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
            int g = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
            int b = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);
            int a = (int) (startColor.getAlpha() + (endColor.getAlpha() - startColor.getAlpha()) * progress);

            context.fill(x, y + i, x + width, y + i + 1, new Color(r, g, b, a).getRGB());
        }
    }

    private int getModuleCount() {
        return 12; // Based on your ModuleManager
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Setting classes
    private static class Setting {
        String name;
        String description;
        boolean boolValue;
        int intValue;
        String stringValue;
        SettingType type;

        public Setting(String name, String description, boolean boolValue, SettingType type) {
            this.name = name;
            this.description = description;
            this.boolValue = boolValue;
            this.type = type;
        }

        public Setting(String name, String stringValue, int intValue, SettingType type) {
            this.name = name;
            this.stringValue = stringValue;
            this.intValue = intValue;
            this.type = type;
        }
    }

    private enum SettingType {
        TOGGLE, SLIDER, LIST, BUTTON
    }
}