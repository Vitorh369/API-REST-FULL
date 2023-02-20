package curso.api.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/*class auxiliar injeção de dependencia*/
@Component
public class ApplicationContextLoad implements ApplicationContextAware {

	@Autowired
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = (ApplicationContext) applicationContext;

	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}