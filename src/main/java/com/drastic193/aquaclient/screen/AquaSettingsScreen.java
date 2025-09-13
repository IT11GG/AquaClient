

// 3) Minimal AquaSettingsScreen.java — opens from main menu's "Client Settings" button
// File: src/main/java/com/drastic193/aquaclient/screen/AquaSettingsScreen.java
package com.drastic193.aquaclient.screen;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;


import java.awt.Color;


public class AquaSettingsScreen extends Screen {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Screen parent;
    private static final Color BG = new Color(0, 0, 0, 255);


    public AquaSettingsScreen(Screen parent) {
        super(Text.literal("Client Settings"));
        this.parent = parent;
    }


    @Override
    protected void init() {
        super.init();
        int w = 200, h = 20;
        int cx = this.width / 2;
        int cy = this.height / 2;
// placeholder settings button(s)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), btn -> mc.setScreen(parent)).dimensions(cx - w/2, cy + 40, w, h).build());
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
// black background as requested
        context.fill(0, 0, width, height, BG.getRGB());
        context.drawText(mc.textRenderer, Text.literal("Client Settings (placeholder)"), 20, 20, 0xFFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }
}