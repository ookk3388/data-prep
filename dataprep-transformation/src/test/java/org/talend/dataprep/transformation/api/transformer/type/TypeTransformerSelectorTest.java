package org.talend.dataprep.transformation.api.transformer.type;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.talend.dataprep.transformation.exception.TransformationErrorCodes.UNABLE_TO_PARSE_JSON;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.api.transformer.json.JsonWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TypeTransformerSelectorTest {

    @Autowired
    private TypeTransformerSelector transformer;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private StringWriter writer;

    private TransformerWriter transformerWriter;

    //@formatter:off
    private final ParsedActions identityAction = new ParsedActions(row -> {}, rowMetadata -> {});
    //@formatter:on

    //@formatter:off
    private final ParsedActions changeLastnameAction = new ParsedActions(
            row -> {
                final String transformedLastname = row.get("lastname").toUpperCase();
                row.set("lastname", transformedLastname);
            },
            rowMetadata -> {}
    );
    //@Formatter:on

    //@formatter:off
    private final ParsedActions getChangeNameAndDeleteAction = new ParsedActions(
            row -> {
                final String transformedLastname = row.get("lastname").toUpperCase();
                final String transformedFirstname = row.get("firstname").toUpperCase();
                row.set("lastname", transformedLastname);
                row.set("firstname", transformedFirstname);
                row.setDeleted(row.get("city").equals("Columbia"));
            },
            rowMetadata -> {}
    );
    //@formatter:on

    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        final JsonGenerator generator = new JsonFactory().createGenerator(writer);
        generator.setCodec(builder.build());
        transformerWriter = new JsonWriter(generator);
    }

    @Test
    public void process_should_transform_records() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("nominal.json");
        final String expectedContent = IOUtils.toString(TypeTransformerSelectorTest.class
                .getResourceAsStream("nominal_result.json"));

        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        //@formatter:off
        final TransformerConfiguration configuration = TransformerConfiguration.builder()
                .input(parser)
                .output(transformerWriter)
                .preview(false)
                .actions(DataSetRow.class, changeLastnameAction.getRowTransformer())
                .actions(RowMetadata.class, changeLastnameAction.getMetadataTransformer())
                .build();
        //@formatter:on

        // when
        transformer.process(configuration);

        // then
        assertThat(writer.toString(), sameJSONAs(expectedContent).allowingAnyArrayOrdering().allowingExtraUnexpectedFields());
    }

    @Test
    public void process_should_throw_exception_when_json_not_valid() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("not_valid_object.json");
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        //@formatter:off
        final TransformerConfiguration configuration = TransformerConfiguration.builder()
                .input(parser)
                .output(transformerWriter)
                .preview(false)
                .actions(DataSetRow.class, changeLastnameAction.getRowTransformer())
                .actions(RowMetadata.class, changeLastnameAction.getMetadataTransformer())
                .build();
        //@formatter:on

        // when
        try {
            transformer.process(configuration);
            fail("should have thrown UserException because input json is not valid");
        }

        // then
        catch (Exception e) {
            Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void process_should_throw_exception_when_column_json_is_not_valid() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("not_valid_col.json");
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        //@formatter:off
        final TransformerConfiguration configuration = TransformerConfiguration.builder()
                .input(parser)
                .output(transformerWriter)
                .preview(false)
                .actions(DataSetRow.class, changeLastnameAction.getRowTransformer())
                .actions(RowMetadata.class, changeLastnameAction.getMetadataTransformer())
                .build();
        //@formatter:on

        // when
        try {
            transformer.process(configuration);
            fail("should have thrown UserException because column json is not valid");
        }

        // then
        catch (Exception e) {
            Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void process_should_throw_exception_when_record_json_is_not_valid() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("not_valid_record.json");
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        //@formatter:off
        final TransformerConfiguration configuration = TransformerConfiguration.builder()
                .input(parser)
                .output(transformerWriter)
                .preview(false)
                .actions(DataSetRow.class, changeLastnameAction.getRowTransformer())
                .actions(RowMetadata.class, changeLastnameAction.getMetadataTransformer())
                .build();
        //@formatter:on

        // when
        try {
            transformer.process(configuration);
            fail("should have thrown UserException because record json is not valid");
        }

        // then
        catch (Exception e) {
            Assert.assertEquals(UNABLE_TO_PARSE_JSON.toString(), e.getMessage());
        }
    }

    @Test
    public void process_should_write_preview_for_the_given_indexes() throws Exception {
        // given
        final InputStream inputStream = TypeTransformerSelectorTest.class.getResourceAsStream("preview.json");
        final String expectedContent = IOUtils.toString(TypeTransformerSelectorTest.class
                .getResourceAsStream("preview_result.json"));

        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(inputStream);

        final List<Integer> indexes = new ArrayList<>(3);
        indexes.add(1);
        indexes.add(3);
        indexes.add(5);

        //@formatter:off
        final TransformerConfiguration configuration = TransformerConfiguration.builder().input(parser)
                .output(transformerWriter)
                .indexes(indexes)
                .preview(true)
                .actions(DataSetRow.class, identityAction.getRowTransformer())
                .actions(DataSetRow.class, getChangeNameAndDeleteAction.getRowTransformer())
                .build();
        //@formatter:on

        // when
        transformer.process(configuration);

        // then
        assertEquals(expectedContent, writer.toString(), false);
    }
}