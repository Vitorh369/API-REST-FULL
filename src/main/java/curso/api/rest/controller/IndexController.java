package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.UsuarioRepository;

/*ARQUITETURA REST*/

// PARA LIBERA A CLASS TODA BASTA COLOCAR APENAS ANOTAÇÃO @CrossOrigin
//@CrossOrigin(origins = "*" ) //LIBERAÇÃO DE REQUISIÇÃO AJAX PARA QUALQUE LUGAR ACESSAR, QUALQUE SISTEMA PODERA ACESSAR! PODEMOS PASSAR UMA ORIGEM ESPECIFICA PARA UMA OU DEIXAR LIVRE PARA VARIOS COMPUTADORES
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	/* SERVIÇO REST */

	@GetMapping(value = "/{id}/relaoriopdf", produces = "application/json" ) // produces para devolver sempre um json
	public ResponseEntity<UsuarioDTO> relatorio(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	
	//@CrossOrigin(origins = "www.teste.com")// PODEMOS DEIXAR APENAS UM ENDPOINT DIPONIVEL PARA UM CLIENTE ESPECIFICO, COLOCANDO UM ENREÇO 
	@GetMapping(value = "/{id}", produces = "application/json") // produces para devolver sempre um json
	public ResponseEntity<UsuarioDTO> initV1(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	
	//@CrossOrigin(origins = {"www.teste.com", "www.teste2.com"})//PODEMOS CRIAR UMA ARRAY DE PARA VARIOS ENDEREÇO ESPECIFICO
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return "ok";
	}
	
	@DeleteMapping(value = "/{id}/venda", produces = "application/text")
	public String deleteVenda(@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id); /*iria dedleta todas as vendas do usuario*/
		
		return "ok";
	}

	
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheusuario" , allEntries = true) // REMOVE O CACHE QUE NÃO ESTA SENDO USADO A MUITO TEMPO
	@CachePut("cacheusuario") //INDENTIFICA ATUALIZÇOES NO BANCO DE DADOS, E COLOCA NO CACHE
	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		//Thread.sleep(6000); /*SIMULANDO PROCESSO LENTO DE 6 SEGUNDOS PARA TESTAR O CACHE*/

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		for(int pos = 0;  pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
			}
		
		/*CONSUMINDO API PUBLICA EXTERNA*/
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		
		/*CONSUMINDO API PUBLICA EXTERNA*/
		
		
		
		String senhaCripitografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCripitografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarVenda(@PathVariable Long iduser, @PathVariable Long idvenda) {

		/* SIMULANDO UMA VENDA */
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity("id user:" + iduser + " idvenda: " + idvenda, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for(int pos = 0;  pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
			}
		
		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		if(!userTemporario.getSenha().equals(usuario.getSenha())) {/*SENHAS DIFERENTES*/
			String senhaCripitografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCripitografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario); // save: salva e atualiza

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity<Usuario> updateVenda(@PathVariable Long iduser, @PathVariable Long idvenda) {

		/* SIMULANDO UMA VENDA */
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity("Venda atualizada", HttpStatus.OK);
	}
	
	

}
