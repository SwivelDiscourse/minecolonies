package com.minecolonies.coremod.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Mirror;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Contains colony specific utility.
 */
public final class ColonyUtils
{
    /**
     * Private constructor to hide implicit one.
     */
    private ColonyUtils()
    {
        /**
         * Intentionally left empty.
         */
    }

    /**
     * Checks if the colony has new subscribers.
     *
     * @param oldSubscribers old subscribers.
     * @param subscribers    all subscribers.
     * @return true if so.
     */
    public static boolean hasNewSubscribers(@NotNull final Set<EntityPlayerMP> oldSubscribers, @NotNull final Set<EntityPlayerMP> subscribers)
    {
        for (final EntityPlayerMP player : subscribers)
        {
            if (!oldSubscribers.contains(player))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculated the corner of a building.
     *
     * @param pos        the central position.
     * @param world      the world.
     * @param wrapper    the structureWrapper.
     * @param rotation   the rotation.
     * @param isMirrored if its mirrored.
     * @return a tuple with the required corners.
     */
    public static Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> calculateCorners(
      final BlockPos pos,
      final World world,
      final StructureWrapper wrapper,
      final int rotation,
      final boolean isMirrored)
    {
        wrapper.rotate(rotation, world, pos, isMirrored ? Mirror.FRONT_BACK : Mirror.NONE);
        wrapper.setPosition(pos);

        final int x1 = wrapper.getPosition().getX() - wrapper.getOffset().getX() - 1;
        final int z1 = wrapper.getPosition().getZ() - wrapper.getOffset().getZ() - 1;
        final int x2 = wrapper.getPosition().getX() + (wrapper.getWidth() - wrapper.getOffset().getX());
        final int z2 = wrapper.getPosition().getZ() + (wrapper.getLength() - wrapper.getOffset().getZ());

        return new Tuple<>(new Tuple<>(x1, x2), new Tuple<>(z1, z2));
    }
}
