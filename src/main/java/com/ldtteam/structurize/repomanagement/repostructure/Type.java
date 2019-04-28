package com.structurize.coremod.repomanagement.repostructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for carrying type and all of its files
 */
public class Type
{
    // Info variables
    private final String id;
    private       String name;
    private       String origin;
    private       String fileExtension;

    /**
     * Path to local root
     */
    private       String localPath = "";

    /** 
     * List of files, file name | file origin == style name
     */
    protected List<Trinuple<String, String, String>> files = new ArrayList<Trinuple<String, String, String>>();

    /**
     * Creates new type
     */
    public Type(
        final String id,
        final String name,
        final String origin
        )
    {
        this.id = id;
        this.name = name;
        this.origin = origin;
    }

    // Subtype management

    public void addFile(final String fileId, final String fileName, final String origin)
    {
        files.add(new Trinuple<String, String, String>(fileId, fileName, origin));
    }

    public void addFile(final Trinuple<String, String, String> file)
    {
        files.add(file);
    }

    public void setFile(final int i, final Trinuple<String, String, String> file)
    {
        files.set(i, file);
    }

    /**
     * Returns Trinuple<String, String, String> with file id, name and origin if found, else null
     */
    public Trinuple<String, String, String> getFileById(final String fileId)
    {
        return files.stream().filter(file -> file.getFirst().equals(fileId)).findFirst().orElse(null);
    }

    /**
     * Returns Trinuple<String, String, String> with file id, name and origin if found, else null
     */
    public Trinuple<String, String, String> getFileByName(final String fileName)
    {
        return files.stream().filter(file -> file.getSecond().equals(fileName)).findFirst().orElse(null);
    }

    public List<Trinuple<String, String, String>> getFiles()
    {
        return files;
    }
    
    public String getFilePathById(final String fileId)
    {
        final Trinuple<String, String, String> file = files.stream().filter(fileCheck -> fileCheck.getFirst().equals(fileId)).findFirst().orElse(null);

        if (file == null)
        {
            return null;
        }
        return localPath.replace(origin, file.getThird()) + "/" + file.getFirst() + fileExtension;
    }

    // Casual setters

    public void setName(final String name)
    {
        this.name = name;
    }
    public void setOrigin(final String origin)
    {
        this.origin = origin;
    }
    public void setLocalPath(final String localPath)
    {
        this.localPath = localPath;
    }
    public void setFileExtension(final String fileExtension)
    {
        this.fileExtension = fileExtension;
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
    public String getOrigin()
    {
        return origin;
    }
    public String getFileExtension()
    {
        return fileExtension;
    }
    public String getLocalPath()
    {
        return localPath;
    }
}