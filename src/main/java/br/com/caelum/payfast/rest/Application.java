package br.com.caelum.payfast.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import br.com.caelum.payfast.filter.Oauth2Filter;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
	
	@Bean
	public FilterRegistrationBean<Oauth2Filter> loggingFilter(){
	    FilterRegistrationBean<Oauth2Filter> registrationBean 
	      = new FilterRegistrationBean<>();
	         
	    registrationBean.setFilter(new Oauth2Filter());
	    registrationBean.addUrlPatterns("/v1/pagamentos/*");
	         
	    return registrationBean;    
	}

}
