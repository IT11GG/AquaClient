
// - Opens with RSHIFT per existing keybind (no change needed in client initializer)
// - Pseudo-blur background (good-looking dim/softening; proper framebuffer blur requires extra shader/framebuffer work,
//   which I can add if you want but it needs careful testing). The pseudo-blur affects only the background; GUI elements are drawn on top.
// - GUI clamps to screen bounds so it never goes off-screen and supports mouse-wheel scrolling for the modules list.
// - Uses project's ModuleManager.modules list and Module API (getName, isEnabled, toggle, getCategory).

// File: src/main/java/com/drastic193/aquaclient/gui/AquaClickGuiScreen.java
package com.drastic193.aquaclient.gui;

import com.drastic193.aquaclient.module.Module;
import com.drastic193.aquaclient.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.List;

public class AquaClickGuiScreen extends Screen {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Layout / sizing — will be clamped to screen
    private int guiX = 40;
    private int guiY = 40;
    private int guiWidth = 720;
    private int guiHeight = 420;

    private int minWidth = 260;
    private int minHeight = 160;

    // Hover/scroll
    private int hoveredIndex = -1;
    private int scrollOffset = 0;

    // Colors (Meteor-like palette)
    private static final Color BACKGROUND_COLOR = new Color(12, 14, 17, 220);
    private static final Color ACCENT = new Color(72, 149, 255, 220);
    private static final Color ACCENT_DARK = new Color(47, 100, 180, 200);
    private static final Color ELEMENT_BG = new Color(20, 24, 28, 200);
    private static final Color TEXT_COLOR = new Color(220, 225, 230);
    private static final Color HOVER_COLOR = new Color(255, 255, 255, 20);

    public AquaClickGuiScreen() {
        super(Text.literal("Aqua ClickGUI"));
    }

