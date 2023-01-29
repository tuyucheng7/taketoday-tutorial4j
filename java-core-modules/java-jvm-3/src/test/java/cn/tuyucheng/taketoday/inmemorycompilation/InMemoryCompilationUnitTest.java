package cn.tuyucheng.taketoday.inmemorycompilation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.Collections;
import java.util.List;

public class InMemoryCompilationUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCompilationUnitTest.class);

    final static String QUALIFIED_CLASS_NAME = "cn.tuyucheng.taketoday.inmemorycompilation.TestClass";

    final static String SOURCE_CODE =
      "package cn.tuyucheng.taketoday.inmemorycompilation;\n"
        + "public class TestClass implements InMemoryClass {\n"
        + "@Override\n"
        + "    public void runCode() {\n"
        + "        System.out.println(\"code is running...\");\n"
        + "    }\n"
        + "}\n";

    @Test
    public void whenStringIsCompiled_ThenCodeShouldExecute() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));

        List<JavaFileObject> sourceFiles = Collections.singletonList(new JavaSourceFromString(QUALIFIED_CLASS_NAME, SOURCE_CODE));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);

        boolean result = task.call();

        if (!result) {
            diagnostics.getDiagnostics()
              .forEach(d -> LOGGER.error(String.valueOf(d)));
        } else {
            ClassLoader classLoader = manager.getClassLoader(null);
            Class<?> clazz = classLoader.loadClass(QUALIFIED_CLASS_NAME);
            InMemoryClass instanceOfClass = (InMemoryClass) clazz.newInstance();

            Assertions.assertInstanceOf(InMemoryClass.class, instanceOfClass);

            instanceOfClass.runCode();
        }
    }
}