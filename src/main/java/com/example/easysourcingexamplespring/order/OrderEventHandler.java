package com.example.easysourcingexamplespring.order;

import io.github.alikelleci.easysourcing.core.messaging.eventhandling.annotations.HandleEvent;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderCancelled;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderConfirmed;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderDelivered;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderPlaced;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderShipped;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

  @HandleEvent
  public void on(OrderPlaced event) { /* insert into read model */ }

  @HandleEvent
  public void on(OrderConfirmed event) { /* update read model */ }

  @HandleEvent
  public void on(OrderShipped event) { /* send shipping notification */ }

  @HandleEvent
  public void on(OrderDelivered event) { /* update read model */ }

  @HandleEvent
  public void on(OrderCancelled event) { /* remove from read model */ }
}

