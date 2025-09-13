// src/main/java/com/drastic193/aquaclient/module/modules/misc/Disabler.java
package com.drastic193.aquaclient.module.modules.misc;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Disabler extends Module {
    public Disabler() {
        super("Disabler", Category.MISC);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled()) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            }
        });
    }

    @Override
    public void onDisable() {
        // No cleanup
    }
}