// File: src/main/java/com/drastic193/aquaclient/screen/DeltaMultiplayerScreen.java
package com.drastic193.aquaclient.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class DeltaMultiplayerScreen extends MultiplayerScreen {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private float animationTime = 0.0f;

    // Version switcher
    private TextFieldWidget versionField;
    private boolean isVersionDropdownOpen = false;
    private int selectedVersionIndex = 0;
    private final String[] availableVersions = {
            "1.21.1", "1.21", "1.20.6", "1.20.4", "1.20.1", "1.19.4", "1.19.2", "1.18.2", "1.17.1", "1.16.5"
    };

    // Delta style colors
    private static final Color BG_MAIN = new Color(12, 12, 16, 255);
    private static final Color BG_SECONDARY = new Color(18, 18, 24, 200);
    private static final Color BG_TERTIARY = new Color(24, 24, 32, 180);
    private static final Color BG_HOVER = new Color(35, 35, 45, 255);

    private static final Color ACCENT_PRIMARY = new Color(138, 43, 226, 255);
    private static final Color ACCENT_SUCCESS = new Color(34, 197, 94, 255);
    private static final Color ACCENT_WARNING = new Color(251, 191, 36, 255);
    private static final Color ACCENT_INFO = new Color(59, 130, 246, 255);

    private static final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(170, 170, 180, 255);
    private static final Color TEXT_DISABLED = new Color(100, 100, 100, 255);

    public DeltaMultiplayerScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected void init() {
        super.init();

        // Version switcher field (top-left)
        versionField = new TextFieldWidget(mc.textRenderer, 15, 15, 100, 20,
                Text.literal("Version"));
        versionField.setText(availableVersions[selectedVersionIndex]);
        versionField.setEditable(false);
        this.addSelectableChild(versionField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime += delta * 0.02f;

        // Custom background instead of default
        renderDeltaBackground(context);

        // Render version switcher
        renderVersionSwitcher(context, mouseX, mouseY);

        // Render custom overlay elements
        renderCustomOverlays(context);

        // Call parent render but modify some elements
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderDeltaBackground(DrawContext context) {
        // Dark base background
        context.fill(0, 0, this.width, this.height, BG_MAIN.getRGB());

        // Animated gradient waves
        for (int y = 0; y < this.height; y += 4) {
            float wave1 = (float) Math.sin((y + animationTime * 60) * 0.008f) * 0.3f + 0.2f;
            float wave2 = (float) Math.cos((y + animationTime * 40) * 0.006f) * 0.2f + 0.15f;

            int alpha1 = Math.max(0, Math.min(80, (int) (wave1 * 60)));
            int alpha2 = Math.max(0, Math.min(60, (int) (wave2 * 40)));

            // Purple wave
            context.fill(0, y, this.width, y + 4,
                    new Color(138, 43, 226, alpha1).getRGB());

            // Blue accent
            context.fill(this.width / 2, y, this.width, y + 4,
                    new Color(59, 130, 246, alpha2).getRGB());
        }

        // Subtle grid pattern
        int gridSize = 40;
        for (int x = 0; x < this.width; x += gridSize) {
            for (int y = 0; y < this.height; y += gridSize) {
                float alpha = (float) Math.sin(animationTime + x * 0.01f + y * 0.01f) * 0.02f + 0.01f;
                if (alpha > 0) {
                    int alphaInt = Math.max(0, Math.min(20, (int) (alpha * 255)));
                    context.fill(x, y, x + 1, y + 1,
                            new Color(255, 255, 255, alphaInt).getRGB());
                }
            }
        }
    }

    private void renderVersionSwitcher(DrawContext context, int mouseX, int mouseY) {
        int switcherX = 15;
        int switcherY = 15;
        int switcherWidth = 140;
        int switcherHeight = 25;

        // Background panel for version switcher
        drawRoundedRect(context, switcherX - 5, switcherY - 5, switcherWidth + 10, switcherHeight + 10,
                8, new Color(BG_SECONDARY.getRed(), BG_SECONDARY.getGreen(), BG_SECONDARY.getBlue(), 200));

        // Label
        context.drawText(mc.textRenderer, "Version:", switcherX, switcherY + 5, TEXT_SECONDARY.getRGB(), false);

        // Version display box
        boolean isHovered = mouseX >= switcherX + 50 && mouseX <= switcherX + switcherWidth &&
                mouseY >= switcherY && mouseY <= switcherY + switcherHeight;

        Color boxColor = isHovered ? BG_HOVER : BG_TERTIARY;
        drawRoundedRect(context, switcherX + 50, switcherY, 85, 25, 4, boxColor);

        // Current version text
        String currentVersion = availableVersions[selectedVersionIndex];
        context.drawText(mc.textRenderer, currentVersion, switcherX + 60, switcherY + 8,
                TEXT_PRIMARY.getRGB(), false);

        // Dropdown arrow
        String arrow = isVersionDropdownOpen ? "▲" : "▼";
        context.drawText(mc.textRenderer, arrow, switcherX + 125, switcherY + 8,
                ACCENT_PRIMARY.getRGB(), false);

        // Version status indicator
        Color statusColor = getVersionStatusColor(currentVersion);
        context.fill(switcherX + 140, switcherY + 10, switcherX + 145, switcherY + 15, statusColor.getRGB());

        // Dropdown menu
        if (isVersionDropdownOpen) {
            renderVersionDropdown(context, switcherX + 50, switcherY + 30, mouseX, mouseY);
        }
    }

    private void renderVersionDropdown(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int dropdownWidth = 85;
        int itemHeight = 20;
        int maxItems = Math.min(availableVersions.length, 8);
        int dropdownHeight = maxItems * itemHeight + 4;

        // Dropdown background
        drawRoundedRect(context, x, y, dropdownWidth, dropdownHeight, 4, BG_SECONDARY);
        drawRoundedBorder(context, x, y, dropdownWidth, dropdownHeight, 4, ACCENT_PRIMARY);

        // Dropdown items
        for (int i = 0; i < maxItems; i++) {
            int itemY = y + 2 + i * itemHeight;
            String version = availableVersions[i];

            boolean isItemHovered = mouseX >= x && mouseX <= x + dropdownWidth &&
                    mouseY >= itemY && mouseY <= itemY + itemHeight;
            boolean isSelected = i == selectedVersionIndex;

            // Item background
            if (isSelected) {
                drawRoundedRect(context, x + 2, itemY, dropdownWidth - 4, itemHeight, 2,
                        new Color(ACCENT_PRIMARY.getRed(), ACCENT_PRIMARY.getGreen(),
                                ACCENT_PRIMARY.getBlue(), 100));
            } else if (isItemHovered) {
                drawRoundedRect(context, x + 2, itemY, dropdownWidth - 4, itemHeight, 2, BG_HOVER);
            }

            // Version text
            Color textColor = isSelected ? ACCENT_PRIMARY : TEXT_PRIMARY;
            context.drawText(mc.textRenderer, version, x + 8, itemY + 6, textColor.getRGB(), false);

            // Version status dot
            Color statusColor = getVersionStatusColor(version);
            context.fill(x + dropdownWidth - 15, itemY + 8, x + dropdownWidth - 10, itemY + 13,
                    statusColor.getRGB());
        }
    }

    private void renderCustomOverlays(DrawContext context) {
        // Server count overlay (top-right)
        String serverCount = "Servers: " + (this.serverListWidget != null ? this.serverListWidget.children().size() : 0);
        int serverCountWidth = mc.textRenderer.getWidth(serverCount);

        drawRoundedRect(context, this.width - serverCountWidth - 25, 15, serverCountWidth + 15, 20, 6,
                new Color(BG_SECONDARY.getRed(), BG_SECONDARY.getGreen(), BG_SECONDARY.getBlue(), 200));
        context.drawText(mc.textRenderer, serverCount, this.width - serverCountWidth - 18, 21,
                TEXT_SECONDARY.getRGB(), false);

        // Connection status indicator
        drawConnectionStatus(context);

        // Quick connect panel
        renderQuickConnectPanel(context);

        // Delta watermark
        renderWatermark(context);
    }

    private void drawConnectionStatus(DrawContext context) {
        int statusX = this.width - 50;
        int statusY = 50;

        // Connection status background
        drawRoundedRect(context, statusX, statusY, 40, 60, 8, BG_TERTIARY);

        // Status title
        context.drawText(mc.textRenderer, "Net", statusX + 10, statusY + 8, TEXT_SECONDARY.getRGB(), false);

        // Connection bars (animated)
        for (int i = 0; i < 4; i++) {
            int barHeight = 8 + i * 3;
            int barY = statusY + 45 - barHeight;
            float strength = (float) Math.sin(animationTime * 5 + i) * 0.5f + 0.5f;

            Color barColor = strength > 0.7f ? ACCENT_SUCCESS :
                    strength > 0.4f ? ACCENT_WARNING : ACCENT_PRIMARY;

            int alpha = (int) (strength * 255);
            Color finalColor = new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), alpha);

            context.fill(statusX + 8 + i * 6, barY, statusX + 11 + i * 6, statusY + 45,
                    finalColor.getRGB());
        }
    }

    private void renderQuickConnectPanel(DrawContext context) {
        int panelX = 15;
        int panelY = 60;
        int panelWidth = 200;
        int panelHeight = 100;

        // Panel background
        drawRoundedRect(context, panelX, panelY, panelWidth, panelHeight, 8, BG_TERTIARY);
        drawRoundedBorder(context, panelX, panelY, panelWidth, panelHeight, 8,
                new Color(ACCENT_INFO.getRed(), ACCENT_INFO.getGreen(), ACCENT_INFO.getBlue(), 100));

        // Panel title
        context.drawText(mc.textRenderer, "Quick Connect", panelX + 10, panelY + 8,
                ACCENT_INFO.getRGB(), false);

        // Popular servers
        String[] popularServers = {"Hypixel", "Mineplex", "CubeCraft", "Hive"};
        Color[] serverColors = {ACCENT_WARNING, ACCENT_SUCCESS, ACCENT_INFO, ACCENT_PRIMARY};

        for (int i = 0; i < popularServers.length; i++) {
            int buttonY = panelY + 25 + i * 18;

            // Server button background
            drawRoundedRect(context, panelX + 10, buttonY, panelWidth - 20, 15, 3,
                    new Color(serverColors[i].getRed(), serverColors[i].getGreen(),
                            serverColors[i].getBlue(), 60));

            // Server name
            context.drawText(mc.textRenderer, popularServers[i], panelX + 15, buttonY + 4,
                    TEXT_PRIMARY.getRGB(), false);

            // Player count (mock)
            String playerCount = (1000 + i * 500) + " online";
            int playerCountWidth = mc.textRenderer.getWidth(playerCount);
            context.drawText(mc.textRenderer, playerCount,
                    panelX + panelWidth - playerCountWidth - 15, buttonY + 4,
                    TEXT_SECONDARY.getRGB(), false);
        }
    }

    private void renderWatermark(DrawContext context) {
        String watermark = "AquaClient Delta";
        int watermarkWidth = mc.textRenderer.getWidth(watermark);

        // Watermark background with glow
        for (int i = 2; i >= 0; i--) {
            int glowAlpha = 30 - i * 8;
            drawRoundedRect(context, this.width - watermarkWidth - 25 + i, this.height - 25 + i,
                    watermarkWidth + 15, 15, 4,
                    new Color(138, 43, 226, glowAlpha));
        }

        drawRoundedRect(context, this.width - watermarkWidth - 25, this.height - 25,
                watermarkWidth + 15, 15, 4,
                new Color(0, 0, 0, 150));

        // Rainbow watermark text
        float hue = (animationTime * 2) % 1.0f;
        Color rainbowColor = Color.getHSBColor(hue, 0.8f, 1.0f);

        context.drawText(mc.textRenderer, watermark, this.width - watermarkWidth - 18, this.height - 21,
                rainbowColor.getRGB(), false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Version switcher click
            if (mouseX >= 65 && mouseX <= 150 && mouseY >= 15 && mouseY <= 40) {
                isVersionDropdownOpen = !isVersionDropdownOpen;
                return true;
            }

            // Version dropdown item selection
            if (isVersionDropdownOpen && mouseX >= 65 && mouseX <= 150) {
                int dropdownY = 45;
                int itemHeight = 20;
                int clickedItem = (int) ((mouseY - dropdownY - 2) / itemHeight);

                if (clickedItem >= 0 && clickedItem < availableVersions.length) {
                    selectedVersionIndex = clickedItem;
                    versionField.setText(availableVersions[selectedVersionIndex]);
                    isVersionDropdownOpen = false;

                    // In real implementation, this would trigger version switching
                    switchVersion(availableVersions[selectedVersionIndex]);
                    return true;
                }
            }

            // Quick connect buttons
            if (mouseX >= 25 && mouseX <= 195) {
                for (int i = 0; i < 4; i++) {
                    int buttonY = 85 + i * 18;
                    if (mouseY >= buttonY && mouseY <= buttonY + 15) {
                        quickConnectToServer(i);
                        return true;
                    }
                }
            }

            // Close dropdown if clicking elsewhere
            if (isVersionDropdownOpen) {
                isVersionDropdownOpen = false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && isVersionDropdownOpen) { // ESC key
            isVersionDropdownOpen = false;
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void switchVersion(String version) {
        // In a real implementation, this would:
        // 1. Download the specified version if not available
        // 2. Switch the game to use that version
        // 3. Restart with the new version

        System.out.println("Switching to version: " + version);

        // For demonstration, just show a message
        // In real implementation, you'd need to integrate with a version switcher like MultiMC or similar
    }

    private void quickConnectToServer(int serverIndex) {
        String[] serverIPs = {
                "mc.hypixel.net",
                "us.mineplex.com",
                "play.cubecraft.net",
                "play.hivemc.com"
        };

        if (serverIndex < serverIPs.length) {
            // In real implementation, this would connect to the server
            System.out.println("Quick connecting to: " + serverIPs[serverIndex]);
        }
    }

    private Color getVersionStatusColor(String version) {
        // Mock version status based on version
        return switch (version) {
            case "1.21.1", "1.21" -> ACCENT_SUCCESS; // Latest/stable
            case "1.20.6", "1.20.4", "1.20.1" -> ACCENT_WARNING; // Older but supported
            default -> TEXT_DISABLED; // Very old/legacy
        };
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color) {
        // Simplified rounded rectangle
        context.fill(x, y, x + width, y + height, color.getRGB());

        // Corner rounding effect (simplified)
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

        // Corner pixels (simplified)
        context.fill(x + radius - 1, y + 1, x + radius, y + 2, color.getRGB());
        context.fill(x + width - radius, y + 1, x + width - radius + 1, y + 2, color.getRGB());
        context.fill(x + radius - 1, y + height - 2, x + radius, y + height - 1, color.getRGB());
        context.fill(x + width - radius, y + height - 2, x + width - radius + 1, y + height - 1, color.getRGB());
    }
}