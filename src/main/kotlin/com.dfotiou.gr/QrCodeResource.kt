package com.dfotiou.gr

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status


@Path("/qr")
class QrCodeResource(
    private val qrCodeService: QRCodeService
) {

    @GET
    @Path("/download/svg")
    @Produces(MediaType.APPLICATION_SVG_XML)
    fun getQrCodeSVGDownload(@QueryParam("text") text: String?): Response? {
        if (text == null || text.isBlank()) {
            return Response
                .status(Status.BAD_REQUEST)
                .entity("QR Code data cannot be null or empty")
                .type(MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "attachment; filename=qrcode.svg")
                .build()
        }
        val svg: String? = qrCodeService.toSvgString(qrCodeService.generateQrCode(text), 4, "#FFFFFF", "#000000", true)
        return Response.ok(svg)
            .header("Content-Disposition", "attachment; filename=qr-code.svg")
            .build()
    }


}