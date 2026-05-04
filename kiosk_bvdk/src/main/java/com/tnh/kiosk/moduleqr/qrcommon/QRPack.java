package com.tnh.kiosk.moduleqr.qrcommon;

import org.apache.commons.lang3.StringUtils;

import com.tnh.kiosk.moduleqr.beans.QRAddtionalBean;
import com.tnh.kiosk.moduleqr.beans.QRBean;
import com.tnh.kiosk.moduleqr.beans.QRPackBean;
import com.tnh.kiosk.moduleqr.qr.exception.InvalidLengthException;

/**
 *
 * @author Tran Ba Y
 */
public final class QRPack {

    /**
     *
     * @param bean
     * @param privateKey
     * @return
     * @throws InvalidLengthException
     */
    public final QRPackBean pack(QRBean bean, String privateKey) throws InvalidLengthException {
        StringBuilder content = new StringBuilder();
        if (StringUtils.isNotEmpty(bean.getPayLoad())) {
            content.append(QRTag.PAY_LOAD)
                    .append(padLeft(bean.getPayLoad().length()))
                    .append(bean.getPayLoad());
        }

        if (StringUtils.isNotEmpty(bean.getPointOIMethod())) {
            content.append(QRTag.POINT_OI_METHOD)
                    .append(padLeft(bean.getPointOIMethod().length()))
                    .append(bean.getPointOIMethod());
        }

        if (StringUtils.isNotEmpty(bean.getVisa())) {
            content.append(QRTag.VISA)
                    .append(padLeft(bean.getVisa().length()))
                    .append(bean.getVisa());
        }

        if (StringUtils.isNotEmpty(bean.getMaster())) {
            content.append(QRTag.MASTERCARD)
                    .append(padLeft(bean.getMaster().length()))
                    .append(bean.getMaster());
        }

        if (StringUtils.isNotEmpty(bean.getUnionPay())) {
            content.append(QRTag.UNIONPAY)
                    .append(padLeft(bean.getUnionPay().length()))
                    .append(bean.getUnionPay());
        }

        String merchantAccount = packMerchant(bean);
        if (StringUtils.isNotEmpty(merchantAccount)) {
            content.append(QRTag.MC_ACCOUNT)
                    .append(padLeft(merchantAccount.length()))
                    .append(merchantAccount);
        }

        if (StringUtils.isNotEmpty(bean.getMerchantCC())) {
            content.append(QRTag.MC_CATEGORY_CODE)
                    .append(padLeft(bean.getMerchantCC().length()))
                    .append(bean.getMerchantCC());
        }

        if (StringUtils.isNotEmpty(bean.getCcy())) {
            content.append(QRTag.CCY)
                    .append(padLeft(bean.getCcy().length()))
                    .append(bean.getCcy());
        }

        if (StringUtils.isNotEmpty(bean.getAmount())) {
            content.append(QRTag.AMOUNT)
                    .append(padLeft(bean.getAmount().length()))
                    .append(bean.getAmount());
        }
        if (StringUtils.isNotEmpty(bean.getTipAndFee())) {
            content.append(QRTag.TIP_AND_FEE)
                    .append(padLeft(bean.getTipAndFee().length()))
                    .append(bean.getTipAndFee());
        }

        if (StringUtils.isNotEmpty(bean.getFixedFee())) {
            content.append(QRTag.FIXED_FEE)
                    .append(padLeft(bean.getFixedFee().length()))
                    .append(bean.getFixedFee());
        }

        if (StringUtils.isNotEmpty(bean.getPercentFee())) {
            content.append(QRTag.PERCENT_FEE)
                    .append(padLeft(bean.getPercentFee().length()))
                    .append(bean.getPercentFee());
        }

        if (StringUtils.isNotEmpty(bean.getCountryCode())) {
            content.append(QRTag.COUNTRY_CODE)
                    .append(padLeft(bean.getCountryCode().length()))
                    .append(bean.getCountryCode());
        }

        if (StringUtils.isNotEmpty(bean.getMerchantName())) {
            content.append(QRTag.MC_NAME)
                    .append(padLeft(bean.getMerchantName().length()))
                    .append(bean.getMerchantName());
        }

        if (StringUtils.isNotEmpty(bean.getMerchantCity())) {
            content.append(QRTag.MC_CITY)
                    .append(padLeft(bean.getMerchantCity().length()))
                    .append(bean.getMerchantCity());
        }

        if (StringUtils.isNotEmpty(bean.getPinCode())) {
            content.append(QRTag.MC_PIN_CODE)
                    .append(padLeft(bean.getPinCode().length()))
                    .append(bean.getPinCode());
        }

        String addtionalData = packAddtional(bean.getAddtionalBean(), privateKey);
        bean.setAddtionalData(addtionalData);
        if (StringUtils.isNotEmpty(addtionalData)) {
            content.append(QRTag.ADDTIONAL_DATA)
                    .append(padLeft(addtionalData.length()))
                    .append(addtionalData);
        }

        if (StringUtils.isNotEmpty(bean.getTerm())) {
            content.append(QRTag.TERM)
                    .append(padLeft(bean.getTerm().length()))
                    .append(bean.getTerm());
        }

        String dataToCRC = content.append(QRTag.CRC16).append(QRTag.CRC_LENGHT).toString();
        String crc16 = CRC16.caculate(dataToCRC);
        bean.setCrc16(crc16);
        return QRPackBean.builder().qrData(dataToCRC + crc16).qrBean(bean).build();
    }

