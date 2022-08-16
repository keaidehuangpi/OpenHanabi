package cn.hanabi.gui.cloudmusic.ui;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.utils.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

@ObfuscationClass
public class CustomTextField {

	public String textString;
	public float x;
	public float y;
	public boolean isFocused;
	public boolean isTyping;
	public boolean back;
	public int ticks = 0;
	public int selectedChar;
	public float offset;

	public CustomTextField(String text) {
		this.textString = text;
		this.selectedChar = this.textString.length();
	}

	public void draw(float x, float y) {
		this.x = x;
		this.y = y;

		if (this.selectedChar > this.textString.length())
			this.selectedChar = this.textString.length();
		else if (this.selectedChar < 0)
			this.selectedChar = 0;

		int selectedChar = this.selectedChar;

		RenderUtil.drawRoundedRect(this.x, this.y + 3f, this.x + 115f, this.y + 15f, 1, 0xff34373c);

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtil.doGlScissor((int) this.x + 1, (int) this.y + 3, 113, 11);
		Hanabi.INSTANCE.fontManager.wqy13.drawString(this.textString, this.x + 1.5f - this.offset, this.y + 4F, Color.GRAY.getRGB());

		if (this.isFocused) {
			float width = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString.substring(0, selectedChar)) + 4;
			float posX = this.x + width - this.offset;
			RenderUtil.drawRect(posX - 0.5f, this.y + 5.5f, posX, this.y + 12.5f, RenderUtil.reAlpha(Color.GRAY.getRGB(), ticks / 500 % 2 == 0 ? 1f : 0));
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();

		this.tick();
	}

	public void tick() {
		if (isFocused)
			ticks++;
		else
			ticks = 0;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseID) {
		boolean hovering = RenderUtil.isHovering(mouseX, mouseY, this.x, this.y + 3f, this.x + 115f, this.y + 15f);

		if (hovering && mouseID == 0 && !this.isFocused) {
			this.isFocused = true;
			this.selectedChar = this.textString.length();
		} else if (!hovering) {
			this.isFocused = false;
			this.isTyping = false;
		}

	}

	public float newTextWidth;
	public float oldTextWidth;
	public float charWidth;
	public String oldString;
	public StringBuilder stringBuilder;

	public void keyPressed(int key) {
		if (key == Keyboard.KEY_ESCAPE) {
			this.isFocused = false;
			this.isTyping = false;
		}

		if (this.isFocused) {
			float width;
			float barOffset;
			if (GuiScreen.isKeyComboCtrlV(key)){
				this.textString = (GuiScreen.getClipboardString());
				return;
			}
			switch (key) {
				case Keyboard.KEY_RETURN:
					this.isFocused = false;
					this.isTyping = false;
					this.ticks = 0;
					break;
				case Keyboard.KEY_INSERT:
					Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable clipTf = sysClip.getContents(null);
					if (clipTf != null) {
						if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							try {
								this.textString = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}

					this.selectedChar = this.textString.length();
					width = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString.substring(0, this.selectedChar))
							+ 2;
					barOffset = width - this.offset;
					if (barOffset > 111F) {
						this.offset += barOffset - 111F;
					}

					break;

				case Keyboard.KEY_BACK:
					try {
						if (this.selectedChar <= 0)
							break;
						if (this.textString.length() != 0) {
							oldString = this.textString;
							stringBuilder = new StringBuilder(oldString);
							stringBuilder.charAt(this.selectedChar - 1);
							stringBuilder.deleteCharAt(this.selectedChar - 1);
							this.textString = ChatAllowedCharacters.filterAllowedCharacters(stringBuilder.toString());
							--this.selectedChar;
							if (Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(oldString) + 2 > 111F && this.offset > 0.0F) {
								newTextWidth = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString) + 2;
								oldTextWidth = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(oldString) + 2;
								charWidth = newTextWidth - oldTextWidth;
								if (newTextWidth <= 111F && oldTextWidth - 111F > charWidth)
									charWidth = 111F - oldTextWidth;

								this.offset += charWidth;
							}

							if (this.selectedChar > this.textString.length()) {
								this.selectedChar = this.textString.length();
							}

							this.ticks = 0;
						}
					} catch (Exception ignored) {
					}
					break;
				case Keyboard.KEY_HOME:
					this.selectedChar = 0;
					this.offset = 0.0F;
					this.ticks = 0;
					break;
				case Keyboard.KEY_LEFT:
					if (this.selectedChar > 0) {
						--this.selectedChar;
					}

					width = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString.substring(0, this.selectedChar))
							+ 2;
					barOffset = width - this.offset;
					barOffset -= 2.0F;

					if (barOffset < 0.0F)
						this.offset += barOffset;
					this.ticks = 0;
					break;
				case Keyboard.KEY_RIGHT:
					if (this.selectedChar < this.textString.length()) {
						++this.selectedChar;
					}

					width = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString.substring(0, this.selectedChar))
							+ 2;
					barOffset = width - this.offset;
					if (barOffset > 111F) {
						this.offset += barOffset - 111F;
					}
					this.ticks = 0;
					break;
				case Keyboard.KEY_END:
					this.selectedChar = this.textString.length();
					width = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString.substring(0, this.selectedChar))
							+ 2;
					barOffset = width - this.offset;
					if (barOffset > 111F) {
						this.offset += barOffset - 111F;
					}
					this.ticks = 0;
			}
		}
	}

	public void charTyped(char c) {
		if (this.isFocused && ChatAllowedCharacters.isAllowedCharacter(c)) {
			if (!this.isTyping)
				this.isTyping = true;

			oldString = this.textString;
			stringBuilder = new StringBuilder(oldString);
			stringBuilder.insert(this.selectedChar, c);
			this.textString = ChatAllowedCharacters.filterAllowedCharacters(stringBuilder.toString());
			if (this.selectedChar > this.textString.length()) {
				this.selectedChar = this.textString.length();
			} else if (this.selectedChar == oldString.length() && this.textString.startsWith(oldString)) {
				this.selectedChar += this.textString.length() - oldString.length();
			} else {
				++this.selectedChar;
				float width = Hanabi.INSTANCE.fontManager.wqy13
						.getStringWidth(this.textString.substring(0, this.selectedChar)) + 2;
				newTextWidth = width - this.offset;
				if (newTextWidth > 111F)
					this.offset += newTextWidth - 111F;
			}

			newTextWidth = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(this.textString) + 2;
			oldTextWidth = Hanabi.INSTANCE.fontManager.wqy13.getStringWidth(oldString) + 2;
			if (newTextWidth > 111F) {
				if (oldTextWidth < 111F)
					oldTextWidth = 111F;

				charWidth = newTextWidth - oldTextWidth;
				if (this.selectedChar == this.textString.length())
					this.offset += charWidth;
			}
			ticks = 0;
		}
	}
}