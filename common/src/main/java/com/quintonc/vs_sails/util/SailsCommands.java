package com.quintonc.vs_sails.util;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class SailsCommands {


    public static boolean debug = false;
    public static String debugType = "none";



    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("vs_sails")
                .requires(source -> source.hasPermission(2))
                .then(literal("debug")
                    .requires(source -> source.hasPermission(2))
                    .then(literal("wind_stats")
                        .executes(context -> {
                            debug = true;
                            debugType = "wind_stats";
                            context.getSource().sendSuccess(() -> Component.literal("Valkyrien Sails debug set to: wind_stats"), false);
                            return 1;
                        })
                    )
                    .then(literal("ship_angles")
                        .executes(context -> {
                            debug = true;
                            debugType = "ship_angles";
                            context.getSource().sendSuccess(() -> Component.literal("Valkyrien Sails debug set to: ship_angles"), false);
                            return 1;
                        })
                    )
                    .then(literal("fluid_stats")
                            .executes(context -> {
                                debug = true;
                                debugType = "fluid_stats";
                                context.getSource().sendSuccess(() -> Component.literal("Valkyrien Sails debug set to: fluid_stats"), false);
                                return 1;
                            })
                    )
                    .then(literal("none")
                        .executes(context -> {
                            debug = false;
                            debugType = "none";
                            context.getSource().sendSuccess(() -> Component.literal("Valkyrien Sails debug set to: none"), false);
                            return 1;
                        })
                    )
                )
        );
    }

}
