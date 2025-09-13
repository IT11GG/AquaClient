// src/main/java/com/drastic193/aquaclient/module/modules/movement/Fly.java
package com.drastic193.aquaclient.module.modules.movement;

import com.drastic193.aquaclient.module.Module;

public class Fly extends Module {
    public Fly() {
        super("Fly", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = false;
        }
    }

    @Override
    public void onTick() {
        if (isEnabled() && mc.player != null) {
            mc.player.getAbilities().flying = true;
        }
    }
}