package com.dfotiou.gr

import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import java.util.Objects.requireNonNull


@Path("/qr-page")
class QRCodePage(
    private val qrCodeService: QRCodeService,
    private var home: Template,
    private var about: Template,
    private var qrcode: Template
) {

    init {
        this.home = requireNonNull(home, "Home page is required")
        this.about = requireNonNull(about, "About page is required")
        this.qrcode = requireNonNull(qrcode, "Result page is required")
    }

    @GET
    @Path("/about")
    @Produces(MediaType.TEXT_HTML)
    fun get(): TemplateInstance {
        return about.instance()
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun get(@QueryParam("url") url: String?): TemplateInstance {
        return url
            ?.let {
                qrcode.data(
                    "url", url,
                    "base64Image", qrCodeService.toBase64Image(qrCodeService.generateQrCode(url), 4, 4, 0x000000, 0xFFFFFF)
                )
            }
            ?: home.instance()
    }

}
