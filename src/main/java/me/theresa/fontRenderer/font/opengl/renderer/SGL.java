package me.theresa.fontRenderer.font.opengl.renderer;

import org.lwjgl.opengl.EXTSecondaryColor;
import org.lwjgl.opengl.EXTTextureMirrorClamp;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface SGL {

    int GL_TEXTURE_2D = GL11.GL_TEXTURE_2D;


    int GL_RGBA = GL11.GL_RGBA;

    int GL_RGB = GL11.GL_RGB;

    int GL_UNSIGNED_BYTE = GL11.GL_UNSIGNED_BYTE;

    int GL_LINEAR = GL11.GL_LINEAR;

    int GL_NEAREST = GL11.GL_NEAREST;

    int GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER;

    int GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER;

    int GL_POINT_SMOOTH = GL11.GL_POINT_SMOOTH;

    int GL_POLYGON_SMOOTH = GL11.GL_POLYGON_SMOOTH;

    int GL_LINE_SMOOTH = GL11.GL_LINE_SMOOTH;

    int GL_SCISSOR_TEST = GL11.GL_SCISSOR_TEST;


    int GL_MODULATE = GL11.GL_MODULATE;

    int GL_TEXTURE_ENV = GL11.GL_TEXTURE_ENV;

    int GL_TEXTURE_ENV_MODE = GL11.GL_TEXTURE_ENV_MODE;


    int GL_QUADS = GL11.GL_QUADS;

    int GL_POINTS = GL11.GL_POINTS;

    int GL_LINES = GL11.GL_LINES;

    int GL_LINE_STRIP = GL11.GL_LINE_STRIP;

    int GL_TRIANGLES = GL11.GL_TRIANGLES;

    int GL_TRIANGLE_FAN = GL11.GL_TRIANGLE_FAN;


    int GL_SRC_ALPHA = GL11.GL_SRC_ALPHA;

    int GL_ONE = GL11.GL_ONE;

    int GL_ONE_MINUS_DST_ALPHA = GL11.GL_ONE_MINUS_DST_ALPHA;

    int GL_DST_ALPHA = GL11.GL_DST_ALPHA;

    int GL_ONE_MINUS_SRC_ALPHA = GL11.GL_ONE_MINUS_SRC_ALPHA;


    int GL_COMPILE = GL11.GL_COMPILE;

    int GL_MAX_TEXTURE_SIZE = GL11.GL_MAX_TEXTURE_SIZE;

    int GL_COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT;

    int GL_DEPTH_BUFFER_BIT = GL11.GL_DEPTH_BUFFER_BIT;

    int GL_BLEND = GL11.GL_BLEND;

    int GL_COLOR_CLEAR_VALUE = GL11.GL_COLOR_CLEAR_VALUE;

    int GL_LINE_WIDTH = GL11.GL_LINE_WIDTH;

    int GL_CLIP_PLANE0 = GL11.GL_CLIP_PLANE0;

    int GL_CLIP_PLANE1 = GL11.GL_CLIP_PLANE1;

    int GL_CLIP_PLANE2 = GL11.GL_CLIP_PLANE2;

    int GL_CLIP_PLANE3 = GL11.GL_CLIP_PLANE3;


    int GL_COMPILE_AND_EXECUTE = GL11.GL_COMPILE_AND_EXECUTE;


    int GL_RGBA8 = GL11.GL_RGBA;

    int GL_RGBA16 = GL11.GL_RGBA16;

    int GL_BGRA = GL12.GL_BGRA;

    int GL_MIRROR_CLAMP_TO_EDGE_EXT = EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT;


    int GL_TEXTURE_WRAP_S = GL11.GL_TEXTURE_WRAP_S;

    int GL_TEXTURE_WRAP_T = GL11.GL_TEXTURE_WRAP_T;


    int GL_CLAMP = GL11.GL_CLAMP;


    int GL_COLOR_SUM_EXT = EXTSecondaryColor.GL_COLOR_SUM_EXT;


    int GL_ALWAYS = GL11.GL_ALWAYS;


    int GL_DEPTH_TEST = GL11.GL_DEPTH_TEST;


    int GL_NOTEQUAL = GL11.GL_NOTEQUAL;

    int GL_EQUAL = GL11.GL_EQUAL;

    int GL_SRC_COLOR = GL11.GL_SRC_COLOR;

    int GL_ONE_MINUS_SRC_COLOR = GL11.GL_ONE_MINUS_SRC_COLOR;

    int GL_MODELVIEW_MATRIX = GL11.GL_MODELVIEW_MATRIX;


    void flush();


    void initDisplay(int width, int height);


    void enterOrtho(int xsize, int ysize);


    void glClearColor(float red, float green, float blue, float alpha);


    void glClipPlane(int plane, DoubleBuffer buffer);


    void glScissor(int x, int y, int width, int height);


    void glLineWidth(float width);


    void glClear(int value);


    void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);


    void glLoadIdentity();


    void glGetInteger(int id, IntBuffer ret);


    void glGetFloat(int id, FloatBuffer ret);


    void glEnable(int item);


    void glDisable(int item);


    void glBindTexture(int target, int id);


    void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels);


    void glDeleteTextures(IntBuffer buffer);


    void glColor4f(float r, float g, float b, float a);


    void glTexCoord2f(float u, float v);


    void glVertex3f(float x, float y, float z);


    void glVertex2f(float x, float y);


    void glRotatef(float angle, float x, float y, float z);


    void glTranslatef(float x, float y, float z);


    void glBegin(int geomType);


    void glEnd();


    void glTexEnvi(int target, int mode, int value);


    void glPointSize(float size);


    void glScalef(float x, float y, float z);


    void glPushMatrix();


    void glPopMatrix();


    void glBlendFunc(int src, int dest);


    int glGenLists(int count);


    void glNewList(int id, int option);


    void glEndList();


    void glCallList(int id);


    void glCopyTexImage2D(int target, int level, int internalFormat,
                          int x, int y, int width, int height, int border);


    void glReadPixels(int x, int y, int width, int height, int format, int type,
                      ByteBuffer pixels);


    void glTexParameteri(int target, int param, int value);


    float[] getCurrentColor();


    void glDeleteLists(int list, int count);


    void glDepthMask(boolean mask);


    void glClearDepth(float value);


    void glDepthFunc(int func);


    void setGlobalAlphaScale(float alphaScale);


    void glLoadMatrix(FloatBuffer buffer);


    void glGenTextures(IntBuffer ids);


    void glGetError();


    void glTexImage2D(int target, int i, int dstPixelFormat,
                      int get2Fold, int get2Fold2, int j, int srcPixelFormat,
                      int glUnsignedByte, ByteBuffer textureBuffer);


    void glTexSubImage2D(int glTexture2d, int i, int pageX, int pageY,
                         int width, int height, int glBgra, int glUnsignedByte,
                         ByteBuffer scratchByteBuffer);


    boolean canTextureMirrorClamp();

    boolean canSecondaryColor();

    void glSecondaryColor3ubEXT(byte b, byte c, byte d);
}
