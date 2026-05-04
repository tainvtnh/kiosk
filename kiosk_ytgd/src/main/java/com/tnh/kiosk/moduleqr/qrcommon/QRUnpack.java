package com.tnh.kiosk.moduleqr.qrcommon;

import org.apache.commons.lang3.StringUtils;
import com.tnh.kiosk.moduleqr.beans.QRAddtionalBean;
import com.tnh.kiosk.moduleqr.beans.QRBean;

public final class QRUnpack {

    /**
     *
     * @param data
     * @param bean
     * @return
     */
    public static QRBean unpack(String data, QRBean bean) throws NumberFormatException, IndexOutOfBoundsException {
        if (data.length() > 4) {
            String header = data.substring(0, 4);
            String tag = header.substring(0, 2);
            String len = header.substring(2);
            String body = data.substring(4, 4 + Integer.parseInt(len));

            switch (tag) {
                case QRTag.PAY_LOAD:
                    bean.setPayLoad(body);
                    break;
                case QRTag.POINT_OI_METHOD:
                    bean.setPointOIMethod(body);
                    break;
                case QRTag.MC_ACCOUNT:
                    findMcAccount(body, bean);
                    break;
                case QRTag.MC_CATEGORY_CODE:
                    bean.setMerchantCC(body);
                    break;
                case QRTag.CCY:
                    bean.setCcy(body);
                    break;
                case QRTag.AMOUNT:
                    bean.setAmount(body);
                    break;
                case QRTag.TIP_AND_FEE:
                    bean.setTipAndFee(body);
                    break;
                case QRTag.FIXED_FEE:
                    bean.setFixedFee(body);
                    break;
                case QRTag.PERCENT_FEE:
                    bean.setPercentFee(body);
                    break;
                case QRTag.COUNTRY_CODE:
                    bean.setCountryCode(body);
                    break;
                case QRTag.MC_NAME:
                    bean.setMerchantName(body);
                    break;
                case QRTag.MC_CITY:
                    bean.setMerchantCity(body);
                    break;
                case QRTag.MC_PIN_CODE:
                    bean.setPinCode(body);
                    break;
                case QRTag.ADDTIONAL_DATA:
                    bean.setAddtionalData(body);
                    break;
                case QRTag.TERM:
                    bean.setTerm(body);
                    break;
                case QRTag.CRC16:
                    bean.setCrc16(body);
                    break;
                case QRTag.VISA:
                    bean.setVisa(body);
                    break;
                case QRTag.MASTERCARD:
                    bean.setMaster(body);
                    break;
                case QRTag.UNIONPAY:
                    bean.setUnionPay(body);
                    break;
                default:
                    break;
            }
            String subData = data.substring(body.length() + 4);
            unpack(subData, bean);
        }
        return bean;
    }

    /**
     *
     * @param mcAccount
     * @return
     */
    private static void findMcAccount(String mcAccount, QRBean bean) {
        String header = mcAccount.substring(0, 4);
        String len = header.substring(2);
        String masterMC = mcAccount.substring(4, 4 + Integer.parseInt(len));
        bean.setMasterMerchant(masterMC);
        String bodyMerchantCode = mcAccount.substring(4 + Integer.parseInt(len));
        String merchantCode = findMerchantCode(bodyMerchantCode);
        bean.setMerchantCode(merchantCode);
    }

    private static String findMerchantCode(String mcAccount) {
        String header = mcAccount.substring(0, 4);
        String len = header.substring(2);
        String body = mcAccount.substring(4, 4 + Integer.parseInt(len));
        return body;
    }

    /**
     *
     * @param addtional
     * @param bean
     * @return
     */
    public static QRAddtionalBean unpackAddtionalData(String addtional, QRAddtionalBean bean) throws Exception {
        if (StringUtils.isEmpty(addtional)) {
            return null;
        }
        if (addtional.length() > 4) {
            String header = addtional.substring(0, 4);
            String tag = header.substring(0, 2);
            String len = header.substring(2);
            String body = addtional.substring(4, 4 + Integer.parseInt(len));
            switch (tag) {
                case QRTag.BILL_NUMBER:
                    bean.setBillNumber(body);
                    break;
                case QRTag.STORE_ID:
                    bean.setStoreID(body);
                    break;
                case QRTag.LOYALTY_NUMBER:
                    bean.setBillNumber(body);
                    break;
                case QRTag.REFERENCE_NUMBER:
                    String prefix = body.substring(0, 2);
                    String clearRefID = body.substring(12);
                    bean.setReferenceID(prefix + clearRefID);
                    if (body.length() > 14) {
                        String expireDate = body.substring(2, 12);
                        bean.setExpDate(expireDate);
                    }
                    break;
                case QRTag.CUSTOMER_ID:
                    bean.setCustomerID(body);
                    break;
                case QRTag.TERMINAL_ID:
                    bean.setTerminalID(body);
                    break;
                case QRTag.PERPOSE:
                    bean.setPurpose(body);
                    break;
                case QRTag.CONSUMER_DATA:
                    bean.setConsumerData(body);
                    break;
                default:
                    break;
            }
            String subData = addtional.substring(body.length() + 4);
            unpackAddtionalData(subData, bean);
        }
        return bean;
    }

    /**
     *
     * @param args
     */
//    public static void main(String[] args) {
//        QRBean beanQR = QRBean.builder().build();
//        QRAddtionalBean addBean = QRAddtionalBean.builder().build();
//
//        String data = "000201010212261690840511234384525204490053037045405700095802VN5915NguyetTran Shop6008DONGTHAP610600541162700320Nguyet Tran Shop CS1050702LONHG070400010805LONHG 5110 1706152359 87826304D3D8";
//        QRBean unpackDa = unpack(data, beanQR);
//        System.out.println("Data full : " + unpackDa.toString());
//
//        QRAddtionalBean unpaclDaa = unpackAddtionalData(unpackDa.getAddtionalData(), addBean);
//
//        System.out.println("Data addd:  " + unpaclDaa.toString());
//    }
}
