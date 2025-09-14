// File: src/main/java/com/drastic193/aquaclient/module/ModuleManager.java
package com.drastic193.aquaclient.module;

import com.drastic193.aquaclient.module.modules.combat.KillAura;
import com.drastic193.aquaclient.module.modules.combat.AutoClicker;
import com.drastic193.aquaclient.module.modules.combat.Aim;
import com.drastic193.aquaclient.module.modules.visuals.ESP;
import com.drastic193.aquaclient.module.modules.visuals.XRay;
import com.drastic193.aquaclient.module.modules.visuals.StorageESP;
import com.drastic193.aquaclient.module.modules.movement.Fly;
import com.drastic193.aquaclient.module.modules.movement.Speed;
import com.drastic193.aquaclient.module.modules.movement.Sprint;
import com.drastic193.aquaclient.module.modules.movement.Spider;
import com.drastic193.aquaclient.module.modules.movement.Noclip;
import com.drastic193.aquaclient.module.modules.misc.Disabler;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static List<Module> modules = new ArrayList<>();

    public static void init() {
        // Combat
        modules.add(new KillAura());
        modules.add(new AutoClicker());
        modules.add(new Aim());

        // Visuals
        modules.add(new ESP());
        modules.add(new XRay());
        modules.add(new StorageESP());

        // Movement
        modules.add(new Fly());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Spider());
        modules.add(new Noclip());

        // Misc
        modules.add(new Disabler());

        System.out.println("ModuleManager initialized with " + modules.size() + " modules");
    }

    public static List<Module> getModulesByCategory(Module.Category category) {
        List<Module> categoryModules = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                categoryModules.add(module);
            }
        }
        return categoryModules;
    }

    public static Module getModule(String name) {
        return modules.stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return modules.stream()
                .filter(module -> module.getClass() == clazz)
                .findFirst()
                .orElse(null);
    }

    public static List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .collect(java.util.stream.Collectors.toList());
    }

    public static void disableAll() {
        modules.forEach(module -> module.setEnabled(false));
    }

    public static void onTick() {
        modules.stream()
                .filter(Module::isEnabled)
                .forEach(Module::onTick);
    }

    public static void onRender() {
        modules.stream()
                .filter(Module::isEnabled)
                .forEach(module -> {
                    // Call render method if module has one
                });
    }
}