package cn.ktchen.landlords;

import cn.ktchen.landlords.card.Card;
import cn.ktchen.landlords.card.SingerCards;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.List;

@SpringBootApplication
public class LandlordsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LandlordsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}

