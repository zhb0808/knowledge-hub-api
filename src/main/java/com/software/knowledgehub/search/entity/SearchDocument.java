package com.software.knowledgehub.search.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "kb_document_search", createIndex = false)
@Setting(shards = 1, replicas = 0)
public class SearchDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String summary;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Long)
    private Long knowledgeBaseId;

    @Field(type = FieldType.Keyword)
    private String knowledgeBaseName;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Long)
    private List<Long> tagIds;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Date)
    private OffsetDateTime updatedTime;
}
