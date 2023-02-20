package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*ESSA CLASS MAPEIA URL, ENDEREÇO, AUTORIZA OU BLOQUEIA ACESSOS A URLS*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	/*CONFIGURA AS SOLICITAÇÕES DE ACESSO HTTP*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			
		/*ATIVANDO A PROTEÇÃO CONTRA USUARIO QUE NÃO ESTÃO VALIDADOS POR TOKEN*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())	
		
		/*ATIVANDO A PERMISSAO PARA ACESSO A PAGINA INICIAL DO SISTEMA*/
		.disable().authorizeHttpRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
	
		
		/*liberação para fazer get, delete, put, save e etc... para todos os tipos de portas*/
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		/*URL DE LOGOUT - REDIRECIONA APOS DESLOGAR DO SISTEMA*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*MAPEIA URL DE LOGOUT E INVALIDA O USUARIO*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
		
		/*"FILTRA REQUISIÇÃO DE LOGIN PARA AUTENTICÃO"*/
		and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		
		/*FILTRA DEMAIS REQUISIÇÕES PARA VERIFICAR A PRESENÇA DE TOKEN JWT NO HEADER HTTP*/
		.addFilterBefore(new JWTAAPIAutenticaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			
		/*SERVICE QUE IRA CONSULTA O USUARIO NO BANCO DE DADOS*/
		auth.userDetailsService(implementacaoUserDetailsService)
		
		/*TIPO DE CRIPITOGRAFIA DA SENHA*/
		.passwordEncoder(new BCryptPasswordEncoder());
		
	}
	
}
