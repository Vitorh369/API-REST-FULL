package curso.api.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/*FILTRO AONDE TODAS AS REQUISIÇOES SERÃO CAPTURADAS PARA AUTENTICAR*/
public class JWTAAPIAutenticaoFilter extends GenericFilterBean{


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/*Estabelece a autenticação para a requisição*/
		
		Authentication authentication = new JWTTokeAutenticationService().
				getAuhentication((HttpServletRequest) request, (HttpServletResponse) response);
		
		/*Coloca o processo de autenticação no spring security*/
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		/*Continua o processo*/
		
		
		chain.doFilter(request, response);
		
	}

}
