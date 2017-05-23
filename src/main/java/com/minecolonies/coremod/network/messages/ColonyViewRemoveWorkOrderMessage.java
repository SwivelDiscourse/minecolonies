package com.minecolonies.coremod.network.messages;

import com.minecolonies.api.colony.ColonyManager;
import com.minecolonies.coremod.colony.Colony;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewRemoveWorkOrderMessage implements IMessage, IMessageHandler<ColonyViewRemoveWorkOrderMessage, IMessage>
{

    private int colonyId;
    private int workOrderId;

    /**
     * Empty constructor used when registering the message.
     */
    public ColonyViewRemoveWorkOrderMessage()
    {
        super();
    }

    /**
     * Creates an object for the remove message for citizen.
     *
     * @param colony      colony the workOrder is in.
     * @param workOrderId workOrder ID.
     */
    public ColonyViewRemoveWorkOrderMessage(@NotNull final Colony colony, final int workOrderId)
    {
        this.colonyId = colony.getID();
        this.workOrderId = workOrderId;
    }

    @Override
    public void fromBytes(@NotNull final ByteBuf buf)
    {
        colonyId = buf.readInt();
        workOrderId = buf.readInt();
    }

    @Override
    public void toBytes(@NotNull final ByteBuf buf)
    {
        buf.writeInt(colonyId);
        buf.writeInt(workOrderId);
    }

    @Nullable
    @Override
    public IMessage onMessage(@NotNull final ColonyViewRemoveWorkOrderMessage message, final MessageContext ctx)
    {
        return ColonyManager.handleColonyViewRemoveWorkOrderMessage(message.colonyId, message.workOrderId);
    }
}
