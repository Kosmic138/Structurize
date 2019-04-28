package com.structurize.coremod.repomanagement;

import java.util.ArrayList;
import java.util.List;

import com.structurize.coremod.repomanagement.repostructure.Style;

import net.minecraft.client.Minecraft;

public class Styles
{
    private static List<Style> styles = new ArrayList<Style>();

    /**
     * Local folder for downloading
     */
    private static String downloadFolder = "";

    /**
     * Private constructor to hide implicit public one.
     */
    protected Styles()
    {
        /*
         * Intentionally left empty.
         */
    }

    public void addStyle(final Style style)
    {
        styles.add(style);
    }

    public void setStyle(final int i, final Style style)
    {
        styles.set(i, style);
    }

    public Style getStyleById(final String id)
    {
        return styles.stream().filter(style -> style.getId().equals(id)).findFirst().orElse(null);
    }

    public Style getStyleByName(final String name)
    {
        final Style result = styles.stream().filter(style -> style.getName().equals(name)).findFirst().orElse(null);
        if (result == null)
        {
            return styles.stream().filter(style -> style.getShortame().equals(name)).findFirst().orElse(null);
        }
        return result;
    }

    public List<Style> getStyles()
    {
        return styles;
    }

    public void setDownloadFolder(final String downloadFolder)
    {
        this.downloadFolder = downloadFolder;
    }

    public String getDownloadFolder()
    {
        return Minecraft.getMinecraft().gameDir + "/" + downloadFolder;
    }
}