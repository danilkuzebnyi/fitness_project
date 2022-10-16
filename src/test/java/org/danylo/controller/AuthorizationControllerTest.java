package org.danylo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTelInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.danylo.model.Country;
import org.danylo.model.Role;
import org.danylo.model.Status;
import org.danylo.model.User;
import org.danylo.repository.UserRepository;
import org.danylo.service.CountryService;
import org.danylo.service.RecaptchaService;
import org.danylo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationControllerTest {
    WebClient webClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @InjectMocks
    MockHttpSession httpSession;

    @SpyBean
    UserService userService;

    @SpyBean
    CountryService countryService;

    @SpyBean
    RecaptchaService recaptchaService;

//
//    @BeforeEach
//    void beforeAll(WebApplicationContext context) {
//        webClient =  MockMvcWebClientBuilder
//                .webAppContextSetup(context)
//                .build();
//    }

    @Test
    void getLogInFormIsSuccessful() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void redirectToLoginIfPageNeedsAuthorization() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void loginSuccessfulIfUserExists() throws Exception {
        mockMvc.perform(formLogin().user("gorin@gmail.com").password("gorin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trainers"));
    }

    @Test
    void loginFailIfWrongCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("user", "gorin")
                        .param("password", "gorin")
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/login?error")
                );
    }

    @Test
    void getRegisterInForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void registerInSuccessful() throws Exception {
        User user = buildUser("t1@test.com", "t1t1");

        mockMvc.perform(get("/signup"))
                .andDo(result -> {
                    httpSession = (MockHttpSession) result.getRequest().getSession();
                    countryService.setCountryFieldsToHttpSession(user.getCountry(), httpSession);
                });
//
//        doNothing().when(countryService).setCountryFieldsToHttpSession(user.getCountry(), httpSession);
//        verify(countryService).setCountryFieldsToHttpSession(any(), any());
//
//
        String recaptchaResponse = "";
        when(recaptchaService.validateResponse(recaptchaResponse)).thenReturn(true);
        when(userService.isUsernameExist(user)).thenReturn(false);
        //when(httpSession.getAttribute("selectedCountry")).thenReturn(user.getCountry());
        httpSession.setAttribute("selectedCountry", user.getCountry());
        httpSession.setAttribute("countries", countryService.getAll());
        httpSession.setAttribute("code", user.getCountry().getCode());

        mockMvc.perform(post("/signup")
                        .session(httpSession)
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName())
                        .param("selectedCountry", String.valueOf(user.getCountry().getId()))
                        .param("telephoneNumber", user.getTelephoneNumber())
                        .param("username", user.getUsername())
                        .param("password", user.getPassword())
                        .param("g-recaptcha-response", recaptchaResponse)
                        //.contentType(MediaType.APPLICATION_JSON)
                        //.content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                //.andDo(result -> countryService.setCountryFieldsToHttpSession(user.getCountry(), httpSession))
                .andExpect(status().isOk());


        //mockMvc.perform(post("/signup").session(httpSession));

        //httpSession.setAttribute("selectedCountry", new Country());

        //when(httpSession.getAttribute("selectedCountry")).thenReturn(new Country());
        verify(userService).save(user);
        //verify(httpSession).setAttribute("activation", any());
    }

    @Test
    void registerIn_Success_WhenFillFormInTemplate() throws Exception {
        mockMvc.perform(get("/signup").with(csrf()));
        webClient = MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc)
                .build();

        User user = buildUser("t1@test.com", "1111");

        HtmlPage page = webClient.getPage("http://localhost/signup");
        HtmlForm form = page.getHtmlElementById("registration-form");
        HtmlTextInput firstName = page.getHtmlElementById("firstName");
        HtmlTextInput lastName = page.getHtmlElementById("lastName");
        HtmlSelect country = page.getHtmlElementById("select-country");
        HtmlTelInput telephoneNumber = page.getHtmlElementById("telephoneNumber");
        HtmlEmailInput username = page.getHtmlElementById("username");
        HtmlPasswordInput password = page.getHtmlElementById("password");

        firstName.setValueAttribute(user.getFirstName());
        lastName.setValueAttribute(user.getLastName());
        country.setSelectedAttribute(String.valueOf(user.getCountry().getId()), true);
        telephoneNumber.setValueAttribute(user.getTelephoneNumber());
        username.setValueAttribute(user.getUsername());
        password.setValueAttribute(user.getPassword());

        HtmlButton submit = page.getHtmlElementById("submit-button");
        HtmlPage newPage = submit.click();

        assertEquals("/login", newPage.getUrl().toString());

    }

    @Test
    void registerInFail() throws Exception {
        User user = buildUser("gorin@gmail.com", "t1t1");

        String recaptchaResponse = "";
        when(recaptchaService.validateResponse(recaptchaResponse)).thenReturn(true);
        doReturn(true).when(userService).isUsernameExist(user);
        //when(userService.isUsernameExist(user)).thenReturn(true);

        mockMvc.perform(post("/signup")
                        .param("g-recaptcha-response", recaptchaResponse)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andDo(print())
                .andExpect(model().attributeHasFieldErrors("user", "country", "username"));

        verify(userService, never()).save(user);
    }

    @Test
    void getActivationCode() throws Exception {
        String code = UUID.randomUUID().toString();
        mockMvc.perform(get("/activation/{code}", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(userService).activateByCode(code);
    }

    User buildUser(String username, String password) {
        User user = new User();
        user.setId(1);
        user.setFirstName("Max");
        user.setLastName("Shevchenko");
        Country country = new Country();
        country.setId(225);
        country.setName("Ukraine");
        country.setCode("380");
        user.setCountry(country);
        user.setTelephoneNumber("678888888");
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.USER);
        user.setActivationCode(UUID.randomUUID().toString());
        user.setStatus(Status.ACTIVE);

        return user;
    }


    private static final class SessionHolder {
        private SessionWrapper session;


        public SessionWrapper getSession() {
            return session;
        }

        public void setSession(SessionWrapper session) {
            this.session = session;
        }
    }


    private static class SessionWrapper extends MockHttpSession {
        private final HttpSession httpSession;

        public SessionWrapper(HttpSession httpSession) {
            this.httpSession = httpSession;
        }

        @Override
        public Object getAttribute(String name) {
            return this.httpSession.getAttribute(name);
        }
    }
}