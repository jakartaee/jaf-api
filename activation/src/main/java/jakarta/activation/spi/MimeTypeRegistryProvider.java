package jakarta.activation.spi;

import jakarta.activation.MimeTypeRegistry;

import java.io.IOException;
import java.io.InputStream;

public interface MimeTypeRegistryProvider {

    MimeTypeRegistry getByFileName(String name) throws IOException;

    MimeTypeRegistry getByInputStream(InputStream inputStream) throws IOException;

    MimeTypeRegistry getDefault();
}