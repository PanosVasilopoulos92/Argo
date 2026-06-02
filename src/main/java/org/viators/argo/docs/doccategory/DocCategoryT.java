package org.viators.argo.docs.doccategory;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.docs.files.DocumentFileT;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doc_categories")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class DocCategoryT extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 60)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "docCategory", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<DocumentFileT> documentFiles = new HashSet<>();

    public void addDocumentFile(DocumentFileT documentFile) {
        if (documentFile != null) {
            this.documentFiles.add(documentFile);
            documentFile.setDocCategory(this);
        }
    }

}
