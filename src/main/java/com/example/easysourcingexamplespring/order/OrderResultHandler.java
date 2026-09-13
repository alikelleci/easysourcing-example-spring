package com.example.easysourcingexamplespring.order;

import io.github.alikelleci.easysourcing.core.messaging.resulthandling.annotations.HandleResult;
import com.example.easysourcingexamplespring.order.OrderCommand.CancelOrder;
import com.example.easysourcingexamplespring.order.OrderCommand.ConfirmOrder;
import com.example.easysourcingexamplespring.order.OrderCommand.DeliverOrder;
import com.example.easysourcingexamplespring.order.OrderCommand.PlaceOrder;
import com.example.easysourcingexamplespring.order.OrderCommand.ShipOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class OrderResultHandler {

  @HandleResult
  public void handle(PlaceOrder command) {
  }

  @HandleResult
  public void handle(ConfirmOrder command) {
  }

  @HandleResult
  public void handle(ShipOrder command) {
  }

  @HandleResult
  public void handle(DeliverOrder command) {
  }

  @HandleResult
  public void handle(CancelOrder command) {
  }

}

