/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.config;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.service.LocaleManager;
import org.slf4j.Logger;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.service.annotation.Priority;
import org.jbpm.formModeler.service.annotation.Startable;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class that builds the forms modeler's core forms.
 */
@ApplicationScoped
public class CoreFormsBuilder implements Startable {

    private Logger log = LoggerFactory.getLogger(CoreFormsBuilder.class);

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private LocaleManager localeManager;

    @Inject @Config("default,InputText,InputTextBigDecimal,InputTextBigInteger,CheckBox,CheckBoxPrimitiveBoolean," +
            "Date,InputTextDouble,InputTextPrimitiveDouble,InputTextFloat,InputTextPrimitiveFloat," +
            "InputTextByte,InputTextPrimitiveByte,InputTextInteger,InputTextPrimitiveInteger,InputTextLong," +
            "InputTextPrimitiveLong,InputTextShort,InputTextPrimitiveShort,HTMLLabel,Separator, Subform, " +
            "MultipleSubform,InputTextCharacter,InputTextPrimitiveCharacter,InputTextEmail,InputTextArea," +
            "HTMLEditor,Link,I18nHTMLText,CustomInput")
    protected String[] coreFormNames;

    public Priority getPriority() {
        return Priority.HIGH;
    }

    public String getFormPath(String formName) {
        return "org/jbpm/formModeler/core/forms/" + formName + ".form";
    }

    public String getFormResourcesPath(String lang) {
        return "org/jbpm/formModeler/core/forms/forms-resources" + lang + ".properties";
    }

    public void start() {
        Map<String, Properties> formResources = new HashMap<String, Properties>();

        for (String lang : localeManager.getPlatformAvailableLangs()) {
            try {
                String key = lang.equals(localeManager.getDefaultLang()) ? "" : "_" + lang;

                InputStream in =  Thread.currentThread().getContextClassLoader().getResourceAsStream(getFormResourcesPath(key));

                if (in == null) continue;
                Properties props = new Properties();
                props.load(in);
                formResources.put(lang, props);
            } catch (Exception e) {
                log.warn("Error loading resources form lang \"{}\": {}", lang, e);
            }
        }

        for (String formName : coreFormNames) {
            String formPath = getFormPath(formName);
            try {
                // Form is read, deserialized and added to the form manager.
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(formPath);
                Form systemForm = formSerializationManager.loadFormFromXML(is, formResources);
                formManager.addSystemForm(systemForm);
            } catch (Exception e) {
                log.error("Error reading core form file: " + formPath, e);
            }
        }
    }
}
