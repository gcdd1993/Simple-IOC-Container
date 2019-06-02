package io.github.gcdd1993.ioc.util;

import io.github.gcdd1993.ioc.annotation.Bean;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * TODO
 *
 * @author gaochen
 * @date 2019/6/2
 */
public class PackageScannerTest {

    @Test
    public void findClassesWithAnnotation() {

        List<Class> classList = PackageScanner.findClassesWithAnnotation("io.github.gcdd1993.ioc.util", Bean.class);
        System.out.println(classList);
    }
}