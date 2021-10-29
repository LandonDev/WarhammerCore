/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.zcore.persist;

import java.util.UUID;

public abstract class Entity {
    private transient String id = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
        if (this instanceof PlayerEntity && this.id.length() > 16 && this.id.contains("-")) {
            try {
                UUID found = UUID.fromString(this.id);
                ((PlayerEntity) this).setCachedUUID(found);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public boolean shouldBeSaved() {
        return true;
    }

    public void attach() {
        EM.attach(this);
    }

    public void detach() {
        EM.detach(this);
    }

    public boolean attached() {
        return EM.attached(this);
    }

    public boolean detached() {
        return EM.detached(this);
    }

    public void preDetach() {
    }

    public void postDetach() {
    }
}

