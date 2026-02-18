package org.example.curio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.curio.dto.CreateFormRequest;
import org.example.curio.dto.FormDto;
import org.example.curio.entity.FormStatus;
import org.example.curio.entity.User;
import org.example.curio.service.FormService;
import org.example.curio.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormController.class)
class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FormService formService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private User testUser;
    private FormDto testFormDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("admin@curio.io")
                .name("Admin")
                .password("encoded")
                .build();

        testFormDto = new FormDto(1L, "Employee Pulse", "Monthly check-in",
                FormStatus.DRAFT, 1L, List.of(), null, null);

        when(userService.findByEmail("admin@curio.io")).thenReturn(Optional.of(testUser));
    }

    @Test
    @WithMockUser(username = "admin@curio.io")
    void createForm_withValidRequest_returns201() throws Exception {
        when(formService.createForm(any(), eq(1L))).thenReturn(testFormDto);

        CreateFormRequest request = new CreateFormRequest("Employee Pulse", "Monthly check-in", List.of());

        mockMvc.perform(post("/api/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Employee Pulse"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @WithMockUser(username = "admin@curio.io")
    void getForm_withValidId_returns200() throws Exception {
        when(formService.getForm(1L)).thenReturn(testFormDto);

        mockMvc.perform(get("/api/forms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Employee Pulse"));
    }

    @Test
    @WithMockUser(username = "admin@curio.io")
    void getMyForms_returnsListOfForms() throws Exception {
        when(formService.getFormsByUser(1L)).thenReturn(List.of(testFormDto));

        mockMvc.perform(get("/api/forms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Employee Pulse"));
    }

    @Test
    @WithMockUser(username = "admin@curio.io")
    void publishForm_returns200WithPublishedStatus() throws Exception {
        FormDto published = new FormDto(1L, "Employee Pulse", "Monthly check-in",
                FormStatus.PUBLISHED, 1L, List.of(), null, null);
        when(formService.publishForm(1L)).thenReturn(published);

        mockMvc.perform(patch("/api/forms/1/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void getForm_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/forms/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createForm_withoutAuthentication_returns401() throws Exception {
        CreateFormRequest request = new CreateFormRequest("Test", null, List.of());

        mockMvc.perform(post("/api/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
