package br.com.croa.api.listener;

import br.com.croa.api.config.EventConfig;
import br.com.croa.api.event.ClientEvent;
import br.com.croa.api.helper.MessageHelper;
import br.com.croa.api.service.AuditService;
import br.com.croa.api.util.AmqpConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static br.com.croa.api.exception.ErrorCodeEnum.ERROR_CLIENT_PROCESSOR_AUDIT;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditClientConsumer {

    private final AuditService auditService;
    private final MessageHelper messageHelper;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final EventConfig eventConfig;

    @RabbitListener(queues = {"${api.event.audit.queue}"})
    public void consume(Message message) {
        try {
            if (validateXDeath(message)) return;
            final var clientEvent = objectMapper.readValue(message.getBody(), ClientEvent.class);
            auditService.audit(clientEvent);
        } catch (Exception e) {
            log.error(messageHelper.get(ERROR_CLIENT_PROCESSOR_AUDIT), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private boolean validateXDeath(Message message) {
        final var xDeath = message.getMessageProperties().getXDeathHeader();
        if (xDeath != null && ((Long) xDeath.get(0).get(AmqpConstants.AMQP_QUEUE_MESSAGE_DEATH_COUNT)) > 2l) {
            Try.run(() -> rabbitTemplate.send(EMPTY, eventConfig.getCroaEventParkingLotQueue(),
                            new Message(message.getBody(), MessagePropertiesBuilder.newInstance()
                                    .setContentType(APPLICATION_JSON_VALUE).build())))
                    .andThenTry(() -> log.info("ALARM!! mensages in parkinglot"))
                    .onFailure(e -> log.error(messageHelper.get(ERROR_CLIENT_PROCESSOR_AUDIT), e));
            return true;
        }
        return false;
    }

}
