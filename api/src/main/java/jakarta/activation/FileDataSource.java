/*
 * Copyright (c) 1997, 2023, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The FileDataSource class implements a simple DataSource object
 * that encapsulates a file. It provides data typing services via
 * a FileTypeMap object. <p>
 *
 * <b>FileDataSource Typing Semantics</b><p>
 *
 * The FileDataSource class delegates data typing of files
 * to an object subclassed from the FileTypeMap class.
 * The <code>setFileTypeMap</code> method can be used to explicitly
 * set the FileTypeMap for an instance of FileDataSource. If no
 * FileTypeMap is set, the FileDataSource will call the FileTypeMap's
 * getDefaultFileTypeMap method to get the System's default FileTypeMap.
 * <p>
 * <b>API Note:</b>
 * It is recommended to construct a {@code FileDataSource} using a {@code Path}
 * instead of using a {@code File} since {@code Path} contains enhanced functionality.
 *
 * @see jakarta.activation.DataSource
 * @see jakarta.activation.FileTypeMap
 * @see jakarta.activation.MimetypesFileTypeMap
 */
public class FileDataSource implements DataSource {

    // keep track of original 'ref' passed in, non-null
    // one indicated which was passed in:
    private Path _path = null;
    private FileTypeMap typeMap = null;

    /**
     * Creates a FileDataSource from a File object. <i>Note:
     * The file will not actually be opened until a method is
     * called that requires the file to be opened.</i>
     * <p>
     * <b>API Note:</b>
     * {@code FileDataSource(Path)} constructor should be preferred over this one.
     *
     * @param file the file
     */
    public FileDataSource(File file) {
        _path = file.toPath();    // save the file Object...
    }

    /**
     * Creates a FileDataSource from a Path object. <i>Note: The file will not
     * actually be opened until a method is called that requires the file to be
     * opened.</i>
     *
     * @param path the file
     */
    public FileDataSource(Path path) {
        _path = path;
    }

    /**
     * Creates a FileDataSource from
     * the specified path name. <i>Note:
     * The file will not actually be opened until a method is
     * called that requires the file to be opened.</i>
     *
     * @param name the system-dependent file name.
     */
    public FileDataSource(String name) {
        this(Paths.get(name));    // use the file constructor
    }

    /**
     * This method will return an InputStream representing the
     * the data and will throw an IOException if it can
     * not do so. This method will return a new
     * instance of InputStream with each invocation.
     *
     * @return an InputStream
     */
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(_path);
    }

    /**
     * This method will return an OutputStream representing the
     * the data and will throw an IOException if it can
     * not do so. This method will return a new instance of
     * OutputStream with each invocation.
     *
     * @return an OutputStream
     */
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(_path);
    }

    /**
     * This method returns the MIME type of the data in the form of a
     * string. This method uses the currently installed FileTypeMap. If
     * there is no FileTypeMap explicitly set, the FileDataSource will
     * call the <code>getDefaultFileTypeMap</code> method on
     * FileTypeMap to acquire a default FileTypeMap. <i>Note: By
     * default, the FileTypeMap used will be a MimetypesFileTypeMap.</i>
     *
     * @return the MIME Type
     * @see jakarta.activation.FileTypeMap#getDefaultFileTypeMap
     */
    public String getContentType() {
        // check to see if the type map is null?
        if (typeMap == null)
            return FileTypeMap.getDefaultFileTypeMap().getContentType(_path);
        else
            return typeMap.getContentType(_path);
    }

    /**
     * Return the <i>name</i> of this object. The FileDataSource
     * will return the file name of the object.
     *
     * @return the name of the object.
     * @see jakarta.activation.DataSource
     */
    public String getName() {
        return _path.getFileName().toString();
    }

    /**
     * Return the File object that corresponds to this FileDataSource.
     *
     * @return the File object for the file represented by this object.
     */
    public File getFile() {
        return _path.toFile();
    }

    /**
     * Return the Path object that corresponds to this FileDataSource.
     *
     * @return the Path object for the file represented by this object.
     */
    public Path getPath() {
        return _path;
    }

    /**
     * Set the FileTypeMap to use with this FileDataSource
     *
     * @param map The FileTypeMap for this object.
     */
    public void setFileTypeMap(FileTypeMap map) {
        typeMap = map;
    }
}
