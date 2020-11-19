package org.wit.ctw.plugins.demo.dictionarypluginsdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.wit.ctw.plugins.demo.dictionarypluginsdemo.web.mapper")
public class DictionaryPluginsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DictionaryPluginsDemoApplication.class, args);
	}

}
