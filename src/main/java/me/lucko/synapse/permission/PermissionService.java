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

package me.lucko.synapse.permission;

import me.lucko.synapse.GenericService;
import me.lucko.synapse.permission.context.Context;
import me.lucko.synapse.permission.options.SetOptions;
import me.lucko.synapse.permission.options.UnsetOptions;
import me.lucko.synapse.permission.subject.Group;
import me.lucko.synapse.permission.subject.User;
import me.lucko.synapse.util.FutureResult;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A service to interface with Bukkit permission plugins.
 */
public interface PermissionService extends GenericService {

    /**
     * Gets a {@link User} instance for an online player.
     *
     * <p>May throw an exception if the player is {@link Player#isOnline() not
     * online}.</p>
     *
     * <p>Permission plugins will <b>always</b> return a result for online
     * players.</p>
     *
     * @param player the player
     * @return a user
     */
    @Nonnull
    User getUser(@Nonnull Player player);

    /**
     * Gets a {@link User} instance for a given unique id.
     *
     * <p>Permission plugins will <b>always</b> return a result for online
     * players, and <b>may</b> return a result of offline players.</p>
     *
     * @param uniqueId the unique id of the player to lookup
     * @return a user, or null if one could not be retrieved
     * @see Player#getUniqueId()
     */
    @Nullable
    User getUser(@Nonnull UUID uniqueId);

    /**
     * Makes a request to load a user, and then passes the result of the lookup
     * to the given {@link FutureResult}.
     *
     * <p>In some cases, a database lookup may be required to retrieve the
     * necessary data.</p>
     *
     * @param uniqueId the unique id of the player to lookup
     * @return the future result encapsulating the request
     */
    @Nonnull
    FutureResult<User> loadUser(@Nonnull UUID uniqueId);

    /**
     * Gets a collection of all groups known to the permission plugin.
     *
     * <p>The returned collection is immutable and will not update live.</p>
     *
     * @return a collection of known groups
     */
    @Nonnull
    Collection<Group> getGroups();

    /**
     * Gets a group by name.
     *
     * @param name the name of the group
     * @return the group, if present
     */
    @Nullable
    Group getGroup(@Nonnull String name);

    /**
     * Gets the {@link SetOptions} normally used by the plugin.
     *
     * @return the normal set options
     */
    @Nonnull
    SetOptions getNormalSetOptions();

    /**
     * Gets the {@link UnsetOptions} normally used by the plugin.
     *
     * @return the normal unset options
     */
    @Nonnull
    UnsetOptions getNormalUnsetOptions();

    /**
     * Gets if the permission plugin supports contexts of a given type.
     *
     * <p>Note that most plugins support the {@link Context#WORLD_KEY} as a
     * minimum.</p>
     *
     * @param key the context key
     * @return true if the permission plugin supports the context
     */
    boolean supportsContextType(@Nonnull String key);

    /**
     * Creates a context instance for use with this {@link PermissionService}.
     *
     * <p>Will throw a {@link IllegalArgumentException} if this
     * {@link PermissionService} does not support the given context key.</p>
     *
     * @param key the context key
     * @param value the context value
     * @return a context instance
     * @throws IllegalArgumentException if the service doesn't support the given context
     */
    @Nonnull
    Context createContext(@Nonnull String key, @Nonnull String value) throws IllegalArgumentException;

}
