// src/main/java/com/drastic193/aquaclient/module/modules/movement/Spider.java
package com.drastic193.aquaclient.module.modules.movement;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Spider extends Module {
    public Spider() {
        super("Spider", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled() && mc.player.horizontalCollision) {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.5, mc.player.getVelocity().z); // Climb walls
            }
        });
    }

    @Override
    public void onDisable() {
        // No cleanup
    }
}