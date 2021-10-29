package com.massivecraft.factions.util.reflection;

import java.util.LinkedList;
import java.util.List;

public class ParamBuilder {
    private List<RParam> parameters = new LinkedList<>();

    public List<RParam> getParameters() {
        return this.parameters;
    }

    private int validObjectCount = 0;

    public int getValidObjectCount() {
        return this.validObjectCount;
    }

    public void setValidObjectCount(int validObjectCount) {
        this.validObjectCount = validObjectCount;
    }

    public ParamBuilder(Class<?> clazz, Object obj) {
        add(clazz, obj);
    }

    public ParamBuilder add(RParam param) {
        this.parameters.add(param);
        if (param != null)
            this.validObjectCount++;
        return this;
    }

    public ParamBuilder add(Class<?> clazz, Object param) {
        add(new RParam(clazz, param));
        return this;
    }

    public ParamBuilder() {}
}
