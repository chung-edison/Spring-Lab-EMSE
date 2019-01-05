package com.emse.spring.faircorp;

import com.emse.spring.faircorp.hello.GreetingService;
import com.emse.spring.faircorp.model.light.LightController;
import com.emse.spring.faircorp.model.MeasurementDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
@IntegrationComponentScan
public class FaircorpApplicationConfig {

    @Autowired
    private LightController lightController;

    @Bean
    public CommandLineRunner greetingCommandLine(GreetingService greetingService) { // (3)
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                greetingService.greet("Spring");
            }
        };
    }

    private String brokerUrl = "tcp://m20.cloudmqtt.com:16305";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setUserName("actuator");
        options.setPassword("actuator".toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    // MQTT Outbound Adapter PUBLISHER

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("actuator", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("sensor/#");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // MQTT Inbound Adapter SUBSCRIBER

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("actuator", mqttClientFactory(),
                        "sensor/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String fullTopic = message.getHeaders().get("mqtt_receivedTopic").toString();
                String topic[] = fullTopic.split("/");
                Long id = Long.parseLong(topic[1]);
                String sensorTopic = topic[2];
                String jsonData = message.getPayload().toString();
                System.out.println(fullTopic + " " + jsonData + " " + id);
                if (sensorTopic.equals("DATA")) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        MeasurementDto[] mListDto = objectMapper.readValue(jsonData, MeasurementDto[].class);
                        for (MeasurementDto measurementDto:mListDto){
                            if(measurementDto.getN().equals("lightLevel"))
                                lightController.updateLightLevel(id, measurementDto.getV());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }


}