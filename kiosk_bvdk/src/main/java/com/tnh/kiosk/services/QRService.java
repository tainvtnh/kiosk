package com.tnh.kiosk.services;

import com.tnh.kiosk.moduleqr.beans.QRAddtionalBean;
import com.tnh.kiosk.moduleqr.beans.QRBean;
import com.tnh.kiosk.moduleqr.beans.QRPackBean;
import com.tnh.kiosk.moduleqr.qrcommon.QRPack;
import com.tnh.kiosk.properties.QRProperties;
import com.tnh.kiosk.moduleqr.qr.exception.InvalidLengthException;
import com.tnh.kiosk.dtos.payments.QRPaymentResponse;
import com.tnh.kiosk.forms.payments.QRPaymentRequest;
import com.tnh.kiosk.utils.ChecksumUtil;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QRService {

    private final RestTemplate restTemplate;
    
    private QRProperties qrProperties;

    @Value("${qrinfo.payment.apiUrl}")
    private String apiUrl;

    @Value("${qrinfo.payment.secretKey}")
    private String secretKey;

    public QRService(QRProperties qrProperties, ChecksumUtil checksumUtil, RestTemplateBuilder builder) {
        this.qrProperties = qrProperties;
        this.restTemplate = builder.build();
    }

    public String generateQRCode(String merchantCode, String merchantName, String amount) throws InvalidLengthException {

        System.out.println(">>> qrProperties = " + qrProperties);

        QRBean bean = QRBean.builder()
                .payLoad(qrProperties.getDefaultConfig().getPayLoad())
                .pointOIMethod(qrProperties.getDefaultConfig().getPointOIMethod())
                .merchantCode(merchantCode)
                .merchantName(merchantName)
                .masterMerchant(qrProperties.getDefaultConfig().getMasterMerchant())
                .merchantCC(qrProperties.getMerchant().getCc())
                .ccy(qrProperties.getDefaultConfig().getCcy())
                .countryCode(qrProperties.getDefaultConfig().getCountryCode())
                .merchantCity(qrProperties.getAddress().getCity())
                .pinCode(qrProperties.getAddress().getPinCode())
                .amount(amount) // có thể truyền động vào
                .build();
        QRAddtionalBean addBean = QRAddtionalBean.builder()
                .billNumber(qrProperties.getAdditional().getBillNumber())
                .terminalID(qrProperties.getAdditional().getTerminalID())
                .storeID(qrProperties.getAdditional().getStoreID())
                .purpose(qrProperties.getAdditional().getPurpose())
                .build();
        bean.setAddtionalBean(addBean);
        QRPack pack = new QRPack();
        QRPackBean data = pack.pack(bean, "private-key");

        return data.getQrData();
    }


    public QRPaymentResponse processQRPayment(QRPaymentRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<QRPaymentRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<QRPaymentResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    QRPaymentResponse.class
            );

            log.info("Response from NCC: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Error calling getbill API: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể kết nối tới API NCC", e);
        }
    }
}
