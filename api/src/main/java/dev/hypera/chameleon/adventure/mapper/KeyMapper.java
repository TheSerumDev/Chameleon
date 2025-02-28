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
package dev.hypera.chameleon.adventure.mapper;

import dev.hypera.chameleon.util.Preconditions;
import java.lang.reflect.Method;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adventure Key mapper.
 */
public final class KeyMapper implements Mapper<Key> {

    private @Nullable Method keyCreateMethod;
    private @Nullable Method keyAsStringMethod;

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws ReflectiveOperationException {
        Preconditions.checkState(!isLoaded(), "mapper has already been loaded");
        Class<?> keyClass = Class.forName(AdventureMapper.ORIGINAL_KEY_CLASS_NAME);
        this.keyCreateMethod = keyClass.getMethod("key", String.class);
        this.keyAsStringMethod = keyClass.getMethod("asString");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded() {
        return this.keyCreateMethod != null && this.keyAsStringMethod != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Object map(@NotNull Key key) throws ReflectiveOperationException {
        Preconditions.checkState(isLoaded(), "mapper has not been loaded");
        Preconditions.checkNotNull("key", key);
        return Objects.requireNonNull(this.keyCreateMethod).invoke(null, key.asString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Key mapBackwards(@NotNull Object key) throws ReflectiveOperationException {
        Preconditions.checkState(isLoaded(), "mapper has not been loaded");
        Preconditions.checkNotNull("key", key);
        @Subst("key") String str = (String) Objects.requireNonNull(this.keyAsStringMethod).invoke(key);
        return Key.key(str);
    }

}
