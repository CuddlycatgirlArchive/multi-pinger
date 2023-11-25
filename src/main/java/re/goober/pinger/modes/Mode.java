package re.goober.pinger.modes;


import de.florianmichael.rclasses.pattern.functional.IName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

@Getter
@AllArgsConstructor
public abstract class Mode implements IName {

    private final String name;
    public abstract void ping(final CommandLine commandLine);

}
