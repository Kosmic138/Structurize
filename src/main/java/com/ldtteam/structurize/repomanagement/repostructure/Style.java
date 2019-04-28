package com.ldtteam.structurize.repomanagement.repostructure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

/**
 * Class for carrying style and all of its heads
 * <p>
 * Also carries every possible information to display it
 * <p>
 * Is the root element of structurize's styles list
 */
public class Style {
    // Info variables
    private final String id;
    private final String name;
    private final String description;
    private final String image;
    private final ResourceLocation origin;
    private final String joke;
    private final String author;
    private final String shortname;

    /**
     * Path to local root
     */
    private String localPath = "";

    /**
     * ID of base style
     */
    private final String base;

    /**
     * List of subtype
     */
    protected List<Head> heads = new ArrayList<Head>();

    /**
     * Creates new style
     */
    public Style(final String id, final String name, final String description, final String image,
            final ResourceLocation origin, final String joke, final String author, final String base,
            final String shortname) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.origin = origin;
        this.joke = joke;
        this.author = author;
        this.base = base;
        this.shortname = shortname;
    }

    // Subtype management

    public void addHead(final Head head) {
        heads.add(head);
    }

    public void setHead(final int i, final Head head) {
        heads.set(i, head);
    }

    /**
     * @return found Head or null
     */
    public Head getHeadById(final String headId) {
        for (Head head : heads) {
            if (head.getId().equals(headId)) {
                return head;
            }
        }
        return null;
    }

    public List<Head> getHeads() {
        return heads;
    }

    // Casual setters

    public void setLocalPath(final String localPath) {
        this.localPath = localPath;
    }

    // Casual getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public ResourceLocation getOrigin() {
        return origin;
    }

    public String getJoke() {
        return joke;
    }

    public String getAuthor() {
        return author;
    }

    public String getBase() {
        return base;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getShortame() {
        return shortname.isEmpty() ? name : shortname;
    }
}