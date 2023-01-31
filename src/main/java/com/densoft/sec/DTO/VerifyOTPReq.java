package com.densoft.sec.DTO;

import lombok.Data;

@Data
public class VerifyOTPReq {
    private String email;
    private String code;
}
