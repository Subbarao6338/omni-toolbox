package com.nature.docs.data.pdf

import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.util.*

object ESignEngine {

    fun signDocument(doc: PDDocument, signatureId: String): String {
        val auditTrailId = UUID.randomUUID().toString()
        // Add metadata for audit trail
        val info = doc.documentInformation
        info.setCustomMetadataValue("ESignAuditTrail", auditTrailId)
        info.setCustomMetadataValue("SignatureId", signatureId)
        info.setCustomMetadataValue("SignDate", Date().toString())
        return auditTrailId
    }
}
