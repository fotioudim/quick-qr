package com.dfotiou.gr

import io.nayuki.qrcodegen.QrCode
import jakarta.enterprise.context.ApplicationScoped
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO


@ApplicationScoped
class QRCodeService {

    fun generateQrCode(data: String): QrCode {
        return QrCode.encodeText(data, QrCode.Ecc.LOW)
    }


    /**
     * Returns a string of SVG code for an image depicting the specified QR Code, with the specified
     * number of border modules. The string always uses Unix newlines (\n), regardless of the platform.
     * @param qr the QR Code to render (not `null`)
     * @param border the number of border modules to add, which must be non-negative
     * @param lightColor the color to use for light modules, in any format supported by CSS, not `null`
     * @param darkColor the color to use for dark modules, in any format supported by CSS, not `null`
     * @param download if true, the svg will be downloaded, otherwise it will be displayed in the browser
     * @return a string representing the QR Code as an SVG XML document
     * @throws NullPointerException if any object is `null`
     * @throws IllegalArgumentException if the border is negative
     */
    fun toSvgString(qr: QrCode?, border: Int, lightColor: String?, darkColor: String?, download: Boolean): String {
        Objects.requireNonNull<QrCode?>(qr)
        Objects.requireNonNull<String?>(lightColor)
        Objects.requireNonNull<String?>(darkColor)
        require(border >= 0) { "Border must be non-negative" }
        val brd = border.toLong()
        val sb = StringBuilder()
        if (download) {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n")
        }
        sb.append(
            String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 %1\$d %1\$d\" stroke=\"none\">\n",
                qr!!.size + brd * 2
            )
        )
        sb.append("\t<rect width=\"100%\" height=\"100%\" fill=\"" + lightColor + "\"/>\n")
        sb.append("\t<path d=\"")
        for (y in 0..<qr.size) {
            for (x in 0..<qr.size) {
                if (qr.getModule(x, y)) {
                    if (x != 0 || y != 0) sb.append(" ")
                    sb.append(String.format("M%d,%dh1v1h-1z", x + brd, y + brd))
                }
            }
        }
        return sb
            .append("\" fill=\"" + darkColor + "\"/>\n")
            .append("</svg>\n")
            .toString()
    }

    /**
     * Returns a base64 encoded string of the QR Code image
     * @param qr the QR Code to render (not `null`)
     * @param scale the side length (measured in pixels, must be positive) of each module
     * @param border the number of border modules to add, which must be non-negative
     * @param lightColor the color to use for light modules, in 0xRRGGBB format
     * @param darkColor the color to use for dark modules, in 0xRRGGBB format
     * @return a base64 encoded string of the QR Code image
     * @throws NullPointerException if the QR Code is `null`
     * @throws IllegalArgumentException if the scale or border is out of range
     */
    fun toBase64Image(qr: QrCode?, scale: Int, border: Int, lightColor: Int, darkColor: Int): String? {
        Objects.requireNonNull<QrCode?>(qr)
        require(!(scale <= 0 || border < 0)) { "Value out of range" }
        require(!(border > Int.Companion.MAX_VALUE / 2 || qr!!.size + border * 2L > Int.Companion.MAX_VALUE / scale)) { "Scale or border too large" }

        val image = BufferedImage(
            (qr.size + border * 2) * scale, (qr.size + border * 2) * scale,
            BufferedImage.TYPE_INT_RGB
        )
        for (y in 0..<image.getHeight()) {
            for (x in 0..<image.getWidth()) {
                val color = qr.getModule(x / scale - border, y / scale - border)
                image.setRGB(x, y, if (color) darkColor else lightColor)
            }
        }

        try {
            ByteArrayOutputStream().use { baos ->
                ImageIO.write(image, "PNG", baos)
                return Base64.getEncoder().encodeToString(baos.toByteArray())
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to encode image to base64", e)
        }
    }

    companion object

}