package com.baomidou.jobs.admin;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.baomidou.jobs.starter.starter.EnableJobsMybatisPlus;

/**
 * Job Admin
 *
 * @author xxl jobob
 * @since 2019-05-31
 */
@EnableJobsMybatisPlus
@EnableTransactionManagement
@SpringBootApplication
public class JobsAdminApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(JobsAdminApplication.class);
		application.setBannerMode(Banner.Mode.CONSOLE);
		application.run(args);
	}
}