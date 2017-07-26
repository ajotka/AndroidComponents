package project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class Application {

    public static void main(String args[]){
        Calendar.getInstance(TimeZone.getTimeZone("GMT"), new Locale("pl-PL"));
        Locale.setDefault(new Locale("pl-PL"));
        SpringApplication.run(Application.class, args);
    }
}
