package com.example.service.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.util.Yaml;

/**
 * Generates the Open API documentation for the service.
 */
public class APIDocsGenerator {

    private static final Logger LOGGER = Logger.getLogger(APIDocsGenerator.class.getName());
    private static final String API_DOCS_PATH = "target/api";
    private static final String API_DOC_FILENAME = "openapi";

    private static String apiDocsPath;
    private static String apiDocsFileName;

    private APIDocsGenerator() {
        //reduce scope
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 6) {
            handleApiDocsArguments(args);
            configureApiDocs(args);
        } else {
            LOGGER.info("usage is: \n"
                + " - path\n"
                + " - filename\n"
                + " - title\n"
                + " - version\n"
                + " - contact name\n"
                + " - contact email\n"
                + " - url\n"
                + " - terms\n"
                + " \n");
        }
    }

    private static void handleApiDocsArguments(String[] args) {
        apiDocsPath = args[0] != null ? args[0] : API_DOCS_PATH;
        apiDocsFileName = args[1] != null ? args[1] : API_DOC_FILENAME;
    }

    private static void configureApiDocs(String[] args) {
        BeanConfig beanConfig = new BeanConfig();
        Info info = new Info();
        info.setTitle(getTitle(args[2]));
        info.setVersion(getVersion(args[3]));
        Contact contact = new Contact();
        contact.setName(getContactName(args[4]));
        contact.setEmail(getContactEmail(args[5]));
        contact.setUrl(getUrl(args[6]));
        info.setContact(contact);
        info.setTermsOfService(getTerms(args[7]));
        info.description(getDesription(args[8]));
        Swagger api = beanConfig.getSwagger();
        api.setInfo(info);
        beanConfig.setResourcePackage(
                "com.example.resource.greeting" + ","
                + "com.example.service" + ","
                + "com.example.service.api");
        beanConfig.setScan(true);
    }

    private static String getTitle(String title) {
        return title != null ? title : "Greetings Service";
    }

    private static String getVersion(String version) {
        return version != null ? version : "unset Version";
    }

    private static String getContactName(String contactName) {
        return contactName != null ? contactName : "no contact name";
    }

    private static String getContactEmail(String contactEmail) {
        return contactEmail != null ? contactEmail : "nocontact@mail.anywhere";
    }

    private static String getUrl(String url) {
        return url != null ? url : "https://no-ulr.set.anwhere";
    }

    private static String getTerms(String terms) {
        return terms != null ? terms : "for educational purposes only";
    }

    private static String getDesription(String description) {
        return description != null ? description : "No description created";
    }

    public static void writeAPI(Swagger api) {
        File folder = new File(apiDocsPath);
        try {
            if (!folder.exists()) {
                folder.mkdir();
            }
            File json = new File(folder, apiDocsFileName + ".json");
            File yaml = new File(folder, apiDocsFileName + ".yml");
            ObjectMapper generator = new ObjectMapper();
            generator.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            generator.writeValue(json, api);
            Yaml.mapper().writeValue(yaml, api);
        } catch (IOException ioe) {
            String msg = "could not create file" + ioe;
            LOGGER.log(Level.INFO, msg);
        }
    }
}
