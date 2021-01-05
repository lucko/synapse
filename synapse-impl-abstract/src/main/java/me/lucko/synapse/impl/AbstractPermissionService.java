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

package me.lucko.synapse.impl;

import me.lucko.synapse.permission.PermissionService;
import me.lucko.synapse.permission.membership.GroupMembership;
import me.lucko.synapse.permission.node.PermissionNode;
import me.lucko.synapse.permission.property.Property;
import me.lucko.synapse.permission.property.PropertyBuilder;
import me.lucko.synapse.permission.subject.Group;
import me.lucko.synapse.permission.subject.User;
import me.lucko.synapse.util.FutureAction;
import me.lucko.synapse.util.FutureResult;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Provides an abstract implementation of {@link PermissionService}.
 *
 * @param <U> the user type used by the implementation
 * @param <G> the group type used by the implementation
 */
public abstract class AbstractPermissionService<U, G> implements PermissionService {

    protected abstract @NonNull U getUser(@NonNull Player player);
    protected abstract @Nullable U getUser(@NonNull UUID uniqueId);
    protected abstract @NonNull CompletableFuture<U> loadUser(@NonNull UUID uniqueId);

    protected abstract @NonNull Iterable<G> getGroups();
    protected abstract @Nullable G getGroup(@NonNull String name);
    protected abstract @NonNull CompletableFuture<G> loadGroup(@NonNull String name);

