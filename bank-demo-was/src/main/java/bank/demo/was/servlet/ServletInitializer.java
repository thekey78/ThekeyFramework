package bank.demo.was.servlet;

import bank.demo.was.BankDemoWasApplication;
import lombok.NonNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(@NonNull SpringApplicationBuilder application) {
        return application.sources(BankDemoWasApplication.class);
    }
}
