// src/main/java/com/drastic193/aquaclient/module/modules/movement/Noclip.java
package com.drastic193.aquaclient.module.modules.movement;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Noclip extends Module {
    public Noclip() {
        super("Noclip", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled()) {
                mc.player.noClip = true;
                mc.player.fallDistance = 0;
            }
        });
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }
}