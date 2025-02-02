package net.aspw.client.visual.font.smooth

import net.aspw.client.util.render.ColorUtils.stripColor
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font

class CFontRenderer(font: Font?, antiAlias: Boolean, fractionalMetrics: Boolean) :
    CFont(font, antiAlias, fractionalMetrics) {
    protected var boldChars = arrayOfNulls<CharData>(256)
    protected var italicChars = arrayOfNulls<CharData>(256)
    protected var boldItalicChars = arrayOfNulls<CharData>(256)
    private val colorCode = IntArray(32)
    protected var texBold: DynamicTexture? = null
    protected var texItalic: DynamicTexture? = null
    protected var texItalicBold: DynamicTexture? = null

    init {
        setupMinecraftColorcodes()
        setupBoldItalicIDs()
    }

    fun drawString(text: String, x: Float, y: Float, color: Int): Float {
        return this.drawString(text, x.toDouble(), y.toDouble(), color, false)
    }

    fun drawStringWithShadow(text: String?, x: Double, y: Double, color: Int): Float {
        val shadowWidth = drawString(text, x + 0.5, y + 0.5, color, true)
        return Math.max(shadowWidth, drawString(text, x, y, color, false))
    }

    fun drawStringWithOutline(str: String?, x: Float, y: Float, color: Int, outline: Int) {
        this.drawString(stripColor(str), (x - 1f).toDouble(), y.toDouble(), outline, false)
        this.drawString(stripColor(str), (x + 1f).toDouble(), y.toDouble(), outline, false)
        this.drawString(stripColor(str), x.toDouble(), (y + 1f).toDouble(), outline, false)
        this.drawString(stripColor(str), x.toDouble(), (y - 1f).toDouble(), outline, false)
        this.drawString(stripColor(str), x.toDouble(), y.toDouble(), color, false)
    }

    fun drawCenteredString(text: String, x: Float, y: Float, color: Int): Float {
        return this.drawString(text, x - (getStringWidth(text) / 2).toFloat() - 1, y, color)
    }

    fun drawCenteredStringWithShadow(text: String, x: Float, y: Float, color: Int): Float {
        return drawStringWithShadow(
            text,
            (x - (getStringWidth(text) / 2).toFloat() - 1).toDouble(),
            y.toDouble(),
            color
        )
    }

    fun drawCenteredStringWithShadow(text: String, x: Double, y: Double, color: Int): Float {
        return drawStringWithShadow(text, x - (getStringWidth(text) / 2).toDouble() - 1, y, color)
    }

    fun drawString(text: String?, x: Double, y: Double, color: Int, shadow: Boolean): Float {
        var x = x
        var y = y
        var color = color
        --x
        if (text == null) {
            return 0.0f
        }
        if (color == 553648127) {
            color = 16777215
        }
        if (color and -0x4000000 == 0x0) {
            color = color or -0x1000000
        }
        if (shadow) {
            color = color and 0xFCFCFC shr 2 or (color and Color(20, 20, 20, 200).rgb)
        }
        var currentData = charData
        val alpha = (color shr 24 and 0xFF) / 255.0f
        var bold = false
        var italic = false
        var strikethrough = false
        var underline = false
        x *= 2.0
        y = (y - 3.0) * 2.0
        GL11.glPushMatrix()
        GlStateManager.scale(0.5, 0.5, 0.5)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(
            (color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f,
            alpha
        )
        val size = text.length
        GlStateManager.enableTexture2D()
        GlStateManager.bindTexture(tex.glTextureId)
        GL11.glBindTexture(3553, tex.glTextureId)
        var i = 0
        while (i < size) {
            val character = text[i]
            if (character.toString() == "\u00a7") {
                var colorIndex = 21
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text[i + 1])
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (colorIndex < 16) {
                    bold = false
                    italic = false
                    underline = false
                    strikethrough = false
                    GlStateManager.bindTexture(tex.glTextureId)
                    currentData = charData
                    if (colorIndex < 0) {
                        colorIndex = 15
                    }
                    if (shadow) {
                        colorIndex += 16
                    }
                    val colorcode = colorCode[colorIndex]
                    GlStateManager.color(
                        (colorcode shr 16 and 0xFF) / 255.0f, (colorcode shr 8 and 0xFF) / 255.0f,
                        (colorcode and 0xFF) / 255.0f, alpha
                    )
                } else if (colorIndex == 17) {
                    bold = true
                    currentData = if (italic) {
                        GlStateManager.bindTexture(texItalicBold!!.glTextureId)
                        boldItalicChars
                    } else {
                        GlStateManager.bindTexture(texBold!!.glTextureId)
                        boldChars
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true
                } else if (colorIndex == 19) {
                    underline = true
                } else if (colorIndex == 20) {
                    italic = true
                    currentData = if (bold) {
                        GlStateManager.bindTexture(texItalicBold!!.glTextureId)
                        boldItalicChars
                    } else {
                        GlStateManager.bindTexture(texItalic!!.glTextureId)
                        italicChars
                    }
                } else if (colorIndex == 21) {
                    bold = false
                    italic = false
                    underline = false
                    strikethrough = false
                    GlStateManager.color(
                        (color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f,
                        (color and 0xFF) / 255.0f, alpha
                    )
                    GlStateManager.bindTexture(tex.glTextureId)
                    currentData = charData
                }
                ++i
            } else if (character.code < currentData.size) {
                GL11.glBegin(4)
                drawChar(currentData, character, x.toFloat(), y.toFloat())
                GL11.glEnd()
                if (strikethrough) {
                    drawLine(
                        x,
                        y + currentData[character.code]!!.height / 2.0f,
                        x + currentData[character.code]!!.width - 8.0,
                        y + currentData[character.code]!!.height / 2.0f,
                        1.0f
                    )
                }
                if (underline) {
                    drawLine(
                        x,
                        y + currentData[character.code]!!.height - 2.0,
                        x + currentData[character.code]!!.width - 8.0,
                        y + currentData[character.code]!!.height - 2.0,
                        1.0f
                    )
                }
                x += (currentData[character.code]!!.width - 9 + charOffset).toDouble()
            }
            ++i
        }
        GL11.glPopMatrix()
        return x.toFloat() / 2.0f
    }

    override fun getStringWidth(text: String): Int {
        if (text == null) {
            return 0
        }
        var width = 0
        var currentData = charData
        var bold = false
        var italic = false
        val size = text.length
        var i = 0
        while (i < size) {
            val character = text[i]
            if (character == '\u00a7' && i < size) {
                val colorIndex = "0123456789abcdefklmnor".indexOf(character)
                if (colorIndex < 16) {
                    bold = false
                    italic = false
                } else if (colorIndex == 17) {
                    bold = true
                    currentData = if (italic) boldItalicChars else boldChars
                } else if (colorIndex == 20) {
                    italic = true
                    currentData = if (bold) boldItalicChars else italicChars
                } else if (colorIndex == 21) {
                    bold = false
                    italic = false
                    currentData = charData
                }
                ++i
            } else if (character.code < currentData.size && character >= '\u0000') {
                width += currentData[character.code]!!.width - 9 + charOffset
            }
            ++i
        }
        return width / 2
    }

    override fun setFont(font: Font) {
        super.setFont(font)
        setupBoldItalicIDs()
    }

    override fun setAntiAlias(antiAlias: Boolean) {
        super.setAntiAlias(antiAlias)
        setupBoldItalicIDs()
    }

    override fun setFractionalMetrics(fractionalMetrics: Boolean) {
        super.setFractionalMetrics(fractionalMetrics)
        setupBoldItalicIDs()
    }

    private fun setupBoldItalicIDs() {
        texBold = setupTexture(
            font.deriveFont(1), antiAlias, fractionalMetrics,
            boldChars
        )
        texItalic = setupTexture(
            font.deriveFont(2), antiAlias, fractionalMetrics,
            italicChars
        )
    }

    private fun drawLine(x: Double, y: Double, x1: Double, y1: Double, width: Float) {
        GL11.glDisable(3553)
        GL11.glLineWidth(width)
        GL11.glBegin(1)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x1, y1)
        GL11.glEnd()
        GL11.glEnable(3553)
    }

    fun wrapWords(text: String, width: Double): List<String> {
        val finalWords = ArrayList<String>()
        if (getStringWidth(text).toDouble() > width) {
            val words = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var currentWord = ""
            var lastColorCode = 65535
            val n = words.size
            var n2 = 0
            while (n2 < n) {
                val word = words[n2]
                var i = 0
                while (i < word.toCharArray().size) {
                    val c = word.toCharArray()[i]
                    if (c == '\u00a7' && i < word.toCharArray().size - 1) {
                        lastColorCode = word.toCharArray()[i + 1].code
                    }
                    ++i
                }
                currentWord = if (getStringWidth("$currentWord$word ").toDouble() < width) {
                    "$currentWord$word "
                } else {
                    finalWords.add(currentWord)
                    (167 + lastColorCode).toString() + word + " "
                }
                ++n2
            }
            if (currentWord.length > 0) {
                if (getStringWidth(currentWord).toDouble() < width) {
                    finalWords.add((167 + lastColorCode).toString() + currentWord + " ")
                    currentWord = ""
                } else {
                    for (s in formatString(currentWord, width)) {
                        finalWords.add(s)
                    }
                }
            }
        } else {
            finalWords.add(text)
        }
        return finalWords
    }

    fun formatString(string: String, width: Double): List<String> {
        val finalWords = ArrayList<String>()
        var currentWord = ""
        var lastColorCode = 65535
        val chars = string.toCharArray()
        var i = 0
        while (i < chars.size) {
            val c = chars[i]
            if (c == '\u00a7' && i < chars.size - 1) {
                lastColorCode = chars[i + 1].code
            }
            currentWord = if (getStringWidth(currentWord + c).toDouble() < width) {
                currentWord + c
            } else {
                finalWords.add(currentWord)
                (167 + lastColorCode).toString() + c.toString()
            }
            ++i
        }
        if (currentWord.length > 0) {
            finalWords.add(currentWord)
        }
        return finalWords
    }

    private fun setupMinecraftColorcodes() {
        var index = 0
        while (index < 32) {
            val noClue = (index shr 3 and 1) * 85
            var red = (index shr 2 and 1) * 170 + noClue
            var green = (index shr 1 and 1) * 170 + noClue
            var blue = (index shr 0 and 1) * 170 + noClue
            if (index == 6) {
                red += 85
            }
            if (index >= 16) {
                red /= 4
                green /= 4
                blue /= 4
            }
            colorCode[index] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
            ++index
        }
    }

    fun trimStringToWidth(text: CharSequence, width: Int, reverse: Boolean): String {
        val builder = StringBuilder()
        var f = 0.0f
        val i = if (reverse) text.length - 1 else 0
        val j = if (reverse) -1 else 1
        var flag = false
        var flag1 = false
        var k = i
        while (k >= 0 && k < text.length && f < width) {
            val c0 = text[k]
            val f1 = getStringWidth(c0.toString()).toFloat()
            if (flag) {
                flag = false
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag1 = false
                    }
                } else {
                    flag1 = true
                }
            } else if (f1 < 0.0f) {
                flag = true
            } else {
                f += f1
                if (flag1) ++f
            }
            if (f > width) break
            if (reverse) {
                builder.insert(0, c0)
            } else {
                builder.append(c0)
            }
            k += j
        }
        return builder.toString()
    }
}