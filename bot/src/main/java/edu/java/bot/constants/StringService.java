package edu.java.bot.constants;

import edu.java.bot.commands.Command;
import edu.java.bot.tracks.Track;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringService {

    private StringService() {
    }

    public static final String TEXT_PARAMETER_IN_REQUEST = "text";

    public static final String COMMAND_NOT_EXISTS = "Command not exists";
    public static final String COMMAND_TRIGGER = "/";
    public static final String COMMAND_NEED_HELP_ARGUMENT = "-h";

    public static final String COMMAND_START_NAME = "start";
    public static final String COMMAND_START_DESCRIPTION = "User registration";
    public static final String COMMAND_START_SUCCESSFUL_REGISTRATION_MESSAGE = "You was registered!";
    public static final String COMMAND_START_HELPMESSAGE = String.format(
        "Using %s%s without arguments for user registration",
        COMMAND_TRIGGER,
        COMMAND_START_NAME
    );
    public static final String PLEASE_REGISTER =
        String.format("Please, registry firstly %s%s", COMMAND_TRIGGER, COMMAND_START_NAME);

    public static final String COMMAND_HELP_NAME = "help";
    public static final String COMMAND_HELP_DESCRIPTION = "Print list of available commands";
    public static final String COMMAND_HELP_HELPMESSAGE = String.format(
        "Using %s%s without arguments for print list of available commands",
        COMMAND_TRIGGER,
        COMMAND_HELP_NAME
    );

    public static final String COMMAND_LIST_NAME = "list";
    public static final String COMMAND_LIST_DESCRIPTION = "Print tracking list";
    public static final String COMMAND_LIST_HELPMESSAGE = String.format(
        "Using %s%s without arguments printing tracking links",
        COMMAND_TRIGGER,
        COMMAND_LIST_NAME
    );

    public static final String COMMAND_NOT_A_COMMAND_NAME = "not-a-command";
    public static final String COMMAND_NOT_A_COMMAND_DESCRIPTION = "";
    public static final String COMMAND_NOT_A_COMMAND_NOT_SUPPORT_MESSAGE =
        "Suddenly bot doesn't support not-command messages";

    public static final String COMMAND_TRACK_NAME = "track";
    public static final String COMMAND_TRACK_DESCRIPTION = "Start link tracking";
    public static final Map<String, String> COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION = Map.of(
        "link", "Link of the page you want to start tracking",
        "name", "Your link name (Optional)"
    );

    public static final String COMMAND_TRACK_HELPMESSAGE = String.format(
        """
            Using: %s%s link [, name] or %s%s link
            Arguments:
            %s
            """,
        COMMAND_TRIGGER,
        COMMAND_TRACK_NAME,
        COMMAND_TRIGGER,
        COMMAND_TRACK_NAME,
        argumentsToString(COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION)
    );

    public static final String COMMAND_UNTRACK_NAME = "untrack";
    public static final String COMMAND_UNTRACK_DESCRIPTION = "Stop link tracking";
    public static final String COMMAND_UNTRACK_LINK_NOT_TRACKED = "This link didn't track";
    public static final Map<String, String> COMMAND_UNTRACK_ARGUMENTS_TO_DESCRIPTION = Map.of(
        "linkName", "Name of the link you want to untrack"
    );

    public static final String COMMAND_UNTRACK_HELPMESSAGE = String.format(
        """
            Using: %s%s linkName
            Arguments:
            %s
            """,
        COMMAND_TRIGGER,
        COMMAND_UNTRACK_NAME,
        argumentsToString(COMMAND_UNTRACK_ARGUMENTS_TO_DESCRIPTION)
    );

    public static String commandNotSupports(String command) {
        return String.format(
            "Command %s%s not supported!",
            COMMAND_TRIGGER,
            command
        );
    }

    public static String commandNameWithDescription(Command command) {
        return String.format(
            "%s%s - %s\n",
            COMMAND_TRIGGER,
            command.getName(),

            command.getDescription()
        );
    }

    public static String invalidTrackingLink(List<String> supportedServices) {
        StringBuilder builder = new StringBuilder();
        builder
            .append("Invalid link!\n")
            .append("Supported services:\n");
        supportedServices.forEach(it -> builder.append(String.format("-- %s\n", it)));
        return builder.toString();
    }

    public static String startTracking(Track track) {
        return String.format("Start tracking %s", track.link());
    }

    public static String endTracking(Track track) {
        return String.format("End tracking %s", track.link());
    }

    public static String trackInList(Track track) {
        return String.format("-- %s: %s\n", track.name(), track.link());
    }

    public static String tracksToPrettyView(Set<Track> tracks) {
        StringBuilder builder = new StringBuilder();
        if (tracks.isEmpty()) {
            builder.append("There aren't any tracks");
        } else {
            builder.append("Your tracks:\n");
            tracks.forEach(track -> builder.append(StringService.trackInList(track)));
        }
        return builder.toString();
    }

    public static String availableCommandsToPrettyView(List<Command> availableCommands) {
        StringBuilder builder = new StringBuilder();
        builder.append("Available commands list:\n");
        availableCommands.forEach(command -> builder.append(
            StringService.commandNameWithDescription(command)
        ));

        return builder.toString();
    }

    public static String commandNeedHelp(Command command) {
        return String.format(
            "Use %s%s %s for getting help with command",
            COMMAND_TRIGGER,
            command.getName(),
            COMMAND_NEED_HELP_ARGUMENT
        );
    }

    private static String argumentsToString(Map<String, String> argumentsToDescription) {
        StringBuilder builder = new StringBuilder();
        argumentsToDescription.forEach((key, value) -> builder.append(String.format("%s: %s\n", key, value)));
        return builder.toString();
    }

}
