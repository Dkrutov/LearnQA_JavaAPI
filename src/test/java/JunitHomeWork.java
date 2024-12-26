import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JunitHomeWork {

    @Test
    public void testLength() {
        String hello = "Hello, world!!!";
        assertTrue( hello.length() <= 15,"Unexpected length");
    }

}
