package com.backend.hamilton;

import com.backend.hamilton.application.port.out.TestProductCatalogConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestProductCatalogConfig.class)
class HamiltonApplicationTests {

	@Test
	void contextLoads() {
	}

}
