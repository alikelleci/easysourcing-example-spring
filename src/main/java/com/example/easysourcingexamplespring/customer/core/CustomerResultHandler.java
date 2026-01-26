package com.example.easysourcingexamplespring.customer.core;

import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.AddCredits;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.ChangeFirstName;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.ChangeLastName;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.CreateCustomer;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.DeleteCustomer;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.IssueCredits;
import io.github.alikelleci.easysourcing.core.messaging.resulthandling.annotations.HandleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerResultHandler {

  @HandleResult
  public void handle(CreateCustomer command) {
  }

  @HandleResult
  public void handle(ChangeFirstName command) {
  }

  @HandleResult
  public void handle(ChangeLastName command) {
  }

  @HandleResult
  public void handle(AddCredits event) {
  }

  @HandleResult
  public void handle(IssueCredits event) {
  }

  @HandleResult
  public void handle(DeleteCustomer event) {
  }

}

