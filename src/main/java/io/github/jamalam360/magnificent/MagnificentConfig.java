package io.github.jamalam360.magnificent;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

/**
 * @author Jamalam360
 */

@Config(name = "magnificent")
public class MagnificentConfig implements ConfigData {
    public boolean displayModName = true;

    public static MagnificentConfig get() {
        return AutoConfig.getConfigHolder(MagnificentConfig.class).getConfig();
    }
}
