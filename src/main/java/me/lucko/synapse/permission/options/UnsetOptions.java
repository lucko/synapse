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

package me.lucko.synapse.permission.options;

import me.lucko.synapse.permission.PermissionService;
import me.lucko.synapse.permission.context.Context;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents the options and settings to use when unsetting a value.
 *
 * <p>Implementations of this interface are immutable.</p>
 */
@Immutable
public interface UnsetOptions {

    /**
     * Gets if the permission plugin supports setting expiry times on values
     *
     * @return true if the permission plugin supports temporary values
     */
    boolean supportsExpiry();

    /**
     * This setting declares that the unset operation should only affect a value
     * with the given expiry time.
     *
     * <p>Will throw an {@link UnsupportedOperationException} if the permission
     * plugin does not support temporary values.</p>
     *
     * @param expiryTime the time to match
     * @return a copy of the current {@link SetOptions} instance, with a modified expiry property
     * @throws UnsupportedOperationException if the permission plugin doesn't support temporary values
     */
    @Nonnull
    UnsetOptions withExpiry(long expiryTime) throws UnsupportedOperationException;

    /**
     * This setting declares that the unset operation should match a value
     * regardless of it's expiry time.
     *
     * @return a copy of the current {@link SetOptions} instance, with a modified expiry property
     */
    @Nonnull
    UnsetOptions matchAnyExpiry();

    /**
     * This setting declares that the unset operation should only affect a value
     * applied with the given contexts.
     *
     * <p>Context instances should be obtained using
     * {@link PermissionService#createContext(String, String)}.</p>
     *
     * <p>Note that the method to retrieve contexts will throw an exception if
     * the plugin doesn't support the given key.</p>
     *
     * @param contexts the contexts to match
     * @return a copy of the current {@link SetOptions} instance, with a modified contexts property
     */
    @Nonnull
    UnsetOptions withContexts(@Nonnull Set<Context> contexts);

    /**
     * This setting declares that the unset operation should match a value
     * regardless of it's contexts.
     *
     * @return a copy of the current {@link SetOptions} instance, with a modified contexts property
     */
    @Nonnull
    UnsetOptions matchAnyContext();

    /**
     * Gets if the operation should match a value by it's expiry time
     *
     * @return if the operation should match by expiry time
     */
    boolean shouldMatchExpiry();

    /**
     * Gets the time to match by.
     *
     * <p>Throws {@link IllegalStateException} if the operation shouldn't match
     * by expiry.</p>
     *
     * @return the expiry time to match by
     * @throws IllegalStateException if the operation shouldn't match by expiry
     */
    long getExpiryTime() throws IllegalStateException;

    /**
     * Gets if the operation should match a value by it's contexts
     *
     * @return if the operation should match by contexts
     */
    boolean shouldMatchContexts();

    /**
     * Gets the contexts to match by.
     *
     * <p>Throws {@link IllegalStateException} if the operation shouldn't match
     * by contexts.</p>
     *
     * @return the contexts to match by
     * @throws IllegalStateException if the operation shouldn't match by contexts
     */
    Set<Context> getContexts() throws IllegalStateException;

}
