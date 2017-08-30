import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by azorin on 29/08/2017.
 */
class DatabaseTest {

    public int i = 0;
    @BeforeEach
    void setUp() {
        i++;
        System.out.print("BeforeEach");
    }

    @Test
    void main() {
        i++;
        assert(1==0);
    }

    @Test
    void reconscile() {
        i++;
        assert(1==1);
    }

    @AfterEach
    void tearDown() {
        i++;
        System.out.print("tearDown"+i);
    }
}