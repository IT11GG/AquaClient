// src/main/java/com/drastic193/aquaclient/module/modules/visuals/XRay.java
package com.drastic193.aquaclient.module.modules.visuals;

import com.drastic193.aquaclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class XRay extends Module {
    public static final Set<Block> VISIBLE_BLOCKS = new HashSet<>();
    private static boolean enabled = false; // Статичний прапор для швидкого доступу

    static {
        // Додаємо блоки, які повинні бути видимі при XRay
        VISIBLE_BLOCKS.add(Blocks.DIAMOND_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        VISIBLE_BLOCKS.add(Blocks.GOLD_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        VISIBLE_BLOCKS.add(Blocks.IRON_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        VISIBLE_BLOCKS.add(Blocks.COAL_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        VISIBLE_BLOCKS.add(Blocks.COPPER_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        VISIBLE_BLOCKS.add(Blocks.EMERALD_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        VISIBLE_BLOCKS.add(Blocks.LAPIS_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        VISIBLE_BLOCKS.add(Blocks.REDSTONE_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        VISIBLE_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);
        VISIBLE_BLOCKS.add(Blocks.ANCIENT_DEBRIS);

        // Додаємо контейнери
        VISIBLE_BLOCKS.add(Blocks.CHEST);
        VISIBLE_BLOCKS.add(Blocks.ENDER_CHEST);
        VISIBLE_BLOCKS.add(Blocks.TRAPPED_CHEST);
        VISIBLE_BLOCKS.add(Blocks.BARREL);
        VISIBLE_BLOCKS.add(Blocks.SHULKER_BOX);

        // Додаємо спавнери
        VISIBLE_BLOCKS.add(Blocks.SPAWNER);

        // Додаємо портали
        VISIBLE_BLOCKS.add(Blocks.END_PORTAL_FRAME);
        VISIBLE_BLOCKS.add(Blocks.NETHER_PORTAL);
    }

    public XRay() {
        super("XRay", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        enabled = true;
        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }

    @Override
    public void onDisable() {
        enabled = false;
        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }

    // Статичний метод для швидкого доступу з мікіну
    public static boolean isXRayEnabled() {
        return enabled;
    }

    public static boolean isVisible(Block block) {
        return enabled && VISIBLE_BLOCKS.contains(block);
    }
}