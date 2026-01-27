package com.fis.lms_service.infra.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.extra.spring.SpringUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

/** Admin 1/26/2026 */
@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
    Snowflake snowflake = SpringUtil.getBean(Snowflake.class);
    return snowflake.nextId();
  }
}
