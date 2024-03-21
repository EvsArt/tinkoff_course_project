package edu.java.bot.constants;

import edu.java.bot.commands.Command;
import edu.java.bot.links.Link;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringService {

    public static final String COMMAND_NOT_EXISTS = "Command not exists";
    public static final String COMMAND_START_NAME = "start";
    public static final String COMMAND_START_DESCRIPTION = "User registration";
    public static final String COMMAND_START_SUCCESSFUL_REGISTRATION_MESSAGE = "You was registered!";
    public static final String COMMAND_START_HELPMESSAGE = String.format(
        "Using %s%s without arguments for user registration",
        Constants.COMMAND_TRIGGER,
        COMMAND_START_NAME
    );
    public static final String PLEASE_REGISTER =
        String.format("Please, registry firstly %s%s", Constants.COMMAND_TRIGGER, COMMAND_START_NAME);
    public static final String COMMAND_HELP_NAME = "help";
    public static final String COMMAND_HELP_DESCRIPTION = "Print list of available commands";
    public static final String COMMAND_HELP_HELPMESSAGE = String.format(
        "Using %s%s without arguments for print list of available commands",
        Constants.COMMAND_TRIGGER,
        COMMAND_HELP_NAME
    );
    public static final String COMMAND_LIST_NAME = "list";
    public static final String COMMAND_LIST_DESCRIPTION = "Print tracking list";
    public static final String COMMAND_LIST_HELPMESSAGE = String.format(
        "Using %s%s without arguments printing tracking links",
        Constants.COMMAND_TRIGGER,
        COMMAND_LIST_NAME
    );
    public static final String COMMAND_NOT_A_COMMAND_NAME = "not-a-command";
    public static final String COMMAND_NOT_A_COMMAND_DESCRIPTION = "";
    public static final String COMMAND_NOT_A_COMMAND_NOT_SUPPORTS_MESSAGE =
        "Suddenly bot doesn't support not-command messages";
    public static final String COMMAND_TRACK_NAME = "track";
    public static final String COMMAND_TRACK_DESCRIPTION = "Start url tracking";
    public static final Map<String, String> COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION = Map.of(
        "url", "Link of the page you want to start tracking",
        "name", "Your url name (Optional)"
    );
    public static final String COMMAND_TRACK_HELPMESSAGE = String.format(
        """
            Using: %s%s url [, name] or %s%s url
            Arguments:
            %s
            """,
        Constants.COMMAND_TRIGGER,
        COMMAND_TRACK_NAME,
        Constants.COMMAND_TRIGGER,
        COMMAND_TRACK_NAME,
        argumentsToString(COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION)
    );
    public static final String COMMAND_UNTRACK_NAME = "untrack";
    public static final String COMMAND_UNTRACK_DESCRIPTION = "Stop url tracking";
    public static final String COMMAND_UNTRACK_LINK_NOT_TRACKED = "This url didn't track";
    public static final Map<String, String> COMMAND_UNTRACK_ARGUMENTS_TO_DESCRIPTION = Map.of(
        "linkName", "Name of the url you want to untrack"
    );
    public static final String COMMAND_UNTRACK_HELPMESSAGE = String.format(
        """
            Using: %s%s linkName
            Arguments:
            %s
            """,
        Constants.COMMAND_TRIGGER,
        COMMAND_UNTRACK_NAME,
        argumentsToString(COMMAND_UNTRACK_ARGUMENTS_TO_DESCRIPTION)
    );

    private StringService() {
    }

    public static String commandNotSupports(String command) {
        return String.format(
            "Command %s%s not supported!",
            Constants.COMMAND_TRIGGER,
            command
        );
    }

    public static String commandNameWithDescription(Command command) {
        return String.format(
            "%s%s - %s\n",
            Constants.COMMAND_TRIGGER,
            command.getName(),

            command.getDescription()
        );
    }

    public static String invalidTrackingLink(List<String> supportedServices) {
        StringBuilder builder = new StringBuilder();
        builder
            .append("Invalid url!\n")
            .append("Supported services:\n");
        supportedServices.forEach(it -> builder.append(String.format("-- %s\n", it)));
        return builder.toString();
    }

    public static String startTracking(Link link) {
        return String.format("Start tracking %s", link.url());
    }

    public static String endTracking(Link link) {
        return String.format("End tracking %s", link.url());
    }

    public static String errorWithTrackLink(Link link) {
        return String.format("Error with tracking link %s. Please try another one", link.url());
    }

    public static String errorWithUntrackLink(Link link) {
        return String.format("Error with untracking link %s. Please try another one", link.url());
    }

    public static String errorWithGettingLinks() {
        return "Error with getting links";
    }

    public static String linkNotExists(Link link) {
        return String.format("Error with untracking link %s. Link not exists", link.url());
    }

    private static String trackInList(Link link) {
        return String.format("-- %s: %s\n", link.name(), link.url());
    }

    public static String tracksToPrettyView(Set<Link> links) {
        StringBuilder builder = new StringBuilder();
        if (links.isEmpty()) {
            builder.append("There aren't any tracks");
        } else {
            builder.append("Your tracks:\n");
            links.forEach(track -> builder.append(StringService.trackInList(track)));
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
            Constants.COMMAND_TRIGGER,
            command.getName(),
            Constants.COMMAND_NEED_HELP_ARGUMENT
        );
    }

    private static String argumentsToString(Map<String, String> argumentsToDescription) {
        StringBuilder builder = new StringBuilder();
        argumentsToDescription.forEach((key, value) -> builder.append(String.format("%s: %s\n", key, value)));
        return builder.toString();
    }

    public static String receiveUpdate(String url, String description) {
        return "Received update!\n%s\n%s".formatted(url, description);
    }
}
