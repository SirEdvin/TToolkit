/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.ingame.api;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.ingame.mod.TestAPI;
import net.minecraft.test.TestList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Assertion state of a computer.
 *
 * @see TestAPI For the Lua interface for this.
 * @see TestExtensionsKt#thenComputerOk(TestList, String, String)
 */
public class ComputerState {
    public static final String DONE = "DONE";

    protected static final Map<String, ComputerState> lookup = new ConcurrentHashMap<>();

    protected final Set<String> markers = new HashSet<>();
    protected @Nullable String error;

    public boolean isDone(@Nonnull String marker) {
        return markers.contains(marker);
    }

    public void check(@Nonnull String marker) {
        if (!markers.contains(marker))
            throw new IllegalStateException("Not yet at " + marker);
        if (error != null)
            throw new RuntimeException(error);
    }

    public @Nullable String getError() {
        return error;
    }

    public static ComputerState get(String label) {
        return lookup.get(label);
    }

    public static void dump(String label) {
        ComputerState state = lookup.get(label);
        ComputerCraft.log.warn("{} -> {}", label, state.markers);
    }
}
