package com.tnh.kiosk.moduleqr.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QRBean {

    private String payLoad;

    private String pointOIMethod;

    private String masterMerchant; // to chuc dang ky like master merchant

    private String merchantCode;

    private String merchantCC;

    private String ccy;

    private String amount;

    private String tipAndFee;

    private String fixedFee;

    private String percentFee;

    private String countryCode;

    private String merchantName;

    private String merchantCity;

    private String pinCode;

    private String addtionalData;

    private String term; // Ky cuoc hoa don
    private String crc16;

    private QRAddtionalBean addtionalBean;

    private String visa;
    private String master;
    private String unionPay;

    public String getPointOIMethod() {
        return pointOIMethod;
    }

    public void setPointOIMethod(String pointOIMethod) {
        this.pointOIMethod = pointOIMethod;
    }

    public String getMasterMerchant() {
        return masterMerchant;
    }

    public void setMasterMerchant(String masterMerchant) {
        this.masterMerchant = masterMerchant;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantCC() {
        return merchantCC;
    }

    public void setMerchantCC(String merchantCC) {
        this.merchantCC = merchantCC;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTipAndFee() {
        return tipAndFee;
    }

    public void setTipAndFee(String tipAndFee) {
        this.tipAndFee = tipAndFee;
    }

    public String getFixedFee() {
        return fixedFee;
    }

    public void setFixedFee(String fixedFee) {
        this.fixedFee = fixedFee;
    }

    public String getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(String percentFee) {
        this.percentFee = percentFee;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getAddtionalData() {
        return addtionalData;
    }

    public void setAddtionalData(String addtionalData) {
        this.addtionalData = addtionalData;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCrc16() {
        return crc16;
    }

    public void setCrc16(String crc16) {
        this.crc16 = crc16;
    }

    public QRAddtionalBean getAddtionalBean() {
        return addtionalBean;
    }

    public void setAddtionalBean(QRAddtionalBean addtionalBean) {
        this.addtionalBean = addtionalBean;
    }

    public String getVisa() {
        return visa;
    }

    public void setVisa(String visa) {
        this.visa = visa;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getUnionPay() {
        return unionPay;
    }

    public void setUnionPay(String unionPay) {
        this.unionPay = unionPay;
    }
    
    
    

}
