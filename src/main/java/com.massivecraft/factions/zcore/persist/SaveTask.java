/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.zcore.MPlugin;

public class SaveTask
        implements Runnable {
    private static boolean running = false;
    MPlugin p;

    public SaveTask(MPlugin p) {
        this.p = p;
    }

    @Override
    public void run() {
        if (!this.p.getAutoSave() || running) {
            return;
        }
        running = true;
        this.p.preAutoSave();
        EM.saveAllToDisc();
        this.p.postAutoSave();
        running = false;
    }
}

