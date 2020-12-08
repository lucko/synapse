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

import me.lucko.synapse.permission.options.PropertyBuilder;
import me.lucko.synapse.util.FutureAction;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

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
     * @return the value of the subjects prefix
     */
    @Nullable String getPrefix();

    /**
     * Gets the subjects suffix value.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * @return the value of the subjects suffix
     */
    @Nullable String getSuffix();

    /**
     * Gets the meta value corresponding to the given key for the subject.
     *
     * <p>If the permission plugin supports it, this method should account for
     * inherited data too.</p>
     *
     * @param key the key
     * @return the value for the key
     */
    @Nullable String getMetadata(@NonNull String key);

    /**
     * Sets a prefix for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setPrefix(String, Consumer)} without setting any extra properties.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param prefix the prefix to set, or null to remove any current prefix
     * @return a future result
     */
    default @NonNull FutureAction setPrefix(@Nullable String prefix) {
        return this.setPrefix(prefix, props -> {});
    }

    /**
     * Sets a prefix for the subject with extra properties.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param prefix the prefix to set, or null to remove any current prefix
     * @param properties the properties to set the prefix with
     * @return a future result
     */
    @NonNull FutureAction setPrefix(@Nullable String prefix, @NonNull Consumer<PropertyBuilder> properties);

    /**
     * Sets a suffix for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setSuffix(String, Consumer)} without setting any extra properties.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param suffix the suffix to set, or null to remove any current suffix
     * @return a future result
     */
    default @NonNull FutureAction setSuffix(@Nullable String suffix) {
        return this.setSuffix(suffix, props -> {});
    }

    /**
     * Sets a suffix for the subject with extra properties.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param suffix the suffix to set, or null to remove any current suffix
     * @param properties the properties to set the suffix with
     * @return a future result
     */
    @NonNull FutureAction setSuffix(@Nullable String suffix, @NonNull Consumer<PropertyBuilder> properties);

    /**
     * Sets a metadata value for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setMetadata(String, String, Consumer)} without setting any extra properties.</p>
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
    default @NonNull FutureAction setMetadata(@NonNull String key, @Nullable String value) {
        return this.setMetadata(key, value, props -> {});
    }

    /**
     * Sets a metadata value for the subject with extra properties.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param key the metadata key
     * @param value the value
     * @param properties the properties to set the value with
     * @return a future result
     */
    @NonNull FutureAction setMetadata(@NonNull String key, @Nullable String value, @NonNull Consumer<PropertyBuilder> properties);

}
