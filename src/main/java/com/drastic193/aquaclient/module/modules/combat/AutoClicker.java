// src/main/java/com/drastic193/aquaclient/module/modules/combat/AutoClicker.java
package com.drastic193.aquaclient.module.modules.combat;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

public class AutoClicker extends Module {
    public AutoClicker() {
        super("AutoClicker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled() && mc.options.attackKey.isPressed()) {
                Entity entity = mc.targetedEntity;
                if (entity instanceof LivingEntity) {
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        });
    }

    @Override
    public void onDisable() {
        // No cleanup
    }
}