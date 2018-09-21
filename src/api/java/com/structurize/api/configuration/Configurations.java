package com.structurize.api.configuration;

import net.minecraftforge.common.config.Config;

import static com.structurize.api.util.constant.Constants.*;

@Config(modid = MOD_ID)
public class Configurations
{
    @Config.Comment("Should local schematics be allowed?")
    public static boolean allowLocalSchematics = true;

    @Config.Comment("Max amount of schematics to be cached on the server")
    public static int maxCachedSchematics = 100;

    @Config.Comment("Max amount of changes cached to be able to undo")
    public static int maxCachedChanges = 10;

    @Config.Comment("Max world operations per tick (Max blocks to place, remove or replace)")
    public static int maxOperationsPerTick = 100;

    @Config.Comment("List of repositories of schematics to load from (use \"local:\" for loading schematics from local files")
    public static String[] repositoriesUrls = new String[]
    {
        "https://raw.githubusercontent.com/Nightenom/minecolonies-schematics-test/",
        "local:structurize"
    };
}
