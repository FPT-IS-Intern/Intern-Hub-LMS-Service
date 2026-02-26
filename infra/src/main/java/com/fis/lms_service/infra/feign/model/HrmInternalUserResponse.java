package com.fis.lms_service.infra.feign.model;

import lombok.Data;

@Data
public class HrmInternalUserResponse {

    private Long userId;
    private String idNumber;
    private String companyEmail;
}
