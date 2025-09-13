// Reverted + improved GUI files
// 1) AquaMainMenu.java — restored original main-menu behaviour (Singleplayer/Multiplayer/Options/Quit)
// + added a small "Client Settings" button that opens a minimal settings screen.
// Background is plain black as you requested.


// File: src/main/java/com/drastic193/aquaclient/screen/AquaMainMenu.java
package com.drastic193.aquaclient.screen;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;


import java.awt.Color;


public class AquaMainMenu extends Screen {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private static final Color BG = new Color(0, 0, 0);


    public AquaMainMenu() {
        super(Text.literal("AquaClient"));
    }


    @Override
    protected void init() {
        super.init();
// Recreate vanilla-like main menu buttons (using builder API so constructor access is safe)
        int w = 200;
        int h = 20;
        int cx = this.width / 2;
        int cy = this.height / 4 + 48;


// Singleplayer
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"), btn -> {
            client.setScreen(new SelectWorldScreen(this));
        }).dimensions(cx - w / 2, cy, w, h).build());


// Multiplayer
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"), btn -> {
            client.setScreen(new MultiplayerScreen(this));
        }).dimensions(cx - w / 2, cy + 24, w, h).build());


// Options (vanilla options screen)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Options"), btn -> {
            client.setScreen(new OptionsScreen(this, client.options));
        }).dimensions(cx - w / 2, cy + 48, w, h).build());


// Client Settings (our custom screen, minimal)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Client Settings"), btn -> {
            client.setScreen(new com.drastic193.aquaclient.screen.AquaSettingsScreen(this));
        }).dimensions(cx - w / 2, cy + 72, w, h).build());


// Quit
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Quit"), btn -> {
            client.scheduleStop();
        }).dimensions(cx - w / 2, cy + 96, w, h).build());
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
// Plain black background per request
        context.fill(0, 0, width, height, BG.getRGB());


// Draw centered panel with title
        int pw = 420, ph = 160;
        int px = (width - pw) / 2;
        int py = (height - ph) / 2;
        context.fill(px, py, px + pw, py + ph, 0x0A000000);


        context.drawText(client.textRenderer, Text.literal("AquaClient"), px + 16, py + 14, 0xFFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }
}