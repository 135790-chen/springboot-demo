package com.example.demo.message.controller;

import com.example.demo.common.GlobalExceptionHandler;
import com.example.demo.message.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class KafkaTestControllerTest {

    private MockMvc mockMvc;
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaProducerService = mock(KafkaProducerService.class);
        KafkaTestController controller = new KafkaTestController();
        ReflectionTestUtils.setField(controller, "kafkaProducerService", kafkaProducerService);
        ReflectionTestUtils.setField(controller, "objectMapper", new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void status_KafkaAvailable() throws Exception {
        when(kafkaProducerService.isKafkaAvailable()).thenReturn(true);

        mockMvc.perform(get("/kafka/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void status_KafkaNotAvailable() throws Exception {
        when(kafkaProducerService.isKafkaAvailable()).thenReturn(false);

        mockMvc.perform(get("/kafka/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void receiveStudent_studentServiceUnavailable() throws Exception {
        // 学生服务未启动时返回 500
        mockMvc.perform(post("/kafka/receive-student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试\",\"age\":20,\"grade\":\"大一\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
