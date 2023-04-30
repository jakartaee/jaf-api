/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation;

/**
 * The ActivationDataFlavor class is similar to the JDK's
 * <code>java.awt.datatransfer.DataFlavor</code> class. It allows
 * Jakarta Activation to
 * set all three values stored by the DataFlavor class via a new
 * constructor. It also contains improved MIME parsing in the <code>equals
 * </code> method. Except for the improved parsing, its semantics are
 * identical to that of the JDK's DataFlavor class.
 */

public class ActivationDataFlavor {

    /*
     * Raison d'etre:
     *
     * The DataFlavor class included in JDK 1.1 has several limitations
     * including poor MIME type parsing, and the limitation of
     * only supporting serialized objects and InputStreams as
     * representation objects. This class 'fixes' that.
     */

    private String mimeType = null;
    private MimeType mimeObject = null;
    private String humanPresentableName = null;
    private Class<?> representationClass = null;

    /**
     * Construct an ActivationDataFlavor that represents an arbitrary
     * Java object.
     * <p>
     * The returned ActivationDataFlavor will have the following
     * characteristics:
     * <p>
     * representationClass = representationClass<br>
     * mimeType            = mimeType<br>
     * humanName           = humanName
     *
     * @param representationClass  the class used in this ActivationDataFlavor
     * @param mimeType             the MIME type of the data represented by this class
     * @param humanPresentableName the human presentable name of the flavor
     */
    public ActivationDataFlavor(Class<?> representationClass,
                                String mimeType, String humanPresentableName) {
        // init private variables:
        this.mimeType = mimeType;
        this.humanPresentableName = humanPresentableName;
        this.representationClass = representationClass;
    }

    /**
     * Construct an ActivationDataFlavor that represents a MimeType.
     * <p>
     * The returned ActivationDataFlavor will have the following
     * characteristics:
     * <p>
     * If the mimeType is "application/x-java-serialized-object;
     * class=", the result is the same as calling new
     * ActivationDataFlavor(Class.forName()) as above.
     * <p>
     * otherwise:
     * <p>
     * representationClass = InputStream<p>
     * mimeType = mimeType
     *
     * @param representationClass  the class used in this ActivationDataFlavor
     * @param humanPresentableName the human presentable name of the flavor
     */
    public ActivationDataFlavor(Class<?> representationClass,
                                String humanPresentableName) {
        this.mimeType = "application/x-java-serialized-object";
        this.representationClass = representationClass;
        this.humanPresentableName = humanPresentableName;
    }

    /**
     * Construct an ActivationDataFlavor that represents a MimeType.
     * <p>
     * The returned ActivationDataFlavor will have the following
     * characteristics:
     * <p>
     * If the mimeType is "application/x-java-serialized-object; class=",
     * the result is the same as calling new
     * ActivationDataFlavor(Class.forName()) as above, otherwise:
     * <p>
     * representationClass = InputStream<br>
     * mimeType = mimeType
     *
     * @param mimeType             the MIME type of the data represented by this class
     * @param humanPresentableName the human presentable name of the flavor
     */
    public ActivationDataFlavor(String mimeType, String humanPresentableName) {
        this.mimeType = mimeType;
        try {
            this.representationClass = Class.forName("java.io.InputStream");
        } catch (ClassNotFoundException ex) {
            // XXX - should never happen, ignore it
        }
        this.humanPresentableName = humanPresentableName;
    }

    /**
     * Return the MIME type for this ActivationDataFlavor.
     *
     * @return the MIME type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Return the representation class.
     *
     * @return the representation class
     */
    public Class<?> getRepresentationClass() {
        return representationClass;
    }

    /**
     * Return the Human Presentable name.
     *
     * @return the human presentable name
     */
    public String getHumanPresentableName() {
        return humanPresentableName;
    }

    /**
     * Set the human presentable name.
     *
     * @param humanPresentableName the name to set
     */
    public void setHumanPresentableName(String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }

