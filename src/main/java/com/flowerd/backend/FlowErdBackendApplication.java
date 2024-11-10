package com.flowerd.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class FlowErdBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowErdBackendApplication.class, args);
	}

	// 데이터베이스 초기화
	@Bean
	public CommandLineRunner initDataBase(ReactiveMongoTemplate reactiveMongoTemplate) {
		return args -> {
			// 각 컬렉션 삭제 작업을 연결
			Mono<Void> initProcess = reactiveMongoTemplate.dropCollection("table")
					.then(reactiveMongoTemplate.dropCollection("column"))
					.then(reactiveMongoTemplate.dropCollection("constraints"))
					.then(reactiveMongoTemplate.dropCollection("diagram_table"))
					.then(reactiveMongoTemplate.dropCollection("schema"))
					.then(reactiveMongoTemplate.dropCollection("diagram"))
					.then(reactiveMongoTemplate.dropCollection("project"));

			// 모든 작업 완료 후 구독
			initProcess
					.doOnSuccess(unused -> System.out.println("모든 컬렉션이 성공적으로 삭제되었습니다."))
					.doOnError(error -> System.err.println("컬렉션 삭제 중 오류 발생: " + error.getMessage()))
					.subscribe();
		};
	}

}
