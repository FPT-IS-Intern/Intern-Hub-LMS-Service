package com.fis.lms_service.infra.feign;

import com.fis.lms_service.infra.feign.model.HrmInternalUserResponse;
import com.intern.hub.library.common.dto.ResponseApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hrm-internal-user", url = "${feign.client.config.hrm.url}")
public interface HrmUserInternalFeignClient {

    @GetMapping("/hrm/internal/users/{userId}")
    ResponseApi<HrmInternalUserResponse> getInternalUserById(@PathVariable("userId") Long userId);
}
