package cn.hanabi.injection.interfaces;

import java.util.List;

public interface IShaderGroup {
    List getShaders();

    void createBindFramebuffers(int width, int height);

    void loadShaderGroup(float partialTicks);
}
