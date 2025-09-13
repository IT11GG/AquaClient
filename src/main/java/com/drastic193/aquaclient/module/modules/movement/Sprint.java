// src/main/java/com/drastic193/aquaclient/module/modules/movement/Sprint.java
package com.drastic193.aquaclient.module.modules.movement;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled() && mc.player.input.movementForward > 0) {
                mc.player.setSprinting(true);
            }
        });
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
    }
}