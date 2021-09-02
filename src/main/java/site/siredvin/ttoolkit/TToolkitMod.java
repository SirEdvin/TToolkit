package site.siredvin.ttoolkit;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.ingame.mod.TestAPI;
import dan200.computercraft.ingame.mod.TestLoader;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

@Mod(TToolkitMod.MOD_ID)
public class TToolkitMod {
    public static final Logger LOGGER = LogManager.getLogger();
    private static Path sourceDir = null;

    public static final String MOD_ID = "ttoolkit";
    private static int counter = 0;

    public static void performConfiguration(@Nonnull Path sourceDir, int counter) {
        if (TToolkitMod.sourceDir != null)
            throw new IllegalArgumentException("Cannot update sourceDir!");
        TToolkitMod.sourceDir = sourceDir;
        TToolkitMod.counter = counter;
        StructureUtils.testStructuresDir = sourceDir.resolve("structures").toString();
    }

    public static boolean isConfigured() {
        return sourceDir != null;
    }

    public static synchronized int getCounter() {
        return counter;
    }

    public static synchronized void decreaseCounter() {
        counter--;
    }

    public static @Nullable Path getSourceDir() {
        return sourceDir;
    }

    public TToolkitMod() {
        // Register ourselves for server and other game events we are interested in
        ComputerCraftAPI.registerAPIFactory(TestAPI::new);
        MinecraftForge.EVENT_BUS.register(this);
        TestLoader.setup();
    }
}