    /**
     *
     * @param bean
     * @param privateKey
     * @return
     * @throws InvalidLengthException
     */
    public final String packAddtional(QRAddtionalBean bean, String privateKey) throws InvalidLengthException {
        StringBuilder content = new StringBuilder(100);
        if (StringUtils.isNotEmpty(bean.getBillNumber())) {
            content.append(QRTag.BILL_NUMBER)
                    .append(padLeft(bean.getBillNumber().length()))
                    .append(bean.getBillNumber());
        }
        if (StringUtils.isNotEmpty(bean.getStoreID())) {
            content.append(QRTag.STORE_ID)
                    .append(padLeft(bean.getStoreID().length()))
                    .append(bean.getStoreID());
        }

        if (StringUtils.isNotEmpty(bean.getReferenceID())) {
            String referenceID;
            String clearReferenceID = bean.getReferenceID().substring(2);
            String prefix = bean.getReferenceID().substring(0, 2);
            if (StringUtils.isNotEmpty(bean.getExpDate())) {
                referenceID = prefix + bean.getExpDate() + clearReferenceID;
            } else {
                referenceID = prefix + EXPIRE_DATE_EMPTY + clearReferenceID;
            }
            content.append(QRTag.REFERENCE_NUMBER)
                    .append(padLeft(referenceID.length()))
                    .append(referenceID);
        }
        if (StringUtils.isNotEmpty(bean.getCustomerID())) {
            content.append(QRTag.CUSTOMER_ID)
                    .append(padLeft(bean.getCustomerID().length()))
                    .append(bean.getCustomerID());
        }

        if (StringUtils.isNotEmpty(bean.getTerminalID())) {
            content.append(QRTag.TERMINAL_ID)
                    .append(padLeft(bean.getTerminalID().length()))
                    .append(bean.getTerminalID());
        }

        if (StringUtils.isNotEmpty(bean.getPurpose())) {
            content.append(QRTag.PERPOSE)
                    .append(padLeft(bean.getPurpose().length()))
                    .append(StringUtils.stripAccents(bean.getPurpose()));
        }

        String consumerData = "";
        if (StringUtils.isNotEmpty(bean.getConsumerAddress())) {
            consumerData = bean.getConsumerAddress();
        }
        if (StringUtils.isNotEmpty(bean.getConsumerMobile())) {
            consumerData = consumerData + bean.getConsumerMobile();
        }
        if (StringUtils.isNotEmpty(bean.getConsumerEmail())) {
            consumerData = consumerData + bean.getConsumerEmail();
        }
        if (StringUtils.isNotEmpty(consumerData)) {
            content.append(QRTag.CONSUMER_DATA)
                    .append(padLeft(consumerData.length()))
                    .append(consumerData);
        }
        return content.toString();
    }

    private String packMerchant(QRBean bean) throws InvalidLengthException {
        StringBuilder content = new StringBuilder(100);
        if (StringUtils.isNotEmpty(bean.getMasterMerchant())) {
            content.append(QRTag.MC_ACCOUNT_GUID)
                    .append(padLeft(bean.getMasterMerchant().length()))
                    .append(bean.getMasterMerchant());
        }
        if (StringUtils.isNotEmpty(bean.getMerchantCode())) {
            content.append(QRTag.MC_ACCOUNT_MC_ID)
                    .append(padLeft(bean.getMerchantCode().length()))
                    .append(bean.getMerchantCode());
        }
        return content.toString();
    }

    /**
     *
     * @return
     */
    private String padLeft(int lenght) throws InvalidLengthException {
        if (lenght > 99) {
            throw new InvalidLengthException("Lenght field can not be greater than 99");
        }
        return StringUtils.leftPad(String.valueOf(lenght), 2, "0");
    }

    private static final String EXPIRE_DATE_EMPTY = "0000000000";

}