    @Override
    protected void init() {
        super.init();
        // clamp initial size to screen (so GUI never goes off-screen)
        int margin = 28;
        guiWidth = Math.min(guiWidth, Math.max(minWidth, this.width - margin));
        guiHeight = Math.min(guiHeight, Math.max(minHeight, this.height - margin));
        guiX = Math.max(10, (this.width - guiWidth) / 2);
        guiY = Math.max(10, (this.height - guiHeight) / 2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // --- PSEUDO-BLUR / dim background ---
        // Real Gaussian blur requires framebuffer/shader — here we do a tasteful layered dim that visually softens
        // the game behind the GUI without blurring UI elements.
        int w = this.width;
        int h = this.height;
        // layered translucent fills to create a softening effect
        context.fill(0, 0, w, h, 0xC0000000); // darken
        context.fill(0, 0, w, h, 0x10000000);
        context.fill(0, 0, w, h, 0x08000000);

        // Ensure GUI is within bounds (in case window resized while GUI opened)
        guiWidth = Math.min(guiWidth, this.width - 20);
        guiHeight = Math.min(guiHeight, this.height - 20);
        guiX = Math.max(10, Math.min(guiX, this.width - guiWidth - 10));
        guiY = Math.max(10, Math.min(guiY, this.height - guiHeight - 10));

        // Main card
        fillRoundedPanel(context, guiX, guiY, guiWidth, guiHeight, BACKGROUND_COLOR.getRGB());

        // Header bar
        int headerH = 34;
        context.fill(guiX, guiY, guiX + guiWidth, guiY + headerH, ACCENT.getRGB());
        context.drawText(mc.textRenderer, Text.literal("AquaClient — Modules"), guiX + 12, guiY + 8, TEXT_COLOR.getRGB(), false);

        // Left column: modules list
        int leftW = Math.max(200, Math.min(320, guiWidth / 3));
        int padding = 12;
        int leftX = guiX + padding;
        int leftY = guiY + headerH + padding;
        int leftH = guiHeight - headerH - padding * 2;

        drawPanel(context, leftX - 6, leftY - 6, leftW + 12, leftH + 12, ELEMENT_BG.getRGB());
        renderModulesList(context, leftX, leftY, leftW, leftH, mouseX, mouseY);

        // Right column: module details
        int rightX = guiX + leftW + padding * 2;
        int rightW = guiWidth - leftW - padding * 4;
        int rightY = leftY;
        int rightH = leftH;
        drawPanel(context, rightX - 6, rightY - 6, rightW + 12, rightH + 12, ELEMENT_BG.getRGB());
        renderModuleDetails(context, rightX, rightY, rightW, rightH);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderModulesList(DrawContext context, int x, int y, int w, int h, int mouseX, int mouseY) {
        List<Module> modules = ModuleManager.modules; // project uses public static list
        int rowH = 24;
        int visible = Math.max(1, h / rowH);

        // clamp scroll offset
        int maxOffset = Math.max(0, modules.size() - visible);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        if (scrollOffset < 0) scrollOffset = 0;

        // reset hovered index each frame
        hoveredIndex = -1;

        for (int i = 0; i < visible; i++) {
            int idx = i + scrollOffset;
            if (idx >= modules.size()) break;
            int ry = y + i * rowH;
            Module m = modules.get(idx);

            // Background row
            int bgColor = 0x00000000;
            context.fill(x, ry, x + w, ry + rowH - 4, bgColor);

            if (m.isEnabled()) {
                context.fill(x - 8, ry + 4, x - 4, ry + rowH - 8, ACCENT.getRGB());
            }

            int textX = x + 6;
            int textY = ry + 6;
            int nameColor = m.isEnabled() ? ACCENT.getRGB() : TEXT_COLOR.getRGB();
            context.drawText(mc.textRenderer, Text.literal(m.getName()), textX, textY, nameColor, false);

            String state = m.isEnabled() ? "ON" : "OFF";
            int stateW = mc.textRenderer.getWidth(state);
            context.drawText(mc.textRenderer, Text.literal(state), x + w - stateW - 6, textY, TEXT_COLOR.getRGB(), false);

            // Hover detection
            if (mouseX >= x && mouseX <= x + w && mouseY >= ry && mouseY <= ry + rowH - 4) {
                context.fill(x, ry, x + w, ry + rowH - 4, HOVER_COLOR.getRGB());
                hoveredIndex = idx;
            }
        }
    }

    private void renderModuleDetails(DrawContext context, int x, int y, int w, int h) {
        List<Module> modules = ModuleManager.modules;
        Module current = (hoveredIndex >= 0 && hoveredIndex < modules.size()) ? modules.get(hoveredIndex) : null;
        if (current == null) {
            context.drawText(mc.textRenderer, Text.literal("Select a module to see details"), x + 12, y + 12, TEXT_COLOR.getRGB(), false);
            return;
        }

        context.drawText(mc.textRenderer, Text.literal(current.getName()), x + 12, y + 12, ACCENT.getRGB(), false);

        // Modules in this project do not have description strings; show category as information
        String desc = "Category: " + current.getCategory().name();
        drawWrappedText(context, desc, x + 12, y + 36, w - 24, TEXT_COLOR.getRGB());

        int btnW = 120, btnH = 26;
        int btnX = x + 12;
        int btnY = y + h - btnH - 18;
        drawButton(context, btnX, btnY, btnW, btnH, current.isEnabled() ? "Disable" : "Enable", current.isEnabled());
    }

    private void drawButton(DrawContext context, int x, int y, int w, int h, String text, boolean active) {
        int bg = active ? ACCENT_DARK.getRGB() : ACCENT.getRGB();
        context.fill(x, y, x + w, y + h, bg);
        int tx = x + (w - mc.textRenderer.getWidth(text)) / 2;
        int ty = y + (h - 8) / 2 + 1;
        context.drawText(mc.textRenderer, Text.literal(text), tx, ty, TEXT_COLOR.getRGB(), false);
    }

    private void drawWrappedText(DrawContext context, String text, int x, int y, int wrapWidth, int color) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineH = 10;
        int curY = y;
        for (String w : words) {
            String test = line.length() == 0 ? w : line + " " + w;
            if (mc.textRenderer.getWidth(test) > wrapWidth) {
                context.drawText(mc.textRenderer, Text.literal(line.toString()), x, curY, color, false);
                line = new StringBuilder(w);
                curY += lineH;
            } else {
                if (line.length() > 0) line.append(' ');
                line.append(w);
            }
        }
        if (line.length() > 0) context.drawText(mc.textRenderer, Text.literal(line.toString()), x, curY, color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredIndex >= 0 && hoveredIndex < ModuleManager.modules.size()) {
            Module m = ModuleManager.modules.get(hoveredIndex);
            m.toggle();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // invert amount to match normal scrolling direction
        int scrollDelta = (int) Math.signum(amount);
        scrollOffset = scrollOffset - scrollDelta;
        if (scrollOffset < 0) scrollOffset = 0;
        int visible = Math.max(1, (guiHeight - 50) / 24);
        int maxOffset = Math.max(0, ModuleManager.modules.size() - visible);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC closes GUI
        if (keyCode == 256) {
            mc.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // --- Small helper draws ---
    private void fillRoundedPanel(DrawContext context, int x, int y, int w, int h, int color) {
        context.fill(x + 6, y, x + w - 6, y + h, color);
        context.fill(x, y + 6, x + w, y + h - 6, color);
    }

    private void drawPanel(DrawContext context, int x, int y, int w, int h, int color) {
        context.fill(x, y, x + w, y + h, color);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}


