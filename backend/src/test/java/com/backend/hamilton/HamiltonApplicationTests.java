package com.backend.hamilton;

import com.backend.hamilton.application.port.out.TestProductCatalogConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestProductCatalogConfig.class)
class HamiltonApplicationTests {

	@Test
	void contextLoads() {
	}

}
