package com.ldtteam.structurize.network.messages;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Interface for all network messages
 */
public interface IMessage
{
    /**
     * Writes message data to buffer
     * 
     * @param buf network data byte buffer
     */
    public void toBytes(final PacketBuffer buf);

    /**
     * Reads message data from buffer
     * 
     * @param buf network data byte buffer
     */
    public void fromBytes(final PacketBuffer buf);

    /**
     * Executes message action
     * 
     * @param ctxIn           network context of incoming message
     * @param isLogicalServer whether message arrived at logical server side
     */
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer);
}