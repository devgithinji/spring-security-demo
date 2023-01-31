package com.densoft.sec.DTO;

import lombok.Data;

@Data
public class ResetPasswordReq {
    private String email;
    private String password;
    private String confirmPassword;
    private String code;
}
