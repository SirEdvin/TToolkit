/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.ingame.mod;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import site.siredvin.ttoolkit.TToolkitMod;

import java.util.Collections;
import java.util.Optional;

import static net.minecraft.world.level.levelgen.WorldGenSettings.withOverworld;

@Mod.EventBusSubscriber(modid = TToolkitMod.MOD_ID, value = Dist.CLIENT)
public final class ClientHooks {
    private static final Logger LOG = LogManager.getLogger(TestHooks.class);

    private static boolean triggered = false;

    private ClientHooks() {
    }

    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
        if (triggered || !(event.getGui() instanceof TitleScreen)) return;
        triggered = true;

        ClientHooks.openWorld();
    }

    private static void openWorld() {
        Minecraft minecraft = Minecraft.getInstance();

        // Clear some options before we get any further.
        minecraft.options.autoJump = false;
        minecraft.options.renderClouds = CloudStatus.OFF;
        minecraft.options.particles = ParticleStatus.MINIMAL;
        minecraft.options.tutorialStep = TutorialSteps.NONE;
        minecraft.options.renderDistance = 6;
        minecraft.options.gamma = 1.0;

        if (minecraft.getLevelSource().levelExists("test")) {
            LOG.info("World exists, loading it");
            Minecraft.getInstance().loadLevel("test");
        } else {
            LOG.info("World does not exist, creating it for the first time");

            RegistryAccess.RegistryHolder registries = RegistryAccess.builtin();

            Registry<DimensionType> dimensions = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            Registry<Biome> biomes = registries.registryOrThrow(Registry.BIOME_REGISTRY);

            FlatLevelGeneratorSettings flatSettings = FlatLevelGeneratorSettings.getDefault(biomes)
                    .withLayers(
                            Collections.singletonList(new FlatLayerInfo(4, Blocks.WHITE_CONCRETE)),
                            new StructureSettings(Optional.empty(), Collections.emptyMap())
                    );
            flatSettings.setBiome(() -> biomes.get(Biomes.DESERT));

            WorldGenSettings generator = new WorldGenSettings(0, false, false, withOverworld(
                    dimensions,
                    DimensionType.defaultDimensions(dimensions, biomes, registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY), 0),
                    new FlatLevelSource(flatSettings)
            ));

            LevelSettings settings = new LevelSettings(
                    "test", GameType.CREATIVE, false, Difficulty.PEACEFUL, true,
                    new GameRules(), DataPackConfig.DEFAULT
            );
            Minecraft.getInstance().createLevel("test", settings, registries, generator);
        }
    }
}