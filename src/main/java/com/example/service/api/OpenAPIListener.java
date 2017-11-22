package com.example.service.api;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;

/**
 * Listens for the Open API output and adds a standard template to the automatic generated API content.
 */
@SwaggerDefinition
public class OpenAPIListener implements ReaderListener {

    @Override
    public void beforeScan(Reader reader, Swagger apiDocs) {
        Tag purpose = new Tag()
                .name("education")
                .description("this was made for educational purposes");
        Tag status = new Tag()
                .name("generated")
                .description("this API was partly generated");
        apiDocs.addTag(purpose);
        apiDocs.addTag(status);
    }

    @Override
    public void afterScan(Reader reader, Swagger apiDocs) {
        Map<String, Path> paths = apiDocs.getPaths();
        paths.forEach((k, p) -> {
            List<Operation> operations = p.getOperations();
            operations.forEach(operation -> {
                ResponseDoc.addStandardResponses(operation);
                HeaderDocs.addStandardParameters(operation);
                ResponseDoc.addVerbSpecificHeaders(p);
            });
        });
        APIDocsGenerator.writeAPI(apiDocs);
    }
}
