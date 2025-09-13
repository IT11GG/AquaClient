// src/main/java/com/drastic193/aquaclient/module/modules/combat/Aim.java
package com.drastic193.aquaclient.module.modules.combat;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class Aim extends Module {
    public Aim() {
        super("Aim", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mc.player != null && isEnabled()) {
                Entity closest = null;
                double minDist = Double.MAX_VALUE;
                for (Entity entity : mc.world.getEntities()) {
                    if (entity instanceof LivingEntity && entity != mc.player) {
                        double dist = mc.player.getPos().distanceTo(entity.getPos());
                        if (dist < minDist) {
                            minDist = dist;
                            closest = entity;
                        }
                    }
                }
                if (closest != null) {
                    Vec3d dir = closest.getEyePos().subtract(mc.player.getEyePos()).normalize();
                    mc.player.setYaw((float) Math.toDegrees(Math.atan2(dir.z, dir.x)) - 90f);
                    mc.player.setPitch((float) -Math.toDegrees(Math.asin(dir.y / dir.length())));
                }
            }
        });
    }

    @Override
    public void onDisable() {
        // No cleanup
    }
}