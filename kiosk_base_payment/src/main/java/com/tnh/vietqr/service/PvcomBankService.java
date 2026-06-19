package com.tnh.vietqr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnh.kiosk.services.KioskService;
import com.tnh.vietqr.config.VietQrProperties;
import com.tnh.vietqr.dto.KioskCreateInvoiceRequest;
import com.tnh.vietqr.entity.VietQrInvoice;
import com.tnh.vietqr.integration.PvcomBankClient;
import com.tnh.vietqr.integration.dto.*;
import com.tnh.vietqr.repository.VietQrInvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class PvcomBankService {

    private final VietQrProperties properties;
    private final PvcomBankClient pvcomBankClient;
    private final VietQrInvoiceRepository invoiceRepository;
    private final KioskService kioskService;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private String cachedAccessToken;
    private Instant tokenExpiryTime;

    public PvcomBankService(VietQrProperties properties,
            PvcomBankClient pvcomBankClient,
            VietQrInvoiceRepository invoiceRepository,
            KioskService kioskService,
            ObjectMapper objectMapper,
            ResourceLoader resourceLoader) {
        this.properties = properties;
        this.pvcomBankClient = pvcomBankClient;
        this.invoiceRepository = invoiceRepository;
        this.kioskService = kioskService;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Authenticates and caches the OAuth2 Access Token.
     */
    private synchronized String getOrRefreshToken() {
        if (cachedAccessToken != null && tokenExpiryTime != null
                && Instant.now().isBefore(tokenExpiryTime.minusSeconds(300))) {
            return cachedAccessToken;
        }

        try {
            log.info("Fetching a new Access Token from PVcomBank...");
            String body = "client_id=" + URLEncoder.encode(properties.getClientId(), StandardCharsets.UTF_8.name()) +
                    "&client_secret=" + URLEncoder.encode(properties.getClientSecret(), StandardCharsets.UTF_8.name()) +
                    "&scope=" + URLEncoder.encode("openid", StandardCharsets.UTF_8.name()) +
                    "&grant_type=" + URLEncoder.encode("client_credentials", StandardCharsets.UTF_8.name());

            TokenResponse response = pvcomBankClient.getToken(body);
            if (response != null && response.getAccessToken() != null) {
                cachedAccessToken = response.getAccessToken();
                tokenExpiryTime = Instant.now().plusSeconds(response.getExpiresIn());
                log.info("Access Token retrieved successfully. Expires in {} seconds.", response.getExpiresIn());
                return cachedAccessToken;
            }
        } catch (Exception e) {
            log.error("Failed to authenticate with PVcomBank: {}", e.getMessage(), e);
        }
        throw new RuntimeException("PVcomBank Authentication failed");
    }

    /**
     * Exposes invoice creation to Kiosk application.
     */
    public InvoiceResponse createInvoice(KioskCreateInvoiceRequest req) {
        verifyKioskChecksum(req);
        String token = "Bearer " + getOrRefreshToken();
        String invoiceCode = "INV" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));

        InvoiceRequest apiRequest = new InvoiceRequest();
        apiRequest.setAmount(req.getAmount());
        apiRequest.setCurrency("VND");
        apiRequest.setInvoiceCode(invoiceCode);
        apiRequest.setDescription(req.getDescription());
        apiRequest.setPaymentMethod("bank_transfer");
        apiRequest.setPaymentMethodType("viet_qr");
        apiRequest.setInvoiceExpirySeconds(req.getInvoiceExpirySeconds());

        // Merchant details loaded from central configurations
        MerchantInfo merchant = new MerchantInfo();
        merchant.setClientId(properties.getMerchant().getClientId());
        merchant.setName(properties.getMerchant().getName());
        merchant.setAccountNo(properties.getMerchant().getAccountNo());
        apiRequest.setMerchant(merchant);
        apiRequest.setCustomer(new CustomerInfo());

        log.info("Calling PVcomBank to create invoice: {}", invoiceCode);
        logCurlCreateInvoice(token, properties.getMerchant().getClientId(), apiRequest);
        InvoiceResponse apiResponse = pvcomBankClient.createInvoice(
                token,
                properties.getMerchant().getClientId(),
                apiRequest);
        log.info("PVcomBank createInvoice response: {}", apiResponse);

        if (apiResponse != null && "00".equals(apiResponse.getStatusCode()) && apiResponse.getData() != null) {
            InvoiceData responseData = apiResponse.getData();

            VietQrInvoice localInvoice = new VietQrInvoice();
            localInvoice.setInvoiceCode(invoiceCode);
            localInvoice.setAmount(req.getAmount());
            localInvoice.setCurrency("VND");
            localInvoice.setDescription(req.getDescription());
            localInvoice.setStatus("PENDING");
            localInvoice.setAlias(responseData.getAlias());
            if (responseData.getNextAction() != null) {
                localInvoice.setQrCode(responseData.getNextAction().getQrCode());
            }
            if (responseData.getInvoiceExpiryTime() != null) {
                try {
                    String expiryStr = responseData.getInvoiceExpiryTime().trim().replace(" ", "T");
                    java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(expiryStr);
                    localInvoice.setExpiryTime(ldt.atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
                } catch (Exception e) {
                    log.error("Failed to parse invoice_expiry_time: {}. Error: {}", responseData.getInvoiceExpiryTime(),
                            e.getMessage());
                }
            }
            localInvoice.setCreatedAt(Instant.now());
            localInvoice.setUpdatedAt(Instant.now());

            // Save Kiosk/HIS execution parameters
            localInvoice.setPaymentType(req.getPaymentType());
            localInvoice.setMaHoSo(req.getMaHoSo());
            localInvoice.setMaNb(req.getMaNb());
            localInvoice.setMaThietBi(req.getMaThietBi());
            localInvoice.setTenThuNgan(req.getTenThuNgan());
            localInvoice.setMaPos(req.getMaPOS());
            localInvoice.setPhieuThuId(req.getPhieuThuId());
            localInvoice.setChecksum(req.getChecksum());

            if (req.getCtDetails() != null) {
                try {
                    localInvoice.setCtDetailsJson(objectMapper.writeValueAsString(req.getCtDetails()));
                } catch (Exception e) {
                    log.error("Failed to serialize CT details JSON: {}", e.getMessage());
                }
            }

            invoiceRepository.save(localInvoice);
            log.info("Saved local invoice record for code: {}", invoiceCode);
        }

        return apiResponse;
    }

    /**
     * Cancels / force-expires an invoice.
     */
    public UpdateInvoiceResponse cancelInvoice(String invoiceCode) {
        VietQrInvoice localInvoice = invoiceRepository.findByInvoiceCode(invoiceCode)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceCode));

        if (!"PENDING".equals(localInvoice.getStatus())) {
            throw new IllegalStateException(
                    "Only PENDING invoices can be cancelled. Current status: " + localInvoice.getStatus());
        }

        String token = "Bearer " + getOrRefreshToken();

        UpdateInvoiceRequest apiRequest = new UpdateInvoiceRequest();
        apiRequest.setAlias(localInvoice.getAlias());
        apiRequest.setInvoiceCode(invoiceCode);
        apiRequest.setForceExpireNow(true);

        MerchantInfoUpdate merchantUpdate = new MerchantInfoUpdate();
        merchantUpdate.setClientId(properties.getMerchant().getClientId());
        merchantUpdate.setAccountNo(properties.getMerchant().getAccountNo());
        apiRequest.setMerchant(merchantUpdate);

        log.info("Calling PVcomBank to cancel invoice: {}", invoiceCode);
        UpdateInvoiceResponse apiResponse = pvcomBankClient.updateInvoice(
                token,
                properties.getMerchant().getClientId(),
                apiRequest);
        log.info("PVcomBank cancelInvoice response: {}", apiResponse);

        if (apiResponse != null && "00".equals(apiResponse.getStatusCode())) {
            localInvoice.setStatus("EXPIRED");
            localInvoice.setUpdatedAt(Instant.now());
            invoiceRepository.save(localInvoice);
            log.info("Invoice {} status updated to EXPIRED locally", invoiceCode);
        }

        return apiResponse;
    }

    /**
     * Queries transaction details and updates local state + hospital database.
     */
    public TransactionQueryResponse queryTransaction(String invoiceCode) {
        VietQrInvoice localInvoice = invoiceRepository.findByInvoiceCode(invoiceCode)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceCode));

        if ("PAID".equals(localInvoice.getStatus())) {
            TransactionQueryResponse response = new TransactionQueryResponse();
            response.setStatusCode("00");
            response.setMessage("SUCCESS");
            TransactionData data = new TransactionData();
            data.setDescription(invoiceCode);
            data.setAmount(localInvoice.getAmount());
            data.setCurrency(localInvoice.getCurrency());
            response.setData(data);
            return response;
        }

        String token = "Bearer " + getOrRefreshToken();

        TransactionQueryRequest apiRequest = new TransactionQueryRequest();
        apiRequest.setClientId(properties.getClientId());
        apiRequest.setOrderId(invoiceCode);
        try {
            apiRequest.setAccount(Long.parseLong(properties.getMerchant().getAccountNo()));
        } catch (NumberFormatException e) {
            log.error("Invalid account number configuration: {}", properties.getMerchant().getAccountNo());
            throw new RuntimeException("Invalid merchant account number format");
        }

        log.info("Calling PVcomBank to query transaction status for invoice: {}", invoiceCode);
        TransactionQueryResponse apiResponse = pvcomBankClient.queryTransaction(token, apiRequest);
        log.info("PVcomBank queryTransaction response: {}", apiResponse);

        if (apiResponse != null && "00".equals(apiResponse.getStatusCode()) && apiResponse.getData() != null) {
            log.info("Transaction query successful. Processing payment success for invoice: {}", invoiceCode);
            processPaymentSuccess(localInvoice, apiResponse.getData().getTranId(), apiResponse.getData().getTranDate());
        }

        return apiResponse;
    }

    /**
     * Processes signed payment callback (IPN) from PVcomBank.
     */
    public IpnResponse processIpn(IpnPayload payload) {
        IpnResponse response = new IpnResponse();

        log.info("Verifying IPN callback signature...");
        boolean isSignatureValid = verifySignature(payload);
        if (!isSignatureValid) {
            log.warn("IPN Signature verification failed!");
            response.setVerifySignature("false");
            response.setData(Collections.singletonMap("error", "Invalid signature"));
            return response;
        }

        IpnData data = payload.getData();
        String invoiceCode = data.getDescription(); // PVcomBank passes invoice code in description
        log.info("IPN Signature verified. Processing payment for invoice: {}", invoiceCode);

        response.setVerifySignature("true");
        Map<String, String> resData = new HashMap<>();
        resData.put("orderId", invoiceCode);
        resData.put("status", "processed");
        response.setData(resData);

        // Optional<VietQrInvoice> optInvoice =
        // invoiceRepository.findByInvoiceCode(invoiceCode);
        // if (optInvoice.isPresent()) {
        // VietQrInvoice invoice = optInvoice.get();
        // if (!"PAID".equals(invoice.getStatus())) {
        // log.info("Verifying IPN signature success for invoice: {}", invoiceCode);
        // processPaymentSuccess(invoice, data.getTranId(), data.getTranDate());
        // } else {
        // log.info("Invoice {} has already been processed and marked as PAID.",
        // invoiceCode);
        // }
        // response.setVerifySignature("true");
        // Map<String, String> resData = new HashMap<>();
        // resData.put("orderId", invoiceCode);
        // resData.put("status", "processed");
        // response.setData(resData);
        // } else {
        // log.error("Invoice {} not found in local records!", invoiceCode);
        // response.setVerifySignature("false");
        // response.setData(Collections.singletonMap("error", "Invoice not found"));
        // }

        return response;
    }

    /**
     * Helper to process payment success, updating DB and triggering HIS stored
     * procedures.
     */
    private void processPaymentSuccess(VietQrInvoice invoice, String tranId, String tranDate) {
        invoice.setStatus("PAID");
        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        // Call HIS Stored Procedure to update status
        // try {
        // Map<String, Object> spParams = new HashMap<>();
        // spParams.put("maHoSo", invoice.getMaHoSo());
        // spParams.put("maNb", invoice.getMaNb());
        // spParams.put("maGiaoDich", tranId);

        // // Format transaction date for DB compatibility (replace 'T' with ' ')
        // String formattedDate = tranDate != null ? tranDate.replace("T", " ") : "";
        // spParams.put("thoiGianThanhToanUTC", formattedDate);
        // spParams.put("trangThaiThanhToan", "1"); // 1 represents PAID
        // spParams.put("hinhThucThanhToan", "viet_qr");
        // spParams.put("maThietBi", invoice.getMaThietBi());
        // spParams.put("tenThuNgan", invoice.getTenThuNgan() != null ?
        // invoice.getTenThuNgan() : "Kiosk VietQR");
        // spParams.put("maPOS", invoice.getMaPos());
        // spParams.put("soTien", invoice.getAmount());
        // spParams.put("phieuThuId", invoice.getPhieuThuId());
        // spParams.put("checksum", invoice.getChecksum());

        // Map<String, Object> result;
        // if ("DVKT".equalsIgnoreCase(invoice.getPaymentType())) {
        // log.info("Executing DVKT payment update procedure for invoice: {}",
        // invoice.getInvoiceCode());
        // if (invoice.getCtDetailsJson() != null) {
        // try {
        // List<Map<String, Object>> details = objectMapper.readValue(
        // invoice.getCtDetailsJson(),
        // new TypeReference<List<Map<String, Object>>>() {
        // });
        // // If details are present, typically the procedure is updated with the
        // detailed
        // // items
        // if (!details.isEmpty()) {
        // // Map individual details to parameters (as done in standard kiosk base)
        // Map<String, Object> detail = details.get(0); // For single or comma-separated
        // lists
        // spParams.put("CT_soPhieu", detail.get("CT_soPhieu"));
        // spParams.put("CT_dichVuId", detail.get("CT_dichVuId"));
        // spParams.put("CT_soTien", detail.get("CT_soTien"));
        // }
        // } catch (Exception e) {
        // log.error("Failed to parse local invoice CT details JSON: {}",
        // e.getMessage());
        // }
        // }
        // result = kioskService.executeProcedure("update-payment-dvkt", spParams);
        // } else {
        // log.info("Executing standard CONGKHAM payment update procedure for invoice:
        // {}",
        // invoice.getInvoiceCode());
        // result = kioskService.executeProcedure("update-payment", spParams);
        // }
        // log.info("HIS update procedure result: {}", result);
        // } catch (Exception e) {
        // log.error("Failed to execute HIS payment update procedure: {}",
        // e.getMessage(), e);
        // }
    }

    /**
     * Verifies the PVcomBank callback SHA512withRSA signature.
     */
    public boolean verifySignature(IpnPayload payload) {
        try {
            IpnData data = payload.getData();
            // msg = tranId + account + currency + amount + tranDate + description
            // (concatenated directly)
            String msg = data.getTranId() + data.getAccount() + data.getCurrency() + data.getAmount()
                    + data.getTranDate() + data.getDescription();

            PublicKey pubKey = getPublicKey(properties.getSecurity().getPvcombankPublicKey());
            Signature sig = Signature.getInstance("SHA512withRSA");
            sig.initVerify(pubKey);
            sig.update(msg.getBytes(StandardCharsets.UTF_8));

            return sig.verify(Base64.getDecoder().decode(payload.getSignature()));
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage(), e);
            return false;
        }
    }

    private PublicKey getPublicKey(String keyConfig) throws Exception {
        String keyContent = keyConfig;
        if (keyConfig.startsWith("classpath:") || keyConfig.startsWith("file:") || keyConfig.endsWith(".pem")) {
            Resource resource = resourceLoader.getResource(keyConfig);
            try (InputStream is = resource.getInputStream()) {
                keyContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        String cleanKey = keyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private void logCurlCreateInvoice(String token, String clientId, InvoiceRequest apiRequest) {
        try {
            String jsonBody = objectMapper.writeValueAsString(apiRequest);
            String curl = String.format(
                    "curl -X POST %s/external-public/external-public/bill/api/v1/invoices \\\n" +
                            "  -H \"Content-Type: application/json\" \\\n" +
                            "  -H \"Authorization: %s\" \\\n" +
                            "  -H \"client-id: %s\" \\\n" +
                            "  -d '%s'",
                    properties.getBaseUrl(), token, clientId, jsonBody);
            log.info("Equivalent cURL command for testing:\n{}", curl);
        } catch (Exception e) {
            log.warn("Failed to generate cURL log: {}", e.getMessage());
        }
    }

    private void verifyKioskChecksum(KioskCreateInvoiceRequest req) {
        String clientChecksum = req.getChecksum();
        if (clientChecksum == null || clientChecksum.trim().isEmpty()) {
            throw new IllegalArgumentException("Checksum is missing or empty");
        }

        String maHoSo = req.getMaHoSo() != null ? req.getMaHoSo() : "";
        String maNb = req.getMaNb() != null ? req.getMaNb() : "";
        String amount = req.getAmount() != null ? req.getAmount() : "";
        String phieuThuId = req.getPhieuThuId() != null ? req.getPhieuThuId() : "";
        String key = properties.getKiosk() != null && properties.getKiosk().getChecksumKey() != null
                ? properties.getKiosk().getChecksumKey()
                : "";

        String rawData = maHoSo + maNb + amount + phieuThuId + key;

        String expectedMd5 = calculateMd5(rawData);
        String expectedSha256 = calculateSha256(rawData);

        boolean match = expectedMd5.equalsIgnoreCase(clientChecksum) || expectedSha256.equalsIgnoreCase(clientChecksum);
        if (!match) {
            log.warn("Checksum verification failed. Expected MD5: {}, Expected SHA-256: {}, Received: {}", expectedMd5,
                    expectedSha256, clientChecksum);
            throw new IllegalArgumentException("Invalid checksum signature");
        }
        log.info("Checksum verified successfully for maHoSo: {}", maHoSo);
    }

    private String calculateMd5(String data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            log.error("MD5 algorithm not found", e);
            throw new RuntimeException("Hashing error", e);
        }
    }

    private String calculateSha256(String data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("Hashing error", e);
        }
    }
}
