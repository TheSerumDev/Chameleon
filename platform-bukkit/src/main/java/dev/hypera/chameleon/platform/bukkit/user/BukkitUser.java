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
package dev.hypera.chameleon.platform.bukkit.user;

import dev.hypera.chameleon.platform.PlatformChameleon;
import dev.hypera.chameleon.platform.server.GameMode;
import dev.hypera.chameleon.user.ServerUser;
import dev.hypera.chameleon.util.Preconditions;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit server user implementation.
 */
public final class BukkitUser implements ServerUser, ForwardingAudience.Single {

    private final @NotNull PlatformChameleon<JavaPlugin> chameleon;
    private final @NotNull Player player;
    private final @NotNull Audience audience;

    /**
     * Bukkit user constructor.
     *
     * @param chameleon Bukkit Chameleon implementation.
     * @param player    Player to be wrapped.
     */
    @Internal
    public BukkitUser(@NotNull PlatformChameleon<JavaPlugin> chameleon, @NotNull Player player) {
        this.chameleon = chameleon;
        this.player = player;
        this.audience = chameleon.getAdventure().player(player.getUniqueId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getName() {
        return Objects.requireNonNull(this.player.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasInteractiveChat() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull UUID getId() {
        return this.player.getUniqueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Optional<SocketAddress> getAddress() {
        return Optional.ofNullable(this.player.getAddress());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLatency() {
        return this.player.getPing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(@NotNull String channel, byte[] data) {
        Preconditions.checkNotNull("channel", channel);
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(this.chameleon.getPlatformPlugin(), channel)) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this.chameleon.getPlatformPlugin(), channel);
        }

        this.player.sendPluginMessage(this.chameleon.getPlatformPlugin(), channel, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(@NotNull Component reason) {
        Preconditions.checkNotNull("reason", reason);
        this.player.kickPlayer(LegacyComponentSerializer.legacySection().serialize(reason));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(@NotNull String permission) {
        Preconditions.checkNotNull("permission", permission);
        return this.player.hasPermission(permission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull GameMode getGameMode() {
        return convertGameModeToChameleon(this.player.getGameMode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGameMode(@NotNull GameMode gameMode) {
        Preconditions.checkNotNull("gameMode", gameMode);
        this.player.setGameMode(convertGameModeToBukkit(gameMode));
    }

    /**
     * Get the Bukkit player for this user.
     *
     * @return Bukkit player.
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * Get the audience for this user.
     *
     * @return audience.
     */
    @Override
    public @NotNull Audience audience() {
        return this.audience;
    }

    private @NotNull org.bukkit.GameMode convertGameModeToBukkit(@NotNull GameMode gameMode) {
        switch (gameMode) {
            case CREATIVE:
                return org.bukkit.GameMode.CREATIVE;
            case ADVENTURE:
                return org.bukkit.GameMode.ADVENTURE;
            case SPECTATOR:
                return org.bukkit.GameMode.SPECTATOR;
            default:
                return org.bukkit.GameMode.SURVIVAL;
        }
    }

    private @NotNull GameMode convertGameModeToChameleon(@NotNull org.bukkit.GameMode gameMode) {
        switch (gameMode) {
            case CREATIVE:
                return GameMode.CREATIVE;
            case ADVENTURE:
                return GameMode.ADVENTURE;
            case SPECTATOR:
                return GameMode.SPECTATOR;
            default:
                return GameMode.SURVIVAL;
        }
    }

}
