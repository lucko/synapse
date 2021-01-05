/*
 * This file is part of synapse, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.synapse.util;

import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Represents the result of an action which may have no yet been fully applied.
 */
public interface FutureAction {

    /**
     * Attaches a completion callback to this {@link FutureAction}.
     *
     * <p>If the action is already complete, the runnable will be called immediately.</p>
     *
     * <p>If it is not complete, the runnable will be called synchronously using
     * the Bukkit scheduler when the action is completed.</p>
     *
     * @param plugin a plugin instance to use when running the callback
     * @param runnable the runnable
     */
    void whenComplete(@NonNull Plugin plugin, @NonNull Runnable runnable);

    /**
     * Blocks the current thread until the action has completed.
     *
     * <p>This method should only be called from an async task!</p>
     */
    void join();

    /**
     * Encapsulates this {@link FutureAction} as a {@link CompletableFuture}.
     *
     * @return a future
     */
    @NonNull CompletableFuture<Void> asFuture();

}
