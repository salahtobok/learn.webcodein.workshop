package com.webcodein.workshop.csp.model;

public enum CspDirective {
    DEFAULT_SRC("default-src"),
    SCRIPT_SRC("script-src"),
    SCRIPT_SRC_ELEM("script-src-elem"),
    STYLE_SRC("style-src"),
    IMG_SRC("img-src"),
    CONNECT_SRC("connect-src"),
    OBJECT_SRC("object-src"),
    BASE_URI("base-uri"),
    FORM_ACTION("form-action"),
    FRAME_ANCESTORS("frame-ancestors"),
    REPORT_TO("report-to"),
    REPORT_URI("report-uri"),
    UPGRADE_INSECURE_REQUESTS("upgrade-insecure-requests");

    private final String headerName;

    CspDirective(String headerName) {
        this.headerName = headerName;
    }

    public String headerName() {
        return headerName;
    }
}
