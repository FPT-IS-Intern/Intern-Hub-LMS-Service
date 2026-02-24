package com.fis.lms_service.infra.generator;

import com.intern.hub.library.common.utils.Snowflake;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

/** Admin 1/26/2026 */
@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

  private final Snowflake snowflake;

  public SnowflakeIdGenerator(Snowflake snowflake) {
    this.snowflake = snowflake;
  }

  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
    return snowflake.next();
  }
}
