package com.fis.lms_service.core;

import com.fis.lms_service.common.StringFormatter;
import com.fis.lms_service.infra.TestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/** Admin 1/24/2026 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestService {

  TestRepository testRepository;

  public String processRequest(String name) {
    String dbData = testRepository.getDataFromDb();
    return StringFormatter.format(name) + "(String đã format từ core) " + dbData;
  }
}
