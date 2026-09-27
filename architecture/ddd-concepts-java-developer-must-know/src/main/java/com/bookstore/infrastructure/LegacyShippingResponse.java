package com.bookstore.infrastructure;

public class LegacyShippingResponse {
    private String wgt;
    private String sigReq;
    
    public LegacyShippingResponse(String wgt, String sigReq) {
        this.wgt = wgt;
        this.sigReq = sigReq;
    }
    
    public String getWgt() { return wgt; }
    public String getSigReq() { return sigReq; }
}
