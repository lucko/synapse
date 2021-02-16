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

import me.lucko.synapse.permission.membership.GroupMembership;
import me.lucko.synapse.permission.node.PermissionNode;
import me.lucko.synapse.permission.property.PropertyBuilder;
import me.lucko.synapse.util.FutureAction;

import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents an object which can hold permissions and inherit from groups.
 */
public interface PermissionSubject {

    /**
     * Gets the permissions this subject has set.
     *
     * <p>The result of this method <b>will not</b> contain any permissions
     * which would normally be inherited from parent groups.</p>
     *
     * <p>The returned collection is immutable and will not update live.</p>
     *
     * @return the permissions this subject has
     */
    @NonNull Collection<PermissionNode> getPermissions();

    /**
     * Gets the groups this subject inherits from.
     *
     * <p>The result of this method <b>will not</b> contain deeper memberships,
     * and only returns the groups the subject directly inherits from.</p>
     *
     * <p>The returned collection is immutable and will not update live.</p>
     *
     * @return the groups this subject has
     */
    @NonNull Collection<GroupMembership> getGroups();

    /**
     * Runs a permission check on the subject.
     *
     * <p>Unlike all other methods in this interface, this method <b>should</b>
     * account for inheritance rules.</p>
     *
     * <p>The result should be as accurate as calling
     * {@link Permissible#hasPermission(String)}, and return the same result in
     * most cases.</p>
     *
     * @param permission the permission
     * @return the result of the check
     */
    boolean checkPermission(@NonNull String permission);

    /**
     * Sets a permission for the subject.
     *
     * <p>Calling this method is equivalent to calling
     * {@link #setPermission(String, Consumer)} without setting any extra properties.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param permission the permission
     * @return a future result
     */
    default @NonNull FutureAction setPermission(@NonNull String permission) {
        return this.setPermission(permission, props -> {});
    }

    /**
     * Sets a permission for the subject with extra properties.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param permission the permission
     * @param properties the properties to set the permission with
     * @return a future result
     */
    @NonNull FutureAction setPermission(@NonNull String permission, @NonNull Consumer<PropertyBuilder> properties);

    /**
     * Unsets a permission for the subject.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param permission the permission
     * @return a future result
     */
    @NonNull FutureAction unsetPermission(@NonNull PermissionNode permission);

    /**
     * Adds a group to the subject (makes the subject inherit permissions
     * and other groups from it).
     *
     * <p>Calling this method is equivalent to calling
     * {@link #addGroup(Group, Consumer)} without setting any extra properties.</p>
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param group the group
     * @return a future result
     */
    default @NonNull FutureAction addGroup(@NonNull Group group) {
        return this.addGroup(group, props -> {});
    }

    /**
     * Adds a group to the subject (makes the subject inherit permissions
     * and other groups from it) with extra properties.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param group the group
     * @param properties the properties to add the group with
     * @return a future result
     */
    @NonNull FutureAction addGroup(@NonNull Group group, @NonNull Consumer<PropertyBuilder> properties);

    /**
     * Removes a group from the subject.
     *
     * <p>The result of this action may not apply immediately, and the change
     * may be applied asynchronously. If you want to wait until the action has
     * been fully applied, pass a callback using
     * {@link FutureAction#whenComplete(Plugin, Runnable)}.</p>
     *
     * @param group the group
     * @return a future result
     */
    @NonNull FutureAction removeGroup(@NonNull GroupMembership group);

}
