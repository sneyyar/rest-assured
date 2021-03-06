/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.restassured.builder;

import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.internal.mapping.ObjectMapperSerializationContextImpl;
import com.jayway.restassured.internal.mapping.ObjectMapping;
import com.jayway.restassured.internal.multipart.MultiPartSpecificationImpl;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.specification.MultiPartSpecification;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Builder for creating more advanced multi-part requests.
 * <p/>
 * Usage example:
 * <pre>
 * File myFile = ..
 * given().multiPart(new MultiPartSpecBuilder(myFile).with().fileName("some-name.txt").and().with().mimeType("application/vnd.mycompany+text").build()). ..
 * </pre>
 */
public class MultiPartSpecBuilder {

    private final ObjectMapper explicitObjectMapper;
    private final ObjectMapperType explicitObjectMapperType;
    private Object content;
    private String controlName;
    private String mimeType;
    private String charset;
    private String fileName;
    private boolean isControlNameExplicit;
    private boolean isFileNameExplicit;

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(Object content) {
        Validate.notNull(content, "Multi-part content cannot be null");
        this.content = content;
        this.controlName = "file";
        this.isControlNameExplicit = false;
        this.isFileNameExplicit = false;
        this.explicitObjectMapper = null;
        this.explicitObjectMapperType = null;
    }

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(Object content, ObjectMapperType objectMapperType) {
        Validate.notNull(content, "Multi-part content cannot be null");
        Validate.notNull(objectMapperType, "Object mapper type cannot be null");
        this.explicitObjectMapperType = objectMapperType;
        this.explicitObjectMapper = null;
        this.content = content;
        this.controlName = "file";
        this.isControlNameExplicit = false;
        this.isFileNameExplicit = false;
    }

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(Object content, ObjectMapper objectMapper) {
        Validate.notNull(content, "Multi-part content cannot be null");
        Validate.notNull(objectMapper, "Object mapper cannot be null");
        this.explicitObjectMapper = objectMapper;
        this.explicitObjectMapperType = null;
        this.content = content;
        this.controlName = "file";
        this.isControlNameExplicit = false;
        this.isFileNameExplicit = false;
    }

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(InputStream content) {
        this((Object) content);
    }

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(String content) {
        this((Object) content);
    }

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(byte[] content) {
        this((Object) content);
    }

    /**
     * Create a new multi-part specification with control name equal to file.
     *
     * @param content The content to include in the multi-part specification.
     */
    public MultiPartSpecBuilder(File content) {
        this((Object) content);
    }

    /**
     * Specify the control name of this multi-part.
     *
     * @param controlName The control name to use. Default is <code>file</code>.
     * @return An instance of MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder controlName(String controlName) {
        Validate.notEmpty(controlName, "Control name cannot be empty");
        this.controlName = controlName;
        this.isControlNameExplicit = true;
        return this;
    }

    /**
     * Specify the file name of this multi-part. Note that this is only applicable for input streams, byte arrays and files
     * and <i>not</i> string content.
     *
     * @param fileName The file name to use.
     * @return An instance of MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder fileName(String fileName) {
        this.fileName = fileName;
        this.isFileNameExplicit = true;
        return this;
    }

    /**
     * Specify the mime-type for this multi-part.
     *
     * @param mimeType The mime-type
     * @return An instance of MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder mimeType(String mimeType) {
        Validate.notEmpty(mimeType, "Mime-type cannot be empty");
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Specify the charset for this charset.
     *
     * @param charset The charset to use
     * @return An instance of MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder charset(String charset) {
        Validate.notEmpty(charset, "Charset cannot be empty");
        if (content instanceof byte[] || content instanceof InputStream) {
            throw new IllegalArgumentException("Cannot specify charset input streams or byte arrays.");
        }
        this.charset = charset;
        return this;
    }

    /**
     * Just a method that can be used as syntactic sugar.
     *
     * @return The same instance of the MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder with() {
        return this;
    }

    /**
     * Just a method that can be used as syntactic sugar.
     *
     * @return The same instance of the MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder and() {
        return this;
    }

    /**
     * Specify the charset for this charset.
     *
     * @param charset The charset to use
     * @return An instance of MultiPartSpecBuilder
     */
    public MultiPartSpecBuilder charset(Charset charset) {
        Validate.notNull(charset, "Charset cannot be null");
        this.charset = charset.toString();
        return this;
    }

    /**
     * Set the filename of the multi-part to empty (none). This means that the "filename" part will be excluded in the multi-part request.
     * <p>
     * This is the same as calling {@link #fileName(String)} with <code>null</code>.
     * </p>
     *
     * @return An instance of MultiPartSpecBuilder
     * @see #fileName(String)
     */
    public MultiPartSpecBuilder emptyFileName() {
        return fileName(null);
    }

    public MultiPartSpecification build() {
        MultiPartSpecificationImpl spec = new MultiPartSpecificationImpl();
        spec.setCharset(charset);
        applyContentToSpec(spec);
        spec.setControlName(controlName);
        spec.setControlName(controlName);
        spec.setFileName(fileName);
        spec.setMimeType(mimeType);
        spec.setControlNameSpecifiedExplicitly(isControlNameExplicit);
        spec.setFileNameSpecifiedExplicitly(isFileNameExplicit);
        return spec;
    }

    private void applyContentToSpec(MultiPartSpecificationImpl spec) {
        final Object actualContent;
        if (explicitObjectMapper != null) {
            ObjectMapperSerializationContextImpl ctx = new ObjectMapperSerializationContextImpl();
            ctx.setObject(content);
            ctx.setContentType(mimeType);
            actualContent = explicitObjectMapper.serialize(ctx);
        } else if (explicitObjectMapperType != null) {
            actualContent = ObjectMapping.serialize(content, mimeType, null, explicitObjectMapperType, new ObjectMapperConfig(), new EncoderConfig());
        } else {
            actualContent = content;
        }
        spec.setContent(actualContent);
    }
}
