package re.goober.pinger.modes;

import de.florianmichael.rclasses.pattern.storage.named.NamedStorage;
import re.goober.pinger.modes.impl.IcmpModeImpl;
import re.goober.pinger.modes.impl.TcpModeImpl;

public class ModeStorage extends NamedStorage<Mode> {

    @Override
    public void init() {
        add(
                new TcpModeImpl(),
                new IcmpModeImpl()
        );
    }

}
