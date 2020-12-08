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

import me.lucko.synapse.context.Context;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Generic wrapper around a property that applies to a given setting.
 *
 * @param <T> the property value type
 */
public interface Property<T> {

    /**
     * Property to indicate whether a setting has been negated.
     */
    Property<Boolean> NEGATED = create("negated", false);

    /**
     * Property to indicate that a setting will expire at a given time.
     */
    Property<Instant> EXPIRY = create("expiry", null);

    /**
     * Property to indicate that a setting will only apply in a given world.
     */
    Property<String> REQUIRED_WORLD = create("world", null);

    /**
     * Property to indicate that a setting will only apply in a given server.
     */
    Property<String> REQUIRED_SERVER = create("server", null);

    /**
     * Property to indicate that a setting will only apply when the given contexts are satisfied.
     */
    Property<Set<Context>> REQUIRED_CONTEXT = create("context", null);

    /**
     * Create a new property.
     *
     * @param name the name of the property
     * @param defaultValue the default value of the property
     * @param <T> the property value type
     * @return the property object
     */
    static <T> @NonNull Property<T> create(@NonNull String name, @Nullable T defaultValue) {
        Objects.requireNonNull(name, "name");
        return new PropertyImpl<>(name, defaultValue);
    }

    /**
     * Get the property name.
     *
     * @return the name
     */
    @NonNull String name();

    /**
     * Get the default value.
     *
     * @return the default value
     */
    @Nullable T defaultValue();

}
