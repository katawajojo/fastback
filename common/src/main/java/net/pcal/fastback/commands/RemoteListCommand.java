/*
 * FastBack - Fast, incremental Minecraft backups powered by Git.
 * Copyright (C) 2022 pcal.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package net.pcal.fastback.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.pcal.fastback.logging.UserLogger;
import net.pcal.fastback.logging.UserMessage;
import net.pcal.fastback.repo.SnapshotId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;
import static net.pcal.fastback.commands.Commands.SUCCESS;
import static net.pcal.fastback.commands.Commands.gitOp;
import static net.pcal.fastback.commands.Commands.subcommandPermission;
import static net.pcal.fastback.config.OtherConfigKey.REMOTE_PUSH_URL;
import static net.pcal.fastback.utils.Executor.ExecutionLock.NONE;

enum RemoteListCommand implements Command {

    INSTANCE;

    private static final String COMMAND_NAME = "remote-list";

    @Override
    public void register(final LiteralArgumentBuilder<ServerCommandSource> argb, PermissionsFactory<ServerCommandSource> pf) {
        argb.then(
                literal(COMMAND_NAME).
                        requires(subcommandPermission(COMMAND_NAME, pf)).
                        executes(RemoteListCommand::execute)
        );
    }

    private static int execute(final CommandContext<ServerCommandSource> cc) {
        final UserLogger log = UserLogger.ulog(cc);
        gitOp(NONE, log, repo -> {
            final List<SnapshotId> snapshots = new ArrayList<>(repo.getRemoteSnapshots());
            Collections.sort(snapshots);
            snapshots.forEach(sid -> log.message(UserMessage.raw(sid.getShortName())));
            log.message(UserMessage.localized("fastback.chat.remote-list-done", snapshots.size(), repo.getConfig().getString(REMOTE_PUSH_URL)));
        });
        return SUCCESS;
    }
}
