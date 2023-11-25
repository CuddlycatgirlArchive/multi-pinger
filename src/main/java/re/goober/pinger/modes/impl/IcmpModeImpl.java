package re.goober.pinger.modes.impl;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import re.goober.pinger.Pinger;
import re.goober.pinger.modes.Mode;

import java.io.IOException;
import java.net.*;

public class IcmpModeImpl extends Mode {

    private final Logger logger = LogManager.getLogger("ICMP");

    // @formatter:off
    public IcmpModeImpl() { super("icmp"); }
    // @formatter:on

    @Override
    public void ping(final CommandLine cmdLine) {
        final long currentTime = System.currentTimeMillis();

        try {
            final InetAddress address = InetAddress.getByName(cmdLine.getOptionValue(Pinger.TARGET));

            // Check if the target is reachable
            if (address.isReachable(Integer.parseInt(cmdLine.getOptionValue(Pinger.TIMEOUT, "1000")))) {
                logger.info(
                        "Probing {}/icmp - Target is responding - time={}ms",
                        cmdLine.getOptionValue(Pinger.TARGET),
                        System.currentTimeMillis() - currentTime
                );
            } else throw new IOException("Target is not responding");
        } catch (NumberFormatException e) {
            logger.fatal("Invalid timeout specified!");
        } catch (UnknownHostException e) {
            logger.fatal("Unknown host specified!");
        } catch (IOException e) {
            if (e instanceof SocketException && e.getMessage().contains("denied")) {
                logger.error(
                        "Probing {}/icmp - System is denying access - time={}ms",
                        cmdLine.getOptionValue(Pinger.TARGET),
                        System.currentTimeMillis() - currentTime
                );
                return;
            }

            logger.error(
                    "Probing {}/icmp - Target is not responding - time={}ms",
                    cmdLine.getOptionValue(Pinger.TARGET),
                    System.currentTimeMillis() - currentTime
            );
        }
    }

}
