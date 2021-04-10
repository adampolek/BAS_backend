package com.example.bas.backend;

import com.example.bas.backend.model.UserRole;
import com.example.bas.backend.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class BackendApplication
//		implements CommandLineRunner
{

	private final UserRoleService userRoleService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		UserRole role1 = new UserRole();
//		UserRole role2 = new UserRole();
//		role1.setName("ROLE_USER");
//		role2.setName("ROLE_ADMIN");
//		userRoleService.save(role1);
//		userRoleService.save(role2);
//	}
}
