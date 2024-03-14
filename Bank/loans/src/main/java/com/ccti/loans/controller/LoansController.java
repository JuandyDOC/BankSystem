package com.ccti.loans.controller;

import com.ccti.loans.constants.LoansConstants;
import com.ccti.loans.service.ILoansService;
import com.ccti.loans.dto.ContactInfoDto;
import com.ccti.loans.dto.LoansDto;
import com.ccti.loans.dto.ResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
// @AllArgsConstructor
@RequiredArgsConstructor
@Validated
public class LoansController {

    @NotNull
    private ILoansService iLoansService;

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
    public ResponseEntity<ResponseDto> createLoan(
            @RequestParam @Email(message = "A valid email address must be entered") String email) {
        iLoansService.createLoan(email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(LoansConstants.STATUS_201, LoansConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<LoansDto> fetchLoanDetails(
            @RequestParam @Email(message = "A valid email address must be entered") String email) {
        LoansDto loansDto = iLoansService.fetchLoan(email);
        return ResponseEntity.status(HttpStatus.OK).body(loansDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoanDetails(@Valid @RequestBody LoansDto loansDto) {
        boolean isUpdated = iLoansService.updateLoan(loansDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoanDetails(
            @RequestParam @Email(message = "A valid email address must be entered") String email) {
        boolean isDeleted = iLoansService.deleteLoan(email);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_DELETE));
        }
    }

}
