package org.viators.argo.docs.files;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.docs.doccategory.DocCategoryT;

@Entity
@Table(name = "document_files")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class DocumentFileT extends BaseEntity {

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "description", length = 400)
    private String description;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_category_id", referencedColumnName = "id", nullable = false)
    private DocCategoryT docCategory;
}
