package jakarta.activation.spi;

import jakarta.activation.MailcapRegistry;

import java.io.IOException;
import java.io.InputStream;

public interface MailcapRegistryProvider {

    MailcapRegistry getByFileName(String name) throws IOException;

    MailcapRegistry getByInputStream(InputStream inputStream) throws IOException;

    MailcapRegistry getDefault();
}
