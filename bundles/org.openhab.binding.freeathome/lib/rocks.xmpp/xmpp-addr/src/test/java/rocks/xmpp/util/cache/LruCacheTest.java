/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.util.cache;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author Christian Schudt
 */
public class LruCacheTest {

    public static void main(String[] args) {
        // Showcases the performance differences between
        // ConcurrentLinkedDeque and ConcurrentLinkedQueue
        Map<String, String> cache = new LruCache<>(5000);
        String a = "a";
        String b = "b";
        cache.put(a, "");
        for (int i = 0; ; i++) {
            if (i % 1024 == 0) {
                System.out.println("i = " + i);
            }
            cache.put(b, String.valueOf(i));
            cache.get(b);
            cache.remove(b);
        }
    }

    @Test
    public void testPut() {
        Map<String, String> cache = new LruCache<>(2);

        Assert.assertNull(cache.put("1", "1"));
        cache.put("2", "2");
        cache.put("3", "3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertTrue(cache.containsKey("2"));
        Assert.assertTrue(cache.containsKey("3"));

        cache.get("2");
        cache.put("4", "4");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertTrue(cache.containsKey("4"));
        Assert.assertTrue(cache.containsKey("2"));
    }

    @Test
    public void testCompute() {
        LruCache<String, String> cache = new LruCache<>(2);

        Assert.assertEquals(cache.compute("1", (k, v) -> "v1"), "v1");
        Assert.assertEquals(cache.compute("2", (k, v) -> "v2"), "v2");
        Assert.assertEquals(cache.compute("3", (k, v) -> "v3"), "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.compute("1", (k, v) -> "v1"), "v1");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.queue.size(), 2);
        Assert.assertNotNull(cache.get("1"));
        Assert.assertNotNull(cache.get("3"));
    }

    @Test
    public void testComputeIfAbsent() {
        LruCache<String, String> cache = new LruCache<>(2);

        Assert.assertEquals(cache.computeIfAbsent("1", k -> "v1"), "v1");
        Assert.assertEquals(cache.computeIfAbsent("2", k -> "v2"), "v2");
        Assert.assertEquals(cache.computeIfAbsent("3", k -> "v3"), "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.computeIfAbsent("1", k -> "v1"), "v1");
        Assert.assertEquals(cache.computeIfAbsent("3", k -> "v3"), "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.queue.size(), 2);
        Assert.assertNotNull(cache.get("1"));
        Assert.assertNotNull(cache.get("3"));
    }

    @Test
    public void testComputeIfPresent() {
        LruCache<String, String> cache = new LruCache<>(2);

        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");

        Assert.assertEquals(cache.computeIfPresent("1", (k, v) -> "v1"), null);
        Assert.assertEquals(cache.computeIfPresent("2", (k, v) -> "v2"), "v2");
        Assert.assertEquals(cache.computeIfPresent("3", (k, v) -> "v3"), "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.computeIfPresent("3", (k, v) -> "v3"), "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.queue.size(), 2);
        Assert.assertNotNull(cache.get("2"));
        Assert.assertNotNull(cache.get("3"));
    }

    @Test
    public void testReplace() {
        LruCache<String, String> cache = new LruCache<>(2);
        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");

        Assert.assertEquals(cache.replace("1", "v1"), null);
        Assert.assertEquals(cache.replace("2", "v2"), "v2");
        Assert.assertEquals(cache.replace("3", "v3"), "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.replace("2", "v2"), "v2");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.queue.size(), 2);
        Assert.assertNotNull(cache.get("2"));
        Assert.assertNotNull(cache.get("3"));
    }

    @Test
    public void testPutIfAbsent() {
        LruCache<String, String> cache = new LruCache<>(2);

        cache.putIfAbsent("1", "v1");
        cache.putIfAbsent("2", "v2");
        cache.putIfAbsent("3", "v3");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.putIfAbsent("2", "v2"), "v2");

        Assert.assertEquals(cache.size(), 2);
        Assert.assertEquals(cache.queue.size(), 2);
        Assert.assertNotNull(cache.get("2"));
        Assert.assertNotNull(cache.get("3"));
    }

    @Test
    public void testNonExistentValue() {
        LruCache<String, String> cache = new LruCache<>(2);

        Assert.assertNull(cache.get("test"));
        Assert.assertEquals(cache.size(), 0);
        Assert.assertEquals(cache.queue.size(), 0);
    }
}
