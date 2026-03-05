package com.intern.hub.infra.feign;

import com.intern.hub.infra.feign.model.HrmUserClientModel;
import com.intern.hub.library.common.dto.ResponseApi;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hrm", url = "${feign.client.config.hrm.url}")
public interface HrmInternalFeignClient {

    @GetMapping("/hrm/internal/users/{userId}")
    ResponseApi<HrmUserClientModel> getUserByIdInternal(@PathVariable("userId") Long userId);

    @GetMapping("/hrm/internal/users/by-email")
    ResponseApi<HrmUserClientModel> getUserByEmailInternal(@RequestParam("email") String email);

    @PostMapping("/hrm/internal/users/by-ids")
    ResponseApi<List<HrmUserClientModel>> getUsersByIdsInternal(@RequestBody List<Long> userIds);
}
