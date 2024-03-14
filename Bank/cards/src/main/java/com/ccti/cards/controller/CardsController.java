package com.ccti.cards.controller;

import com.ccti.cards.constants.CardsConstants;
import com.ccti.cards.service.ICardsService;
import com.ccti.cards.dto.CardsDto;
import com.ccti.cards.dto.ContactInfoDto;
import com.ccti.cards.dto.ResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping(path = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
// @AllArgsConstructor
@RequiredArgsConstructor
@Validated
public class CardsController {

    @NotNull
    private ICardsService iCardsService;

    @Value("${build.version}")
    private String buildVersion;

    @NotNull
    private Environment environment;

    @NotNull
    private ContactInfoDto contactInfoDto;

    @GetMapping(value = "/build-version", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }

    @GetMapping("/javaHome")
    public ResponseEntity<String> getJavaHome() {
        return ResponseEntity.status(HttpStatus.OK).body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<ContactInfoDto> getContactInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(contactInfoDto);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(
            @Valid @RequestParam @Email(message = "A valid email address must be entered") String email) {
        iCardsService.createCard(email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CardsDto> fetchCardDetails(
            @RequestParam @Email(message = "A valid email address must be entered") String email) {
        CardsDto cardsDto = iCardsService.fetchCard(email);
        return ResponseEntity.status(HttpStatus.OK).body(cardsDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCardDetails(@Valid @RequestBody CardsDto cardsDto) {
        boolean isUpdated = iCardsService.updateCard(cardsDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCardDetails(
            @RequestParam @Email(message = "A valid email address must be entered") String email) {
        boolean isDeleted = iCardsService.deleteCard(email);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_DELETE));
        }
    }

}
