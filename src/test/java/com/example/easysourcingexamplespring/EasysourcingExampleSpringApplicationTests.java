package com.example.easysourcingexamplespring;

import com.example.easysourcingexamplespring.domain.CustomerCommand;
import com.example.easysourcingexamplespring.domain.CustomerCommand.CreateCustomer;
import com.example.easysourcingexamplespring.domain.CustomerEvent;
import com.example.easysourcingexamplespring.domain.CustomerEvent.CustomerCreated;
import com.example.easysourcingexamplespring.handlers.CustomerCommandHandler;
import com.example.easysourcingexamplespring.handlers.CustomerEventHandler;
import com.example.easysourcingexamplespring.handlers.CustomerEventSourcingHandler;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

class EasysourcingExampleSpringApplicationTests {

	private TopologyTestDriver testDriver;
	private TestInputTopic<String, Command> commands;
	private TestOutputTopic<String, Event> events;

	@BeforeEach
	void setUp() {
		EasySourcing easySourcing = EasySourcing.builder()
				.registerHandler(new CustomerCommandHandler())
				.registerHandler(new CustomerEventSourcingHandler())
				.build();

		testDriver = new TopologyTestDriver(easySourcing.topology());

		commands = testDriver.createInputTopic(
				CustomerCommand.class.getAnnotation(TopicInfo.class).value(),
				new StringSerializer(),
				new JsonSerializer<>());

		events = testDriver.createOutputTopic(
				CustomerEvent.class.getAnnotation(TopicInfo.class).value(),
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
	void CustomerCreatedTest() {
		Command command = Command.builder()
				.payload(CreateCustomer.builder()
						.id("cust-1")
						.firstName("John")
						.lastName("Doe")
						.build())
				.build();

		System.out.println(command);

		// publish command to topic with aggregateId as key!
		commands.pipeInput(command.getAggregateId(), command);

		// read events
		List<Event> result = events.readValuesToList();

		// assert
		assertThat(result.size(), equalTo(1));
		Event event = result.get(0);
		assertThat(event.getPayload(), instanceOf(CustomerCreated.class));
	}
}
