package com.ldtteam.structurize.repomanagement.repostructure;

import java.util.List;

import net.minecraft.util.ResourceLocation;

public class DefaultStyle extends Style {

    /**
     * Registry of heads
     */
    private List<String> registryHeads;

    public DefaultStyle(final String id, final String name, final String description, final String image,
            final ResourceLocation origin, final String joke, final String author, final String base,
            final String shortname) {
        super(id, name, description, image, origin, joke, author, base, shortname);
    }

    public DefaultStyle(final Style style) {
        super(style.getId(), style.getName(), style.getDescription(), style.getImage(), style.getOrigin(),
                style.getJoke(), style.getAuthor(), style.getBase(), style.getShortame());
        super.heads = style.getHeads();
    }

    public void setRegistry(final List<String> registry) {
        this.registryHeads = registry;
    }

    public List<String> getRegistry() {
        return registryHeads;
    }
}