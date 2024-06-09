package com.example.videohits.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMqConfig {

	@Value("${rabbitmq.connection.host}")
	public String host;

	@Value("${rabbitmq.connection.port}")
	public Integer port;

	@Value("${rabbitmq.connection.username}")
	public String user;

	@Value("${rabbitmq.connection.password}")
	public String password;

	@Value("${rabbitmq.queue.video-hit}")
	private String queueVideoHit;

	@Value("${rabbitmq.exchange.video-hit}")
	private String topicExchangeVideoHit;
	
	@Value("${rabbitmq.exchange.dead-letter}")
	private String topicExchangeDeadLetter;
	
	private static final String DEAD_LETTER_EXCHANGE_PARAM = "x-dead-letter-exchange";

	@Bean
	Queue queueVideoHitGroup1() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put(DEAD_LETTER_EXCHANGE_PARAM, topicExchangeDeadLetter);
		return new Queue(queueVideoHit, true, false, false, arguments);
	}
	
	@Bean
	TopicExchange exchangeVideoHit() {
		return new TopicExchange(topicExchangeVideoHit);
	}
	
	@Bean
	Binding bindingVideoHitGroup1() {
		return BindingBuilder.bind(queueVideoHitGroup1()).to(exchangeVideoHit()).with("#");
	}
	
	@Bean
	TopicExchange deadLetterExchange() {
		return new TopicExchange(topicExchangeDeadLetter);
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(host);
		connectionFactory.setPort(port);
		connectionFactory.setUsername(user);
		connectionFactory.setPassword(password);

		return connectionFactory;
	}
	
	@Bean
	public MessageConverter messageConverter() {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		return new Jackson2JsonMessageConverter(objectMapper);
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		final RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}
	
	@Bean
	public RabbitListenerContainerFactory<SimpleMessageListenerContainer> prefetchRabbitListenerContainerFactory(
			ConnectionFactory rabbitConnectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(rabbitConnectionFactory);
		factory.setPrefetchCount(5);
		factory.setAdviceChain(workMessagesRetryInterceptor());

		return factory;
	}

	@Bean
	public RetryOperationsInterceptor workMessagesRetryInterceptor() {
		return RetryInterceptorBuilder.stateless().maxAttempts(3).backOffOptions(1000, 2, 10000)
				.recoverer(new RejectAndDontRequeueRecoverer()).build();
	}
}
