/*
 * Copyright 2015-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Properties;

public class MavenWrapperDownloader {
    private static final String WRAPPER_PROPERTIES_PATH = ".mvn/wrapper/maven-wrapper.properties";
    private static final String DEFAULT_WRAPPER_URL =
            "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar";

    public static void main(String[] args) {
        try {
            File baseDirectory = new File(System.getProperty("user.dir"));
            File wrapperProperties = new File(baseDirectory, WRAPPER_PROPERTIES_PATH);
            if (!wrapperProperties.exists()) {
                System.err.println("Missing maven-wrapper.properties at " + wrapperProperties.getAbsolutePath());
                System.exit(1);
            }

            Properties properties = new Properties();
            try (InputStream in = Files.newInputStream(wrapperProperties.toPath())) {
                properties.load(in);
            }

            String wrapperUrl = properties.getProperty("wrapperUrl", DEFAULT_WRAPPER_URL);
            File outputFile = new File(baseDirectory, ".mvn/wrapper/maven-wrapper.jar");

            if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory: " + outputFile.getParentFile());
            }

          System.out.println("Downloading Maven wrapper from " + wrapperUrl);
          URL wrapperJarUrl = java.net.URI.create(wrapperUrl).toURL();
          try (ReadableByteChannel rbc = Channels.newChannel(wrapperJarUrl.openStream());
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }

            System.out.println("Maven wrapper downloaded to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to download Maven wrapper jar: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
