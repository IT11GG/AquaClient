// File: src/main/java/com/drastic193/aquaclient/screen/AquaAccountManager.java
package com.drastic193.aquaclient.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.session.Session;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AquaAccountManager extends Screen {
    private final Screen parent;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private float animationTime = 0.0f;

    // UI Elements
    private TextFieldWidget usernameField;
    private int selectedAccount = -1;
    private int hoveredAccount = -1;
    private boolean isAddingAccount = false;

    // Colors (Aqua style)
    private static final Color BG_MAIN = new Color(12, 12, 16, 255);
    private static final Color BG_SECONDARY = new Color(18, 18, 24, 200);
    private static final Color BG_TERTIARY = new Color(24, 24, 32, 180);
    private static final Color BG_HOVER = new Color(35, 35, 45, 255);

    private static final Color ACCENT_PRIMARY = new Color(138, 43, 226, 255);
    private static final Color ACCENT_SUCCESS = new Color(34, 197, 94, 255);
    private static final Color ACCENT_WARNING = new Color(251, 191, 36, 255);
    private static final Color ACCENT_DANGER = new Color(239, 68, 68, 255);

    private static final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(170, 170, 180, 255);
    private static final Color TEXT_DISABLED = new Color(100, 100, 100, 255);

    // Mock account storage (in real implementation, save to file)
    private final List<AccountInfo> accounts = new ArrayList<>();

    public AquaAccountManager(Screen parent) {
        super(Text.literal("Account Manager"));
        this.parent = parent;

        // Add some demo accounts
        accounts.add(new AccountInfo("Player123", AccountType.CRACKED, true));
        accounts.add(new AccountInfo("TestUser", AccountType.CRACKED, false));
        accounts.add(new AccountInfo("PremiumUser", AccountType.PREMIUM, false));
    }

    @Override
    protected void init() {
        super.init();

        // Username input field
        usernameField = new TextFieldWidget(mc.textRenderer,
                this.width / 2 - 100, this.height - 100, 200, 20,
                Text.literal("Username"));
        usernameField.setPlaceholder(Text.literal("Enter username..."));
        usernameField.setMaxLength(16);
        this.addSelectableChild(usernameField);
        this.setInitialFocus(usernameField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationTime += delta * 0.02f;

        // Animated background
        renderAnimatedBackground(context);

        // Main panel
        renderMainPanel(context, mouseX, mouseY);

        // Header
        renderHeader(context);

        // Account list
        renderAccountList(context, mouseX, mouseY);

        // Add account section
        renderAddAccountSection(context, mouseX, mouseY);

        // Control buttons
        renderControlButtons(context, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderAnimatedBackground(DrawContext context) {
        // Dark base
        context.fill(0, 0, this.width, this.height, BG_MAIN.getRGB());

        // Animated waves
        for (int y = 0; y < this.height; y += 3) {
            float wave = (float) Math.sin((y + animationTime * 80) * 0.01f) * 0.3f + 0.2f;
            int alpha = Math.max(0, Math.min(60, (int) (wave * 80)));

            context.fill(0, y, this.width, y + 3,
                    new Color(138, 43, 226, alpha).getRGB());
        }
    }

    private void renderMainPanel(DrawContext context, int mouseX, int mouseY) {
        int panelWidth = Math.min(800, this.width - 40);
        int panelHeight = Math.min(600, this.height - 40);
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        // Panel background
        drawRoundedRect(context, panelX, panelY, panelWidth, panelHeight, 12, BG_SECONDARY);

        // Panel border with glow
        drawRoundedBorder(context, panelX, panelY, panelWidth, panelHeight, 12,
                new Color(138, 43, 226, 100));

        // Subtle inner glow
        drawRoundedRect(context, panelX + 2, panelY + 2, panelWidth - 4, panelHeight - 4, 10,
                new Color(255, 255, 255, 5));
    }

    private void renderHeader(DrawContext context) {
        int centerX = this.width / 2;
        int headerY = (this.height - Math.min(600, this.height - 40)) / 2 + 20;

        // Title
        String title = "ACCOUNT MANAGER";
        int titleWidth = mc.textRenderer.getWidth(title);

        // Title glow effect
        for (int i = 2; i >= 0; i--) {
            int glowAlpha = 40 - i * 10;
            context.drawText(mc.textRenderer, title,
                    centerX - titleWidth / 2 + i, headerY + i,
                    new Color(138, 43, 226, glowAlpha).getRGB(), false);
        }

        context.drawText(mc.textRenderer, title,
                centerX - titleWidth / 2, headerY,
                TEXT_PRIMARY.getRGB(), false);

        // Subtitle
        String subtitle = "Manage your Minecraft accounts";
        int subtitleWidth = mc.textRenderer.getWidth(subtitle);
        context.drawText(mc.textRenderer, subtitle,
                centerX - subtitleWidth / 2, headerY + 18,
                TEXT_SECONDARY.getRGB(), false);

        // Current account indicator
        String currentUser = "Current: " + mc.getSession().getUsername();
        int currentWidth = mc.textRenderer.getWidth(currentUser);

        drawRoundedRect(context, centerX - currentWidth / 2 - 10, headerY + 40,
                currentWidth + 20, 16, 8,
                new Color(34, 197, 94, 100));

        context.drawText(mc.textRenderer, currentUser,
                centerX - currentWidth / 2, headerY + 44,
                ACCENT_SUCCESS.getRGB(), false);
    }

    private void renderAccountList(DrawContext context, int mouseX, int mouseY) {
        int panelWidth = Math.min(800, this.width - 40);
        int panelX = (this.width - panelWidth) / 2;
        int listY = (this.height - Math.min(600, this.height - 40)) / 2 + 100;
        int listHeight = 300;

        // List background
        drawRoundedRect(context, panelX + 20, listY, panelWidth - 40, listHeight, 8, BG_TERTIARY);

        // List header
        context.drawText(mc.textRenderer, "Your Accounts (" + accounts.size() + ")",
                panelX + 35, listY + 10, TEXT_PRIMARY.getRGB(), false);

        // Account items
        hoveredAccount = -1;
        int itemHeight = 45;
        int startY = listY + 35;

        for (int i = 0; i < accounts.size(); i++) {
            AccountInfo account = accounts.get(i);
            int itemY = startY + i * itemHeight;

            if (itemY + itemHeight > listY + listHeight - 10) break;

            boolean isHovered = mouseX >= panelX + 25 && mouseX <= panelX + panelWidth - 25 &&
                    mouseY >= itemY && mouseY <= itemY + itemHeight - 5;

            if (isHovered) hoveredAccount = i;

            boolean isSelected = selectedAccount == i;
            boolean isCurrent = account.username.equals(mc.getSession().getUsername());

            // Item background
            Color itemBg = BG_TERTIARY;
            if (isCurrent) {
                itemBg = new Color(34, 197, 94, 60);
            } else if (isSelected) {
                itemBg = new Color(138, 43, 226, 60);
            } else if (isHovered) {
                itemBg = BG_HOVER;
            }

            drawRoundedRect(context, panelX + 30, itemY, panelWidth - 60, itemHeight - 5, 6, itemBg);

            // Current account indicator
            if (isCurrent) {
                context.fill(panelX + 35, itemY + 5, panelX + 37, itemY + itemHeight - 10,
                        ACCENT_SUCCESS.getRGB());
            }

            // Account type icon
            String typeIcon = account.type == AccountType.PREMIUM ? "⭐" : "👤";
            Color typeColor = account.type == AccountType.PREMIUM ? ACCENT_WARNING : TEXT_SECONDARY;
            context.drawText(mc.textRenderer, typeIcon, panelX + 45, itemY + 8,
                    typeColor.getRGB(), false);

            // Username
            context.drawText(mc.textRenderer, account.username, panelX + 65, itemY + 8,
                    TEXT_PRIMARY.getRGB(), false);

            // Status
            String status = isCurrent ? "ACTIVE" : (account.isOnline ? "ONLINE" : "OFFLINE");
            Color statusColor = isCurrent ? ACCENT_SUCCESS :
                    (account.isOnline ? ACCENT_SUCCESS : TEXT_DISABLED);
            context.drawText(mc.textRenderer, status, panelX + 65, itemY + 22,
                    statusColor.getRGB(), false);

            // Action buttons
            if (isHovered && !isCurrent) {
                // Use button
                drawRoundedRect(context, panelX + panelWidth - 120, itemY + 8, 50, 20, 4,
                        new Color(138, 43, 226, 150));
                context.drawText(mc.textRenderer, "Use",
                        panelX + panelWidth - 105, itemY + 13, TEXT_PRIMARY.getRGB(), false);

                // Delete button
                drawRoundedRect(context, panelX + panelWidth - 65, itemY + 8, 50, 20, 4,
                        new Color(239, 68, 68, 150));
                context.drawText(mc.textRenderer, "Delete",
                        panelX + panelWidth - 55, itemY + 13, TEXT_PRIMARY.getRGB(), false);
            }
        }

        // Empty state
        if (accounts.isEmpty()) {
            String emptyText = "No accounts added yet";
            int emptyWidth = mc.textRenderer.getWidth(emptyText);
            context.drawText(mc.textRenderer, emptyText,
                    panelX + (panelWidth - emptyWidth) / 2, listY + listHeight / 2,
                    TEXT_DISABLED.getRGB(), false);
        }
    }

    private void renderAddAccountSection(DrawContext context, int mouseX, int mouseY) {
        int panelWidth = Math.min(800, this.width - 40);
        int panelX = (this.width - panelWidth) / 2;
        int sectionY = (this.height - Math.min(600, this.height - 40)) / 2 + 420;

        // Section background
        drawRoundedRect(context, panelX + 20, sectionY, panelWidth - 40, 80, 8, BG_TERTIARY);

        // Section title
        context.drawText(mc.textRenderer, "Add New Account",
                panelX + 35, sectionY + 10, TEXT_PRIMARY.getRGB(), false);

        // Username field label
        context.drawText(mc.textRenderer, "Username:",
                panelX + 35, sectionY + 30, TEXT_SECONDARY.getRGB(), false);

        // Username field (rendered by Minecraft)
        usernameField.setX(panelX + 110);
        usernameField.setY(sectionY + 25);

        // Account type buttons
        boolean isCrackedSelected = !isAddingAccount || true; // Default to cracked

        // Cracked button
        Color crackedBg = isCrackedSelected ? new Color(138, 43, 226, 150) : BG_HOVER;
        drawRoundedRect(context, panelX + 330, sectionY + 25, 80, 20, 4, crackedBg);
        context.drawText(mc.textRenderer, "👤 Cracked",
                panelX + 340, sectionY + 30, TEXT_PRIMARY.getRGB(), false);

        // Premium button (disabled for demo)
        drawRoundedRect(context, panelX + 420, sectionY + 25, 80, 20, 4,
                new Color(100, 100, 100, 100));
        context.drawText(mc.textRenderer, "⭐ Premium",
                panelX + 428, sectionY + 30, TEXT_DISABLED.getRGB(), false);

        // Add button
        boolean canAdd = usernameField.getText() != null && !usernameField.getText().trim().isEmpty();
        Color addButtonColor = canAdd ? ACCENT_SUCCESS : new Color(100, 100, 100, 100);

        drawRoundedRect(context, panelX + 520, sectionY + 25, 60, 20, 4, addButtonColor);
        context.drawText(mc.textRenderer, "Add",
                panelX + 540, sectionY + 30,
                canAdd ? TEXT_PRIMARY.getRGB() : TEXT_DISABLED.getRGB(), false);
    }

    private void renderControlButtons(DrawContext context, int mouseX, int mouseY) {
        int panelWidth = Math.min(800, this.width - 40);
        int panelX = (this.width - panelWidth) / 2;
        int buttonY = (this.height - Math.min(600, this.height - 40)) / 2 + 520;

        // Back button
        drawRoundedRect(context, panelX + 20, buttonY, 80, 30, 6,
                new Color(100, 100, 100, 150));
        context.drawText(mc.textRenderer, "← Back",
                panelX + 35, buttonY + 10, TEXT_PRIMARY.getRGB(), false);

        // Refresh button
        drawRoundedRect(context, panelX + 120, buttonY, 80, 30, 6,
                new Color(138, 43, 226, 150));
        context.drawText(mc.textRenderer, "🔄 Refresh",
                panelX + 130, buttonY + 10, TEXT_PRIMARY.getRGB(), false);

        // Import/Export buttons (placeholder)
        drawRoundedRect(context, panelX + panelWidth - 180, buttonY, 80, 30, 6,
                new Color(251, 191, 36, 150));
        context.drawText(mc.textRenderer, "Import",
                panelX + panelWidth - 160, buttonY + 10, TEXT_PRIMARY.getRGB(), false);

        drawRoundedRect(context, panelX + panelWidth - 90, buttonY, 80, 30, 6,
                new Color(251, 191, 36, 150));
        context.drawText(mc.textRenderer, "Export",
                panelX + panelWidth - 70, buttonY + 10, TEXT_PRIMARY.getRGB(), false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle username field first
        usernameField.mouseClicked(mouseX, mouseY, button);

        if (button == 0) { // Left click
            int panelWidth = Math.min(800, this.width - 40);
            int panelX = (this.width - panelWidth) / 2;

            // Back button
            int buttonY = (this.height - Math.min(600, this.height - 40)) / 2 + 520;
            if (mouseX >= panelX + 20 && mouseX <= panelX + 100 &&
                    mouseY >= buttonY && mouseY <= buttonY + 30) {
                mc.setScreen(parent);
                return true;
            }

            // Add button
            int sectionY = (this.height - Math.min(600, this.height - 40)) / 2 + 420;
            if (mouseX >= panelX + 520 && mouseX <= panelX + 580 &&
                    mouseY >= sectionY + 25 && mouseY <= sectionY + 45) {
                addAccount();
                return true;
            }

            // Account list interactions
            if (hoveredAccount >= 0 && hoveredAccount < accounts.size()) {
                // Use button
                if (mouseX >= panelX + panelWidth - 120 && mouseX <= panelX + panelWidth - 70) {
                    useAccount(hoveredAccount);
                    return true;
                }
                // Delete button
                else if (mouseX >= panelX + panelWidth - 65 && mouseX <= panelX + panelWidth - 15) {
                    deleteAccount(hoveredAccount);
                    return true;
                }
                // Select account
                else {
                    selectedAccount = hoveredAccount;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC key
            mc.setScreen(parent);
            return true;
        }

        if (keyCode == 257 && usernameField.isFocused()) { // Enter key
            addAccount();
            return true;
        }

        return usernameField.keyPressed(keyCode, scanCode, modifiers) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return usernameField.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
    }

    private void addAccount() {
        String username = usernameField.getText();
        if (username != null && !username.trim().isEmpty()) {
            username = username.trim();

            // Check if account already exists
            final String finalUsername = username; // Make effectively final for lambda
            boolean exists = accounts.stream().anyMatch(acc -> acc.username.equalsIgnoreCase(finalUsername));
            if (!exists) {
                accounts.add(new AccountInfo(finalUsername, AccountType.CRACKED, false));
                usernameField.setText("");
                // In real implementation, save to file here
            }
        }
    }

    private void useAccount(int index) {
        if (index >= 0 && index < accounts.size()) {
            AccountInfo account = accounts.get(index);

            // Switch to the selected account (simplified - in real implementation use proper session switching)
            try {
                // Fixed Session constructor with UUID
                UUID accountUUID = new UUID(0, account.username.hashCode()); // Generate UUID from username
                Session newSession = new Session(account.username, accountUUID, "",
                        java.util.Optional.empty(), java.util.Optional.empty(), Session.AccountType.LEGACY);

                // Update current account status
                final String finalUsername = account.username; // Make effectively final
                accounts.forEach(acc -> acc.isOnline = acc.username.equals(finalUsername));

                // In real implementation, you'd need to properly change the session
                // This is just for demonstration
                System.out.println("Switched to account: " + account.username);

            } catch (Exception e) {
                System.err.println("Failed to switch account: " + e.getMessage());
            }
        }
    }

    private void deleteAccount(int index) {
        if (index >= 0 && index < accounts.size()) {
            AccountInfo account = accounts.get(index);
            if (!account.username.equals(mc.getSession().getUsername())) {
                accounts.remove(index);
                if (selectedAccount == index) {
                    selectedAccount = -1;
                }
                // In real implementation, save to file here
            }
        }
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height,
                                 int radius, Color color) {
        context.fill(x, y, x + width, y + height, color.getRGB());
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

    // Account data classes
    private static class AccountInfo {
        public String username;
        public AccountType type;
        public boolean isOnline;

        public AccountInfo(String username, AccountType type, boolean isOnline) {
            this.username = username;
            this.type = type;
            this.isOnline = isOnline;
        }
    }

    private enum AccountType {
        CRACKED, PREMIUM
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}