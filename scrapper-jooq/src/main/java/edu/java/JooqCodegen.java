package edu.java;

import lombok.experimental.UtilityClass;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Property;
import org.jooq.meta.jaxb.Target;

@SuppressWarnings({"MultipleStringLiterals", "UncommentedMain"})
@UtilityClass
public class JooqCodegen {
    public static void main(String[] args) throws Exception {
        Database database = new Database()
            .withName("org.jooq.meta.extensions.liquibase.LiquibaseDatabase")
            .withProperties(
                new Property().withKey("rootPath").withValue("migrations"),
                new Property().withKey("scripts").withValue("master.yaml")
            );

        Generate options = buildOptions();

        Target target = new Target()
            .withPackageName("edu.java.domain.jooq")
            .withDirectory("scrapper/src/main/java");

        Configuration configuration = new Configuration()
            .withGenerator(
                new Generator()
                    .withDatabase(database)
                    .withGenerate(options)
                    .withTarget(target)
            ).withJdbc(
                new Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl("jdbc:postgresql://localhost:5432/scrapper")
                    .withUser("postgres")
                    .withPassword("postgres")
            );

        GenerationTool.generate(configuration);
    }

    private static Generate buildOptions() {
        return new Generate()
            .withGeneratedAnnotation(true)
            .withGeneratedAnnotationDate(false)
            .withNullableAnnotation(true)
            .withNullableAnnotationType("org.jetbrains.annotations.Nullable")
            .withNonnullAnnotation(true)
            .withNonnullAnnotationType("org.jetbrains.annotations.NotNull")
            .withJpaAnnotations(false)
            .withValidationAnnotations(true)
            .withSpringAnnotations(true)
            .withConstructorPropertiesAnnotation(true)
            .withConstructorPropertiesAnnotationOnPojos(true)
            .withConstructorPropertiesAnnotationOnRecords(true)
            .withFluentSetters(false)
            .withDaos(false)
            .withPojos(true)
            .withComments(false)
            .withDeprecated(false)
            .withJavadoc(false)
            .withXmlTypes(false)
            .withIndexes(false)
            .withGlobalDomainReferences(false)
            .withRoutines(false)
            .withEmptySchemas(false)
            .withEmbeddables(false)
            .withInterfaces(false)
            .withRelations(false)
            .withEmptyCatalogs(false)
            .withSequences(false)
            .withSources(false)
            .withTables(false)
            .withQueues(false)
            .withRecordsImplementingRecordN(false)
            .withImplicitJoinPathsToOne(false);
    }
}
