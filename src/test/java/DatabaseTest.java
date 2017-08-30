

import org.junit.jupiter.api.Test;
/**
 * Created by azorin on 29/08/2017.
 */
class DatabaseTest {

    public int i = 0;



    @Test
    void main() {
        i++;
        //System.out.print("main 1");
        System.out.print("main first test\n");
        assert(1==1);
    }


    @Test
    void anothermain() {
        i++;
        //System.out.print("main 2");
        System.out.print("main second test\n");
        assert(1==1);
    }

}