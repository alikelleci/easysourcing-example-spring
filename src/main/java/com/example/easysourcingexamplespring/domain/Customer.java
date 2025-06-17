package com.example.easysourcingexamplespring.domain;

import io.github.alikelleci.easysourcing.core.common.annotations.AggregateId;
import io.github.alikelleci.easysourcing.core.common.annotations.AggregateRoot;
import io.github.alikelleci.easysourcing.core.common.annotations.TopicInfo;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
@AggregateRoot
public class Customer {
  @AggregateId
  private String id;
  private String firstName;
  private String lastName;
  private int credits;
  private Instant birthday;
  private Instant dateCreated;
}
