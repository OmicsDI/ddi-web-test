import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by azorin on 29/08/2017.
 */
class DatabaseTest {
    @BeforeEach
    void setUp() {
        System.out.print("BeforeEach");
    }

    @Test
    void main() {

        assert(1==0);
    }

    @Test
    void reconscile() {

        assert(1==1);

    }

    @AfterEach
    void tearDown() {
        System.out.print("tearDown");
    }
}