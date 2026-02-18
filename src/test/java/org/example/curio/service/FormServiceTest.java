package org.example.curio.service;

import org.example.curio.dto.CreateFormRequest;
import org.example.curio.dto.FormDto;
import org.example.curio.entity.Form;
import org.example.curio.entity.FormStatus;
import org.example.curio.entity.User;
import org.example.curio.repository.FormRepository;
import org.example.curio.repository.QuestionRepository;
import org.example.curio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FormService formService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("admin@curio.io")
                .name("Admin")
                .password("hashed")
                .build();
    }

    @Test
    void createForm_withValidRequest_returnsFormDto() {
        Form savedForm = Form.builder()
                .id(10L)
                .title("Employee Pulse")
                .description("Monthly check-in")
                .createdBy(testUser)
                .status(FormStatus.DRAFT)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(formRepository.save(any(Form.class))).thenReturn(savedForm);

        CreateFormRequest request = new CreateFormRequest("Employee Pulse", "Monthly check-in", List.of());
        FormDto result = formService.createForm(request, 1L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.title()).isEqualTo("Employee Pulse");
        assertThat(result.status()).isEqualTo(FormStatus.DRAFT);
        assertThat(result.createdById()).isEqualTo(1L);
    }

    @Test
    void createForm_whenUserNotFound_throwsRuntimeException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CreateFormRequest request = new CreateFormRequest("Test", null, null);

        assertThatThrownBy(() -> formService.createForm(request, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getForm_withValidId_returnsFormDto() {
        Form form = Form.builder()
                .id(5L)
                .title("Feedback Survey")
                .createdBy(testUser)
                .status(FormStatus.PUBLISHED)
                .build();

        when(formRepository.findById(5L)).thenReturn(Optional.of(form));

        FormDto result = formService.getForm(5L);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.title()).isEqualTo("Feedback Survey");
        assertThat(result.status()).isEqualTo(FormStatus.PUBLISHED);
    }

    @Test
    void getForm_whenFormNotFound_throwsRuntimeException() {
        when(formRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formService.getForm(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Form not found");
    }

    @Test
    void getFormsByUser_returnsListOfDtos() {
        Form form1 = Form.builder().id(1L).title("Survey A").createdBy(testUser).status(FormStatus.DRAFT).build();
        Form form2 = Form.builder().id(2L).title("Survey B").createdBy(testUser).status(FormStatus.PUBLISHED).build();

        when(formRepository.findByCreatedById(1L)).thenReturn(List.of(form1, form2));

        List<FormDto> results = formService.getFormsByUser(1L);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).title()).isEqualTo("Survey A");
        assertThat(results.get(1).title()).isEqualTo("Survey B");
    }

    @Test
    void publishForm_changesStatusToPublished() {
        Form form = Form.builder()
                .id(3L)
                .title("Draft Survey")
                .createdBy(testUser)
                .status(FormStatus.DRAFT)
                .build();

        when(formRepository.findById(3L)).thenReturn(Optional.of(form));
        when(formRepository.save(any(Form.class))).thenAnswer(inv -> inv.getArgument(0));

        FormDto result = formService.publishForm(3L);

        assertThat(result.status()).isEqualTo(FormStatus.PUBLISHED);
    }

    @Test
    void publishForm_whenFormNotFound_throwsRuntimeException() {
        when(formRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formService.publishForm(404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Form not found");
    }
}
