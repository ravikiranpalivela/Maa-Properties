package com.NewsMobile.Config;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class MessageConfig implements WebMvcConfigurer {
		 
	@SuppressWarnings("deprecation")
	@Bean
    public LocaleResolver localeResolver() {	
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.getDefault());
        
        //localeResolver.setDefaultLocale(new Locale("te"));
		/*
		 * localeResolver.setSupportedLocales(Arrays.asList( new Locale("te"), // Telugu
		 * new Locale("en") // English ));
		 */
        
        return localeResolver;
    }

}
