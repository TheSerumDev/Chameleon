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
package dev.hypera.chameleon.logger;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Internal Chameleon logger wrapper.
 */
@Internal
public final class ChameleonInternalLogger implements ChameleonLogger {

    private static final @NotNull String CHAMELEON_PREFIX = "[Chameleon] ";

    private final @NotNull ChameleonLogger logger;

    /**
     * Chameleon internal logger constructor.
     *
     * @param logger Chameleon logger instance to use.
     */
    @Internal
    public ChameleonInternalLogger(@NotNull ChameleonLogger logger) {
        this.logger = logger;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void info(@NotNull String message, @NotNull Object... o) {
        this.logger.info(CHAMELEON_PREFIX + message, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(@NotNull String message, @NotNull Object... o) {
        this.logger.debug(CHAMELEON_PREFIX + message, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(@NotNull String message, @NotNull Object... o) {
        this.logger.warn(CHAMELEON_PREFIX + message, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(@NotNull String message, @NotNull Throwable throwable, @NotNull Object... o) {
        this.logger.warn(CHAMELEON_PREFIX + message, throwable, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(@NotNull String message, @NotNull Object... o) {
        this.logger.error(CHAMELEON_PREFIX + message, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(@NotNull String message, @NotNull Throwable throwable, @NotNull Object... o) {
        this.logger.error(CHAMELEON_PREFIX + message, throwable, o);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ChameleonLogger enableDebug() {
        this.logger.enableDebug();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ChameleonLogger disableDebug() {
        this.logger.disableDebug();
        return this;
    }

}
