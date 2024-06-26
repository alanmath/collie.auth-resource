package insper.collie.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import insper.collie.account.AccountController;
import insper.collie.account.AccountIn;
import insper.collie.account.AccountOut;
import insper.collie.account.LoginIn;

@Service
public class AuthService {

    @Autowired
    private AccountController accountController;

    @Autowired
    private JwtService jwtService;

    @SuppressWarnings("null")
    public String register(Register in) {
        final String password = in.password().trim();
        if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password is required");
        if (password.length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters long");

        ResponseEntity<AccountOut> response = accountController.create(AccountIn.builder()
            .name(in.name())
            .email(in.email())
            .password(password)
            .build()
        );
        if (response.getStatusCode().isError()) throw new IllegalArgumentException("Invalid credentials");
        if (null == response.getBody()) throw new IllegalArgumentException("Invalid credentials");
        return response.getBody().id();
    }

    public LoginOut authenticate(String email, String password, String secret) {
        ResponseEntity<AccountOut> response = accountController.login(LoginIn.builder()
            .email(email)
            .password(password)
            .build()
        );
        if (response.getStatusCode().isError()) throw new IllegalArgumentException("Invalid credentials");
        if (null == response.getBody()) throw new IllegalArgumentException("Invalid credentials");
        final AccountOut account = response.getBody();


        String tokenEntrada = "";
        if (secret.equals("admin")) {
            // Cria um token JWT com um segredo
            
            tokenEntrada = jwtService.create(account.id(), account.name(), "ADMIN");
        }  else {
            // Cria um token JWT
            
            tokenEntrada = jwtService.create(account.id(), account.name(), "regular");
        }
        @SuppressWarnings("null")
        final String token = tokenEntrada;
        
        return LoginOut.builder()
            .token(token)
            .build();
    }

    public Token solve(String token) {
        return jwtService.getToken(token);
    }
}
