package com.sun.activation.registries;

import jakarta.activation.MailcapRegistry;
import jakarta.activation.spi.MailcapRegistryProvider;

import java.io.IOException;
import java.io.InputStream;

public class MailcapRegistryProviderImpl implements MailcapRegistryProvider {

    @Override
    public MailcapRegistry getByFileName(String name) throws IOException {
        return new MailcapFile(name);
    }

    @Override
    public MailcapRegistry getByInputStream(InputStream inputStream) throws IOException {
        return new MailcapFile(inputStream);
    }

    @Override
    public MailcapRegistry getDefault() {
        return new MailcapFile();
    }
}
