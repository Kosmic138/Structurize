package com.structurize.coremod.repomanagement.repostructure;

import java.util.List;

public class DefaultType extends Type
{
    /**
     * Registry of files 
     */
    private List<String> registryFiles;

    public DefaultType(
        final String id,
        final String name,
        final String origin,
        final String fileExtension
        )
    {
        super(id, name, origin);
        super.setFileExtension(fileExtension);
    }

    public DefaultType(final Type type, final String fileExtension)
    {
        super(
            type.getId(),
            type.getName(),
            type.getOrigin()
        );
        super.setFileExtension(fileExtension);
        super.files = type.getFiles();
    }

    public void setRegistry(final List<String> registry)
    {
        this.registryFiles = registry;
    }

    public List<String> getRegistry()
    {
        return registryFiles;
    }
}