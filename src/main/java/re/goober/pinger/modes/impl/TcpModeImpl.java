package re.goober.pinger.modes.impl;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import re.goober.pinger.Pinger;
import re.goober.pinger.modes.Mode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TcpModeImpl extends Mode {

    private final Logger logger = LogManager.getLogger("TCP");

    // @formatter:off
    public TcpModeImpl() { super("tcp"); }
    // @formatter:on

    @Override
    public void ping(final CommandLine cmdLine) {
        final long currentTime = System.currentTimeMillis();

        try (final Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(
                            cmdLine.getOptionValue(Pinger.TARGET),
                            Integer.parseInt(cmdLine.getOptionValue(Pinger.PORT, "80"))
                    ),
                    Integer.parseInt(cmdLine.getOptionValue(Pinger.TIMEOUT, "1000"))
            );

            socket.getOutputStream().write(Pinger.BODY.getValue("TCP Port Probe").getBytes());
            socket.getOutputStream().flush();
            socket.close();

            logger.info(
                    "Probing {}:{}/tcp - Port is open - time={}ms",
                    cmdLine.getOptionValue(Pinger.TARGET),
                    cmdLine.getOptionValue(Pinger.PORT, "80"),
                    System.currentTimeMillis() - currentTime
            );
        } catch (NumberFormatException e) {
            logger.fatal("Invalid port or timeout specified!");
        } catch (UnknownHostException e) {
            logger.fatal("Unknown host specified!");
        } catch (IOException e) {
            if (e instanceof SocketException && e.getMessage().contains("denied")) {
                logger.error(
                        "Probing {}:{}/tcp - System is denying access - time={}ms",
                        cmdLine.getOptionValue(Pinger.TARGET),
                        cmdLine.getOptionValue(Pinger.PORT, "80"),
                        System.currentTimeMillis() - currentTime
                );
                return;
            }

            logger.error(
                    "Probing {}:{}/tcp - Port is closed - time={}ms",
                    cmdLine.getOptionValue(Pinger.TARGET),
                    cmdLine.getOptionValue(Pinger.PORT, "80"),
                    System.currentTimeMillis() - currentTime
            );
        }
    }
}
