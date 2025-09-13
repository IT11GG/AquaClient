// src/main/java/com/drastic193/aquaclient/module/modules/movement/Speed.java
package com.drastic193.aquaclient.module.modules.movement;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Speed extends Module {
    public Speed() {
        super("Speed", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled() && mc.player.input.movementForward != 0) {
                mc.player.setVelocity(mc.player.getVelocity().multiply(2.0, 1.0, 2.0)); // Simple speed boost
            }
        });
    }

    @Override
    public void onDisable() {
        // No cleanup
    }
}