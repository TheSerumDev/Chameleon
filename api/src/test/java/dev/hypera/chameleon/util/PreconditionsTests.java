/*
 * This file is a part of the Chameleon Framework, licensed under the MIT License.
 *
 * Copyright (c) 2021-2023 The Chameleon Framework Authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.hypera.chameleon.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Collections;
import org.junit.jupiter.api.Test;

final class PreconditionsTests {

    @Test
    void checkArgument() {
        assertDoesNotThrow(() -> Preconditions.checkArgument(true));
        assertDoesNotThrow(() -> Preconditions.checkArgument(true, "test"));
        assertDoesNotThrow(() -> Preconditions.checkArgument(true, "Hello, %s!", "world"));
        assertThrowsExactly(IllegalArgumentException.class, () ->
            Preconditions.checkArgument(false));
        assertThrowsExactly(IllegalArgumentException.class, () ->
            Preconditions.checkArgument(false, "test"), "test");
        assertThrowsExactly(IllegalArgumentException.class, () ->
            Preconditions.checkArgument(false, "Hello, %s!", "world"), "Hello, world!");
    }

    @Test
    void checkState() {
        assertDoesNotThrow(() -> Preconditions.checkState(true));
        assertDoesNotThrow(() -> Preconditions.checkState(true, "test"));
        assertDoesNotThrow(() -> Preconditions.checkState(true, "Hello, %s!", "world"));
        assertThrowsExactly(IllegalStateException.class, () ->
            Preconditions.checkState(false));
        assertThrowsExactly(IllegalStateException.class, () ->
            Preconditions.checkState(false, "test"), "test");
        assertThrowsExactly(IllegalStateException.class, () ->
            Preconditions.checkState(false, "Hello, %s!", "world"), "Hello, world!");
    }

    @Test
    void checkNotNull() {
        assertDoesNotThrow(() -> Preconditions.checkNotNull("value"));
        assertDoesNotThrow(() -> Preconditions.checkNotNull("value", "test"));
        assertDoesNotThrow(() -> Preconditions.checkNotNullState("value", "test"));
        assertThrowsExactly(NullPointerException.class, () ->
            Preconditions.checkNotNull(null));
        assertThrowsExactly(IllegalArgumentException.class, () ->
            Preconditions.checkNotNull("test", null), "test");
        assertThrowsExactly(IllegalStateException.class, () ->
            Preconditions.checkNotNullState("test", null), "test");
    }

    @Test
    void checkNoneNull() {
        assertDoesNotThrow(() -> Preconditions.checkNoneNull("test", Collections.emptySet()));
        assertThrowsExactly(IllegalArgumentException.class, () ->
            Preconditions.checkNoneNull("test", null), "test cannot be null");
        assertThrowsExactly(IllegalArgumentException.class, () ->
            Preconditions.checkNoneNull("test", Collections.singleton(null)), "test cannot contain null");
    }

}
