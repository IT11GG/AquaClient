// src/main/java/com/drastic193/aquaclient/mixin/BlockMixin.java
package com.drastic193.aquaclient.mixin;

import com.drastic193.aquaclient.module.modules.visuals.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction facing, BlockPos neighborPos, CallbackInfoReturnable<Boolean> info) {
        // Перевіряємо чи увімкнений XRay
        if (XRay.isXRayEnabled()) {
            Block block = state.getBlock();
            // Якщо блок у списку видимих - показуємо його
            if (XRay.isVisible(block)) {
                info.setReturnValue(true);
            } else {
                // Інакше приховуємо блок (не рендеримо його сторони)
                info.setReturnValue(false);
            }
        }
        // Якщо XRay вимкнений, залишаємо стандартну логіку (не перериваємо виконання)
    }
}