package re.goober.pinger;

import org.apache.commons.cli.*;
import re.goober.pinger.modes.Mode;
import re.goober.pinger.modes.ModeStorage;

public class Pinger {

    private static final Options OPTIONS = new Options();
    private static final CommandLineParser PARSER = new DefaultParser();
    private static final HelpFormatter FORMATTER = new HelpFormatter();
    private static final ModeStorage MODE_STORAGE = new ModeStorage();

    public static final Option TARGET = Option.builder("t")
            .argName("ip address")
            .longOpt("target")
            .hasArg()
            .desc("Specify target to ping")
            .required()
            .build();

    public static final Option PORT = Option.builder("p")
            .argName("tcp port")
            .longOpt("port")
            .hasArg()
            .desc("Specify port to ping")
            .build();

    public static final Option BODY = Option.builder("b")
            .argName("body")
            .longOpt("body")
            .hasArg()
            .desc("Specify payload to ping with")
            .build();

    public static final Option TIMEOUT = Option.builder("to")
            .argName("response timeout")
            .longOpt("timeout")
            .hasArg()
            .desc("Specify the timeout for a response")
            .build();

    private static final Option AMOUNT = Option.builder("a")
            .argName("amount to ping")
            .longOpt("amount")
            .hasArg()
            .desc("Specify the amount of pings to send")
            .build();

    private static final Option DELAY = Option.builder("d")
            .argName("delay to ping with")
            .longOpt("delay")
            .hasArg()
            .desc("Specify the delay between pings")
            .build();


    private static final Option MODE = Option.builder("m")
            .argName("icmp/tcp")
            .longOpt("mode")
            .hasArg()
            .desc("Specify pinging mode (ICMP or TCP)")
            .build();

    /**
     * Main method of the program
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize modes
        MODE_STORAGE.init();

        // Add all command line options
        OPTIONS.addOption(TARGET);
        OPTIONS.addOption(PORT);
        OPTIONS.addOption(BODY);
        OPTIONS.addOption(MODE);
        OPTIONS.addOption(DELAY);
        OPTIONS.addOption(AMOUNT);
        OPTIONS.addOption(TIMEOUT);

        // Parse command line arguments
        try {
            final CommandLine cmdLine = PARSER.parse(OPTIONS, args);

            // Get the mode from the command line arguments
            final Mode mode = MODE_STORAGE.getByName(cmdLine.getOptionValue(MODE, "tcp"), true);
            if (mode == null) {
                FORMATTER.printHelp("java -jar pinger.jar", OPTIONS);
                return;
            }

            final int amountToPing = Integer.parseInt(cmdLine.getOptionValue(AMOUNT, "-1"));

            long lastPing = System.currentTimeMillis();
            int pinged = 0;

            while(amountToPing != -1 || pinged >= amountToPing) {
                // Wait 5 seconds before pinging again
                if (System.currentTimeMillis() - lastPing <= (Integer.parseInt(cmdLine.getOptionValue(DELAY, "1")) * 1000L)) continue;

                // Ping the target
                mode.ping(cmdLine);

                // Update last ping time & ping amounts
                lastPing = System.currentTimeMillis();
                pinged++;
            }

        } catch (ParseException | NumberFormatException e) {
            FORMATTER.printHelp("java -jar pinger.jar -t 1.1.1.1 -m icmp", OPTIONS);
        }
    }

}