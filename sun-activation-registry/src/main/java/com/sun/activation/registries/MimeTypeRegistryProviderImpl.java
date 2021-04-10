package com.sun.activation.registries;

import jakarta.activation.MimeTypeRegistry;
import jakarta.activation.spi.MimeTypeRegistryProvider;

import java.io.IOException;
import java.io.InputStream;

public class MimeTypeRegistryProviderImpl implements MimeTypeRegistryProvider {
    @Override
    public MimeTypeRegistry getByFileName(String name) throws IOException {
        return new MimeTypeFile(name);
    }

    @Override
    public MimeTypeRegistry getByInputStream(InputStream inputStream) throws IOException {
        return new MimeTypeFile(inputStream);
    }

    @Override
    public MimeTypeRegistry getDefault() {
        return new MimeTypeFile();
    }
}
