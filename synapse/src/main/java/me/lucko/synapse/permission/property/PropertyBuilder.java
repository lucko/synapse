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

package me.lucko.synapse.permission.property;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A builder for {@link Property properties}.
 */
public interface PropertyBuilder {

    /**
     * Gets if the given property is supported.
     *
     * @param property the property
     * @return true if the property is supported, false otherwise
     */
    boolean supports(@NonNull Property<?> property);

    /**
     * Sets the given property.
     *
     * @param property the property
     * @param value the property value
     * @param <T> the property value type
     * @return this builder
     * @throws UnsupportedOperationException if the property is not supported
     */
    default <T> @NonNull PropertyBuilder with(@NonNull Property<T> property, @Nullable T value) throws UnsupportedOperationException {
        if (!this.supports(property)) {
            throw new UnsupportedOperationException(this.toString() + " does not support the " + property.name() + " property!");
        }
        return this.withIfSupported(property, value);
    }

    /**
     * Sets the given property, without throwing an exception if the property is not supported.
     *
     * @param property the property
     * @param value the property value
     * @param <T> the property value type
     * @return this builder
     */
    <T> @NonNull PropertyBuilder withIfSupported(@NonNull Property<T> property, @Nullable T value);

}
