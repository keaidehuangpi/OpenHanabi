package me.theresa.fontRenderer.font.opengl.pbuffer;

import me.theresa.fontRenderer.font.Graphics;
import me.theresa.fontRenderer.font.Image;
import me.theresa.fontRenderer.font.SlickException;
import me.theresa.fontRenderer.font.log.Log;
import me.theresa.fontRenderer.font.opengl.InternalTextureLoader;
import me.theresa.fontRenderer.font.opengl.SlickCallable;
import me.theresa.fontRenderer.font.opengl.Texture;
import me.theresa.fontRenderer.font.opengl.TextureImpl;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;


public class PBufferGraphics extends Graphics {


    private Pbuffer pbuffer;

    private final Image image;


    public PBufferGraphics(Image image) throws SlickException {
        super(image.getTexture().getTextureWidth(), image.getTexture().getTextureHeight());
        this.image = image;

        Log.debug("Creating pbuffer(rtt) " + image.getWidth() + "x" + image.getHeight());
        if ((Pbuffer.getCapabilities() & Pbuffer.PBUFFER_SUPPORTED) == 0) {
            throw new SlickException("Your OpenGL card does not support PBuffers and hence can't handle the dynamic images required for this application.");
        }
		if ((Pbuffer.getCapabilities() & Pbuffer.RENDER_TEXTURE_SUPPORTED) == 0) {
			throw new SlickException("Your OpenGL card does not support Render-To-Texture and hence can't handle the dynamic images required for this application.");
		}
	
		init();
	}	

	
	private void init() throws SlickException {
		try {
			Texture tex = InternalTextureLoader.get().createTexture(image.getWidth(), image.getHeight(), image.getFilter());
			
			final RenderTexture rt = new RenderTexture(false, true, false, false, RenderTexture.RENDER_TEXTURE_2D, 0);
			pbuffer = new Pbuffer(screenWidth, screenHeight, new PixelFormat(8, 0, 0), rt, null);

			// Initialise state of the pbuffer context.
			pbuffer.makeCurrent();

			initGL();
			GL.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureID());
			pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
			image.draw(0,0);
			image.setTexture(tex);
			
			Display.makeCurrent();
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to create PBuffer for dynamic image. OpenGL driver failure?");
		}
	}

	
	protected void disable() {
		GL.flush();
		
		// Bind the texture after rendering.
		GL.glBindTexture(GL11.GL_TEXTURE_2D, image.getTexture().getTextureID());
		pbuffer.bindTexImage(Pbuffer.FRONT_LEFT_BUFFER);
		
		try {
			Display.makeCurrent();
		} catch (LWJGLException e) {
			Log.error(e);
		}
		
		SlickCallable.leaveSafeBlock();
	}

	
	protected void enable() {
		SlickCallable.enterSafeBlock();
		
		try {
			if (pbuffer.isBufferLost()) {
				pbuffer.destroy();
				init();
			}

			pbuffer.makeCurrent();
		} catch (Exception e) {
			Log.error("Failed to recreate the PBuffer");
			throw new RuntimeException(e);
		}
		
		// Put the renderer contents to the texture
		GL.glBindTexture(GL11.GL_TEXTURE_2D, image.getTexture().getTextureID());
		pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
		TextureImpl.unbind();
		initGL();
	}
	
	
	protected void initGL() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);        
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                    
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                
        GL11.glClearDepth(1);                                       
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glViewport(0,0,screenWidth,screenHeight);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		enterOrtho();
	}
	
	
	protected void enterOrtho() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, screenWidth, 0, screenHeight, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	
	public void destroy() {
		super.destroy();
		
		pbuffer.destroy();
	}

	public void flush() {
		super.flush();
		
		image.flushPixelData();
	}
}
