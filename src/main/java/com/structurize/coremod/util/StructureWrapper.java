package com.structurize.coremod.util;

import com.structurize.api.util.*;
import com.structurize.coremod.blocks.ModBlocks;
import com.structurize.coremod.management.Manager;
import com.structurize.coremod.placementhandlers.IPlacementHandler;
import com.structurize.coremod.placementhandlers.PlacementHandlers;
import com.structurize.structures.helpers.StructureProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Interface for using the structure codebase.
 */
public final class StructureWrapper
{
    /**
     * The position we use as our uninitialized value.
     */
    private static final BlockPos NULL_POS = new BlockPos(-1, -1, -1);

    /**
     * The Structure position we are at. Defaulted to NULL_POS.
     */
    private final BlockPos.MutableBlockPos progressPos = new BlockPos.MutableBlockPos(-1, -1, -1);
    /**
     * The minecraft world this struture is displayed in.
     */
    private final World          world;
    /**
     * The structure this structure comes from.
     */
    private final StructureProxy structure;
    /**
     * The name this structure has.
     */
    private final String         name;
    /**
     * The anchor position this structure will be
     * placed on in the minecraft world.
     */
    private       BlockPos       position;

    /**
     * Load a structure into this world.
     *
     * @param worldObj the world to load in
     * @param name     the structure name
     */
    public StructureWrapper(final World worldObj, final String name)
    {
        this(worldObj, new StructureProxy(worldObj, name), name);
    }

    /**
     * Create a new StructureProxy.
     *
     * @param worldObj  the world to show it in
     * @param structure the structure it comes from
     * @param name      the name this structure has
     */
    private StructureWrapper(final World worldObj, final StructureProxy structure, final String name)
    {
        world = worldObj;
        this.structure = structure;
        this.name = name;
    }

    /**
     * Load a structure into this world
     * and place it in the right position and rotation.
     *  @param worldObj  the world to load it in
     * @param name      the structures name
     * @param pos       coordinates
     * @param rotations number of times rotated
     * @param mirror    the mirror used.
     * @param complete  paste it complete (with structure blocks) or without
     * @param player the placing player.
     */
    public static void loadAndPlaceStructureWithRotation(
      final World worldObj, @NotNull final String name,
      @NotNull final BlockPos pos, final int rotations, @NotNull final Mirror mirror,
      final boolean complete, final EntityPlayerMP player)
    {
        try
        {
            @NotNull final StructureWrapper structureWrapper = new StructureWrapper(worldObj, name);
            structureWrapper.position = pos;
            structureWrapper.rotate(rotations, worldObj, pos, mirror);
            structureWrapper.placeStructure(pos.subtract(structureWrapper.getOffset()), complete, player);
        }
        catch (final IllegalStateException e)
        {
            Log.getLogger().warn("Could not load structure!", e);
        }
    }

    /**
     * Rotates the structure x times.
     *
     * @param times     times to rotateWithMirror.
     * @param world     world it's rotating it in.
     * @param rotatePos position to rotateWithMirror it around.
     * @param mirror    the mirror to rotate with.
     */
    public void rotate(final int times, @NotNull final World world, @NotNull final BlockPos rotatePos, @NotNull final Mirror mirror)
    {
        structure.rotateWithMirror(times, world, rotatePos, mirror);
    }

