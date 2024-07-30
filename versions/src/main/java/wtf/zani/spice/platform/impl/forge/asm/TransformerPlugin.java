package wtf.zani.spice.platform.impl.forge.asm;
//#if FORGE

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class TransformerPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
//#endif