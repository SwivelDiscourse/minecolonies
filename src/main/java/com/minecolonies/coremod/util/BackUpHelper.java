package com.minecolonies.coremod.util;

import com.google.common.io.Files;
import com.minecolonies.api.colony.IChunkmanagerCapability;
import com.minecolonies.api.util.ChunkLoadStorage;
import com.minecolonies.api.util.Log;
import com.minecolonies.coremod.colony.Colony;
import com.minecolonies.coremod.colony.ColonyManager;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.minecolonies.api.util.constant.ColonyManagerConstants.*;
import static com.minecolonies.coremod.MineColonies.CHUNK_STORAGE_UPDATE_CAP;

public class BackUpHelper
{
    /**
     * Backup the colony
     * @return
     */
    public static boolean backupColonyData()
    {
        ColonyManager.saveColonies(false);
        try(FileOutputStream fos = new FileOutputStream(getBackupSaveLocation(new Date())))
        {
            @NotNull final File saveDir = new File(DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory(), FILENAME_MINECOLONIES_PATH);
            final ZipOutputStream zos = new ZipOutputStream(fos);

            for (int i = 1; i < colonies.getTopID() + 1; i++)
            {
                @NotNull final File file = new File(saveDir, String.format(FILENAME_COLONY, i));
                if (file.exists())
                {
                    addToZipFile(String.format(FILENAME_COLONY, i), zos, saveDir);
                }
            }
            addToZipFile(getSaveLocation().getName(), zos, saveDir);

            zos.close();
        }
        catch (final Exception e)
        {
            /*
             * Intentionally not being thrown.
             */
            Log.getLogger().warn("Unable to backup colony data, please contact an administrator", e);
            return false;
        }

        return true;
    }

    /**
     * Get save location for Minecolonies backup data, from the world/save
     * directory.
     *
     * @return Save file for minecolonies.
     */
    @NotNull
    private static File getBackupSaveLocation(final Date date)
    {
        @NotNull final File saveDir = new File(DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory(), FILENAME_MINECOLONIES_PATH);
        return new File(saveDir, String.format(FILENAME_MINECOLONIES_BACKUP, new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(date)));
    }

    /**
     * Add zip to file.
     * @param fileName the file name.
     * @param zos the output stream.
     * @param folder the folder.
     */
    private static void addToZipFile(final String fileName, final ZipOutputStream zos, final File folder)
    {
        final File file = new File(folder, fileName);
        try(FileInputStream fis = new FileInputStream(file))
        {
            zos.putNextEntry(new ZipEntry(fileName));
            Files.copy(file, zos);
        }
        catch (final Exception e)
        {
            /*
             * Intentionally not being thrown.
             */
            Log.getLogger().warn("Error packing " + fileName + " into the zip.");
        }
    }

    /**
     * Get save location for Minecolonies data, from the world/save directory.
     *
     * @return Save file for minecolonies.
     */
    @NotNull
    public static File getSaveLocation()
    {
        @NotNull final File saveDir = new File(DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory(), FILENAME_MINECOLONIES_PATH);
        return new File(saveDir, FILENAME_MINECOLONIES);
    }

    /**
     * Save an NBTTagCompound to a file.  Does so in a safe manner using an
     * intermediate tmp file.
     *
     * @param file     The destination file to write the data to.
     * @param compound The NBTTagCompound to write to the file.
     */
    public static void saveNBTToPath(@Nullable final File file, @NotNull final NBTTagCompound compound)
    {
        try
        {
            if (file != null)
            {
                file.getParentFile().mkdir();
                CompressedStreamTools.safeWrite(compound, file);
            }
        }
        catch (final IOException exception)
        {
            Log.getLogger().error("Exception when saving ColonyManager", exception);
        }
    }

    /**
     * Load a file and return the data as an NBTTagCompound.
     *
     * @param file The path to the file.
     * @return the data from the file as an NBTTagCompound, or null.
     */
    public static NBTTagCompound loadNBTFromPath(@Nullable final File file)
    {
        try
        {
            if (file != null && file.exists())
            {
                return CompressedStreamTools.read(file);
            }
        }
        catch (final IOException exception)
        {
            Log.getLogger().error("Exception when loading file from path in ColonyManager!", exception);
        }
        return null;
    }

    /**
     * Save all the Colonies.
     */
    public static void saveColonies(final boolean isWorldUnload)
    {
        @NotNull final NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);

        @NotNull final File file = getSaveLocation();
        saveNBTToPath(file, compound);
        @NotNull final File saveDir = new File(DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory(), FILENAME_MINECOLONIES_PATH);
        for (final Colony colony : colonies)
        {
            if (isWorldUnload)
            {
                final NBTTagCompound colonyCompound = new NBTTagCompound();
                colony.writeToNBT(colonyCompound);
                saveNBTToPath(new File(saveDir, String.format(FILENAME_COLONY, colony.getID())), colonyCompound);
            }
            else
            {
                saveNBTToPath(new File(saveDir, String.format(FILENAME_COLONY, colony.getID())), colony.getColonyTag());
            }
        }
    }
}
