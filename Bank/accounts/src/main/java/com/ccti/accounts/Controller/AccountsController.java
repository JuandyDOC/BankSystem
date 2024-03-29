package com.ccti.accounts.Controller;

import com.ccti.accounts.dto.*;
import com.ccti.accounts.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(
        name = "Controlador para servicio de cuenta",
        description = "Endpoints para las operaciones CRUD del servicio"
)

@Validated
//@AllArgsConstructor
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountsController {

    @Nonnull
    private IAccountService iAccountService;

    @Value("${build.version}")
    private String buildVersion;

    @Nonnull
    private Environment environment;

    @NonNull
    private ContactInfoDto contactInfoDto;

    @GetMapping(value = "hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String helloEndPoint(){
        return "Hola desde Microservicios Accounts!!!";
    }

    @GetMapping(value = "dateTime", produces = MediaType.TEXT_PLAIN_VALUE)
    public String dateTime(){
        return LocalDateTime.now().toString();
    }

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

    @Operation(
            summary = "Crea cuentas en el sistema",
            description = "Crea un cliente y una cuenta en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Http Status created",
                content = @Content(
                schema = @Schema(implementation = ResponseDto.class)
        )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Http Internal Server Error",
                content = @Content(
                        schema = @Schema(implementation = ErrorResponseDto.class)
                )
        )
    })

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccounts(@Valid @RequestBody CustomerDto customerDto){
        iAccountService.createAccount(customerDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201", "La cuenta ha sido creada correctamente !!"));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerAccountDto> fetchAccount(
            @RequestParam
            @Email(message = "Debe especificar un email válido")
            String email){
        CustomerAccountDto customerAccountDto = iAccountService.fetchAccount(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerAccountDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCustomerAccount(
            @RequestParam
            @Email(message = "Debe especificar un email válido")
            String email){
        boolean res = iAccountService.deleteAccount(email);
        if(res) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("200", "El cliente ha sido eliminado correctamente !!"));
        }else{
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("500", "Hubo un error al eliminar el cliente !!"));
        }
    }

    @PatchMapping("/updateByEmail")
    public ResponseEntity<ResponseDto> updatebyEmail(
            @Valid @RequestBody CustomerAccountDto customerAccountDto) {
        boolean isUpdated = iAccountService.updateAccount(customerAccountDto);

        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("200", "La cuenta ha sido actualizada correctamente"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("500", "Algo pasó actualizando la cuenta."));
        }

    }
}
