// src/main/java/com/drastic193/aquaclient/module/Module.java
package com.drastic193.aquaclient.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    protected MinecraftClient mc = MinecraftClient.getInstance();
    private String name;
    private boolean enabled;
    private Category category;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public void onTick() {
        // Override if needed
    }

    public enum Category {
        COMBAT, VISUALS, MOVEMENT, MISC
    }
}