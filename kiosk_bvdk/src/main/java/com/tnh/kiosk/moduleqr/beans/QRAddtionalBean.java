package com.tnh.kiosk.moduleqr.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QRAddtionalBean {

    private String billNumber;

    private String mobile;

    private String storeID;

    private String loyaltyNumber;

    private String referenceID;

    private String customerID;

    private String terminalID;

    private String purpose;

    private String expDate;

    private String checkSum;

    private String consumerAddress;

    private String consumerMobile;

    private String consumerEmail;

    private String consumerData;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getLoyaltyNumber() {
        return loyaltyNumber;
    }

    public void setLoyaltyNumber(String loyaltyNumber) {
        this.loyaltyNumber = loyaltyNumber;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getConsumerAddress() {
        return consumerAddress;
    }

    public void setConsumerAddress(String consumerAddress) {
        this.consumerAddress = consumerAddress;
    }

    public String getConsumerMobile() {
        return consumerMobile;
    }

    public void setConsumerMobile(String consumerMobile) {
        this.consumerMobile = consumerMobile;
    }

    public String getConsumerEmail() {
        return consumerEmail;
    }

    public void setConsumerEmail(String consumerEmail) {
        this.consumerEmail = consumerEmail;
    }

    public String getConsumerData() {
        return consumerData;
    }

    public void setConsumerData(String consumerData) {
        this.consumerData = consumerData;
    }

}
