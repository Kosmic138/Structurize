package com.ldtteam.structurize.repomanagement.repostructure;

import java.util.List;

public class DefaultHead extends Head {
    /**
     * Registry of types
     */
    private List<String> registryTypes;

    public DefaultHead(final String id, final String name) {
        super(id, name);
    }

    public DefaultHead(final Head head) {
        super(head.getId(), head.getName());
        super.types = head.getTypes();
    }

    public void setRegistry(final List<String> registry) {
        this.registryTypes = registry;
    }

    public List<String> getRegistry() {
        return registryTypes;
    }
}