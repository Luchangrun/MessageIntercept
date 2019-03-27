package com.tigerobo.venturecapital.messageinterccept;

import java.io.Serializable;
import java.util.List;

public class RequestParams implements Serializable {
    private List<RequestParam> params;

    public List<RequestParam> getParams() {
        return params;
    }

    public void setParams(List<RequestParam> params) {
        this.params = params;
    }

    public static class RequestParam implements Serializable{
        private String paramsName;
        private String paramsValue;

        public RequestParam(String paramsName, String paramsValue) {
            this.paramsName = paramsName;
            this.paramsValue = paramsValue;
        }

        public String getParamsName() {
            return paramsName;
        }

        public void setParamsName(String paramsName) {
            this.paramsName = paramsName;
        }

        public String getParamsValue() {
            return paramsValue;
        }

        public void setParamsValue(String paramsValue) {
            this.paramsValue = paramsValue;
        }
    }
}
