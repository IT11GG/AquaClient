// src/main/java/com/drastic193/aquaclient/client/AquaClientClient.java
package com.drastic193.aquaclient.client;

import com.drastic193.aquaclient.gui.AquaClickGuiScreen;
import com.drastic193.aquaclient.module.Module;
import com.drastic193.aquaclient.module.ModuleManager;
import com.drastic193.aquaclient.screen.AquaMainMenu;
import com.drastic193.aquaclient.screen.AquaMultiplayerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AquaClientClient implements ClientModInitializer {
    public static final String MOD_ID = "aquaclient";
    public static AquaClientClient INSTANCE;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ModuleManager.init();

        // Replace main menu with Aqua style
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                client.setScreen(new AquaMainMenu());
            }
            // Replace multiplayer screen with Aqua style
            else if (screen instanceof MultiplayerScreen && !(screen instanceof AquaMultiplayerScreen)) {
                client.setScreen(new AquaMultiplayerScreen(((MultiplayerScreen) screen).parent));
            }
        });

        // GUI keybind (Right Shift)
        KeyBinding openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aquaclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.aquaclient.main"
        ));

        // Client tick handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle GUI opening
            while (openGuiKey.wasPressed()) {
                client.setScreen(new AquaClickGuiScreen());
            }

            // Update modules
            for (Module module : ModuleManager.modules) {
                module.onTick();
            }
        });

        System.out.println("AquaClient Modern Edition initialized successfully!");
    }
}