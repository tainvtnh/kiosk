package com.tnh.vietqr.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "vietqr_invoices")
public class VietQrInvoice {
    @Id
    @Column(name = "invoice_code", length = 50)
    private String invoiceCode;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "alias", length = 50)
    private String alias;

    @Column(name = "qr_code", length = 1000)
    private String qrCode;

    @Column(name = "expiry_time")
    private Instant expiryTime;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Kiosk-specific fields to update HIS when paid
    @Column(name = "payment_type", length = 20)
    private String paymentType; // CONGKHAM or DVKT

    @Column(name = "ma_ho_so", length = 50)
    private String maHoSo;

    @Column(name = "ma_nb", length = 50)
    private String maNb;

    @Column(name = "ma_thiet_bi", length = 50)
    private String maThietBi;

    @Column(name = "ten_thu_ngan", length = 100)
    private String tenThuNgan;

    @Column(name = "ma_pos", length = 50)
    private String maPos;

    @Column(name = "phieu_thu_id", length = 50)
    private String phieuThuId;

    @Column(name = "checksum", length = 255)
    private String checksum;

    // Optional JSON payload for extra details (e.g. list of services for DVKT)
    @Column(name = "ct_details_json", columnDefinition = "NVARCHAR(MAX)")
    private String ctDetailsJson;
}