    /**
     * Place a structure into the world.
     *  @param pos      coordinates
     * @param complete paste it complete (with structure blocks) or without
     * @param player the placing player.
     */
    private void placeStructure(@NotNull final BlockPos pos, final boolean complete, final EntityPlayerMP player)
    {
        setLocalPosition(pos);
        final ChangeStorage storage = new ChangeStorage(player);

        @NotNull final List<BlockPos> delayedBlocks = new ArrayList<>();

        //structure.getBlockInfo()[0].pos
        for (int j = 0; j < structure.getHeight(); j++)
        {
            for (int k = 0; k < structure.getLength(); k++)
            {
                for (int i = 0; i < structure.getWidth(); i++)
                {
                    @NotNull final BlockPos localPos = new BlockPos(i, j, k);
                    final IBlockState localState = this.structure.getBlockState(localPos);
                    final Block localBlock = localState.getBlock();

                    final BlockPos worldPos = pos.add(localPos);

                    if ((localBlock == ModBlocks.blockSubstitution && !complete))
                    {
                        continue;
                    }

                    storage.addPositionStorage(worldPos, world);

                    if (localState.getMaterial().isSolid())
                    {
                        handleBlockPlacement(worldPos, localState, complete, this.structure.getBlockInfo(localPos).tileentityData);
                    }
                    else
                    {
                        delayedBlocks.add(localPos);
                    }
                }
            }
        }

        for (@NotNull final BlockPos coords : delayedBlocks)
        {
            final IBlockState localState = this.structure.getBlockState(coords);
            final BlockPos newWorldPos = pos.add(coords);
            storage.addPositionStorage(coords, world);
            handleBlockPlacement(newWorldPos, localState, complete, this.structure.getBlockInfo(coords).tileentityData);
        }

        for (int j = 0; j < structure.getHeight(); j++)
        {
            for (int k = 0; k < structure.getLength(); k++)
            {
                for (int i = 0; i < structure.getWidth(); i++)
                {
                    @NotNull final BlockPos localPos = new BlockPos(i, j, k);
                    final Template.EntityInfo info = this.structure.getEntityinfo(localPos);

                    if (info != null)
                    {
                        try
                        {
                            final Entity entity = EntityList.createEntityFromNBT(info.entityData, world);
                            entity.setUniqueId(UUID.randomUUID());
                            world.spawnEntity(entity);
                            storage.addToBeKilledEntity(entity);
                        }
                        catch (final RuntimeException e)
                        {
                            Log.getLogger().info("Couldn't restore entitiy", e);
                        }
                    }
                }
            }
        }

        Manager.addToQueue(storage);
    }

    /**
     * @return Where the hut (or any offset) is in the structure.
     */
    public BlockPos getOffset()
    {
        return structure.getOffset();
    }

    /**
     * This method handles the block placement.
     * When we extract this into another mod, we have to override the method.
     *
     * @param pos the world position.
     * @param localState the local state.
     * @param complete if complete with it.
     * @param tileEntityData the tileEntity.
     */
    private void handleBlockPlacement(final BlockPos pos, final IBlockState localState, final boolean complete, final NBTTagCompound tileEntityData)
    {
        for (final IPlacementHandler handlers : PlacementHandlers.handlers)
        {
            if (handlers.canHandle(world, pos, localState))
            {
                handlers.handle(world, pos, localState, tileEntityData, complete, position);
                return;
            }
        }
    }

