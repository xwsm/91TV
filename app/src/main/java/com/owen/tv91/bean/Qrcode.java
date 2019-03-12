package com.owen.tv91.bean;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/12
 */
public class Qrcode {


    /**
     * qrCodeUrl : http://www.mxnzp.com/api_file/qrcode/7/2/d/d/0/9/a/e/327588b1ddb44cf7a95e43d7ad2f5b90.png
     * content : 你好
     * type : 0
     * qrCodeBase64 : null
     */

    public String qrCodeUrl;
    public String content;
    public int type;
    public String qrCodeBase64;

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
