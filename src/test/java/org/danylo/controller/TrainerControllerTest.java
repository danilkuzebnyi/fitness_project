package org.danylo.controller;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Comparators;
import org.assertj.core.util.Streams;
import org.danylo.model.Specialization;
import org.danylo.repository.SpecializationRepository;
import org.danylo.repository.TrainerRepository;
import org.danylo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("a@gmail.com")
@Transactional
class TrainerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    SpecializationRepository specializationRepository;

    @Autowired
    UserService userService;

    @SpyBean
    @Qualifier("trainerRepository")
    TrainerRepository trainerRepository;

    WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc)
                .build();
    }

    @Test
    void showAllTrainers_PageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/trainers"))
                .andExpect(status().isOk());
    }

    @Test
    void showAllTrainers_ShowedOnlyTrainersWithSelectedSpeciality() throws IOException {
        List<Specialization> specializations = specializationRepository.showAllSpecializations();

        for (Specialization specialization : specializations) {
            String selectedSpeciality = specialization.getName();
            HtmlPage page = webClient.getPage("http://localhost/trainers");
            HtmlElement selectedSpecialityHtmlElement = page.getHtmlElementById(selectedSpeciality);
            HtmlPage pageWithSelectedSpeciality = selectedSpecialityHtmlElement.click();
            List<DomElement> trainerSpecializations = pageWithSelectedSpeciality.getElementsById("trainerSpecializations");

            assertEquals("http://localhost/trainers?specialization=" + selectedSpeciality, pageWithSelectedSpeciality.getUrl().toString());
            assertTrue(trainerSpecializations.stream().allMatch(
                    trainerSpecialization -> Streams.stream(trainerSpecialization.getChildElements())
                            .anyMatch(speciality -> speciality.getFirstChild().toString().equals(selectedSpeciality)
                            )));
        }
    }

    @Test
    void showAllTrainers_SortByAscendingPrice() throws IOException {
        HtmlPage page = webClient.getPage("http://localhost/trainers");
        HtmlElement sortByAscPrice = page.getHtmlElementById("sortByAscPrice");
        HtmlPage pageWithSortByAscPrice = sortByAscPrice.click();
        List<DomElement> trainersPrices = pageWithSortByAscPrice.getElementsById("trainerPrice");

        assertTrue(Comparators.isInOrder(trainersPrices, Comparator.comparing
                (element -> Integer.parseInt(element.getFirstChild().toString().replace(" ₴", "")))
        ));
    }

    @Test
    void showAllTrainers_SortByAscendingPriceWithSelectedSpeciality() throws IOException {
        List<Specialization> specializations = specializationRepository.showAllSpecializations();
        HtmlPage page = webClient.getPage("http://localhost/trainers");

        for (Specialization specialization : specializations) {
            String selectedSpeciality = specialization.getName();
            HtmlElement selectedSpecialityHtmlElement = page.getHtmlElementById(selectedSpeciality);
            HtmlPage pageWithSelectedSpeciality = selectedSpecialityHtmlElement.click();
            HtmlElement sortByAscPrice = pageWithSelectedSpeciality.getHtmlElementById("sortByAscPrice");
            HtmlPage pageWithSelectedSpecialityAndSortByAscPrice = sortByAscPrice.click();
            List<DomElement> trainersPrices = pageWithSelectedSpecialityAndSortByAscPrice.getElementsById("trainerPrice");

            assertEquals("http://localhost/trainers?specialization=" + selectedSpeciality + "&sorting=small_price", pageWithSelectedSpecialityAndSortByAscPrice.getUrl().toString());
            assertTrue(Comparators.isInOrder(trainersPrices, Comparator.comparing
                    (element -> Integer.parseInt(element.getFirstChild().toString().replace(" ₴", "")))
            ));

        }
    }

    @Test
    void showAllTrainers_SortByDescendingPrice() throws IOException {
        HtmlPage page = webClient.getPage("http://localhost/trainers");
        HtmlElement sortByAscPrice = page.getHtmlElementById("sortByDescPrice");
        HtmlPage pageWithSortByAscPrice = sortByAscPrice.click();
        List<DomElement> trainersPrices = pageWithSortByAscPrice.getElementsById("trainerPrice");

        assertTrue(Comparators.isInOrder(trainersPrices, (domElement1, domElement2) -> {
            int price1 = Integer.parseInt(domElement1.getFirstChild().toString().replace(" ₴", ""));
            int price2 = Integer.parseInt(domElement2.getFirstChild().toString().replace(" ₴", ""));
            return Integer.compare(price2, price1);
        }));
    }

    @Test
    void showAllTrainers_SortByDescendingPriceAndSelectedSpeciality() throws IOException {
        List<Specialization> specializations = specializationRepository.showAllSpecializations();
        HtmlPage page = webClient.getPage("http://localhost/trainers");

        for (Specialization specialization : specializations) {
            String selectedSpeciality = specialization.getName();
            HtmlElement selectedSpecialityHtmlElement = page.getHtmlElementById(selectedSpeciality);
            HtmlPage pageWithSelectedSpeciality = selectedSpecialityHtmlElement.click();
            HtmlElement sortByDescPrice = pageWithSelectedSpeciality.getHtmlElementById("sortByDescPrice");
            HtmlPage pageWithSelectedSpecialityAndSortByDescPrice = sortByDescPrice.click();
            List<DomElement> trainersPrices = pageWithSelectedSpecialityAndSortByDescPrice.getElementsById("trainerPrice");

            assertTrue(Comparators.isInOrder(trainersPrices, (domElement1, domElement2) -> {
                int price1 = Integer.parseInt(domElement1.getFirstChild().toString().replace(" ₴", ""));
                int price2 = Integer.parseInt(domElement2.getFirstChild().toString().replace(" ₴", ""));
                return Integer.compare(price2, price1);
            }));
        }
    }

    @Test
    void showAllTrainers_SortByExperience() throws IOException {
        HtmlPage page = webClient.getPage("http://localhost/trainers");
        HtmlElement sortByExperience = page.getHtmlElementById("sortByExperience");
        HtmlPage pageWithSortByExperience = sortByExperience.click();
        List<DomElement> trainers = pageWithSortByExperience.getElementsById("link_wrapper");

        assertTrue(Comparators.isInOrder(trainers, (domElement1, domElement2) -> {
            HtmlPage page1 = null;
            HtmlPage page2 = null;
            try {
                page1 = webClient.getPage("http://localhost/trainers/" + domElement1.getAttribute("onclick").replaceAll("\\D+", ""));
                page2 = webClient.getPage("http://localhost/trainers/" + domElement2.getAttribute("onclick").replaceAll("\\D+", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int trainer1Experience = Integer.parseInt(Objects.requireNonNull(page1).getHtmlElementById("trainer-experience").getFirstChild().toString().replaceAll("\\D+", ""));
            int trainer2Experience = Integer.parseInt(Objects.requireNonNull(page2).getHtmlElementById("trainer-experience").getFirstChild().toString().replaceAll("\\D+", ""));
            return Double.compare(trainer1Experience, trainer2Experience);
        }));
    }

    @Test
    void showAllTrainers_SortByRating() throws IOException {
        HtmlPage page = webClient.getPage("http://localhost/trainers");
        HtmlElement sortByRating = page.getHtmlElementById("sortByRating");
        HtmlPage pageWithSortByRating = sortByRating.click();
        List<DomElement> trainers = pageWithSortByRating.getElementsById("link_wrapper");

        assertTrue(Comparators.isInOrder(trainers, (domElement1, domElement2) -> {
            HtmlPage page1 = null;
            HtmlPage page2 = null;
            try {
                page1 = webClient.getPage("http://localhost/trainers/" + domElement1.getAttribute("onclick").replaceAll("\\D+", ""));
                page2 = webClient.getPage("http://localhost/trainers/" + domElement2.getAttribute("onclick").replaceAll("\\D+", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
            float rating1 = Float.parseFloat(Objects.requireNonNull(page1).getHtmlElementById("current-rating-value").getFirstChild().toString());
            float rating2 = Float.parseFloat(Objects.requireNonNull(page2).getHtmlElementById("current-rating-value").getFirstChild().toString());
            return Float.compare(rating2, rating1);
        }));
    }

    @Test
    void showTrainerPage_PageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/trainers/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/sql/save-rating.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateTrainerPage_InternalServerError_IfUserAlreadyRatedTrainer() throws Exception {
        int trainerId = 1;
        int rating = 4;
        int userId = userService.getCurrentUser().getId();
        mockMvc.perform(post("/trainers/" + trainerId)
                        .param("currentRating", String.valueOf(rating))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(trainerRepository, never()).saveRating(trainerId, userId, rating);
    }

    @Test
    void updateTrainerPage_SaveRating_IfUserTrainedWithTrainerAndNotRatedTrainerYet() throws Exception {
        int trainerId = 12;
        int rating = 4;
        int userId = userService.getCurrentUser().getId();
        mockMvc.perform(post("/trainers/" + trainerId)
                .param("currentRating", String.valueOf(rating))
                .with(csrf()));

        verify(trainerRepository).saveRating(trainerId, userId, rating);
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    void updateTrainerPage_NotSaveRating_IfUserNotTrainedWithTrainer() throws Exception {
        int trainerId = 1;
        int rating = 4;
        int userId = userService.getCurrentUser().getId();
        mockMvc.perform(post("/trainers/" + trainerId)
                .param("currentRating", String.valueOf(rating))
                .with(csrf()));

        verify(trainerRepository, never()).saveRating(trainerId, userId, rating);
    }

    @Test
    void getSuccessPage_PageLoadsSuccessfully_IfUserHasPermission() throws Exception {
        mockMvc.perform(get("/trainers/1/success"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("gorin@gmail.com")
    void getSuccessPage_AccessForbidden_IfUserHasNotPermission() throws Exception {
        mockMvc.perform(get("/trainers/1/success"))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookWorkout_Booked_IfUserHasPermission() throws Exception {
        mockMvc.perform(post("/trainers/{id}/success", 1)
                        .param("date", "2023-01-26")
                        .param("selectedTime", "12:00")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("gorin@gmail.com")
    void bookWorkout_AccessForbidden_IfUserHasNotPermission() throws Exception {
        mockMvc.perform(post("/trainers/{id}/success", 1)
                        .param("date", "2023-01-26")
                        .param("selectedTime", "12:00")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}