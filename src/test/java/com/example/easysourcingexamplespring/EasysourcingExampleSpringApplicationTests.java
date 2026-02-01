package com.example.easysourcingexamplespring;

import com.example.easysourcingexamplespring.customer.shared.CustomerCommand;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.AddCredits;
import com.example.easysourcingexamplespring.customer.shared.CustomerCommand.CreateCustomer;
import com.example.easysourcingexamplespring.customer.shared.CustomerEvent;
import com.example.easysourcingexamplespring.customer.shared.CustomerEvent.CreditsAdded;
import com.example.easysourcingexamplespring.customer.shared.CustomerEvent.CustomerCreated;
import com.example.easysourcingexamplespring.customer.core.CustomerCommandHandler;
import com.example.easysourcingexamplespring.customer.core.CustomerEventSourcingHandler;
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
	void AddCreditsTest() {
		Command command = Command.builder()
				.payload(CreateCustomer.builder()
						.id("cust-1")
						.firstName("John")
						.lastName("Doe")
						.build())
				.build();

		// publish command to topic with aggregateId as key!
		commands.pipeInput(command.getAggregateId(), command);

		 command = Command.builder()
				.payload(AddCredits.builder()
						.id("cust-1")
						.build())
				.build();

		// publish command to topic with aggregateId as key!
		commands.pipeInput(command.getAggregateId(), command);


		// read events
		List<Event> result = events.readValuesToList();

		// assert
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getPayload()).isInstanceOf(CustomerCreated.class);
		assertThat(result.get(1).getPayload()).isInstanceOf(CreditsAdded.class);
	}
}
