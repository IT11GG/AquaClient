// src/main/java/com/drastic193/aquaclient/module/modules/visuals/XRay.java
package com.drastic193.aquaclient.module.modules.visuals;

import com.drastic193.aquaclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class XRay extends Module {
    public static final Set<Block> VISIBLE_BLOCKS = new HashSet<>();

    static {
        VISIBLE_BLOCKS.add(Blocks.DIAMOND_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        VISIBLE_BLOCKS.add(Blocks.GOLD_ORE);
        VISIBLE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
    }

    public XRay() {
        super("XRay", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
    }

    public static boolean isVisible(Block block) {
        return VISIBLE_BLOCKS.contains(block);
    }
}