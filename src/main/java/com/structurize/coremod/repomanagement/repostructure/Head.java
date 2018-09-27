package com.structurize.coremod.repomanagement.repostructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for carrying head and all of its types
 */
public class Head
{
    // Info variables
    private final String id;
    private       String name;

    /**
     * Path to local root
     */
    private       String localPath = "";

    /** 
     * List of subtype
     */
    protected List<Type> types = new ArrayList<Type>();

    /**
     * Creates new head
     */
    public Head(final String id, final String name)
    {
        this.id = id;
        this.name = name;
    }

    // Subtype management

    public void addType(final Type type)
    {
        types.add(type);
    }

    public void setType(final int i, final Type type)
    {
        types.set(i, type);
    }

    /**
     * Returns found Type or null
     */
    public Type getTypeById(final String typeId)
    {
        for (Type type : types)
        {
            if (type.getId().equals(typeId))
            {
                return type;
            }
        }
        return null;
    }

    public List<Type> getTypes()
    {
        return types;
    }

    // Casual setters

    public void setName(final String name)
    {
        this.name = name;
    }
    public void setLocalPath(final String localPath)
    {
        this.localPath = localPath;
    }

    // Casual getters

    public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String getLocalPath()
    {
        return localPath;
    }
}