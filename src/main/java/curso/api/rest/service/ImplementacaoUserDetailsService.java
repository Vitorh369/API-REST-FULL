package curso.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		/*CONSULTA NO BANCO O usuario*/
		
		Usuario usuario =  usuarioRepository.findUserByLogin(username);
		
		if(username == null) {
			throw new UsernameNotFoundException("Usuario não encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
	}

}
