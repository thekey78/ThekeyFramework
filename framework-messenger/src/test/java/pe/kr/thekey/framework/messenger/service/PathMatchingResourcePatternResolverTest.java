package pe.kr.thekey.framework.messenger.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PathMatchingResourcePatternResolverTest {

    @Test
    public void testPathMatchingResourcePatternResolverFile() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:messages/body/test-mapping.xml");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isFile());
        assertTrue(resource.isReadable());
    }

    @Test
    public void testPathMatchingResourcePatternResolverDirectory() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:messages/body/");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isFile());
        assertFalse(resource.isReadable());
    }

    @Test
    public void testPathMatchingResourcePatternResolverDirectory1() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath*:messages/body/");
        assertNotNull(resource);
        assertFalse(resource.exists());
        assertFalse(resource.isFile());
        assertFalse(resource.isReadable());
    }
    @Test
    public void testPathMatchingResourcePatternResolverDirectory2() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:messages/body/");
        assertNotNull(resources);
        for (Resource resource : resources) {
            assertNotNull(resource);
            assertTrue(resource.exists());
            assertFalse(resource.isReadable());
            File dir = resource.getFile();
            assertTrue(dir.isDirectory());
        }
    }

    @Test
    public void testPathMatchingResourcePatternResolverPattern() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:messages/**/*.xml");
        assertNotNull(resources);
        assertEquals(2, resources.length);
        for (Resource resource : resources) {
            assertNotNull(resource);
            assertTrue(resource.exists());
            assertTrue(resource.isReadable());
            assertTrue(resource.isFile());
        }
    }

    @Test
    public void testPathMatchingResourcePatternResolverPattern2() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:messages/**/*.xml");
        assertNotNull(resources);
        assertEquals(2, resources.length);
        for (Resource resource : resources) {
            assertNotNull(resource);
            assertTrue(resource.exists());
            assertTrue(resource.isReadable());
            assertTrue(resource.isFile());
        }
    }
}
