// src/main/java/com/drastic193/aquaclient/module/modules/visuals/ESP.java
package com.drastic193.aquaclient.module.modules.visuals;

import com.drastic193.aquaclient.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

public class ESP extends Module {
    public ESP() {
        super("ESP", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider buffer = context.consumers();
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof LivingEntity && entity != mc.player) {
                    Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
                    // Draw ESP box (simplified, use RenderLayer for lines)
                    // Implement drawing here
                }
            }
        });
    }

    @Override
    public void onDisable() {
        // No cleanup
    }
}