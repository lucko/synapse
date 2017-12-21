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
 * Represents the options and settings to use when setting a value.
 *
 * <p>Implementations of this interface are immutable.</p>
 */
@Immutable
public interface SetOptions {

    /**
     * Gets if the permission plugin supports negated permissions
     *
     * @return true if the permission plugin supports negation
     */
    boolean supportsNegation();

    /**
     * Gets if the permission plugin supports setting expiry times on values
     *
     * @return true if the permission plugin supports temporary values
     */
    boolean supportsExpiry();

    /**
     * This setting declares that the value should be negated.
     *
     * <p>Will throw an {@link UnsupportedOperationException} if the permission
     * plugin does not support negation.</p>
     *
     * @return a copy of the current {@link SetOptions} instance, with a modified negation property
     * @throws UnsupportedOperationException if the permission plugin doesn't support negated permissions
     */
    @Nonnull
    SetOptions negated() throws UnsupportedOperationException;

    /**
     * This setting declares that the value should not be negated.
     *
     * @return a copy of the current {@link SetOptions} instance, with a modified negation property
     */
    @Nonnull
    SetOptions notNegated();

    /**
     * This setting declares when the value should expire.
     *
     * <p>Will throw an {@link UnsupportedOperationException} if the permission
     * plugin does not support temporary values.</p>
     *
     * @param expiryTime the unix timestamp in milliseconds when the value should expire
     * @return a copy of the current {@link SetOptions} instance, with a modified expiry property
     * @throws UnsupportedOperationException if the permission plugin doesn't support temporary values
     */
    @Nonnull
    SetOptions withExpiry(long expiryTime) throws UnsupportedOperationException;

    /**
     * This setting declares that a value should not expire.
     *
     * @return a copy of the current {@link SetOptions} instance, with a modified expiry property
     */
    @Nonnull
    SetOptions noExpiry();

    /**
     * This setting declares that the value should only apply in a given set of
     * contexts.
     *
     * <p>Context instances should be obtained using
     * {@link PermissionService#createContext(String, String)}.</p>
     *
     * <p>Note that the method to retrieve contexts will throw an exception if
     * the plugin doesn't support the given key.</p>
     *
     * @param contexts the contexts to apply the value in
     * @return a copy of the current {@link SetOptions} instance, with a modified contexts property
     */
    @Nonnull
    SetOptions withContexts(@Nonnull Set<Context> contexts);

    /**
     * Gets if the value should be negated, according to this settings instance.
     *
     * @return if the value should be negated
     */
    boolean shouldNegate();

    /**
     * Gets if the value should expire, according to this settings instance.
     *
     * @return if the value should expire
     */
    boolean shouldExpire();

    /**
     * Gets the time when the value should expire, according to this settings instance.
     *
     * <p>Throws an {@link IllegalStateException} if the value isn't going to expire.</p>
     *
     * @return the expiry time
     * @throws IllegalStateException if the value isn't going to expire
     */
    long getExpiryTime() throws IllegalStateException;

    /**
     * Gets the contexts the value should apply in, according to this settings instance.
     *
     * @return the contexts the value should apply in
     */
    @Nonnull
    Set<Context> getContexts();

}
