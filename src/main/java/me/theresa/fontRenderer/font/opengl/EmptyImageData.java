package me.theresa.fontRenderer.font.opengl;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;


public class EmptyImageData implements ImageData {

    private final int width;

    private final int height;


    public EmptyImageData(int width, int height) {
        this.width = width;
        this.height = height;
    }


    public int getDepth() {
        return 32;
	}

	
	public int getHeight() {
		return height;
	}

	
	public ByteBuffer getImageBufferData() {
		return BufferUtils.createByteBuffer(getTexWidth() * getTexHeight() * 4);
	}

	
	public int getTexHeight() {
		return InternalTextureLoader.get2Fold(height);
	}
	
	public int getTexWidth() {
		return InternalTextureLoader.get2Fold(width);
	}

	public int getWidth() {
		return width;
	}

}
