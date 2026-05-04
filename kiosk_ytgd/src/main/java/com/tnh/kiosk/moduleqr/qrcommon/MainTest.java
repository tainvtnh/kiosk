package com.tnh.kiosk.moduleqr.qrcommon;

import com.tnh.kiosk.moduleqr.beans.QRAddtionalBean;
import com.tnh.kiosk.moduleqr.beans.QRBean;
import com.tnh.kiosk.moduleqr.beans.QRPackBean;
import com.tnh.kiosk.moduleqr.qr.exception.InvalidLengthException;

public class MainTest {

    public static void main(String[] args) throws InvalidLengthException {
        QRBean bean = QRBean.builder()
                .payLoad("01")    // mặc định 01
                .pointOIMethod("12")   // mặc định
                .merchantCode("1295195294812")   // BIDV cung cấp cho merchant
                .merchantName("VNPAY")          // BIDV cung cấp cho merchant
                .masterMerchant("970488") // mã cố địnhmaster merchant của BIDV
                .merchantCC("1234")     // BIDV cung cấp cho merchant (loại hình doanh nghiệp)
                .ccy("704")             // cố định
                .amount("50000")        // merchant tự điền
                .countryCode("VN")       // mặc định
                .merchantCity("HANOI")   // merchant tự điền theo chuẩn
                .pinCode("100000")         // ma pin thanh pho
                .build();
        QRAddtionalBean addBean = QRAddtionalBean.builder()
                .billNumber("3998")     // số hóa đơn do merchant tự điền
                .terminalID("0001")     //mã điểm bán merchant tự đièn
                .storeID("SONVC")       // tên điểm bán merchant tự điền
                .purpose("TOPUP")         // mục đích của qr code (không quá 19 kí tự)
                .build();
        bean.setAddtionalBean(addBean);
        QRPack pack = new QRPack();
        QRPackBean data = pack.pack(bean, "private-key");
        System.out.println("Data: " + data.getQrData());

    }
}
