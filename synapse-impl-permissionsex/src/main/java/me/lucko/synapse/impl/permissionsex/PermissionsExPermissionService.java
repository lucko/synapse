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

package me.lucko.synapse.impl.permissionsex;

import me.lucko.synapse.impl.AbstractPermissionService;
import me.lucko.synapse.impl.PropertyExtractor;
import me.lucko.synapse.impl.SimpleGroupMembership;
import me.lucko.synapse.impl.SimplePermissionNode;
import me.lucko.synapse.permission.membership.GroupMembership;
import me.lucko.synapse.permission.node.PermissionNode;
import me.lucko.synapse.permission.property.Property;
import me.lucko.synapse.permission.property.PropertyScope;
import me.lucko.synapse.permission.subject.SubjectType;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import ru.tehkode.permissions.PermissionEntity;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.PermissionsData;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PermissionsExPermissionService extends AbstractPermissionService<PermissionUser, PermissionGroup> {
    private static final Method GET_DATA_METHOD;
    private static final Field TIMED_PERMISSIONS_FIELD;
    private static final Field TIMED_PERMISSIONS_TIME_FIELD;
    private static final Field NATIVE_INTERFACE_FIELD;
    static {
        try {
            GET_DATA_METHOD = PermissionEntity.class.getDeclaredMethod("getData");
            GET_DATA_METHOD.setAccessible(true);

            TIMED_PERMISSIONS_FIELD = PermissionEntity.class.getDeclaredField("timedPermissions");
            TIMED_PERMISSIONS_FIELD.setAccessible(true);

            TIMED_PERMISSIONS_TIME_FIELD = PermissionEntity.class.getDeclaredField("timedPermissionsTime");
            TIMED_PERMISSIONS_TIME_FIELD.setAccessible(true);

            NATIVE_INTERFACE_FIELD = PermissionManager.class.getDeclaredField("nativeI");
            NATIVE_INTERFACE_FIELD.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final PermissionsEx pex;

    public PermissionsExPermissionService(PermissionsEx pex) {
        this.pex = pex;
    }

    @Override
    public @NonNull String getProviderName() {
        return "PermissionsEx";
    }

    @Override
    protected @NonNull Executor getSyncExecutor() {
        return runnable -> this.pex.getServer().getScheduler().runTask(this.pex, runnable);
    }

    @Override
    public boolean supportsProperty(@NonNull SubjectType typeScope, @NonNull PropertyScope scope, @NonNull Property<?> property) {
        switch (scope) {
            case PERMISSION:
                return property == Property.NEGATED || property == Property.REQUIRED_WORLD || property == Property.EXPIRY;
            case GROUP_MEMBERSHIP:
                if (typeScope == SubjectType.USER) {
                    return property == Property.REQUIRED_WORLD || property == Property.EXPIRY;
                } else {
                    return property == Property.REQUIRED_WORLD;
                }
            case PREFIX_OR_SUFFIX:
            case METADATA:
                return property == Property.REQUIRED_WORLD;
            default:
                throw new AssertionError("unknown scope: " + scope);
        }
    }

    @Override
    protected @Nullable PermissionUser getUser(@NonNull UUID uniqueId) {
        return this.pex.getPermissionsManager().getUser(uniqueId);
    }

    @Override
    protected @NonNull CompletableFuture<PermissionUser> loadUser(@NonNull UUID uniqueId) {
        return CompletableFuture.completedFuture(getUser(uniqueId));
    }

    @Override
    protected @NonNull Iterable<PermissionGroup> getGroups() {
        return this.pex.getPermissionsManager().getGroupList();
    }

    @Override
    protected @Nullable PermissionGroup getGroup(@NonNull String name) {
        return this.pex.getPermissionsManager().getGroupNames().contains(name) ?
                this.pex.getPermissionsManager().getGroup(name) : null;
    }

    @Override
    protected @NonNull CompletableFuture<PermissionGroup> loadGroup(@NonNull String name) {
        return CompletableFuture.completedFuture(this.pex.getPermissionsManager().getGroup(name));
    }

    @Override
    protected @Nullable String userGetUsername(UUID uniqueId, PermissionUser user) {
        return user.getName();
    }

    @Override
    protected @Nullable PermissionGroup userGetPrimaryGroup(@NonNull PermissionUser user) {
        return user.getRankLadderGroup(null);
    }

    @Override
    protected @NonNull Collection<PermissionNode> userGetPermissions(@NonNull PermissionUser user) {
        return getPermissions(user);
    }

    @Override
    protected @NonNull Collection<GroupMembership> userGetGroupMemberships(@NonNull PermissionUser user) {
        return getGroupMemberships(user);
    }

    @Override
    protected boolean userCheckPermission(@NonNull PermissionUser user, @NonNull String permission) {
        return user.has(permission);
    }

    @Override
    protected @Nullable String userGetPrefix(@NonNull PermissionUser user) {
        return user.getPrefix();
    }

    @Override
    protected @Nullable String userGetSuffix(@NonNull PermissionUser user) {
        return user.getSuffix();
    }

    @Override
    protected @Nullable String userGetMetadata(@NonNull PermissionUser user, @NonNull String key) {
        return user.getOption(key);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userSetPermission(@NonNull PermissionUser user, @NonNull String permission, @NonNull PropertyExtractor properties) {
        boolean negated = properties.get(Property.NEGATED);
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        Instant expiry = properties.get(Property.EXPIRY);

        if (negated) {
            permission = "-" + permission;
        }

        if (expiry != null) {
            user.addTimedPermission(permission, requiredWorld, (int) Duration.between(Instant.now(), expiry).getSeconds());
        } else {
            user.addPermission(permission, requiredWorld);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userUnsetPermission(@NonNull PermissionUser user, @NonNull String permission, @NonNull PropertyExtractor properties) {
        boolean negated = properties.get(Property.NEGATED);
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        Instant expiry = properties.get(Property.EXPIRY);

        if (negated) {
            permission = "-" + permission;
        }

        if (expiry != null) {
            user.removeTimedPermission(permission, requiredWorld);
        } else {
            user.removePermission(permission, requiredWorld);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userAddGroup(@NonNull PermissionUser user, @NonNull String groupName, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        Instant expiry = properties.get(Property.EXPIRY);
        int lifetime = expiry == null ? 0 : (int) Duration.between(Instant.now(), expiry).getSeconds();

        user.addGroup(groupName, requiredWorld, lifetime);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userRemoveGroup(@NonNull PermissionUser user, @NonNull String groupName, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        user.removeGroup(groupName, requiredWorld);
        user.setOption("group-" + groupName + "-until", null, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userSetPrefix(@NonNull PermissionUser user, @Nullable String prefix, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        user.setPrefix(prefix, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userSetSuffix(@NonNull PermissionUser user, @Nullable String suffix, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        user.setSuffix(suffix, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> userSetMetadata(@NonNull PermissionUser user, @NonNull String key, @Nullable String value, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        user.setOption(key, value, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull String groupGetName(PermissionGroup group) {
        return group.getName();
    }

    @Override
    protected @Nullable String groupGetDisplayName(PermissionGroup group) {
        return null;
    }

    @Override
    protected @NonNull Collection<PermissionNode> groupGetPermissions(@NonNull PermissionGroup group) {
        return getPermissions(group);
    }

    @Override
    protected @NonNull Collection<GroupMembership> groupGetGroupMemberships(@NonNull PermissionGroup group) {
        return getGroupMemberships(group);
    }

    @Override
    protected boolean groupCheckPermission(@NonNull PermissionGroup group, @NonNull String permission) {
        return group.has(permission);
    }

    @Override
    protected @Nullable String groupGetPrefix(@NonNull PermissionGroup group) {
        return group.getPrefix();
    }

    @Override
    protected @Nullable String groupGetSuffix(@NonNull PermissionGroup group) {
        return group.getSuffix();
    }

    @Override
    protected @Nullable String groupGetMetadata(@NonNull PermissionGroup group, @NonNull String key) {
        return group.getOption(key);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupSetPermission(@NonNull PermissionGroup group, @NonNull String permission, @NonNull PropertyExtractor properties) {
        boolean negated = properties.get(Property.NEGATED);
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        Instant expiry = properties.get(Property.EXPIRY);

        if (negated) {
            permission = "-" + permission;
        }

        if (expiry != null) {
            group.addTimedPermission(permission, requiredWorld, (int) Duration.between(Instant.now(), expiry).getSeconds());
        } else {
            group.addPermission(permission, requiredWorld);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupUnsetPermission(@NonNull PermissionGroup group, @NonNull String permission, @NonNull PropertyExtractor properties) {
        boolean negated = properties.get(Property.NEGATED);
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        Instant expiry = properties.get(Property.EXPIRY);

        if (negated) {
            permission = "-" + permission;
        }

        if (expiry != null) {
            group.removeTimedPermission(permission, requiredWorld);
        } else {
            group.removePermission(permission, requiredWorld);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupAddGroup(@NonNull PermissionGroup group, @NonNull String groupName, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);

        List<String> parents = group.getOwnParentIdentifiers(requiredWorld);
        if (parents == null) {
            parents = new ArrayList<>();
        } else {
            parents = new ArrayList<>(parents);
        }
        if (!parents.contains(groupName)) {
            parents.add(groupName);
        }
        group.setParentsIdentifier(parents, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupRemoveGroup(@NonNull PermissionGroup group, @NonNull String groupName, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);

        List<String> parents = group.getOwnParentIdentifiers(requiredWorld);
        if (parents == null || parents.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        parents = new ArrayList<>(parents);
        parents.remove(groupName);

        group.setParentsIdentifier(parents, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupSetPrefix(@NonNull PermissionGroup group, @Nullable String prefix, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        group.setPrefix(prefix, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupSetSuffix(@NonNull PermissionGroup group, @Nullable String suffix, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        group.setSuffix(suffix, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected @NonNull CompletableFuture<Void> groupSetMetadata(@NonNull PermissionGroup group, @NonNull String key, @Nullable String value, @NonNull PropertyExtractor properties) {
        String requiredWorld = properties.get(Property.REQUIRED_WORLD);
        group.setOption(key, value, requiredWorld);
        return CompletableFuture.completedFuture(null);
    }

    private SimplePermissionNode.@NonNull Builder buildPossiblyNegatedPermission(@NonNull String permission) {
        if (permission.startsWith("-")) {
            return new SimplePermissionNode.Builder(permission.substring(1)).withProp(Property.NEGATED, true);
        } else {
            return new SimplePermissionNode.Builder(permission);
        }
    }

    private Collection<PermissionNode> getPermissions(PermissionEntity entity) {
        List<PermissionNode> nodes = new ArrayList<>();

        Map<String, List<String>> permanentPermissions;
        try {
            PermissionsData data = (PermissionsData) GET_DATA_METHOD.invoke(entity);
            permanentPermissions = data.getPermissionsMap();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, List<String>> entry : permanentPermissions.entrySet()) {
            String world = entry.getKey();
            for (String node : entry.getValue()) {
                SimplePermissionNode.Builder builder = buildPossiblyNegatedPermission(node);
                if (world != null) {
                    builder.withProp(Property.REQUIRED_WORLD, world);
                }
                nodes.add(builder.build());
            }
        }

        Map<String, List<String>> timedPermissions;
        Map<String, Long> timedPermissionsTime;

        try {
            timedPermissions = (Map<String, List<String>>) TIMED_PERMISSIONS_FIELD.get(entity);
            timedPermissionsTime = (Map<String, Long>) TIMED_PERMISSIONS_TIME_FIELD.get(entity);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, List<String>> worldData : timedPermissions.entrySet()) {
            String world = worldData.getKey();
            for (String node : worldData.getValue()) {
                long expiry = timedPermissionsTime.getOrDefault(world == null ? "" : world + ":" + node, 0L);

                SimplePermissionNode.Builder builder = buildPossiblyNegatedPermission(node);
                if (world != null) {
                    builder.withProp(Property.REQUIRED_WORLD, world);
                }
                builder.withProp(Property.EXPIRY, Instant.ofEpochSecond(expiry));
                nodes.add(builder.build());
            }
        }

        return nodes;
    }

    private Collection<GroupMembership> getGroupMemberships(PermissionEntity entity) {
        List<GroupMembership> memberships = new ArrayList<>();
        for (Map.Entry<String, List<PermissionGroup>> worldData : entity.getAllParents().entrySet()) {
            String world = worldData.getKey();

            for (PermissionGroup parent : worldData.getValue()) {
                String parentName = parent.getName();
                long expiry = 0L;

                // check for temporary parent
                if (entity instanceof PermissionUser) {
                    String expiryOption = entity.getOption("group-" + parentName + "-until", world);
                    if (expiryOption != null) {
                        try {
                            expiry = Long.parseLong(expiryOption);
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }

                SimpleGroupMembership.Builder builder = buildGroupMembership(parent);
                if (world != null) {
                    builder.withProp(Property.REQUIRED_WORLD, world);
                }
                if (expiry != 0) {
                    builder.withProp(Property.EXPIRY, Instant.ofEpochSecond(expiry));
                }
                memberships.add(builder.build());
            }
        }
        return memberships;
    }
}
