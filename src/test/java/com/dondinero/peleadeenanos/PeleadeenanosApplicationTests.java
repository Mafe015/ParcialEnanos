package com.dondinero.peleadeenanos;

import com.dondinero.peleadeenanos.config.StubJwtDecoderConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(StubJwtDecoderConfig.class)
class PeleadeenanosApplicationTests {

    @Test
    void contextLoads() {
    }

}
