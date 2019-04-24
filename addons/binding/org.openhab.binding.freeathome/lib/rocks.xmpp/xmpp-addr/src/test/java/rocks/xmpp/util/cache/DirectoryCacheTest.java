/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Christian Schudt
 */
public class DirectoryCacheTest {

    private static void deleteDirectory(final Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    @Test
    public void testDirectoryCache() throws IOException {

        // Create a temp cache directory.
        Path file = Files.createTempDirectory(Paths.get("."), "cache_test");
        DirectoryCache cache = new DirectoryCache(file);
        try {
            byte[] data = new byte[]{1, 2, 3};
            Assert.assertEquals(cache.size(), 0);
            Assert.assertFalse(cache.containsKey("1"));
            Assert.assertTrue(cache.isEmpty());
            Assert.assertNull(cache.put("1", data));
            Assert.assertNotNull(cache.put("1", data));
            byte[] loaded = cache.get("1");
            Assert.assertEquals(loaded, data);
            Assert.assertEquals(cache.size(), 1);
            Assert.assertTrue(cache.containsKey("1"));
            Assert.assertFalse(cache.isEmpty());
            Assert.assertNull(cache.remove("0"));
            Assert.assertEquals(cache.remove("1"), new byte[]{1, 2, 3});
            Assert.assertTrue(cache.isEmpty());

            cache.put("1", data);
            cache.put("2", data);
            cache.put("3", data);

            Assert.assertEquals(cache.size(), 3);
            cache.clear();
            Assert.assertTrue(cache.isEmpty());

        } finally {
            deleteDirectory(file);
        }
    }
}
