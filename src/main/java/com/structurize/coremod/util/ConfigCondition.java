package com.structurize.coremod.util;

import com.google.gson.JsonObject;
import com.structurize.api.configuration.Configurations;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * Custom config conditions!
 */
public class ConfigCondition implements IConditionFactory
{

    @Override
    public BooleanSupplier parse(final JsonContext context, final JsonObject json)
    {
        if(!Configurations.gameplay.decorativeBlocksEnabled)
        {
            return () -> false;
        }

        return () -> true;
    }
}
