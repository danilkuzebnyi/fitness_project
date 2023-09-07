package org.danylo.controller;

import org.danylo.model.*;
import org.danylo.service.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockitoSettings
public class AuthorizationControllerUnitTest {
    private MockMvc mockMvc;
    @InjectMocks
    private AuthorizationController authorizationController;
    @Mock
    private UserService userService;
    @Mock
    private CountryService countryService;
    @Mock
    private RecaptchaService recaptchaService;
    @Mock
    private HttpSession httpSession;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorizationController).build();
        verifyNoMoreInteractions(userService, countryService, recaptchaService, httpSession);
    }

    @Test
    void getLogInForm_Successful() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void getRegisterInForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk());
    }

    @Test
    public void registerIn_Success() throws Exception {
        User user = new User();
        String recaptchaResponse = UUID.randomUUID().toString();
        when(httpSession.getAttribute("selectedCountry")).thenReturn(new Country());
        when(userService.isUsernameExist(user)).thenReturn(false);
        when(recaptchaService.validateResponse(recaptchaResponse)).thenReturn(true);

        mockMvc.perform(post("/signup")
                        .param("g-recaptcha-response", recaptchaResponse)
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).save(user);
    }

    @Test
    void registerIn_Fail_WhenUserIsAlreadyRegistered() throws Exception {
        User user = new User();
        String recaptchaResponse = UUID.randomUUID().toString();
        when(httpSession.getAttribute("selectedCountry")).thenReturn(new Country());
        when(recaptchaService.validateResponse(recaptchaResponse)).thenReturn(true);
        when(userService.isUsernameExist(user)).thenReturn(true);

        mockMvc.perform(post("/signup")
                        .param("g-recaptcha-response", recaptchaResponse)
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("authorization/signup"));

        verify(userService).rejectUsernameValue(any(BindingResult.class));
        verify(userService, never()).save(user);
    }

    @Test
    void registerIn_Fail_WhenCountryIsNotSelected() throws Exception {
        User user = new User();
        String recaptchaResponse = UUID.randomUUID().toString();
        when(httpSession.getAttribute("selectedCountry")).thenReturn(null);
        when(recaptchaService.validateResponse(recaptchaResponse)).thenReturn(true);
        when(userService.isUsernameExist(user)).thenReturn(false);

        mockMvc.perform(post("/signup")
                        .param("g-recaptcha-response", recaptchaResponse)
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("authorization/signup"));

        verify(userService).rejectUserCountryValue(any(BindingResult.class));
        verify(userService, never()).save(user);
    }

    @Test
    void registerIn_Fail_WhenRecaptchaValidationIsNotPassed() throws Exception {
        User user = new User();
        String recaptchaResponse = UUID.randomUUID().toString();
        when(httpSession.getAttribute("selectedCountry")).thenReturn(new Country());
        when(recaptchaService.validateResponse(recaptchaResponse)).thenReturn(false);
        when(userService.isUsernameExist(user)).thenReturn(false);

        mockMvc.perform(post("/signup")
                        .param("g-recaptcha-response", recaptchaResponse)
                        .flashAttr("user", user))
                .andExpectAll(
                        status().isOk(),
                        forwardedUrl("authorization/signup"),
                        model().attribute("recaptchaError", ""),
                        model().attribute("confirmationPasswordError", "")
                );
        verify(userService).sendConfirmationMessageToEmail(eq(user), anyString());
        verify(httpSession).setAttribute("isConfirmationMessageSent", true);
        verify(userService, never()).save(user);
    }

    @Test
    void getActivationCode() throws Exception {
        String code = UUID.randomUUID().toString();
        mockMvc.perform(get("/activation/{code}", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).activateByCode(code);
    }
}
