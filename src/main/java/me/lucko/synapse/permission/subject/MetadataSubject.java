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

package me.lucko.synapse.permission.subject;

import me.lucko.synapse.util.FutureAction;
import me.lucko.synapse.permission.PermissionService;
import me.lucko.synapse.permission.context.Context;
import me.lucko.synapse.permission.options.SetOptions;

import org.bukkit.plugin.Plugin;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an object which can hold prefixes, suffixes and metadata.
 */
public interface MetadataSubject {

    /**
     * Gets the subjects prefix value.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * <p>This method will retrieve a prefix according to the subjects
     * "current context". If you want to get a prefix in a specific context,
     * use {@link #getPrefix(Set)}.</p>
     *
     * @return the value of the subjects prefix
     */
    @Nullable
    String getPrefix();

    /**
     * Gets the subjects prefix value.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * @param contexts the contexts to lookup in
     * @return the value of the subjects prefix
     */
    @Nullable
    String getPrefix(@Nonnull Set<Context> contexts);

    /**
     * Gets the subjects suffix value.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * <p>This method will retrieve a suffix according to the subjects
     * "current context". If you want to get a suffix in a specific context,
     * use {@link #getSuffix(Set)}.</p>
     *
     * @return the value of the subjects suffix
     */
    @Nullable
    String getSuffix();

    /**
     * Gets the subjects suffix value.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * @param contexts the contexts to lookup in
     * @return the value of the subjects suffix
     */
    @Nullable
    String getSuffix(@Nonnull Set<Context> contexts);

    /**
     * Gets the meta value corresponding to the given key for the subject.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * <p>This method will retrieve a value according to the subjects
     * "current context". If you want to get a value in a specific context,
     * use {@link #getMetadata(String, Set)}.</p>
     *
     * @param key the key
     * @return the value for the key
     */
    @Nullable
    String getMetadata(@Nonnull String key);

    /**
     * Gets the meta value corresponding to the given key for the subject.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * @param key the key
     * @param contexts the contexts to lookup in
     * @return the value for the key
     */
    @Nullable
    String getMetadata(@Nonnull String key, @Nonnull Set<Context> contexts);

    /**
     * Sets a prefix for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setPrefix(String, SetOptions)} with
     * {@link PermissionService#getNormalSetOptions()}.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param prefix the prefix to set, or null to remove any current prefix
     * @return a future result
     */
    @Nonnull
    FutureAction setPrefix(@Nullable String prefix);

    /**
     * Sets a prefix for the subject with defined options.
     *
     * <p>A {@link SetOptions} instance can be obtained and modified using
     * {@link PermissionService#getNormalSetOptions()}</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param prefix the prefix to set, or null to remove any current prefix
     * @param options the options to set the prefix with
     * @return a future result
     */
    @Nonnull
    FutureAction setPrefix(@Nullable String prefix, @Nonnull SetOptions options);

    /**
     * Sets a suffix for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setSuffix(String, SetOptions)} with
     * {@link PermissionService#getNormalSetOptions()}.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param suffix the suffix to set, or null to remove any current suffix
     * @return a future result
     */
    @Nonnull
    FutureAction setSuffix(@Nullable String suffix);

    /**
     * Sets a suffix for the subject with defined options.
     *
     * <p>A {@link SetOptions} instance can be obtained and modified using
     * {@link PermissionService#getNormalSetOptions()}</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param suffix the suffix to set, or null to remove any current suffix
     * @param options the options to set the suffix with
     * @return a future result
     */
    @Nonnull
    FutureAction setSuffix(@Nullable String suffix, @Nonnull SetOptions options);

    /**
     * Sets a metadata value for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setMetadata(String, String, SetOptions)} with
     * {@link PermissionService#getNormalSetOptions()}.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param key the metadata key
     * @param value the value
     * @return a future result
     */
    @Nonnull
    FutureAction setMetadata(@Nonnull String key, @Nullable String value);

    /**
     * Sets a metadata value for the subject with defined options.
     *
     * <p>A {@link SetOptions} instance can be obtained and modified using
     * {@link PermissionService#getNormalSetOptions()}</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param key the metadata key
     * @param value the value
     * @param options the options to set the value with
     * @return a future result
     */
    @Nonnull
    FutureAction setMetadata(@Nonnull String key, @Nullable String value, @Nonnull SetOptions options);

}
