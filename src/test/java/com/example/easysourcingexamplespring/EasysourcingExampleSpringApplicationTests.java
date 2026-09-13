package com.example.easysourcingexamplespring;

import com.example.easysourcingexamplespring.order.OrderCommand;
import com.example.easysourcingexamplespring.order.OrderCommand.ConfirmOrder;
import com.example.easysourcingexamplespring.order.OrderCommand.PlaceOrder;
import com.example.easysourcingexamplespring.order.OrderCommandHandler;
import com.example.easysourcingexamplespring.order.OrderEvent;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderConfirmed;
import com.example.easysourcingexamplespring.order.OrderEvent.OrderPlaced;
import com.example.easysourcingexamplespring.order.OrderEventSourcingHandler;
import io.github.alikelleci.easysourcing.core.EasySourcing;
import io.github.alikelleci.easysourcing.core.common.annotations.TopicInfo;
import io.github.alikelleci.easysourcing.core.messaging.commandhandling.Command;
import io.github.alikelleci.easysourcing.core.messaging.eventhandling.Event;
import io.github.alikelleci.easysourcing.core.support.serialization.json.JsonDeserializer;
import io.github.alikelleci.easysourcing.core.support.serialization.json.JsonSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EasysourcingExampleSpringApplicationTests {

	private TopologyTestDriver testDriver;
	private TestInputTopic<String, Command> commands;
	private TestOutputTopic<String, Event> events;

	@BeforeEach
	void setUp() {
		EasySourcing easySourcing = EasySourcing.builder()
				.registerHandler(new OrderCommandHandler())
				.registerHandler(new OrderEventSourcingHandler())
				.build();

		testDriver = new TopologyTestDriver(easySourcing.topology());

		commands = testDriver.createInputTopic(
				OrderCommand.class.getAnnotation(TopicInfo.class).value(),
				new StringSerializer(),
				new JsonSerializer<>());

		events = testDriver.createOutputTopic(
				OrderEvent.class.getAnnotation(TopicInfo.class).value(),
				new StringDeserializer(),
				new JsonDeserializer<>(Event.class));
	}

	@AfterEach
	void tearDown() {
		if (testDriver != null) {
			testDriver.close();
		}
	}


	@Test
	void ShipOrderTest() {
		Command command = Command.builder()
				.payload(PlaceOrder.builder()
						.id("order-1")
						.customer("John Doe")
						.shippingAddress("Some Street 212")
						.couponCode("HMX004")
						.build())
				.build();

		// publish command to topic with aggregateId as key!
		commands.pipeInput(command.getAggregateId(), command);

		command = Command.builder()
				.payload(ConfirmOrder.builder()
						.id("order-1")
						.build())
				.build();

		// publish command to topic with aggregateId as key!
		commands.pipeInput(command.getAggregateId(), command);

		// read events
		List<Event> result = events.readValuesToList();

		// assert
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getPayload()).isInstanceOf(OrderPlaced.class);
		assertThat(result.get(1).getPayload()).isInstanceOf(OrderConfirmed.class);
	}

}
