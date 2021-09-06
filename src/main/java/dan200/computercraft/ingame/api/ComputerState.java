/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2021. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.ingame.api;

import dan200.computercraft.ingame.mod.TestAPI;
import net.minecraft.gametest.framework.GameTestSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Assertion state of a computer.
 *
 * @see TestAPI For the Lua interface for this.
 * @see TestExtensionsKt#thenComputerOk(GameTestSequence, String)
 */
public class ComputerState {
    public static final String NOT_EVEN_STARTED = "NOTE EVEN STARTED";
    public static final String DONE = "DONE";
    public static final String START = "START";

    protected static final Map<String, ComputerState> lookup = new ConcurrentHashMap<>();

    protected final List<String> states = new LinkedList<String>() {{ add(NOT_EVEN_STARTED); }};
    protected @Nullable String error;

    public boolean isDone() {
        return states.contains(DONE);
    }

    public boolean isPass(String marker) {
        return states.contains(marker);
    }

    public void check(@Nonnull String marker) {
        if (!states.contains(marker))
            throw new IllegalStateException("Not yet at " + marker);
        if (error != null)
            throw new RuntimeException(error);
    }

    public @Nullable String getError() {
        return error;
    }

    public String getCurrent() {
        return states.get(states.size() - 1).toLowerCase();
    }

    public static ComputerState get(String label) {
        return lookup.get(label);
    }
}
