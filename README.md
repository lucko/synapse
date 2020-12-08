# synapse

API for interacting with Bukkit permission plugins.

### Example usage

```java
package me.lucko.test;

import com.google.common.collect.ImmutableSet;
import me.lucko.synapse.context.Context;
import me.lucko.synapse.context.ContextCalculator;
import me.lucko.synapse.context.ContextService;
import me.lucko.synapse.permission.PermissionService;
import me.lucko.synapse.permission.membership.GroupMembership;
import me.lucko.synapse.permission.node.PermissionNode;
import me.lucko.synapse.permission.property.Property;
import me.lucko.synapse.permission.subject.Group;
import me.lucko.synapse.permission.subject.User;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Example extends JavaPlugin {

  private PermissionService permissions;
  private ContextService contexts;

  @Override
  public void onEnable() {
    // 'PermissionService' is intended to replace the functionality provided by the
    // Vault Permissions API.
    // It should be easily implementable by (or on behalf of) all existing plugins.
    // In theory, we should even be able to use Vault to implement it, and vice-versa.
    this.permissions = getServer().getServicesManager().load(PermissionService.class);

    // 'ContextService' is intended for modern (permission) plugins.
    // I guess it'll mostly target PEX and LP - but synapse will provide a default
    // implementation too, so other plugins can make use of contexts even if a supported
    // perms plugin isn't installed.
    this.contexts = getServer().getServicesManager().load(ContextService.class);

    // We can register context calculators quite easily:
    // This is bound to our plugin instance, so the provider can automatically cleanup
    // if this plugin gets unloaded.
    this.contexts.registerContext(this, "myplugin:gamemode", new ContextCalculator() {
      @Override
      public void calculate(Player target, Consumer<String> consumer) {
        consumer.accept(target.getGameMode().name().toLowerCase(Locale.ROOT));
      }

      @Override
      public Collection<String> possibleValues() {
        return Arrays.stream(GameMode.values())
            .map(gameMode -> gameMode.name().toLowerCase(Locale.ROOT))
            .collect(Collectors.toList());
      }
    });
  }

  public void doSomethingWithPermissions(Player player) {
    // easily get an object containing all permissions data for a player.
    User user = this.permissions.users().get(player);

    // then set a permission!
    user.setPermission("test").whenComplete(this, () -> {
      player.sendMessage("wow you have a new permission!!");
    });

    // set permissions with special properties - like negation or an expiry!
    Instant expiryTime = Instant.now().plus(1, ChronoUnit.HOURS);
    user.setPermission("example.permission", props ->
        props.with(Property.NEGATED, true).with(Property.EXPIRY, expiryTime)
    );

    // get groups!
    Group adminGroup = this.permissions.groups().get("admin");
    if (adminGroup == null) {
      throw new RuntimeException("oh no!");
    }

    // add users to groups, again - with properties!
    user.addGroup(adminGroup, props -> props.with(Property.REQUIRED_WORLD, "nether"));

    // also supports contexts!
    user.setPermission("fly.allow", props ->
        props.with(Property.REQUIRED_CONTEXT, ImmutableSet.of(Context.of("gamemode", "creative")))
    );

    // have a look at their permissions
    for (PermissionNode permission : user.getPermissions()) {
      player.sendMessage("you have permission " + permission.getPermission());
      player.sendMessage("it has the following properties! " + permission.properties());
    }

    // and their group memberships!
    for (GroupMembership membership : user.getGroups()) {
      player.sendMessage("you are in the group " + membership.getGroup().getName());
      player.sendMessage("it has the following properties! " + membership.properties());
    }

    // query prefixes/suffixes/metadata
    String prefix = user.getPrefix();
    String suffix = user.getSuffix();
    String homes = user.getMetadata("homes");

    // and set these in the same way...
    user.setPrefix("[ADMIN]", props -> props.with(Property.REQUIRED_SERVER, "survival"));
  }

  public void doSomethingWithContexts(Player player) {
    // you can query contexts for players quite easily!
    Set<Context> contexts = this.contexts.queryContexts(player);

    for (Context context : contexts) {
      player.sendMessage(context.key() + " -> " + context.value());
    }
  }
}

```