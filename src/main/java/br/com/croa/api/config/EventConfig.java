package br.com.croa.api.config;

import br.com.croa.api.util.AmqpConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
public class EventConfig {

    @Value("${spring.rabbitmq.listener.simple.default-requeue-rejected:false}")
    private boolean defaultRequeueRejected;
    @Value("${spring.rabbitmq.listener.simple.concurrency:5}")
    private int concurrentConsumers;
    @Value("${spring.rabbitmq.listener.simple.prefetch:1}")
    private int prefetchCount;

    @Value("${api.event.audit.exchange}")
    @Getter
    private String croaExchange;
    @Value("${api.event.audit.queue}")
    private String croaEventQueue;
    @Value("${api.event.audit.error.queue}")
    private String croaEventErrorQueue;
    @Value("${api.event.audit.error.ttl}")
    private Integer croaEventErrorTTL;
    @Value("${api.event.audit.parking.lot.queue}")
    @Getter
    private String croaEventParkingLotQueue;

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setDefaultRequeueRejected(defaultRequeueRejected);
        factory.setPrefetchCount(prefetchCount);
        return factory;
    }

    @Bean
    AmqpAdmin amqpAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDefaultPropertyInclusion(NON_NULL);
        return mapper;
    }

    @Bean("jacksonConverter")
    public Jackson2JsonMessageConverter jacksonConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    Declarables createTopology() {
        return new Declarables(createCroaQueue());
    }

    private List<Declarable> createCroaQueue() {
        FanoutExchange fanoutExchange = ExchangeBuilder.fanoutExchange(croaExchange).durable(true).build();
        Queue queue = QueueBuilder.durable(croaEventQueue)
                .withArgument(AmqpConstants.AMQP_DEAD_LETTER_EXCHANGE, AmqpConstants.AMQP_DEFAULT_DIRECT_EXCHANGE)
                .withArgument(AmqpConstants.AMQP_DEAD_LETTER_ROUNTING_KEY, croaEventErrorQueue)
                .build();
        Queue errorQueue = QueueBuilder.durable(croaEventErrorQueue)
                .withArgument(AmqpConstants.AMQP_DEAD_LETTER_EXCHANGE, AmqpConstants.AMQP_DEFAULT_DIRECT_EXCHANGE)
                .withArgument(AmqpConstants.AMQP_DEAD_LETTER_ROUNTING_KEY, croaEventQueue)
                .withArgument(AmqpConstants.AMQP_QUEUE_MESSAGE_TTL, croaEventErrorTTL)
                .build();
        Queue parkingLotQueue = QueueBuilder.durable(croaEventParkingLotQueue).withArgument(AmqpConstants.AMQP_QUEUE_MODE, AmqpConstants.AMQP_QUEUE_MODE_LAZY_VALUE).build();
        Binding binding = BindingBuilder.bind(queue).to(fanoutExchange);

        return List.of(fanoutExchange, queue, errorQueue, parkingLotQueue, binding);
    }

}