    /**
     * Compares the ActivationDataFlavor passed in with this
     * ActivationDataFlavor; calls the <code>isMimeTypeEqual</code> method.
     *
     * @param dataFlavor the ActivationDataFlavor to compare with
     * @return true if the MIME type and representation class
     * are the same
     */
    public boolean equals(ActivationDataFlavor dataFlavor) {
        return (isMimeTypeEqual(dataFlavor.mimeType) &&
                dataFlavor.getRepresentationClass() == representationClass);
    }

    /**
     * @param o the <code>Object</code> to compare with
     * @return true if the object is also an ActivationDataFlavor
     * and is equal to this
     */
    public boolean equals(Object o) {
        return ((o instanceof ActivationDataFlavor) &&
                equals((ActivationDataFlavor) o));
    }

    /**
     * Compares only the <code>mimeType</code> against the passed in
     * <code>String</code> and <code>representationClass</code> is
     * not considered in the comparison.
     * <p>
     * If <code>representationClass</code> needs to be compared, then
     * <code>equals(new DataFlavor(s))</code> may be used.
     *
     * @param s the {@code mimeType} to compare.
     * @return true if the String (MimeType) is equal; false otherwise or if
     * {@code s} is {@code null}
     * @deprecated As inconsistent with <code>hashCode()</code> contract,
     * use <code>isMimeTypeEqual(String)</code> instead.
     */
    @Deprecated
    public boolean equals(String s) {
        if (s == null || mimeType == null)
            return false;
        return isMimeTypeEqual(s);
    }

    /**
     * Returns hash code for this <code>ActivationDataFlavor</code>.
     * For two equal <code>ActivationDataFlavor</code>s, hash codes are equal.
     * For the <code>String</code>
     * that matches <code>ActivationDataFlavor.equals(String)</code>, it is not
     * guaranteed that <code>ActivationDataFlavor</code>'s hash code is equal
     * to the hash code of the <code>String</code>.
     *
     * @return a hash code for this <code>ActivationDataFlavor</code>
     */
    public int hashCode() {
        int total = 0;

        if (representationClass != null) {
            total += representationClass.hashCode();
        }

        // XXX - MIME type equality is too complicated so we don't
        // include it in the hashCode

        return total;
    }

    /**
     * Is the string representation of the MIME type passed in equivalent
     * to the MIME type of this ActivationDataFlavor. <p>
     *
     * ActivationDataFlavor delegates the comparison of MIME types to
     * the MimeType class included as part of Jakarta Activation.
     *
     * @param mimeType the MIME type
     * @return true if the same MIME type
     */
    public boolean isMimeTypeEqual(String mimeType) {
        MimeType mt = null;
        try {
            if (mimeObject == null)
                mimeObject = new MimeType(this.mimeType);
            mt = new MimeType(mimeType);
        } catch (MimeTypeParseException e) {
            // something didn't parse, do a crude comparison
            return this.mimeType.equalsIgnoreCase(mimeType);
        }

        return mimeObject.match(mt);
    }

    /**
     * Called on ActivationDataFlavor for every MIME Type parameter to allow
     * ActivationDataFlavor subclasses to handle special parameters like the
     * text/plain charset parameters, whose values are case insensitive.
     * (MIME type parameter values are supposed to be case sensitive).
     * <p>
     * This method is called for each parameter name/value pair and should
     * return the normalized representation of the parameterValue.
     * This method is never invoked by this implementation.
     *
     * @param parameterName  the parameter name
     * @param parameterValue the parameter value
     * @return the normalized parameter value
     * @deprecated
     */
    @Deprecated
    protected String normalizeMimeTypeParameter(String parameterName,
                                                String parameterValue) {
        return parameterValue;
    }

    /**
     * Called for each MIME type string to give ActivationDataFlavor subtypes
     * the opportunity to change how the normalization of MIME types is
     * accomplished.
     * One possible use would be to add default parameter/value pairs in cases
     * where none are present in the MIME type string passed in.
     * This method is never invoked by this implementation.
     *
     * @param mimeType the MIME type
     * @return the normalized MIME type
     * @deprecated
     */
    @Deprecated
    protected String normalizeMimeType(String mimeType) {
        return mimeType;
    }
}
