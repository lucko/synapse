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

import me.lucko.synapse.permission.subject.Group;
import me.lucko.synapse.permission.subject.MetadataSubject;
import me.lucko.synapse.permission.subject.PermissionSubject;

import java.util.function.Consumer;

/**
 * The scope a {@link Property} is able to apply in.
 */
public enum PropertyScope {

    /**
     * For {@link PermissionSubject#setPermission(String, Consumer)}.
     */
    PERMISSION,

    /**
     * For {@link PermissionSubject#addGroup(Group, Consumer)}.
     */
    GROUP_MEMBERSHIP,

    /**
     * For {@link MetadataSubject#setPrefix(String, Consumer)} or
     * {@link MetadataSubject#setSuffix(String, Consumer)}.
     */
    PREFIX_OR_SUFFIX,

    /**
     * For {@link MetadataSubject#setMetadata(String, String, Consumer)}
     */
    METADATA

}
