package com.ldtteam.structurize.creativetab;

import com.ldtteam.structurize.api.util.constant.Constants;
import com.ldtteam.structurize.blocks.ModBlocks;
import com.ldtteam.structurize.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * Class used to handle the creativeTab of structurize.
 */
public final class ModCreativeTabs
{
    public static final ItemGroup STRUCTURIZE = new ItemGroup(Constants.MOD_ID)
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModItems.buildTool);
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }
    };

    public static final ItemGroup SHINGLES = new ItemGroup(Constants.MOD_ID + ".shingles")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModBlocks.getShingles().stream().findFirst().orElse(null));
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }
    };

    public static final ItemGroup TIMBER_FRAMES = new ItemGroup(Constants.MOD_ID + ".timber_frames")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModBlocks.getTimberFrames().stream().findFirst().orElse(null));
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }
    };

    /**
     * Private constructor to hide the implicit one.
     */
    private ModCreativeTabs()
    {
        /*
         * Intentionally left empty.
         */
    }
}
