package com.hanttamhanttam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
class HanttamhanttamApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private DataSource dataSource;

}

