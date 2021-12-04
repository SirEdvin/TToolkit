/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.ingame.mod;

import com.mojang.brigadier.CommandDispatcher;
import dan200.computercraft.ComputerCraft;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLLoader;
import site.siredvin.ttoolkit.TToolkitMod;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

import static dan200.computercraft.shared.command.builder.HelpingArgumentBuilder.choice;
import static net.minecraft.commands.Commands.literal;

/**
 * Helper commands for importing/exporting the computer directory.
 */
class ToolkitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(choice(TToolkitMod.MOD_ID)
                .then(literal("import").executes(context -> {
                    importFiles(context.getSource().getServer());
                    return 0;
                }))
                .then(literal("export").executes(context -> {
                    exportFiles(context.getSource().getServer());

                    for (TestFunction function : GameTestRegistry.getAllTestFunctions()) {
                        TestCommand.exportTestStructure(context.getSource(), function.getStructureName());
                    }
                    return 0;
                }))
                .then(literal("regen-structures").executes(context -> {
                    for (TestFunction function : GameTestRegistry.getAllTestFunctions()) {
                        dispatcher.execute("test import " + function.getTestName(), context.getSource());
                        TestCommand.exportTestStructure(context.getSource(), function.getStructureName());
                    }
                    return 0;
                }))
                .then(literal("runall").executes(context -> {
                    GameTestRegistry.forgetFailedTests();
                    MultipleTestTracker result = TestHooks.runTests();
                    result.addListener(new Callback(context.getSource(), result));
                    result.addFailureListener(x -> GameTestRegistry.rememberFailedTest(x.getTestFunction()));
                    return 0;
                }))

                .then(literal("promote").executes(context -> {
                    if (!FMLLoader.getDist().isClient()) return error(context.getSource(), "Cannot run on server");

                    promote();
                    return 0;
                }))
                .then(literal("marker").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    BlockPos pos = StructureUtils.findNearestStructureBlock(player.blockPosition(), 15, (ServerLevel) player.getCommandSenderWorld());
                    if (pos == null) return error(context.getSource(), "No nearby test");

                    StructureBlockEntity structureBlock = (StructureBlockEntity) player.getCommandSenderWorld().getBlockEntity(pos);
                    TestFunction info = GameTestRegistry.getTestFunction(structureBlock.getStructurePath());


                    // Kill the existing armor stand
                    ((ServerLevel) player.getCommandSenderWorld()).getEntities(EntityType.ARMOR_STAND, x -> x.isAlive() && x.getName().getString().equals(info.getTestName()))
                            .forEach(Entity::kill);

                    // And create a new one
                    CompoundTag nbt = new CompoundTag();
                    nbt.putBoolean("Marker", true);
                    nbt.putBoolean("Invisible", true);
                    ArmorStand armorStand = EntityType.ARMOR_STAND.create(player.getCommandSenderWorld());
                    armorStand.readAdditionalSaveData(nbt);
                    armorStand.copyPosition(player);
                    armorStand.setCustomName(new TextComponent(info.getTestName()));
                    return 0;
                }))
        );
    }

    public static void importFiles(MinecraftServer server) {
        Path sourceDir = TToolkitMod.getSourceDir();
        Objects.requireNonNull(sourceDir);
        try {
            Copier.replicate(sourceDir.resolve("computers"), server.getWorldPath(new LevelResource(ComputerCraft.MOD_ID)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void exportFiles(MinecraftServer server) {
        Path sourceDir = TToolkitMod.getSourceDir();
        Objects.requireNonNull(sourceDir);
        try {
            Copier.replicate(server.getWorldPath(new LevelResource(ComputerCraft.MOD_ID)), sourceDir.resolve("computers"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void promote() {
        Path sourceDir = TToolkitMod.getSourceDir();
        Objects.requireNonNull(sourceDir);
        try {
            Copier.replicate(
                    Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots"),
                    sourceDir.resolve("screenshots"),
                    x -> !x.toFile().getName().endsWith(".diff.png")
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class Callback implements GameTestListener {
        private final CommandSourceStack source;
        private final MultipleTestTracker result;

        Callback(CommandSourceStack source, MultipleTestTracker result) {
            this.source = source;
            this.result = result;
        }

        @Override
        public void testStructureLoaded(@Nonnull GameTestInfo tracker) {
        }

        @Override
        public void testFailed(@Nonnull GameTestInfo tracker) {
            TestHooks.writeResults(source, result);
        }

        @Override
        public void testPassed(@Nonnull GameTestInfo tracker) {
            TestHooks.writeResults(source, result);
        }
    }

    private static int error(CommandSourceStack source, String message) {
        source.sendFailure(new TextComponent(message).withStyle(ChatFormatting.RED));
        return 0;
    }
}
