package rocks.xmpp.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christian Schudt
 */
public class StringsTest {

    @Test
    public void isNullOrEmpty() {
        Assert.assertTrue(Strings.isNullOrEmpty(null));
        Assert.assertTrue(Strings.isNullOrEmpty(""));
        Assert.assertFalse(Strings.isNullOrEmpty(" "));
    }

    @Test
    public void toDash() {
        Assert.assertNull(Strings.toDash(null));
        Assert.assertEquals(Strings.toDash("StringUtilsTest"), "string-utils-test");
        Assert.assertEquals(Strings.toDash("TestÄaA"), "test-äa-a");
    }

    @Test
    public void toUnderscore() {
        Assert.assertNull(Strings.toUnderscore(null));
        Assert.assertEquals(Strings.toUnderscore("StringUtilsTest"), "string_utils_test");
        Assert.assertEquals(Strings.toUnderscore("TestÄaA"), "test_äa_a");
    }
}