    /**
     * Increment progressPos.
     *
     * @return false if the all the block have been incremented through.
     */
    public boolean incrementBlock()
    {
        if (this.progressPos.equals(NULL_POS))
        {
            this.progressPos.setPos(-1, 0, 0);
        }

        this.progressPos.setPos(this.progressPos.getX() + 1, this.progressPos.getY(), this.progressPos.getZ());
        if (this.progressPos.getX() == structure.getWidth())
        {
            this.progressPos.setPos(0, this.progressPos.getY(), this.progressPos.getZ() + 1);
            if (this.progressPos.getZ() == structure.getLength())
            {
                this.progressPos.setPos(this.progressPos.getX(), this.progressPos.getY() + 1, 0);
                if (this.progressPos.getY() == structure.getHeight())
                {
                    reset();
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if the block in the world is the same as what is in the structure.
     *
     * @return true if the structure block equals the world block.
     */
    public boolean isStructureBlockEqualWorldBlock()
    {
        final IBlockState structureBlockState = structure.getBlockState(this.getLocalPosition());
        final Block structureBlock = structureBlockState.getBlock();

        //All worldBlocks are equal the substitution block
        if (structureBlock == ModBlocks.blockSubstitution)
        {
            return true;
        }

        final BlockPos worldPos = this.getBlockPosition();

        final IBlockState worldBlockState = world.getBlockState(worldPos);

        if (structureBlock == ModBlocks.blockSolidSubstitution && worldBlockState.getMaterial().isSolid())
        {
            return true;
        }

        final Block worldBlock = worldBlockState.getBlock();

        //list of things to only check block for.
        //For the time being any flower pot is equal to each other.
        if (structureBlock instanceof BlockDoor || structureBlock == Blocks.FLOWER_POT)
        {
            return structureBlock == worldBlock;
        }
        else if ((structureBlock instanceof BlockStairs && structureBlockState == worldBlockState)
                   || BlockUtils.isGrassOrDirt(structureBlock, worldBlock, structureBlockState, worldBlockState))
        {
            return true;
        }

        final Template.EntityInfo entityInfo = structure.getEntityinfo(this.getLocalPosition());
        if (entityInfo != null)
        {
            return false;
            //todo get entity at position.
        }

        //had this problem in a super flat world, causes builder to sit doing nothing because placement failed
        return worldPos.getY() <= 0
                 || structureBlockState == worldBlockState;
    }

    /**
     * Reset the progressPos.
     */
    public void reset()
    {
        BlockPosUtil.set(this.progressPos, NULL_POS);
    }

    /**
     * @return progressPos as an immutable.
     */
    @NotNull
    public BlockPos getLocalPosition()
    {
        return this.progressPos.toImmutable();
    }

    /**
     * Change the current progressPos. Used when loading progress.
     *
     * @param localPosition new progressPos.
     */
    public void setLocalPosition(@NotNull final BlockPos localPosition)
    {
        BlockPosUtil.set(this.progressPos, localPosition);
    }

    /**
     * @return World position.
     */
    public BlockPos getBlockPosition()
    {
        return this.progressPos.add(getOffsetPosition());
    }

    /**
     * @return Min world position for the structure.
     */
    public BlockPos getOffsetPosition()
    {
        return position.subtract(getOffset());
    }

    /**
     * Gets the block state for the current local block.
     *
     * @return Current local block state.
     */
    @Nullable
    public IBlockState getBlockState()
    {
        if (this.progressPos.equals(NULL_POS))
        {
            return null;
        }
        return this.structure.getBlockState(this.progressPos);
    }

    /**
     * @return A list of all the entities in the structure.
     */
    @NotNull
    public List<Template.EntityInfo> getEntities()
    {
        return structure.getTileEntities();
    }

    /**
     * Base position of the structure.
     *
     * @return BlockPos representing where the structure is.
     */
    public BlockPos getPosition()
    {
        if (position == null)
        {
            return new BlockPos(0, 0, 0);
        }
        return position;
    }

    /**
     * Set the position, used when loading.
     *
     * @param position Where the structure is in the world.
     */
    public void setPosition(final BlockPos position)
    {
        this.position = position;
    }

    /**
     * Calculate the item needed to place the current block in the structure.
     *
     * @return an item or null if not initialized.
     */
    @Nullable
    public Item getItem()
    {
        @Nullable final Block block = this.getBlock();
        @Nullable final IBlockState blockState = this.getBlockState();
        if (block == null || blockState == null || block == Blocks.AIR || blockState.getMaterial().isLiquid())
        {
            return null;
        }

        final ItemStack stack = BlockUtils.getItemStackFromBlockState(blockState);

        if (!ItemStackUtils.isEmpty(stack))
        {
            return stack.getItem();
        }

        return null;
    }

    /**
     * Calculate the current block in the structure.
     *
     * @return the current block or null if not initialized.
     */
    @Nullable
    public Block getBlock()
    {
        @Nullable final IBlockState state = getBlockState();
        if (state == null)
        {
            return null;
        }
        return state.getBlock();
    }

    /**
     * @return The name of the structure.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The height of the structure.
     */
    public int getHeight()
    {
        return structure.getHeight();
    }

    /**
     * @return The width of the structure.
     */
    public int getWidth()
    {
        return structure.getWidth();
    }

    /**
     * @return The length of the structure.
     */
    public int getLength()
    {
        return structure.getLength();
    }

    /**
     * @return The StructureProxy that houses all the info about what is stored in a structure.
     */
    public StructureProxy getStructure()
    {
        return structure;
    }
}