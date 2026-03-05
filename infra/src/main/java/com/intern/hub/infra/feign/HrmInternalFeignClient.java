package com.intern.hub.infra.feign;

import com.intern.hub.infra.feign.model.HrmUserClientModel;
import com.intern.hub.library.common.dto.ResponseApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hrm", url = "${feign.client.config.hrm.url}")
public interface HrmInternalFeignClient {

    @GetMapping("/hrm/internal/users/{userId}")
    ResponseApi<HrmUserClientModel> getUserByIdInternal(@PathVariable("userId") Long userId);
}

