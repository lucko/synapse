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

import me.lucko.synapse.permission.context.Context;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a player and their permissions data
 */
public interface User extends PermissionSubject, MetadataSubject {

    /**
     * Gets the players unique id
     *
     * @return the uuid
     */
    @Nonnull
    UUID getUniqueId();

    /**
     * Gets the players username, if known.
     *
     * @return the username
     */
    @Nullable
    String getUsername();

    /**
     * Gets the players "primary" group, if they have one.
     *
     * @return the players primary group
     */
    @Nullable
    Group getPrimaryGroup();

    /**
     * Gets the players "primary" group in a given set of contexts,
     * if they have one.
     *
     * @param contexts the contexts to check in
     * @return the players primary group
     */
    @Nullable
    Group getPrimaryGroup(Set<Context> contexts);

}
