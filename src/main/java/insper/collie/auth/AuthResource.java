package insper.collie.auth;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.qos.logback.classic.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;


@RestController
@Tag(name = "Auth", description = "API de Autenticação")
public class AuthResource implements AuthController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthResource.class);
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário", description = "Cria uma nova conta de usuário e retorna o ID do usuário criado.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
        })
    public ResponseEntity<?> create(RegisterIn in) {

        final String id = authService.register(Register.builder()
            .name(in.name())
            .email(in.email())
            .password(in.password())
            .build()
        );

        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri())
            .build();
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Autenticar usuário", description = "Autentica o usuário e retorna um token de acesso.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso", content = @Content(schema = @Schema(implementation = LoginOut.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
        })
    public ResponseEntity<LoginOut> authenticate(CredentialIn in) {
        return ResponseEntity.ok(authService.authenticate(in.email(), in.password()));
    }

    @PostMapping("/solve")
    @Operation(summary = "Resolver token", description = "Valida o token e retorna informações do usuário.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token resolvido com sucesso", content = @Content(schema = @Schema(implementation = SolveOut.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido")
        })
    public ResponseEntity<SolveOut> solve(SolveIn in) {
        final Token token = authService.solve(in.token());
        logger.info("Token: {}", token); // Loga o token
        return ResponseEntity.ok(
            SolveOut.builder()
                .id(token.id())
                .name(token.name())
                .role(token.role())
                .build()
        );
    }
}
