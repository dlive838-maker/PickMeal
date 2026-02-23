package PickMeal.PickMeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // 이제 필요 없으니 지워도 됩니다.
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("PickMeal.PickMeal.mapper")
public class PickMealApplication {
	public static void main(String[] args) {
		SpringApplication.run(PickMealApplication.class, args);
	}
}