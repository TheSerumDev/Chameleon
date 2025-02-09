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
package dev.hypera.chameleon.platform.nukkit.command;

import cn.nukkit.command.CommandSender;
import dev.hypera.chameleon.command.Command;
import dev.hypera.chameleon.command.context.ContextImpl;
import dev.hypera.chameleon.platform.nukkit.NukkitChameleon;
import java.util.Arrays;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Nukkit command wrapper.
 */
@Internal
public final class NukkitCommand extends cn.nukkit.command.Command {

    private final @NotNull NukkitChameleon chameleon;
    private final @NotNull Command command;

    /**
     * Nukkit command constructor.
     *
     * @param chameleon Nukkit Chameleon implementation.
     * @param command   Command to wrap.
     */
    @Internal
    public NukkitCommand(@NotNull NukkitChameleon chameleon, @NotNull Command command) {
        super(command.getName(), "", null,
            command.getAliases().toArray(new String[0]));
        this.chameleon = chameleon;
        this.command = command;
    }

    /**
     * Execute the command.
     *
     * @param sender       the sender who issued this command.
     * @param commandLabel the commandLabel used when issuing this command.
     * @param args         the command arguments.
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1 || this.command.executeSubCommand(new ContextImpl(
            this.chameleon.getUserManager().wrap(sender), this.chameleon,
            Arrays.copyOfRange(args, 1, args.length)), args[0]
        )) {
            this.command.executeCommand(new ContextImpl(
                this.chameleon.getUserManager().wrap(sender),
                this.chameleon, args
            ));
        }

        return true;
    }

}