    protected abstract @Nullable String userGetUsername(UUID uniqueId, U user);
    protected abstract @Nullable G userGetPrimaryGroup(@NonNull U user);
    protected abstract @NonNull Collection<PermissionNode> userGetPermissions(@NonNull U user);
    protected abstract @NonNull Collection<GroupMembership> userGetGroupMemberships(@NonNull U user);
    protected abstract boolean userCheckPermission(@NonNull U user, @NonNull String permission);
    protected abstract @Nullable String userGetPrefix(@NonNull U user);
    protected abstract @Nullable String userGetSuffix(@NonNull U user);
    protected abstract @Nullable String userGetMetadata(@NonNull U user, @NonNull String key);
    protected abstract @NonNull CompletableFuture<Void> userSetPermission(@NonNull U user, @NonNull String permission, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> userUnsetPermission(@NonNull U user, @NonNull String permission, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> userAddGroup(@NonNull U user, @NonNull String groupName, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> userRemoveGroup(@NonNull U user, @NonNull String groupName, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> userSetPrefix(@NonNull U user, @Nullable String prefix, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> userSetSuffix(@NonNull U user, @Nullable String suffix, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> userSetMetadata(@NonNull U user, @NonNull String key, @Nullable String value, @NonNull PropertyExtractor properties);

    protected abstract @NonNull String groupGetName(G group);
    protected abstract @NonNull Collection<PermissionNode> groupGetPermissions(@NonNull G group);
    protected abstract @NonNull Collection<GroupMembership> groupGetGroupMemberships(@NonNull G group);
    protected abstract boolean groupCheckPermission(@NonNull G group, @NonNull String permission);
    protected abstract @Nullable String groupGetPrefix(@NonNull G group);
    protected abstract @Nullable String groupGetSuffix(@NonNull G group);
    protected abstract @Nullable String groupGetMetadata(@NonNull G group, @NonNull String key);
    protected abstract @NonNull CompletableFuture<Void> groupSetPermission(@NonNull G group, @NonNull String permission, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> groupUnsetPermission(@NonNull G group, @NonNull String permission, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> groupAddGroup(@NonNull G group, @NonNull String groupName, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> groupRemoveGroup(@NonNull G group, @NonNull String groupName, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> groupSetPrefix(@NonNull G group, @Nullable String prefix, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> groupSetSuffix(@NonNull G group, @Nullable String suffix, @NonNull PropertyExtractor properties);
    protected abstract @NonNull CompletableFuture<Void> groupSetMetadata(@NonNull G group, @NonNull String key, @Nullable String value, @NonNull PropertyExtractor properties);

    protected SimplePermissionNode.@NonNull Builder buildPermission(@NonNull String permission) {
        return new SimplePermissionNode.Builder(permission);
    }

    protected SimpleGroupMembership.@NonNull Builder buildGroupMembership(@NonNull G group) {
        return new SimpleGroupMembership.Builder(new GroupImpl(group));
    }

    @Override
    public @NonNull Users users() {
        return new UsersImpl();
    }

    @Override
    public @NonNull Groups groups() {
        return new GroupsImpl();
    }

    private final class UsersImpl implements Users {

        @Override
        public @NonNull User get(@NonNull Player player) {
            U user = AbstractPermissionService.this.getUser(player);
            return new UserImpl(player.getUniqueId(), user);
        }

        @Override
        public @Nullable User get(@NonNull UUID uniqueId) {
            U user = AbstractPermissionService.this.getUser(uniqueId);
            return user == null ? null : new UserImpl(uniqueId, user);
        }

        @Override
        public @NonNull FutureResult<User> load(@NonNull UUID uniqueId) {
            CompletableFuture<U> future = AbstractPermissionService.this.loadUser(uniqueId);
            return new CompletableFutureResult<>(future.thenApply(u -> new UserImpl(uniqueId, u)));
        }
    }

    private final class GroupsImpl implements Groups {

        @Override
        public @NonNull Collection<Group> all() {
            List<Group> groups = new ArrayList<>();
            for (G group : AbstractPermissionService.this.getGroups()) {
                groups.add(new GroupImpl(group));
            }
            return groups;
        }

        @Override
        public @Nullable Group get(@NonNull String name) {
            G group = AbstractPermissionService.this.getGroup(name);
            return group == null ? null : new GroupImpl(group);
        }

        @Override
        public @NonNull FutureResult<Group> load(@NonNull String name) {
            CompletableFuture<G> future = AbstractPermissionService.this.loadGroup(name);
            return new CompletableFutureResult<>(future.thenApply(GroupImpl::new));
        }
    }

    private Map<Property<?>, Object> getProperties(Consumer<PropertyBuilder> properties) {
        Map<Property<?>, Object> props = new HashMap<>();
        properties.accept(new PropertyBuilder() {
            @Override
            public boolean supports(@NonNull Property<?> property) {
                return AbstractPermissionService.this.supportsProperty(property);
            }

            @Override
            public @NonNull <T> PropertyBuilder withIfSupported(@NonNull Property<T> property, @Nullable T value) {
                if (!supports(property)) {
                    return this;
                }
                props.put(property, value);
                return this;
            }
        });
        return props;
    }

    private final class UserImpl implements User {
        private final UUID uniqueId;
        private final U user;

        private UserImpl(UUID uniqueId, U user) {
            this.uniqueId = uniqueId;
            this.user = user;
        }

        @Override
        public @NonNull UUID getUniqueId() {
            return this.uniqueId;
        }

        @Override
        public @Nullable String getUsername() {
            return AbstractPermissionService.this.userGetUsername(this.uniqueId, this.user);
        }

        @Override
        public @Nullable Group getPrimaryGroup() {
            G group = AbstractPermissionService.this.userGetPrimaryGroup(this.user);
            return group == null ? null : new GroupImpl(group);
        }

        @Override
        public @NonNull Collection<PermissionNode> getPermissions() {
            return Collections.unmodifiableList(new ArrayList<>(userGetPermissions(this.user)));
        }

        @Override
        public @NonNull Collection<GroupMembership> getGroups() {
            return Collections.unmodifiableList(new ArrayList<>(AbstractPermissionService.this.userGetGroupMemberships(this.user)));
        }

        @Override
        public boolean checkPermission(@NonNull String permission) {
            return AbstractPermissionService.this.userCheckPermission(this.user, permission);
        }

        @Override
        public @NonNull FutureAction setPermission(@NonNull String permission, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.userSetPermission(this.user, permission, props));
        }

        @Override
        public @NonNull FutureAction unsetPermission(@NonNull PermissionNode permission) {
            PropertyExtractor props = new PropertyExtractor(permission.properties());
            return new CompletableFutureAction(AbstractPermissionService.this.userUnsetPermission(this.user, permission.getPermission(), props));
        }

        @Override
        public @NonNull FutureAction addGroup(@NonNull Group group, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.userAddGroup(this.user, group.getName(), props));
        }

        @Override
        public @NonNull FutureAction removeGroup(@NonNull GroupMembership group) {
            PropertyExtractor props = new PropertyExtractor(group.properties());
            return new CompletableFutureAction(AbstractPermissionService.this.userRemoveGroup(this.user, group.getGroup().getName(), props));
        }

        @Override
        public @Nullable String getPrefix() {
            return AbstractPermissionService.this.userGetPrefix(this.user);
        }

        @Override
        public @Nullable String getSuffix() {
            return AbstractPermissionService.this.userGetSuffix(this.user);
        }

        @Override
        public @Nullable String getMetadata(@NonNull String key) {
            return AbstractPermissionService.this.userGetMetadata(this.user, key);
        }

        @Override
        public @NonNull FutureAction setPrefix(@Nullable String prefix, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.userSetPrefix(this.user, prefix, props));
        }

        @Override
        public @NonNull FutureAction setSuffix(@Nullable String suffix, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.userSetSuffix(this.user, suffix, props));
        }

        @Override
        public @NonNull FutureAction setMetadata(@NonNull String key, @Nullable String value, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.userSetMetadata(this.user, key, value, props));
        }
    }

    private final class GroupImpl implements Group {
        private final G group;

        private GroupImpl(G group) {
            this.group = group;
        }

        @Override
        public @NonNull String getName() {
            return AbstractPermissionService.this.groupGetName(this.group);
        }

        @Override
        public @NonNull Collection<PermissionNode> getPermissions() {
            return Collections.unmodifiableList(new ArrayList<>(groupGetPermissions(this.group)));
        }

        @Override
        public @NonNull Collection<GroupMembership> getGroups() {
            return Collections.unmodifiableList(new ArrayList<>(AbstractPermissionService.this.groupGetGroupMemberships(this.group)));
        }

        @Override
        public boolean checkPermission(@NonNull String permission) {
            return AbstractPermissionService.this.groupCheckPermission(this.group, permission);
        }

        @Override
        public @NonNull FutureAction setPermission(@NonNull String permission, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.groupSetPermission(this.group, permission, props));
        }

        @Override
        public @NonNull FutureAction unsetPermission(@NonNull PermissionNode permission) {
            PropertyExtractor props = new PropertyExtractor(permission.properties());
            return new CompletableFutureAction(AbstractPermissionService.this.groupUnsetPermission(this.group, permission.getPermission(), props));
        }

        @Override
        public @NonNull FutureAction addGroup(@NonNull Group group, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.groupAddGroup(this.group, group.getName(), props));
        }

        @Override
        public @NonNull FutureAction removeGroup(@NonNull GroupMembership group) {
            PropertyExtractor props = new PropertyExtractor(group.properties());
            return new CompletableFutureAction(AbstractPermissionService.this.groupRemoveGroup(this.group, group.getGroup().getName(), props));
        }

        @Override
        public @Nullable String getPrefix() {
            return AbstractPermissionService.this.groupGetPrefix(this.group);
        }

        @Override
        public @Nullable String getSuffix() {
            return AbstractPermissionService.this.groupGetSuffix(this.group);
        }

        @Override
        public @Nullable String getMetadata(@NonNull String key) {
            return AbstractPermissionService.this.groupGetMetadata(this.group, key);
        }

        @Override
        public @NonNull FutureAction setPrefix(@Nullable String prefix, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.groupSetPrefix(this.group, prefix, props));
        }

        @Override
        public @NonNull FutureAction setSuffix(@Nullable String suffix, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.groupSetSuffix(this.group, suffix, props));
        }

        @Override
        public @NonNull FutureAction setMetadata(@NonNull String key, @Nullable String value, @NonNull Consumer<PropertyBuilder> properties) {
            PropertyExtractor props = new PropertyExtractor(getProperties(properties));
            return new CompletableFutureAction(AbstractPermissionService.this.groupSetMetadata(this.group, key, value, props));
        }
    }
}
