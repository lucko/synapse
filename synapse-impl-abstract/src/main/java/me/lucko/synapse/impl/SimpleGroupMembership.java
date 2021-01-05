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

import me.lucko.synapse.permission.membership.GroupMembership;
import me.lucko.synapse.permission.property.Property;
import me.lucko.synapse.permission.subject.Group;

import java.util.HashMap;
import java.util.Map;

public class SimpleGroupMembership extends AbstractPropertyQueryable implements GroupMembership {
    private final Group group;

    SimpleGroupMembership(Group group, Map<Property<?>, Object> properties) {
        super(properties);
        this.group = group;
    }

    @Override
    public Group getGroup() {
        return this.group;
    }

    public static final class Builder {
        private final Group group;
        private final Map<Property<?>, Object> properties = new HashMap<>();

        public Builder(Group group) {
            this.group = group;
        }

        public <T> Builder withProp(Property<T> property, T value) {
            this.properties.put(property, value);
            return this;
        }

        public GroupMembership build() {
            return new SimpleGroupMembership(this.group, this.properties);
        }
    }
}
