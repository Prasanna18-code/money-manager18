package in.pras.moneymanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MoneymanageApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneymanageApplication.class, args);
	}

}
