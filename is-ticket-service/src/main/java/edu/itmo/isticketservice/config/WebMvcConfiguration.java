package edu.itmo.isticketservice.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> jsonOnlyConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.getObjectMapper().registerModule(new JavaTimeModule());
        jsonOnlyConverters.add(jsonConverter);

        converters.stream()
                .filter(converter -> !(converter instanceof MappingJackson2XmlHttpMessageConverter)
                        && !(converter instanceof SourceHttpMessageConverter))
                .forEach(jsonOnlyConverters::add);

        converters.clear();
        converters.addAll(jsonOnlyConverters);
    }
}
