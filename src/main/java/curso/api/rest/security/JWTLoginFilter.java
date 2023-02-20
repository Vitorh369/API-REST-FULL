package curso.api.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import curso.api.rest.model.Usuario;

/*ESTABELECE O NOSSO GERENCIADOR DE TOKEN*/
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	/* CONFIGURA O GERENCIADOR DE AUTENTICAÇÃO */
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {

		/* OBIRGA A AUTENTICAR A URL */
		super(new AntPathRequestMatcher(url));

		/* GRENCIADOR DE AUTENTICAÇÃO */
		setAuthenticationManager(authenticationManager);
	}

	/* RETORNA O USUARIO AO PROCESSAR A AUTENTIÇÃO */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		new JWTTokeAutenticationService().addAuthentication((HttpServletResponse) response, authResult.getName());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		/* ESTA PEGANDO O TOKEN PARA VALIDAR */
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

		/* RETORNA O USUARIO LOGIN, SENHA E ACESSO */
		return getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}

}
