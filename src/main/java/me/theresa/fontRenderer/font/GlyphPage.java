package me.theresa.fontRenderer.font;

import me.theresa.fontRenderer.font.effect.Effect;
import me.theresa.fontRenderer.font.opengl.TextureImpl;
import me.theresa.fontRenderer.font.opengl.renderer.Renderer;
import me.theresa.fontRenderer.font.opengl.renderer.SGL;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class GlyphPage {
	
	private static final SGL GL = Renderer.get();

	
	public static final int MAX_GLYPH_SIZE = 256;

	
    private static final ByteBuffer scratchByteBuffer = ByteBuffer.allocateDirect(MAX_GLYPH_SIZE * MAX_GLYPH_SIZE * 4);

	static {
		scratchByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}


	private static final IntBuffer scratchIntBuffer = scratchByteBuffer.asIntBuffer();


	private static final BufferedImage scratchImage = new BufferedImage(MAX_GLYPH_SIZE, MAX_GLYPH_SIZE, BufferedImage.TYPE_INT_ARGB);

	private static final Graphics2D scratchGraphics = (Graphics2D) scratchImage.getGraphics();

	static {
		scratchGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		scratchGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		scratchGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}


	public static FontRenderContext renderContext = scratchGraphics.getFontRenderContext();
	
	
	public static Graphics2D getScratchGraphics() {
		return scratchGraphics;
	}
	
	
	private final UnicodeFont unicodeFont;
	
	private final int pageWidth;
	
	private final int pageHeight;
	
	private final Image pageImage;
	
	private int pageX;
	
	private int pageY;
	
	private int rowHeight;
	
	private boolean orderAscending;
	
	private final List pageGlyphs = new ArrayList(32);

	
	public GlyphPage(UnicodeFont unicodeFont, int pageWidth, int pageHeight) throws SlickException {
		this.unicodeFont = unicodeFont;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;

		pageImage = new Image(pageWidth, pageHeight);
	}

	
	public int loadGlyphs (List glyphs, int maxGlyphsToLoad) throws SlickException {
		if (rowHeight != 0 && maxGlyphsToLoad == -1) {
			// If this page has glyphs and we are not loading incrementally, return zero if any of the glyphs don't fit.
			int testX = pageX;
			int testY = pageY;
			int testRowHeight = rowHeight;
			for (Iterator iter = getIterator(glyphs); iter.hasNext();) {
				Glyph glyph = (Glyph)iter.next();
				int width = glyph.getWidth();
				int height = glyph.getHeight();
				if (testX + width >= pageWidth) {
					testX = 0;
					testY += testRowHeight;
					testRowHeight = height;
				} else if (height > testRowHeight) {
					testRowHeight = height;
				}
				if (testY + testRowHeight >= pageWidth) return 0;
				testX += width;
			}
		}

		Color.white.bind();
		pageImage.bind();

		int i = 0;
		for (Iterator iter = getIterator(glyphs); iter.hasNext();) {
			Glyph glyph = (Glyph)iter.next();
			int width = Math.min(MAX_GLYPH_SIZE, glyph.getWidth());
			int height = Math.min(MAX_GLYPH_SIZE, glyph.getHeight());

			if (rowHeight == 0) {
				// The first glyph always fits.
				rowHeight = height;
			} else {
				// Wrap to the next line if needed, or break if no more fit.
				if (pageX + width >= pageWidth) {
					if (pageY + rowHeight + height >= pageHeight) break;
					pageX = 0;
					pageY += rowHeight;
					rowHeight = height;
				} else if (height > rowHeight) {
					if (pageY + height >= pageHeight) break;
					rowHeight = height;
				}
			}

			renderGlyph(glyph, width, height);
			pageGlyphs.add(glyph);

			pageX += width;

			iter.remove();
			i++;
			if (i == maxGlyphsToLoad) {
				// If loading incrementally, flip orderAscending so it won't change, since we'll probably load the rest next time.
				orderAscending = !orderAscending;
				break;
			}
		}

		TextureImpl.bindNone();

		// Every other batch of glyphs added to a page are sorted the opposite way to attempt to keep same size glyps together.
		orderAscending = !orderAscending;

		return i;
	}

	
	private void renderGlyph(Glyph glyph, int width, int height) throws SlickException {
		// Draw the glyph to the scratch image using Java2D.
		scratchGraphics.setComposite(AlphaComposite.Clear);
		scratchGraphics.fillRect(0, 0, MAX_GLYPH_SIZE, MAX_GLYPH_SIZE);
		scratchGraphics.setComposite(AlphaComposite.SrcOver);
		scratchGraphics.setColor(java.awt.Color.white);
		for (Object o : unicodeFont.getEffects()) ((Effect) o).draw(scratchImage, scratchGraphics, unicodeFont, glyph);
		glyph.setShape(null); // The shape will never be needed again.

		WritableRaster raster = scratchImage.getRaster();
		int[] row = new int[width];
		for (int y = 0; y < height; y++) {
			raster.getDataElements(0, y, width, 1, row);
			scratchIntBuffer.put(row);
		}
		GL.glTexSubImage2D(SGL.GL_TEXTURE_2D, 0, pageX, pageY, width, height, SGL.GL_BGRA, SGL.GL_UNSIGNED_BYTE,
			scratchByteBuffer);
		scratchIntBuffer.clear();

		glyph.setImage(pageImage.getSubImage(pageX, pageY, width, height));
	}

	
	private Iterator getIterator(List glyphs) {
		if (orderAscending) return glyphs.iterator();
		final ListIterator iter = glyphs.listIterator(glyphs.size());
		return new Iterator() {
			public boolean hasNext () {
				return iter.hasPrevious();
			}

			public Object next () {
				return iter.previous();
			}

			public void remove () {
				iter.remove();
			}
		};
	}
	
	public List getGlyphs () {
		return pageGlyphs;
	}

	
	public Image getImage () {
		return pageImage;
	}
}
