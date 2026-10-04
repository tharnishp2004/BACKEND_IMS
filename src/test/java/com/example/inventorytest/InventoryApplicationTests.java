package com.example.inventorytest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import com.example.inventory.InventoryApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = InventoryApplication.class)
@Disabled("Disabled in CI/CD pipeline to avoid requiring a live database connection during build")
class InventoryApplicationTests {

	@Test
	void contextLoads() {
	}

}
