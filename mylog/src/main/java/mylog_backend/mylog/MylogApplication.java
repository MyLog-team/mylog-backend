package mylog_backend.mylog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "mylog_backend.mylog")

public class MylogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MylogApplication.class, args);
	}

}
